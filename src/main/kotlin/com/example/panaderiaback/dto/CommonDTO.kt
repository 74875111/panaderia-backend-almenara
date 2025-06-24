package com.example.panaderiaback.dto

import com.example.panaderiaback.model.DeliveryMethod
import com.example.panaderiaback.model.OrderStatus
import com.example.panaderiaback.model.PaymentMethod
import java.time.LocalDateTime

data class ProductDTO(
    val id: Long,
    val name: String,
    val imgUrl: String?,
    val price: Double,
    val description: String,
    val category: String
)

data class UserDTO(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val phoneNumber: String?
)

data class OrderItemDTO(
    val id: Long,
    val productId: Long,
    val price: Double,
    val quantity: Int,
    val product: ProductDTO?
)

data class SaleOrderDTO(
    val id: Long,
    val items: List<OrderItemDTO>,
    val paymentMethod: PaymentMethod,
    val shippingInfo: String?,
    val deliveryMethod: DeliveryMethod,
    val userId: Long,
    val user: UserDTO?,
    val date: LocalDateTime,
    val status: OrderStatus
)