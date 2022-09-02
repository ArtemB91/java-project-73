package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public final class UserDto {
    private long id;

    @Size(min = 1)
    private String firstName;

    @Size(min = 1)
    private String lastName;

    @Email
    private String email;

    @Size(min = 3)
    private String password;

    public Long getId() {
        return id;
    }

}
