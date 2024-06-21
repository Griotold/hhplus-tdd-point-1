package io.hhplus.tdd.point

import org.assertj.core.api.Assertions.assertThat
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


}