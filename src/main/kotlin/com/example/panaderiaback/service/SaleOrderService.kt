package com.example.panaderiaback.service

import com.example.panaderiaback.model.*
import com.example.panaderiaback.repository.SaleOrderRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class SaleOrderService(private val saleOrderRepository: SaleOrderRepository) {

    fun createOrder(
        items: List<Map<String, Any>>,
        paymentMethod: PaymentMethod,
        shippingInfo: String?,
        deliveryMethod: DeliveryMethod,
        userId: Long
    ): SaleOrderModel {
        val saleOrder = SaleOrderModel(
            paymentMethod = paymentMethod,
            shippingInfo = shippingInfo,
            deliveryMethod = deliveryMethod,
            userId = userId,
            date = LocalDateTime.now()
        )

        // Convertir y agregar los items
        items.forEach { item ->
            val orderItem = OrderItem(
                productId = (item["productId"] as Number).toLong(),
                price = (item["price"] as Number).toDouble(),
                quantity = (item["quantity"] as Number).toInt()
            )
            saleOrder.items.add(orderItem)
            orderItem.saleOrder = saleOrder
        }

        return saleOrderRepository.save(saleOrder)
    }

    fun getAllOrders(): List<SaleOrderModel> {
        return saleOrderRepository.findAll()
    }

    fun getOrderById(id: Long): SaleOrderModel {
        return saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Orden no encontrada con ID: $id") }
    }

    fun getOrdersByUserId(userId: Long): List<SaleOrderModel> {
        return saleOrderRepository.findByUserId(userId)
    }

    fun getOrdersByStatus(status: OrderStatus): List<SaleOrderModel> {
        return saleOrderRepository.findByStatus(status)
    }

    fun updateOrderStatus(id: Long, newStatus: OrderStatus): SaleOrderModel {
        val order = getOrderById(id)

        // Verificar si la orden ya está marcada como TERMINADO
        if (order.status == OrderStatus.TERMINADO) {
            throw IllegalStateException("No se pueden modificar pedidos ya terminados")
        }

        // Actualizar el estado
        order.status = newStatus

        // Guardar la orden actualizada
        return saleOrderRepository.save(order)
    }

    fun deleteOrder(id: Long): Boolean {
        saleOrderRepository.deleteById(id)
        return true
    }
}