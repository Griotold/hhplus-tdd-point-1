package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryTable: PointHistoryTable,
    private val userPointTable: UserPointTable
) {
    fun charge(userId: Long, amount: Long): UserPoint {
        val pointHistory =
            pointHistoryTable.insert(id = userId, amount = amount, TransactionType.CHARGE, System.currentTimeMillis())

        // userPoint 도 넣어주고
        userPointTable.insertOrUpdate(id = userId, amount = amount)

        return UserPoint(id = pointHistory.userId, point = pointHistory.amount, updateMillis = pointHistory.timeMillis)
    }

}
