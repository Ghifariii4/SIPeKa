package com.smkn8jkt.sipeka

import com.smkn8jkt.sipeka.data.model.OrderData
import com.smkn8jkt.sipeka.data.model.ProductResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductCategoryTest {

    @Test
    fun testExplicitCategoryMatches() {
        val food = ProductResponse(id = "1", name = "Sate Ayam", price = 15000.0, category = "Makanan")
        val drink = ProductResponse(id = "2", name = "Jus Jeruk", price = 6000.0, category = "Minuman")
        val snack = ProductResponse(id = "3", name = "Keripik Pisang", price = 5000.0, category = "Snack")
        val combo = ProductResponse(id = "4", name = "Paket Hemat 1", price = 20000.0, category = "Paket")

        assertEquals("Makanan", food.effectiveCategory)
        assertEquals("Minuman", drink.effectiveCategory)
        assertEquals("Snack", snack.effectiveCategory)
        assertEquals("Paket", combo.effectiveCategory)
    }

    @Test
    fun testAutoDetectCategoryFromUmumOrNull() {
        val iceTea = ProductResponse(id = "10", name = "Es Teh Manis Jumbo", price = 5000.0, category = "Umum")
        val coffee = ProductResponse(id = "11", name = "Kopi Susu Dingin", price = 8000.0, category = null)
        val risol = ProductResponse(id = "12", name = "Risol Mayo Lumer", price = 4000.0, category = "")
        val pastel = ProductResponse(id = "13", name = "Pastel Isi Sayur", price = 3000.0, category = "Umum")
        val combo = ProductResponse(id = "14", name = "Paket Nasi Rendang Hemat", price = 25000.0, category = "Umum")
        val friedRice = ProductResponse(id = "15", name = "Nasi Goreng Spesial Telur", price = 16000.0, category = "Umum")

        assertEquals("Minuman", iceTea.effectiveCategory)
        assertEquals("Minuman", coffee.effectiveCategory)
        assertEquals("Snack", risol.effectiveCategory)
        assertEquals("Snack", pastel.effectiveCategory)
        assertEquals("Paket", combo.effectiveCategory)
        assertEquals("Makanan", friedRice.effectiveCategory)
    }

    @Test
    fun testAntiFraudOrderCompletionCheck() {
        val completedOrder = OrderData(
            id = "ORD-001",
            status = "completed",
            completedAt = "2026-10-01T10:00:00Z"
        )
        val pendingOrder = OrderData(
            id = "ORD-002",
            status = "pending",
            completedAt = null
        )

        assertTrue(completedOrder.isCompleted)
        assertFalse(completedOrder.isPending)

        assertFalse(pendingOrder.isCompleted)
        assertTrue(pendingOrder.isPending)
    }
}
