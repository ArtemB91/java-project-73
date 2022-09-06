package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.TestUtils;
import hexlet.code.dto.StatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
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
public class TaskControllerTest {

    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    private TaskDto testTaskDto;

    @BeforeEach
    public void beforeEach() throws Exception {
        testUtils.regDefaultUser();
        testTaskDto = testUtils.addTestTask();
    }

    @Test
    public void testGetTasks() throws Exception {
        MockHttpServletResponse response =
                testUtils.performByUser(get("/tasks"))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                testTaskDto.getName());
    }

    @Test
    public void testGetOneTask() throws Exception {

        MockHttpServletResponse response =
                testUtils.performByUser(get("/tasks/" + testTaskDto.getId()))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains(
                testTaskDto.getName(),
                testTaskDto.getDescription(),
                testTaskDto.getTaskStatus().getName(),
                testTaskDto.getAuthor().getEmail());
    }

    @Test
    public void testCreateTask() throws Exception {
        User defUser = userRepository.findByEmail(testUtils.defaultUserDto().getEmail()).get();
        StatusDto testStatus = testUtils.addTestStatus();

        String content = String.format("""
                {"name": "simple task",
                "description": "simple desc",
                "taskStatusId": %s,
                "executorId" : %s}""",
                testStatus.getId(),
                defUser.getId());

        MockHttpServletResponse response = testUtils
                .performByUser(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        TaskDto taskDto = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        Task task = taskRepository.findById(taskDto.getId()).orElse(null);
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo("simple task");
        assertThat(task.getDescription()).isEqualTo("simple desc");
        assertThat(task.getTaskStatus().getId()).isEqualTo(testStatus.getId());
        assertThat(task.getAuthor().getId()).isEqualTo(defUser.getId());
        assertThat(task.getExecutor().getId()).isEqualTo(defUser.getId());
        assertThat(task.getCreatedAt()).isNotNull();

    }

    @Test
    public void testUpdateTask() throws Exception {
        String contentToUpdate = String.format("""
                {"name": "updated name",
                "description": "updated desc",
                "taskStatusId": %s,
                "executorId" : %s}""",
                testTaskDto.getTaskStatus().getId(),
                null);


        MockHttpServletResponse response = testUtils
                .performByUser(put("/tasks/" + testTaskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentToUpdate))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        TaskDto taskDto = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        Task task = taskRepository.findById(taskDto.getId()).orElse(null);
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo("updated name");
        assertThat(task.getDescription()).isEqualTo("updated desc");
        assertThat(task.getExecutor()).isNull();
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    public void testDeleteTask() throws Exception {
        MockHttpServletResponse response = testUtils
                .performByUser(delete("/tasks/" + testTaskDto.getId()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(taskRepository.existsById(testTaskDto.getId())).isFalse();
    }

}
