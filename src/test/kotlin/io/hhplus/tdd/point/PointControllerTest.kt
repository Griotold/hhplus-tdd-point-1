package io.hhplus.tdd.point

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.nio.charset.StandardCharsets
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("point api 컨트롤러 테스트")
class PointControllerTest (
    @Autowired private val mockMvc: MockMvc
){

    // 중복코드 메소드로 추출하여 재사용
    private fun performPatch(uri: String, amount: Long): MvcResult {
        return mockMvc
            .perform(MockMvcRequestBuilders.patch(uri)
                .content(amount.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    private fun performGet(uri: String): MvcResult {
        return mockMvc
            .perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

    /**
     * 1. 충전하기
     * */

    @DisplayName("1.충전하기 - 정상적인 상황")
    @Test
    fun testPointCharge() {
        // given
        val id = Random.nextLong(from = 1, until = 1000)
        val uri = "/point/${id}/charge"
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())

        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(amount)
    }

    @DisplayName("1.충전하기 - id가 음수이면 400 에러")
    @Test
    fun testTwo() {
        // given
        val id = Random.nextLong(from = -5000, until = -1)
        val uri = "/point/${id}/charge"
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(JSONObject(contentAsString).getString("message")).isEqualTo("없는 유저 ID 입니다.")
    }

    @DisplayName("1.충전하기 - amount가 음수이면 400 에러")
    @Test
    fun testThree() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val uri = "/point/${id}/charge"
        val amount = Random.nextLong(from = -5000, until = -1)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(JSONObject(contentAsString).getString("message")).isEqualTo("충전량은 양수여야 합니다.")

    }

    /**
     * 2. 사용하기
     * */
    @DisplayName("2. 사용하기 - 충전없이 사용하면 400 에러")
    @Test
    fun testFour() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val uri = "/point/${id}/use"
        val amount = Random.nextLong(from = 1, until = 5000)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(JSONObject(contentAsString).getString("message")).isEqualTo("잔고가 부족합니다.")
    }

    @DisplayName("2. 사용하기 - 잔고 내에서 사용")
    @Test
    fun testFive() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val useUri = "/point/${id}/use"
        val chargeAmount = 5000L
        val useAmount = Random.nextLong(from = 1, until = 5000)

        // 먼저 충전
        performPatch(chargeUri, chargeAmount)

        // when
        val mvcResult = performPatch(useUri, useAmount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(chargeAmount - useAmount)
    }

    @DisplayName("2. 사용하기 - 음수 금액 사용 시도 시 400 에러")
    @Test
    fun testSix() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val uri = "/point/${id}/use"
        val amount = Random.nextLong(from = -5000, until = -1)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(JSONObject(contentAsString).getString("message")).isEqualTo("충전량은 양수여야 합니다.")
    }

    @DisplayName("2. 사용하기 - 잔고와 동일한 금액 사용")
    @Test
    fun testSeven() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val useUri = "/point/${id}/use"
        val chargeAmount = 5000L

        // 먼저 충전
        performPatch(chargeUri, chargeAmount)

        // when
        val mvcResult = performPatch(useUri, chargeAmount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(0L)
    }

    /**
     * 3. 포인트 조회
     * */
    @DisplayName("3. 포인트 조회 - 충전을 안하고 조회하면 0원")
    @Test
    fun testEight() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val uri = "/point/${id}"

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(0L)
    }

    @DisplayName("3. 포인트 조회 - 충전을 하고 조회하면 충전한 만큼")
    @Test
    fun testNine() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val uri = "/point/${id}"
        val chargeAmount = 5000L

        // 먼저 충전
        performPatch(chargeUri, chargeAmount)

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(chargeAmount) // 충전한 만큼
    }

    @DisplayName("3. 포인트 조회 - 충전을 두 번 하고 조회하면 모두 더한 만큼 조회")
    @Test
    fun testTen() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val uri = "/point/${id}"
        val chargeAmount = 5000L
        val chargeAmount2 = 3000L

        // 두 번 충전
        performPatch(chargeUri, chargeAmount)
        performPatch(chargeUri, chargeAmount2)

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(chargeAmount + chargeAmount2) // 더한 만큼
    }

    @DisplayName("3. 포인트 조회 - 충전 한 번하고 사용 한 번하고 남은 금액이 제대로 나오는지 테스트")
    @Test
    fun testEleven() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val useUri = "/point/${id}/use"
        val uri = "/point/${id}"
        val chargeAmount = 5000L
        val useAmount = 1500L

        // 한 번 충전하고 두 번 충전하고
        performPatch(chargeUri, chargeAmount)
        performPatch(useUri, useAmount)

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        assertThat(JSONObject(contentAsString).getLong("id")).isEqualTo(id)
        assertThat(JSONObject(contentAsString).getLong("point")).isEqualTo(chargeAmount - useAmount) // 남은 금액
    }

    /**
     * 4. 포인트 내역 (pointHistory)
     * */
    @DisplayName("4. 포인트 내역 - 내역이 아무것도 없을 때는 - emptyList()")
    @Test
    fun testTwelve() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val uri = "/point/${id}/histories"

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        val jsonArray = JSONArray(contentAsString)
        assertThat(jsonArray.length()).isEqualTo(0) // emptyList() 검증
    }

    @DisplayName("4. 포인트 내역 - 한 번 충전하고 내역 확인")
    @Test
    fun testThirteen() {
        // given
        val id = Random.nextLong(from = 1, until = 5000)
        val chargeUri = "/point/${id}/charge"
        val chargeAmount = 5000L
        val uri = "/point/${id}/histories"

        // 먼저 충전
        performPatch(chargeUri, chargeAmount)

        // when
        val mvcResult = performGet(uri)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())
        val jsonArray = JSONArray(contentAsString)
        assertThat(jsonArray.length()).isEqualTo(1) // 충전 한 번 했으니 내역은 1건
        val jsonObject: JSONObject = jsonArray.getJSONObject(0)
        assertThat(jsonObject.getString("type")).isEqualTo(TransactionType.CHARGE.name) // 충전의 enum type 은 CHARGE
    }
}