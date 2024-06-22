package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.exception.InsufficientBalanceException
import io.hhplus.tdd.exception.InvalidAmountException
import io.hhplus.tdd.exception.InvalidUserIdException
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock

@Service
class PointService(
    private val pointHistoryRepository: PointHistoryRepository,
    private val userPointRepository: UserPointRepository
) {

    // 동시성을 고려한 ReentrantLock 선언
    private val lock = ReentrantLock()

    fun charge(userId: Long, amount: Long): UserPoint {
        lock.lock()
        try {
            if (userId < 0) throw InvalidUserIdException()
            if (amount < 0) throw InvalidAmountException()

            // 거래내역 저장
            pointHistoryRepository.insert(id = userId, amount = amount, TransactionType.CHARGE, System.currentTimeMillis())

            // userPoint 도 넣어주고
            val beforeUserPoint = userPointRepository.selectById(userId)
            val afterUserPoint = userPointRepository.insertOrUpdate(id = userId, amount = amount + beforeUserPoint.point)

            return UserPoint(id = afterUserPoint.id,
                point = afterUserPoint.point,
                updateMillis = afterUserPoint.updateMillis
            )
        } finally {
            lock.unlock()
        }

    }

    fun use(userId: Long, amount: Long): UserPoint {
        lock.lock()
        try {
            if (amount < 0) throw InvalidAmountException()
            val userPoint = userPointRepository.selectById(userId)
            if (userPoint.point < amount) throw InsufficientBalanceException()

            // 거래내역 저장
            pointHistoryRepository.insert(id = userId, amount = amount, TransactionType.USE, System.currentTimeMillis())

            // userPoint 에서 차감
            val afterUserPoint = userPointRepository.insertOrUpdate(id = userId, amount = userPoint.point - amount)

            return UserPoint(id = afterUserPoint.id,
                point = afterUserPoint.point,
                updateMillis = afterUserPoint.updateMillis)
        } finally {
            lock.unlock()
        }

    }

    fun retrieve(id: Long): UserPoint {
        return userPointRepository.selectById(id = id)
    }

    fun getHistories(userId: Long): List<PointHistory> {
        return pointHistoryRepository.selectAllByUserId(userId = userId)
    }

}
