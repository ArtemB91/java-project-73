package hexlet.code.service;

import hexlet.code.dto.StatusDto;

import java.util.List;

public interface StatusService {

    StatusDto getStatusById(Long id);

    StatusDto createStatus(StatusDto statusDto);

    List<StatusDto> getStatuses();

    StatusDto updateStatus(Long id, StatusDto statusDto);

    void deleteStatus(Long id);
}
