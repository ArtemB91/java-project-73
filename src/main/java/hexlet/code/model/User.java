package hexlet.code.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;

@Entity(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Instant createdAt;
}
