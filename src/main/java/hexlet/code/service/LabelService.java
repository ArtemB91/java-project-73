package hexlet.code.service;

import hexlet.code.dto.LabelDto;

import java.util.List;

public interface LabelService {

    LabelDto getLabelById(Long id);

    LabelDto createLabel(LabelDto labelDto);

    List<LabelDto> getLabels();

    LabelDto updateLabel(Long id, LabelDto labelDto);

    void deleteLabel(Long id);
}
