package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskShortDto {
    @Size(min = 1)
    private String name;

    private String description;

    @NotNull(message = "Status must be filled in")
    private Long taskStatusId;

    private Long executorId;

    private List<Long> labelIds;
}
