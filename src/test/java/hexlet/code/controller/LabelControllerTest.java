package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.TestUtils;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
class LabelControllerTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void beforeEach() throws Exception {
        testUtils.regDefaultUser();
    }

    @Test
    void testGetLabels() throws Exception {
        LabelDto labelDto = testUtils.addTestLabel();

        MockHttpServletResponse response =
                testUtils.performByUser(get("/labels"))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                labelDto.getName());
    }

    @Test
    void testGetOneLabel() throws Exception {
        LabelDto labelDto = testUtils.addTestLabel();

        MockHttpServletResponse response =
                testUtils.performByUser(get("/labels/" + labelDto.getId()))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                labelDto.getName());
    }


    @Test
    void testCreateLabel() throws Exception {
        LabelDto labelToCreate = new LabelDto("Working");

        MockHttpServletResponse response = testUtils
                .performByUser(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(labelToCreate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        LabelDto labelDto = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        Label label = labelRepository.findById(labelDto.getId()).get();

        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo(labelToCreate.getName());
        assertThat(label.getCreatedAt()).isNotNull();
    }

    @Test
    void testUpdateLabel() throws Exception {

        LabelDto labelToUpdate = new LabelDto("done");
        LabelDto existingLabelDto = testUtils.addTestLabel();

        MockHttpServletResponse response = testUtils
                .performByUser(put("/labels/" + existingLabelDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(labelToUpdate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Label label = labelRepository.findById(existingLabelDto.getId()).get();
        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo(labelToUpdate.getName());
        assertThat(label.getCreatedAt()).isNotNull();
    }

    @Test
    void testDeleteLabel() throws Exception {
        LabelDto existingLabelDto = testUtils.addTestLabel();

        MockHttpServletResponse response = testUtils
                .performByUser(delete("/labels/" + existingLabelDto.getId()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(labelRepository.existsById(existingLabelDto.getId())).isFalse();
    }
}
