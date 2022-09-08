package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.service.LabelService;
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
@RequestMapping(path = "/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @Operation(summary = "Get specific label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping(path = "/{id}")
    public LabelDto getOneLabel(
            @Parameter(description = "Id of label to be found")
            @PathVariable(value = "id") Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of all labels")
    @GetMapping
    public List<LabelDto> getLabels() {
        return labelService.getLabels();
    }

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Label created"),
            @ApiResponse(responseCode = "422", description = "Invalid label data")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto createLabel(
            @Parameter(description = "Label data to create")
            @Valid @RequestBody LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update existing label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found"),
            @ApiResponse(responseCode = "422", description = "Invalid label data")
    })
    @PutMapping(path = "/{id}")
    public LabelDto updateLabel(
            @Parameter(description = "Id of label to be updated")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "Label data to update")
            @Valid @RequestBody LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteLabel(
            @Parameter(description = "Id of label to be deleted")
            @PathVariable(value = "id") Long id) {
        labelService.deleteLabel(id);
    }
}
