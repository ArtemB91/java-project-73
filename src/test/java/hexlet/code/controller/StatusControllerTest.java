package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.TestUtils;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StatusControllerTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private StatusRepository statusRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        testUtils.regDefaultUser();
    }



    @Test
    void testGetStatuses() throws Exception {
        StatusDto statusDto = testUtils.addTestStatus();

        MockHttpServletResponse response =
                testUtils.performByUser(get("/statuses"))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                statusDto.getName());
    }

    @Test
    void testGetOneStatus() throws Exception {
        StatusDto statusDto = testUtils.addTestStatus();

        MockHttpServletResponse response =
                testUtils.performByUser(get("/statuses/" + statusDto.getId()))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                statusDto.getName());
    }


    @Test
    void testCreateStatus() throws Exception {
        StatusDto statusToCreate = new StatusDto("Working");

        MockHttpServletResponse response = testUtils
                .performByUser(post("/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(statusToCreate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        StatusDto statusDto = TestUtils.fromJson(response.getContentAsString(), new TypeReference<StatusDto>() {
        });
        Status status = statusRepository.findById(statusDto.getId()).get();

        assertThat(status).isNotNull();
        assertThat(status.getName()).isEqualTo(statusToCreate.getName());
        assertThat(status.getCreatedAt()).isNotNull();
    }

    @Test
    void testUpdateStatus() throws Exception {

        StatusDto statusToUpdate = new StatusDto("done");
        StatusDto existingStatusDto = testUtils.addTestStatus();

        MockHttpServletResponse response = testUtils
                .performByUser(put("/statuses/" + existingStatusDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(statusToUpdate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Status status = statusRepository.findById(existingStatusDto.getId()).get();
        assertThat(status).isNotNull();
        assertThat(status.getName()).isEqualTo(statusToUpdate.getName());
        assertThat(status.getCreatedAt()).isNotNull();
    }

    @Test
    void testDeleteStatus() throws Exception {
        StatusDto existingStatusDto = testUtils.addTestStatus();

        MockHttpServletResponse response = testUtils
                .performByUser(delete("/statuses/" + existingStatusDto.getId()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(statusRepository.existsById(existingStatusDto.getId())).isFalse();
    }
}
