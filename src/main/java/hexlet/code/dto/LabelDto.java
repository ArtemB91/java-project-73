package hexlet.code.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class LabelDto {

    private Long id;

    @Size(min = 1)
    private String name;

    private Date createdAt;
}
