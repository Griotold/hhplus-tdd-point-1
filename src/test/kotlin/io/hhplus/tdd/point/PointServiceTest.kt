package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryFakeRepository
import io.hhplus.tdd.database.PointHistoryRepository
import io.hhplus.tdd.database.UserPointFakeRepository
import io.hhplus.tdd.database.UserPointRepository
import io.hhplus.tdd.exception.InsufficientBalanceException
import io.hhplus.tdd.exception.InvalidAmountException
import io.hhplus.tdd.exception.InvalidUserIdException
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

    /**
     * charge 관련 테스트
     * */

    // 코틀린은 null-safe 하기 때문에 null 처리를 해줄 필요가 없다.
    @DisplayName("포인트 정보가 없으면 충전이 실패한다")
    @Test
    fun testOne() {
        // given : 유저 아이디 없음, 충전량 없음

        // when : null 을 넣을 수가 없음
        //        pointService.charge(null, null)
        // then
    }

    @DisplayName("id가 음수이면 InvalidUserIdException")
    @Test
    fun testTwo() {
        // given
        val userId = Random.nextLong(from = -5000, until = -1) // 없는 id
        val amount = Random.nextLong(from = 1, until = 5000)

        // when & then
        assertThatThrownBy { pointService.charge(userId, amount) }
            .isInstanceOf(InvalidUserIdException::class.java)
    }

    @DisplayName("amount가 음수이면 InvalidAmountException")
    @Test
    fun testThree() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amount = Random.nextLong(from = -5000, until = -1) // amount가 음수

        // when & then
        assertThatThrownBy { pointService.charge(userId, amount) }
            .isInstanceOf(InvalidAmountException::class.java)
    }

    @DisplayName("id도 양수이고 amount도 양수이면 충전이 된다.")
    @Test
    fun testFour() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        val userPoint = pointService.charge(userId, amount)

        // then
        assertThat(userPoint.point).isEqualTo(amount)
        assertThat(userPoint.id).isEqualTo(userId)
    }

    @DisplayName("두 번 충전 했다면 각각의 더한 값이 리턴된다.")
    @Test
    fun testFive() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amount = Random.nextLong(from = 1, until = 5000)
        val amount2 = Random.nextLong(from = 1, until = 5000)

        // when
        pointService.charge(userId, amount)
        val userPoint2 = pointService.charge(userId, amount2) // 두 번 충전!

        // then
        assertThat(userPoint2.point).isEqualTo(amount + amount2)
    }

    @DisplayName("충전 후 거래 내역이 저장된다")
    @Test
    fun testSix() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        pointService.charge(userId, amount)

        // then
        val histories = pointHistoryRepository.selectAllByUserId(userId)
        assertThat(histories).isNotEmpty
        assertThat(histories.last().amount).isEqualTo(amount)
    }

    /**
     * use 관련 테스트
     * */
    @DisplayName("잔고 없이 사용하면 InsufficientBalanceException")
    @Test
    fun testSeven() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amount = Random.nextLong(from = 1, until = 5000)

        // when & then
        assertThatThrownBy { pointService.use(userId, amount) }
            .isInstanceOf(InsufficientBalanceException::class.java)

    }

    @DisplayName("잔고가 있을 때 정상적인 사용")
    @Test
    fun testEight() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amountToCharge = 5000L
        val amountToUse = 3000L
        val beforeUserPoint = pointService.charge(userId, amountToCharge)

        // when
        val afterUserPoint = pointService.use(userId, amountToUse)

        // then
        assertThat(beforeUserPoint.point).isEqualTo(amountToCharge)
        assertThat(afterUserPoint.point).isEqualTo(amountToCharge - amountToUse)
    }

    @DisplayName("잔고와 동일한 금액 사용")
    @Test
    fun testNine() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amountToCharge = 5000L
        val beforeUserPoint = pointService.charge(userId, amountToCharge)

        // when
        val afterUserPoint = pointService.use(userId, amountToCharge)

        // then
        assertThat(beforeUserPoint.point).isEqualTo(amountToCharge)
        assertThat(afterUserPoint.point).isEqualTo(0L)
    }

    @DisplayName("두 번 사용하여 잔고 정확히 계산")
    @Test
    fun testTen() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amountToCharge = 10000L
        val firstUseAmount = 3000L
        val secondUseAmount = 2000L
        pointService.charge(userId, amountToCharge)

        // when
        pointService.use(userId, firstUseAmount)
        val afterUserPoint = pointService.use(userId, secondUseAmount)

        // then
        assertThat(afterUserPoint.point).isEqualTo(amountToCharge - firstUseAmount - secondUseAmount)
    }

    @DisplayName("음수 금액 사용 시도 시 InvalidAmountException")
    @Test
    fun testEleven() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amountToCharge = 5000L
        pointService.charge(userId, amountToCharge)

        // when & then
        assertThatThrownBy { pointService.use(userId, -1000L) }
            .isInstanceOf(InvalidAmountException::class.java)
    }

    @DisplayName("사용 후 거래 내역 저장")
    @Test
    fun testTwelve() {
        // given
        val userId = Random.nextLong(from = 1, until = 5000)
        val amountToCharge = 5000L
        val amountToUse = 3000L
        pointService.charge(userId, amountToCharge)

        // when
        pointService.use(userId, amountToUse)

        // then
        val histories = pointHistoryRepository.selectAllByUserId(userId)
        assertThat(histories).isNotEmpty
        assertThat(histories.last().amount).isEqualTo(amountToUse)
        assertThat(histories.last().type).isEqualTo(TransactionType.USE)
    }

}