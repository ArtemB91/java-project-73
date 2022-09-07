package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.TestUtils;
import hexlet.code.dto.TaskDto;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet(value = "filtrationDataSet.yml", cleanBefore = true)
class TaskControllerFilterTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils testUtils;

    @Test
    void checkDataSetsLoaded() {
        assertThat(userRepository.count()).isPositive();
        assertThat(taskRepository.count()).isPositive();
    }

    @Test
    void testFilterByTaskStatus() throws Exception {
        MockHttpServletResponse response = testUtils.
                performByUser(get("/tasks/?taskStatus=-1"), "first@first.com")
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        List<TaskDto> tasks = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(2);

        List<Long> ids = tasks.stream()
                .map(t -> t.getId())
                .toList();
        assertThat(ids).containsExactlyInAnyOrder(-1L, -2L);
    }

    @Test
    void testFilterByExecutorId() throws Exception {
        MockHttpServletResponse response = testUtils.
                performByUser(get("/tasks/?executorId=-2"), "first@first.com")
                .andReturn()
                .getResponse();

        List<TaskDto> tasks = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(tasks).hasSize(2);

        List<Long> ids = tasks.stream()
                .map(t -> t.getId())
                .toList();
        assertThat(ids).containsExactlyInAnyOrder(-2L, -3L);

    }

    @Test
    void testFilterByLabel() throws Exception {
        MockHttpServletResponse response = testUtils.
                performByUser(get("/tasks/?labels=-1"), "first@first.com")
                .andReturn()
                .getResponse();

        List<TaskDto> tasks = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(tasks).hasSize(2);

        List<Long> ids = tasks.stream()
                .map(t -> t.getId())
                .toList();
        assertThat(ids).containsExactlyInAnyOrder(-1L, -2L);
    }

    @Test
    void testFilterByAuthorId() throws Exception {
        MockHttpServletResponse response = testUtils.
                performByUser(get("/tasks/?authorId=-3"), "first@first.com")
                .andReturn()
                .getResponse();

        List<TaskDto> tasks = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(tasks).hasSize(3);

        List<Long> ids = tasks.stream()
                .map(t -> t.getId())
                .toList();
        assertThat(ids).containsExactlyInAnyOrder(-2L, -3L, -4L);
    }

    @Test
    void testFilterByTaskStatusAndExecutorIdAndLabelAndAuthorId() throws Exception {
        MockHttpServletResponse response = testUtils.
                performByUser(get("/tasks/?taskStatus=-2&executorId=-2&labels=-3&authorId=-3"),
                        "first@first.com")
                .andReturn()
                .getResponse();

        List<TaskDto> tasks = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() { });
        assertThat(tasks).hasSize(1);

        List<Long> ids = tasks.stream()
                .map(t -> t.getId())
                .toList();
        assertThat(ids).containsExactlyInAnyOrder(-3L);
    }
}
