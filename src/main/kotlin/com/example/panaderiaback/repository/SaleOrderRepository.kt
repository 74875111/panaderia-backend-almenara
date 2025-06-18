package com.example.panaderiaback.repository

import com.example.panaderiaback.model.OrderStatus
import com.example.panaderiaback.model.SaleOrderModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SaleOrderRepository : JpaRepository<SaleOrderModel, Long> {
    fun findByUserId(userId: Long): List<SaleOrderModel>
    fun findByStatus(status: OrderStatus): List<SaleOrderModel>
    fun findByUserIdAndStatus(userId: Long, status: OrderStatus): List<SaleOrderModel>
}