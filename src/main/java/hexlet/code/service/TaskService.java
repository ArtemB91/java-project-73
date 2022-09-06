package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskShortDto;

import java.util.List;

public interface TaskService {

    TaskDto getTaskById(Long id);

    TaskDto createTask(TaskShortDto taskShortDto);

    List<TaskDto> getTasks();

    TaskDto updateTask(Long id, TaskShortDto taskShortDto);

    void deleteTask(Long id);

}
