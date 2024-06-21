package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryFakeRepository
import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointFakeRepository
import io.hhplus.tdd.database.UserPointRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class PointServiceTest() {

    private lateinit var pointService: PointService
    private lateinit var pointHistoryRepository: PointHistoryRepository
    private lateinit var userPointRepository: UserPointRepository

    // fake 객체 넣어주기
    @BeforeEach
    fun setup() {
        pointHistoryRepository = PointHistoryFakeRepository()
        userPointRepository = UserPointFakeRepository()
        pointService = PointService(pointHistoryRepository, userPointRepository)
    }

    // 코틀린은 null-safe 하기 때문에 null 처리를 해줄 필요가 없다.
    @DisplayName("포인트 정보가 없으면 충전이 실패한다")
    @Test
    fun testOne() {
        // given : 유저 아이디 없음, 충전량 없음

        // when : null 을 넣을 수가 없음
        //        pointService.charge(null, null)
        // then
    }

    @DisplayName("id가 음수이면 IllegalArgumentException")
    @Test
    fun testTwo() {
        // given
        val userId = -1L // 없는 id
        val amount = Random.nextLong(from = 1, until = 5000)

        // when & then
        assertThatThrownBy { pointService.charge(userId, amount) }
            .isInstanceOf(IllegalArgumentException::class.java)

        // 예시 추가
        // given
        val userId2 = -2L // 없는 id
        val amount2 = Random.nextLong(from = 1, until = 5000)

        // when & then
        assertThatThrownBy { pointService.charge(userId2, amount2) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @DisplayName("amount가 음수이면 IllegalArgumentException")
    @Test
    fun testThree() {
        // given
        val userId = 1L
        val amount = Random.nextLong(from = -5000, until = -1) // amount가 음수

        // when & then
        assertThatThrownBy { pointService.charge(userId, amount) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @DisplayName("id도 양수이고 amount도 양수이면 충전이 된다.")
    @Test
    fun testFour() {
        // given
        val userId = 1L
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        val userPoint = pointService.charge(userId, amount)

        // then
        assertThat(userPoint.point).isEqualTo(amount)
        assertThat(userPoint.id).isEqualTo(userId)
    }
}