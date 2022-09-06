package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.service.LabelService;
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

    @GetMapping(path = "/{id}")
    public LabelDto getOneLabel(@PathVariable(value = "id") Long id) {
        return labelService.getLabelById(id);
    }

    @GetMapping
    public List<LabelDto> getLabeles() {
        return labelService.getLabels();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto createLabel(@Valid @RequestBody LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @PutMapping(path = "/{id}")
    public LabelDto updateLabel(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable(value = "id") Long id) {
        labelService.deleteLabel(id);
    }
}
