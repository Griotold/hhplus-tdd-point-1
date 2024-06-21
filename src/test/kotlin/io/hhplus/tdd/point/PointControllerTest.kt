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

    private fun performPatch(uri: String, amount: Long): MvcResult {
        return mockMvc
            .perform(MockMvcRequestBuilders.patch(uri)
                .content(amount.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
    }

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
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
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
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST.value())
    }



}