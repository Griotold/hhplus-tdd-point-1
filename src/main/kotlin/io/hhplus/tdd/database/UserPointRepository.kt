package io.hhplus.tdd.database

import io.hhplus.tdd.point.UserPoint

interface UserPointRepository {

    fun selectById(id: Long): UserPoint
    fun insertOrUpdate(id: Long, amount: Long): UserPoint
}