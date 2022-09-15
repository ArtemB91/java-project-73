package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskShortDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskServiceImpl implements TaskService {

    private static final DataNotFoundException TASK_NOT_FOUND = new DataNotFoundException("Task not found");
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LabelRepository labelRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           StatusRepository statusRepository,
                           UserRepository userRepository,
                           UserService userService,
                           LabelRepository labelRepository) {
        this.taskRepository = taskRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.labelRepository = labelRepository;
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        return convertToTaskDto(task);
    }

    @Override
    public TaskDto createTask(TaskShortDto taskShortDto) {
        validateTaskDto(taskShortDto);
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
    public List<TaskDto> getTasks(Predicate predicate) {

        if (predicate == null) {
            return getTasks();
        }

        return StreamSupport.stream(taskRepository.findAll(predicate).spliterator(), false)
                .map(this::convertToTaskDto)
                .toList();
    }

    @Override
    public TaskDto updateTask(Long id, TaskShortDto taskShortDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        validateTaskDto(taskShortDto);
        fillTask(taskShortDto, task);
        return convertToTaskDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TASK_NOT_FOUND);
        taskRepository.delete(task);
    }

    private Task convertToTask(TaskShortDto taskShortDto) {
        return fillTask(taskShortDto, new Task());
    }

    private Task fillTask(TaskShortDto taskShortDto, Task task) {
        task.setName(taskShortDto.getName());
        task.setDescription(taskShortDto.getDescription());

        final Status status = new Status(taskShortDto.getTaskStatusId());
        task.setTaskStatus(status);

        task.setAuthor(userService.currentUser());

        final User executor = Optional.ofNullable(taskShortDto.getExecutorId())
                .map(User::new)
                .orElse(null);

        task.setExecutor(executor);


        final List<Label> labels = Optional.ofNullable(taskShortDto.getLabelIds())
                .map(ids -> ids.stream()
                        .map(Label::new)
                        .collect(Collectors.toList()))
                .orElse(null);

        task.setLabels(labels);

        return task;
    }

    private void validateTaskDto(TaskShortDto taskShortDto) {
        statusRepository.findById(taskShortDto.getTaskStatusId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Status with id %s not found", taskShortDto.getTaskStatusId())));

        Optional.ofNullable(taskShortDto.getExecutorId())
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException(
                                String.format("User with id %s not found", id))));

        Optional.ofNullable(taskShortDto.getLabelIds())
                .map(ids -> ids.stream()
                        .map(id -> labelRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        String.format("Label with id %s not found", id)))));
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
        taskDto.setLabels(task.getLabels());

        return taskDto;
    }


}
