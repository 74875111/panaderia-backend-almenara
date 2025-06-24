package com.example.panaderiaback.controller

import com.example.panaderiaback.model.*
import com.example.panaderiaback.security.UserSecurity
import com.example.panaderiaback.service.SaleOrderService
import com.example.panaderiaback.service.UserService
import com.example.panaderiaback.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*
import com.example.panaderiaback.dto.OrderItemDTO
import com.example.panaderiaback.dto.ProductDTO
import com.example.panaderiaback.dto.SaleOrderDTO
import com.example.panaderiaback.dto.UserDTO

@RestController
@RequestMapping("/api/orders")
class SaleOrderController(
    private val saleOrderService: SaleOrderService,
    private val userService: UserService,
    private val userSecurity: UserSecurity,
    private val productService: ProductService
) {

    data class CreateOrderRequest(
        val items: List<Map<String, Any>>,
        val paymentMethod: PaymentMethod,
        val shippingInfo: String?,
        val deliveryMethod: DeliveryMethod,
        val userId: Long
    )

    data class UpdateStatusRequest(
        val status: OrderStatus
    )

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<SaleOrderDTO> {
        // Verificar que el usuario está creando su propia orden o es un administrador
        val auth = SecurityContextHolder.getContext().authentication
        val userOptional = userService.getUserByEmail(auth.name)

        if (!userOptional.isPresent) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val currentUser = userOptional.get()

        if (currentUser.id != request.userId && currentUser.role != RoleModel.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val createdOrder = saleOrderService.createOrder(
            items = request.items,
            paymentMethod = request.paymentMethod,
            shippingInfo = request.shippingInfo,
            deliveryMethod = request.deliveryMethod,
            userId = request.userId
        )

        val orderDTO = convertToEnrichedDTO(createdOrder)
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO)
    }

    @GetMapping
    fun getAllOrders(authentication: Authentication): ResponseEntity<List<SaleOrderDTO>> {
        val userOptional = userService.getUserByEmail(authentication.name)

        if (!userOptional.isPresent) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val currentUser = userOptional.get()

        // Si es admin o empleado, devolver todas las órdenes
        // Si es usuario normal, solo devolver sus órdenes
        val orders = if (currentUser.role == RoleModel.ADMIN || currentUser.role == RoleModel.EMPLOYEE) {
            saleOrderService.getAllOrders()
        } else {
            saleOrderService.getOrdersByUserId(currentUser.id)
        }

        val orderDTOs = orders.map { convertToEnrichedDTO(it) }
        return ResponseEntity.ok(orderDTOs)
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long, authentication: Authentication): ResponseEntity<SaleOrderDTO> {
        val order = saleOrderService.getOrderById(id)
        val userOptional = userService.getUserByEmail(authentication.name)

        if (!userOptional.isPresent) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val currentUser = userOptional.get()

        // Verificar permisos: solo el dueño de la orden, admin o empleado pueden verla
        if (currentUser.role != RoleModel.ADMIN && currentUser.role != RoleModel.EMPLOYEE && order.userId != currentUser.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val orderDTO = convertToEnrichedDTO(order)
        return ResponseEntity.ok(orderDTO)
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE') or @userSecurity.isCurrentUser(#userId)")
    fun getOrdersByUserId(@PathVariable userId: Long): ResponseEntity<List<SaleOrderDTO>> {
        val orders = saleOrderService.getOrdersByUserId(userId)
        val orderDTOs = orders.map { convertToEnrichedDTO(it) }
        return ResponseEntity.ok(orderDTOs)
    }

    @GetMapping("/status/{status}")
    fun getOrdersByStatus(@PathVariable status: OrderStatus): ResponseEntity<List<SaleOrderDTO>> {
        val orders = saleOrderService.getOrdersByStatus(status)
        val orderDTOs = orders.map { convertToEnrichedDTO(it) }
        return ResponseEntity.ok(orderDTOs)
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateStatusRequest
    ): ResponseEntity<Any> {
        try {
            val updatedOrder = saleOrderService.updateOrderStatus(id, request.status)
            val orderDTO = convertToEnrichedDTO(updatedOrder)
            return ResponseEntity.ok(orderDTO)
        } catch (e: IllegalStateException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Map<String, Boolean>> {
        val deleted = saleOrderService.deleteOrder(id)
        return ResponseEntity.ok(mapOf("deleted" to deleted))
    }

    // Método para convertir de modelo a DTO con datos enriquecidos
    private fun convertToEnrichedDTO(order: SaleOrderModel): SaleOrderDTO {
        // Obtener datos del usuario
        val user = userService.getUser(order.userId).orElse(null)
        val userDTO = user?.let {
            UserDTO(
                id = it.id,
                name = it.name,
                surname = it.surname,
                email = it.email,
                phoneNumber = it.phoneNumber ?: ""
            )
        }

        // Obtener datos de los productos y crear los OrderItemDTO
        val itemDTOs = order.items.map { item ->
            val product = productService.getProductById(item.productId)
            val productDTO = product?.let {
                ProductDTO(
                    id = it.id ?: 0,
                    name = it.name,
                    imgUrl = it.imgUrl,
                    price = it.price,
                    description = it.description,
                    category = it.category
                )
            }

            OrderItemDTO(
                id = item.id,
                productId = item.productId,
                price = item.price,
                quantity = item.quantity,
                product = productDTO
            )
        }

        // Crear y devolver el DTO completo
        return SaleOrderDTO(
            id = order.id,
            items = itemDTOs,
            paymentMethod = order.paymentMethod,
            shippingInfo = order.shippingInfo,
            deliveryMethod = order.deliveryMethod,
            userId = order.userId,
            user = userDTO,
            date = order.date,
            status = order.status
        )
    }
}