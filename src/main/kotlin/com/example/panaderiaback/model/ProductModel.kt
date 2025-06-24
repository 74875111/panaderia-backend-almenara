package com.example.panaderiaback.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(name = "img_url")
    val imgUrl: String? = null,  // URL de la imagen, puede ser nulo

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val category: String
)