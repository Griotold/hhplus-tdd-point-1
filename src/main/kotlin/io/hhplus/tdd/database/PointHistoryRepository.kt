package io.hhplus.tdd.database

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.TransactionType
import org.springframework.stereotype.Repository

@Repository
interface PointHistoryRepository {
    fun insert(id: Long,
               amount: Long,
               transactionType: TransactionType,
               updateMillis: Long,
    ): PointHistory

    fun selectAllByUserId(userId: Long): List<PointHistory>
}