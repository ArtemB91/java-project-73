package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusDto {

    private Long id;

    @Size(min = 1)
    private String name;

    private Date createdAt;

    public StatusDto(String name) {
        this.name = name;
    }
}
