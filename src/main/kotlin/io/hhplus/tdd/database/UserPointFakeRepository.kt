package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPoint

class UserPointFakeRepository: UserPointRepository {
    private val table = HashMap<Long, UserPoint>()

    override fun selectById(id: Long): UserPoint {
        Thread.sleep(Math.random().toLong() * 200L)
        return table[id] ?: UserPoint(id = id, point = 0, updateMillis = System.currentTimeMillis())
    }

    override fun insertOrUpdate(id: Long, amount: Long): UserPoint {
        Thread.sleep(Math.random().toLong() * 300L)
        val userPoint = UserPoint(id = id, point = amount, updateMillis = System.currentTimeMillis())
        table[id] = userPoint
        return userPoint
    }
}