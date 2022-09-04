package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.service.StatusService;
import org.springframework.http.HttpStatus;
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
import java.util.List;

@RestController
@RequestMapping(path = "/statuses")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping(path = "/{id}")
    public StatusDto getOneStatus(@PathVariable(value = "id") Long id) {
        return statusService.getStatusById(id);
    }

    @GetMapping
    public List<StatusDto> getStatuses() {
        return statusService.getStatuses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDto createStatus(@Valid @RequestBody StatusDto statusDto) {
        return statusService.createStatus(statusDto);
    }

    @PutMapping(path = "/{id}")
    public StatusDto updateStatus(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody StatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteStatus(@PathVariable(value = "id") Long id) {
        statusService.deleteStatus(id);
    }
}
