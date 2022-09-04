package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class StatusServiceImpl implements StatusService {

    private static final DataNotFoundException STATUS_NOT_FOUND = new DataNotFoundException("Status not found");
    private final StatusRepository statusRepository;

    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
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
        convertToStatus(statusDto, status);
        return convertToStatusDto(statusRepository.save(status));
    }

    @Override
    public void deleteStatus(Long id) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> STATUS_NOT_FOUND);
        statusRepository.delete(status);
    }

    private Status convertToStatus(StatusDto statusDto) {
        return convertToStatus(statusDto, new Status());
    }

    private Status convertToStatus(StatusDto statusDto, Status status) {
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
