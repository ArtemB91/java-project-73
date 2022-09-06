package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.DataNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class LabelServiceImpl implements LabelService {

    private static final DataNotFoundException LABEL_NOT_FOUND = new DataNotFoundException("Label not found");
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;

    public LabelServiceImpl(LabelRepository labelRepository, TaskRepository taskRepository) {
        this.labelRepository = labelRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public LabelDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> LABEL_NOT_FOUND);
        return convertToLabelDto(label);
    }

    @Override
    public LabelDto createLabel(LabelDto labelDto) {
        Label label = convertToLabel(labelDto);
        return convertToLabelDto(labelRepository.save(label));
    }

    @Override
    public List<LabelDto> getLabels() {
        return StreamSupport.stream(labelRepository.findAll().spliterator(), false)
                .map(this::convertToLabelDto)
                .toList();
    }

    @Override
    public LabelDto updateLabel(Long id, LabelDto labelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> LABEL_NOT_FOUND);
        convertToLabel(labelDto, label);
        return convertToLabelDto(labelRepository.save(label));
    }

    @Override
    public void deleteLabel(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> LABEL_NOT_FOUND);

        if (taskRepository.existsByLabels(label)) {
            throw new IllegalArgumentException("Deletion is prohibited. Label is used in tasks");
        }

        labelRepository.delete(label);
    }

    private Label convertToLabel(LabelDto labelDto) {
        return convertToLabel(labelDto, new Label());
    }

    private Label convertToLabel(LabelDto labelDto, Label label) {
        label.setName(labelDto.getName());
        return label;
    }

    private LabelDto convertToLabelDto(Label label) {
        LabelDto labelDto = new LabelDto();
        labelDto.setId(label.getId());
        labelDto.setName(label.getName());
        labelDto.setCreatedAt(label.getCreatedAt());
        return labelDto;
    }
}
