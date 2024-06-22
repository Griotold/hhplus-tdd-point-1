package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType

class PointHistoryFakeRepository : PointHistoryRepository {
    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    override fun insert(id: Long,
                        amount: Long,
                        transactionType: TransactionType,
                        updateMillis: Long
    ): PointHistory {
        Thread.sleep(Math.random().toLong() * 300L)
        val history = PointHistory(
            id = cursor++,
            userId = id,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
        table.add(history)
        return history
    }

    override fun selectAllByUserId(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }
}