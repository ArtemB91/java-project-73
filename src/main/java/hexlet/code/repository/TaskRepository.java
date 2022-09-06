package hexlet.code.repository;

import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
    boolean existsByAuthor(User user);
    boolean existsByTaskStatus(Status status);
}
