package hexlet.code.dto;

import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;

    @Size(min = 1)
    private String name;

    private String description;

    @NotNull(message = "Status must be filled in")
    private Status taskStatus;

    @NotNull(message = "Author must be filled in")
    private User author;

    private User executor;

    private Date createdAt;

    private List<Label> labels;
}
