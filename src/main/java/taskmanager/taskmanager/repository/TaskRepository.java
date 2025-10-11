package taskmanager.taskmanager.repository;
import java.util.List;
import taskmanager.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByCompleted(boolean completed);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    List<Task> findByTitleContainingIgnoreCaseAndCompleted(String keyword, boolean completed);
}