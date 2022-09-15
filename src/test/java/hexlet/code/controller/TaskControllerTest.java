package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.TestUtils;
import hexlet.code.dto.StatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskShortDto;
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
class TaskControllerTest {

    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    private TaskDto testTaskDto;
    private StatusDto testStatusDto;

    @BeforeEach
    void beforeEach() throws Exception {
        testUtils.regDefaultUser();
        testStatusDto = testUtils.addTestStatus();
        testTaskDto = testUtils.addTestTask(testStatusDto);
    }

    @Test
    void testGetTasks() throws Exception {
        MockHttpServletResponse response =
                testUtils.performByUser(get("/tasks"))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains(
                testTaskDto.getName());
    }

    @Test
    void testGetOneTask() throws Exception {

        MockHttpServletResponse response =
                testUtils.performByUser(get("/tasks/" + testTaskDto.getId()))
                        .andReturn()
                        .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON.toString());
        assertThat(response.getContentAsString()).contains(
                testTaskDto.getName(),
                testTaskDto.getDescription(),
                testTaskDto.getAuthor().getEmail());
    }

    @Test
    void testCreateTask() throws Exception {
        User defUser = userRepository.findByEmail(testUtils.defaultUserDto().getEmail()).get();

        TaskShortDto taskToCreate = new TaskShortDto();
        taskToCreate.setName("simple task");
        taskToCreate.setDescription("simple desc");
        taskToCreate.setTaskStatusId(testStatusDto.getId());
        taskToCreate.setExecutorId(defUser.getId());

        String content = String.format("""
                {"name": "simple task",
                "description": "simple desc",
                "taskStatusId": %s,
                "executorId" : %s}""",
                testStatusDto.getId(),
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
        assertThat(task.getName()).isEqualTo(taskToCreate.getName());
        assertThat(task.getDescription()).isEqualTo(taskToCreate.getDescription());
        assertThat(task.getTaskStatus().getId()).isEqualTo(taskToCreate.getTaskStatusId());
        assertThat(task.getAuthor().getId()).isEqualTo(defUser.getId());
        assertThat(task.getExecutor().getId()).isEqualTo(taskToCreate.getExecutorId());
        assertThat(task.getCreatedAt()).isNotNull();

    }

    @Test
    void testUpdateTask() throws Exception {

        TaskShortDto taskToUpdate = new TaskShortDto();
        taskToUpdate.setName("updated name");
        taskToUpdate.setDescription("updated desc");
        taskToUpdate.setTaskStatusId(testTaskDto.getTaskStatus().getId());

        MockHttpServletResponse response = testUtils
                .performByUser(put("/tasks/" + testTaskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.toJson(taskToUpdate)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        TaskDto taskDto = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        Task task = taskRepository.findById(taskDto.getId()).orElse(null);
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(taskToUpdate.getName());
        assertThat(task.getDescription()).isEqualTo(taskToUpdate.getDescription());
        assertThat(task.getExecutor()).isNull();
        assertThat(task.getCreatedAt()).isNotNull();
    }

    @Test
    void testDeleteTask() throws Exception {
        MockHttpServletResponse response = testUtils
                .performByUser(delete("/tasks/" + testTaskDto.getId()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(taskRepository.existsById(testTaskDto.getId())).isFalse();
    }

}
