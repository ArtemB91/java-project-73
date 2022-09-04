package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public final class UserShortDto {
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private Date createdAt;
}
