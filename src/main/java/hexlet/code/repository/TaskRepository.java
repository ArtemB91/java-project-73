package hexlet.code.repository;

import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import hexlet.code.model.QTask;

public interface TaskRepository extends
        CrudRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {
    boolean existsByAuthor(User user);
    boolean existsByLabels(Label label);
    boolean existsByTaskStatus(Status status);

    @Override
    default void customize(QuerydslBindings bindings, QTask root) {

    }
}
