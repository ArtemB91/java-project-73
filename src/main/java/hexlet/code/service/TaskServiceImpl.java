package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskShortDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class TaskServiceImpl implements TaskService {

    private static final DataNotFoundException TASK_NOT_FOUND = new DataNotFoundException("Task not found");
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           StatusRepository statusRepository,
                           UserRepository userRepository,
                           UserService userService) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        return convertToTaskDto(task);
    }

    @Override
    public TaskDto createTask(TaskShortDto taskShortDto) {
        Task task = convertToTask(taskShortDto);
        return convertToTaskDto(taskRepository.save(task));
    }

    @Override
    public List<TaskDto> getTasks() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .map(this::convertToTaskDto)
                .toList();
    }

    @Override
    public TaskDto updateTask(Long id, TaskShortDto taskShortDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        convertToTask(taskShortDto, task);
        return convertToTaskDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        taskRepository.delete(task);
    }

    private Task convertToTask(TaskShortDto taskShortDto) {
        return convertToTask(taskShortDto, new Task());
    }

    private Task convertToTask(TaskShortDto taskShortDto, Task task) {
        task.setName(taskShortDto.getName());
        task.setDescription(taskShortDto.getDescription());

        // Добавил доп. проверку на уровне бизнес-логики
        // Да, ошибка вылетит и при записи в БД, т.к. поле NotNull,
        // но считаю, что и на уровне бизнес-логики надо проверить для выдачи нормального сообщения
        Status status = statusRepository.findById(taskShortDto.getTaskStatusId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Status with id %s not found", taskShortDto.getTaskStatusId())));
        task.setTaskStatus(status);

        task.setAuthor(userService.currentUser());

        if (taskShortDto.getExecutorId() != null) {
            User executor = userRepository.findById(taskShortDto.getExecutorId()).orElse(null);
            task.setExecutor(executor);
        } else {
            task.setExecutor(null);
        }

        return task;
    }

    private TaskDto convertToTaskDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setTaskStatus(task.getTaskStatus());
        taskDto.setAuthor(task.getAuthor());
        taskDto.setExecutor(task.getExecutor());
        taskDto.setCreatedAt(task.getCreatedAt());

        return taskDto;
    }


}
