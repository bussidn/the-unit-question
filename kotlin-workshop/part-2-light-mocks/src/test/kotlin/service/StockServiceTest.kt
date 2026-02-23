package service

import domain.OrderItem
import domain.Stock
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import repository.StockRepository
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StockServiceTest {

    private lateinit var stockRepository: StockRepository
    private lateinit var stockService: StockService

    @BeforeEach
    fun setUp() {
        stockRepository = mockk()
        stockService = StockService(stockRepository)
    }

    @Test
    fun `checkAvailability returns true when all items are in stock`() {
        // Given
        val items = listOf(
            OrderItem("PROD-001", 5, 10.0),
            OrderItem("PROD-002", 3, 20.0)
        )

        every { stockRepository.findByProductId("PROD-001") } returns Stock("PROD-001", 100)
        every { stockRepository.findByProductId("PROD-002") } returns Stock("PROD-002", 50)

        // When
        val result = stockService.checkAvailability(items)

        // Then
        assertTrue(result)
        verify(exactly = 1) { stockRepository.findByProductId("PROD-001") }
        verify(exactly = 1) { stockRepository.findByProductId("PROD-002") }
    }

    @Test
    fun `checkAvailability returns false when item is out of stock`() {
        // Given
        val items = listOf(
            OrderItem("PROD-001", 5, 10.0),
            OrderItem("PROD-003", 3, 20.0)
        )

        every { stockRepository.findByProductId("PROD-001") } returns Stock("PROD-001", 100)
        every { stockRepository.findByProductId("PROD-003") } returns Stock("PROD-003", 0)

        // When
        val result = stockService.checkAvailability(items)

        // Then
        assertFalse(result)
        verify(exactly = 1) { stockRepository.findByProductId("PROD-001") }
        verify(exactly = 1) { stockRepository.findByProductId("PROD-003") }
    }

    @Test
    fun `checkAvailability calls repository once per item`() {
        // Given
        val items = listOf(
            OrderItem("PROD-001", 1, 10.0),
            OrderItem("PROD-002", 1, 20.0),
            OrderItem("PROD-003", 1, 30.0)
        )

        every { stockRepository.findByProductId(any()) } returns Stock("any", 100)

        // When
        stockService.checkAvailability(items)

        // Then
        verify(exactly = 3) { stockRepository.findByProductId(any()) }
        verify(exactly = 1) { stockRepository.findByProductId("PROD-001") }
        verify(exactly = 1) { stockRepository.findByProductId("PROD-002") }
        verify(exactly = 1) { stockRepository.findByProductId("PROD-003") }
    }

    @Test
    fun `checkAvailability with single item checks stock correctly`() {
        // Given
        val items = listOf(OrderItem("PROD-042", 10, 5.0))

        every { stockRepository.findByProductId("PROD-042") } returns Stock("PROD-042", 15)

        // When
        val result = stockService.checkAvailability(items)

        // Then
        assertTrue(result)
        verify(exactly = 1) { stockRepository.findByProductId("PROD-042") }
        confirmVerified(stockRepository)
    }

    @Test
    fun `reserveStock updates quantity and returns successful reservation`() {
        // Given
        val items = listOf(OrderItem("PROD-001", 5, 10.0))

        every { stockRepository.findByProductId("PROD-001") } returns Stock("PROD-001", 100)
        every { stockRepository.updateQuantity("PROD-001", 95) } just Runs

        // When
        val result = stockService.reserveStock(items)

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0].reserved)
        verify(exactly = 1) { stockRepository.updateQuantity("PROD-001", 95) }
    }
}

