package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get specific status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status found"),
            @ApiResponse(responseCode = "404", description = "Status with that id not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/{id}")
    public StatusDto getOneStatus(
            @Parameter(description = "Id of status to be found")
            @PathVariable(value = "id") Long id) {
        return statusService.getStatusById(id);
    }

    @Operation(summary = "Get list of all statuses")
    @ApiResponse(responseCode = "200", description = "List of all statuses")
    @GetMapping
    public List<StatusDto> getStatuses() {
        return statusService.getStatuses();
    }

    @Operation(summary = "Create new status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Status created"),
            @ApiResponse(responseCode = "422", description = "Invalid status data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDto createStatus(
            @Parameter(description = "Status data to create")
            @Valid @RequestBody StatusDto statusDto) {
        return statusService.createStatus(statusDto);
    }

    @Operation(summary = "Update existing status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Status with that id not found"),
            @ApiResponse(responseCode = "422", description = "Invalid status data")
    })
    @PutMapping(path = "/{id}")
    public StatusDto updateStatus(
            @Parameter(description = "Id of status to be updated")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "Status data to update")
            @Valid @RequestBody StatusDto statusDto) {
        return statusService.updateStatus(id, statusDto);
    }

    @Operation(summary = "Delete status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status deleted"),
            @ApiResponse(responseCode = "404", description = "Status with that id not found")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteStatus(
            @Parameter(description = "Id of status to be deleted")
            @PathVariable(value = "id") Long id) {
        statusService.deleteStatus(id);
    }
}
