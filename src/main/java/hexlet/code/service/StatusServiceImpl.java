package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class StatusServiceImpl implements StatusService {

    private static final DataNotFoundException STATUS_NOT_FOUND = new DataNotFoundException("Status not found");
    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;

    public StatusServiceImpl(StatusRepository statusRepository, TaskRepository taskRepository) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public StatusDto getStatusById(Long id) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> STATUS_NOT_FOUND);
        return convertToStatusDto(status);
    }

    @Override
    public StatusDto createStatus(StatusDto statusDto) {
        Status status = convertToStatus(statusDto);
        return convertToStatusDto(statusRepository.save(status));
    }

    @Override
    public List<StatusDto> getStatuses() {
        return StreamSupport.stream(statusRepository.findAll().spliterator(), false)
                .map(this::convertToStatusDto)
                .toList();
    }

    @Override
    public StatusDto updateStatus(Long id, StatusDto statusDto) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> STATUS_NOT_FOUND);
        fillStatus(statusDto, status);
        return convertToStatusDto(statusRepository.save(status));
    }

    @Override
    public void deleteStatus(Long id) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> STATUS_NOT_FOUND);

        if (taskRepository.existsByTaskStatus(status)) {
            throw new IllegalArgumentException("Deletion is prohibited. Status is used in tasks");
        }

        statusRepository.delete(status);
    }

    private Status convertToStatus(StatusDto statusDto) {
        return fillStatus(statusDto, new Status());
    }

    private Status fillStatus(StatusDto statusDto, Status status) {
        status.setName(statusDto.getName());
        return status;
    }

    private StatusDto convertToStatusDto(Status status) {
        StatusDto statusDto = new StatusDto();
        statusDto.setId(status.getId());
        statusDto.setName(status.getName());
        statusDto.setCreatedAt(status.getCreatedAt());
        return statusDto;
    }
}
