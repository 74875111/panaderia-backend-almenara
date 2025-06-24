package com.example.panaderiaback.model
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime

// Enumeración para el método de pago
enum class PaymentMethod {
    TARJETA,
    YAPE,
    CONTRAENTREGA
}

// Enumeración para el método de entrega
enum class DeliveryMethod {
    ENVIO_A_DIRECCION,
    RECOJO_EN_TIENDA
}

// Enumeración para el estado de la orden
enum class OrderStatus {
    PENDIENTE,
    PREPARACION,
    TERMINADO
}

// Clase para los items de la orden
@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val quantity: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_order_id")
    @JsonBackReference
    var saleOrder: SaleOrderModel? = null
)

// Clase principal para la nota de venta
@Entity
@Table(name = "sale_orders")
data class SaleOrderModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToMany(mappedBy = "saleOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    val items: MutableList<OrderItem> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    val paymentMethod: PaymentMethod,

    @Column(name = "shipping_info")
    val shippingInfo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_method", nullable = false)
    val deliveryMethod: DeliveryMethod,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val date: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDIENTE  // Cambiado a var para ser mutable
)