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

    @Test
    @DisplayName("1.충전하기 - 정상적인 상황")
    fun testPointCharge() {

        // given
        val uri = "/point/1/charge"
        val amount = Random.nextLong(1, 5000)

        // when
        val mvcResult = performPatch(uri, amount)
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)
        val status = mvcResult.response.status

        // then
        assertThat(status).isEqualTo(HttpStatus.OK.value())

        val jsonResponse = JSONObject(contentAsString)
        val id = jsonResponse.getLong("id")
        val point = jsonResponse.getLong("point")

        assertThat(id).isEqualTo(1L)
        assertThat(point).isEqualTo(amount)
    }

}