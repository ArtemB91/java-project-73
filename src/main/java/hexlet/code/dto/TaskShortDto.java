package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
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
