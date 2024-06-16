package io.hhplus.tdd.point

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("point api 컨트롤러 테스트")
class PointControllerTest (
    @Autowired private val mockMvc: MockMvc
){

    @Test
    @DisplayName("충전하기")
    fun testGetIntroductions() {

        // given
        val uri = "/point/1/charge"
        val amount = 100L

        // when
        val mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.patch(uri)
                .content(amount.toString())
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn()
        val contentAsString = mvcResult.response.getContentAsString(StandardCharsets.UTF_8)


        // then
        val jsonResponse = JSONObject(contentAsString)
        val id = jsonResponse.getLong("id")
        val point = jsonResponse.getLong("point")

        assertThat(id).isEqualTo(1L)
        assertThat(point).isEqualTo(amount)
    }
}