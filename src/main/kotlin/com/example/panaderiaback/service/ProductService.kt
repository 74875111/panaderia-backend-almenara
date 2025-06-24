package com.example.panaderiaback.service

import com.example.panaderiaback.model.Product
import com.example.panaderiaback.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

interface ProductService {
    fun getAllProducts(): List<Product>
    fun getProductById(id: Long): Product?
    fun createProduct(product: Product): Product
    fun getProductsPage(pageable: Pageable, search: String?): Page<Product>
    fun getProductsByCategory(category: String, pageable: Pageable): Page<Product>
    fun deleteAllProducts()
}

@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService {
    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProductById(id: Long): Product? {
        return productRepository.findById(id).orElse(null)
    }

    override fun createProduct(product: Product): Product {
        return productRepository.save(product)
    }

    override fun getProductsPage(pageable: Pageable, search: String?): Page<Product> {
        return if (search.isNullOrBlank()) {
            productRepository.findAll(pageable)
        } else {
            productRepository.findByNameContainingIgnoreCase(search, pageable)
        }
    }
    override fun getProductsByCategory(category: String, pageable: Pageable): Page<Product> {
        return productRepository.findByCategoryIgnoreCase(category, pageable)
    }
    override fun deleteAllProducts() {
        productRepository.deleteAll()
    }
}