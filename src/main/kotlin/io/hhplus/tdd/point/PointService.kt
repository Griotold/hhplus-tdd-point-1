package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryRepository: PointHistoryRepository,
    private val userPointRepository: UserPointRepository
) {
    fun charge(userId: Long, amount: Long): UserPoint {
        if (userId < 0) throw IllegalArgumentException()

        val pointHistory =
            pointHistoryRepository.insert(id = userId, amount = amount, TransactionType.CHARGE, System.currentTimeMillis())

        // userPoint 도 넣어주고
        userPointRepository.insertOrUpdate(id = userId, amount = amount)

        return UserPoint(id = pointHistory.userId, point = pointHistory.amount, updateMillis = pointHistory.timeMillis)
    }

}
