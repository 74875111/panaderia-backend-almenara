package com.example.panaderiaback.repository

import com.example.panaderiaback.model.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    // Métodos personalizados si los necesitas
    fun findByCategory(category: String): List<Product>
    fun findByNameContaining(name: String): List<Product>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Product>
    fun findByCategoryIgnoreCase(category: String, pageable: Pageable): Page<Product>
}