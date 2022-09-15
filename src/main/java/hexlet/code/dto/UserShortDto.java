package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class UserShortDto {
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private Date createdAt;
}
