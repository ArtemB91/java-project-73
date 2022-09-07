package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskShortDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private static final String ONLY_THE_AUTHOR = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(path = "/{id}")
    public TaskDto getOneTask(@PathVariable(value = "id") Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public Iterable<TaskDto> getTasks(
            @QuerydslPredicate(root = Task.class) Predicate predicate) {

        return taskService.getTasks(predicate);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskShortDto taskShortDto) {
        return taskService.createTask(taskShortDto);
    }

    @PutMapping(path = "/{id}")
    public TaskDto updateTask(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody TaskShortDto taskShortDto) {
        return taskService.updateTask(id, taskShortDto);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_THE_AUTHOR)
    public void deleteTask(@PathVariable(value = "id") Long id) {
        taskService.deleteTask(id);
    }


}
