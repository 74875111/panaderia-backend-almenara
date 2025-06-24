package com.example.panaderiaback.controller

import com.example.panaderiaback.model.Product
import com.example.panaderiaback.service.ProductService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    // Obtener productos paginados con búsqueda opcional
    @GetMapping
    fun getProducts(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(required = false) name: String?
    ): ResponseEntity<Map<String, Any>> {
        val pageNumber = if (page < 1) 0 else page - 1
        val pageRequest = PageRequest.of(pageNumber, limit, Sort.by("id").ascending())

        val productsPage = productService.getProductsPage(pageRequest, name)

        val response = mapOf(
            "products" to productsPage.content,
            "pagination" to mapOf(
                "totalProducts" to productsPage.totalElements,
                "totalPages" to productsPage.totalPages,
                "currentPage" to (page),
                "limit" to limit
            )
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/category/{categoryName}")
    fun getProductsByCategory(
        @PathVariable categoryName: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<Map<String, Any>> {
        val pageNumber = if (page < 1) 0 else page - 1
        val pageRequest = PageRequest.of(pageNumber, limit, Sort.by("id").ascending())

        val productsPage = productService.getProductsByCategory(categoryName, pageRequest)

        val response = mapOf(
            "category" to categoryName,
            "products" to productsPage.content,
            "pagination" to mapOf(
                "totalProducts" to productsPage.totalElements,
                "totalPages" to productsPage.totalPages,
                "currentPage" to page,
                "limit" to limit
            )
        )

        return ResponseEntity.ok(response)
    }
    // Obtener un producto por ID
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // Crear un nuevo producto
    @PostMapping
    fun createProduct(@RequestBody product: Product): ResponseEntity<Product> {
        val savedProduct = productService.createProduct(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct)
    }

    // Eliminar todos los productos
    @DeleteMapping("/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAllProducts() {
        productService.deleteAllProducts()
    }
}