package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.repository.CrudRepository;

public interface LabelRepository extends CrudRepository<Label, Long> {
}
