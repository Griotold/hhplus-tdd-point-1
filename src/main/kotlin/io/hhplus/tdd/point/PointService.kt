package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryTable: PointHistoryTable,
) {
    fun charge(userId: Long, amount: Long): UserPoint {
        val pointHistory =
            pointHistoryTable.insert(id = userId, amount = amount, TransactionType.CHARGE, System.currentTimeMillis())
        return UserPoint(id = pointHistory.userId, point = pointHistory.amount, updateMillis = pointHistory.timeMillis)
    }

}
