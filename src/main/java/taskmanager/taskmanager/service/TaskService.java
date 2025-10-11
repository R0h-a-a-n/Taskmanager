package taskmanager.taskmanager.service;

import taskmanager.taskmanager.model.Task;
import taskmanager.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class TaskService{
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public Page<Task> getAllTasksPaged(int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return taskRepository.findAll(pageable);
    }

    public Task updateTask(Long id, Task updatedTask){
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setCompleted(updatedTask.isCompleted());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public void deleteTask(Long id){
        if (!taskRepository.existsById(id)){
            throw new RuntimeException("Task not found with id " + id);
        }
        taskRepository.deleteById(id);
    }

    public List<Task> filterTasks(String keyword, Boolean completed){
        if (keyword != null && completed != null){
            return taskRepository.findByTitleContainingIgnoreCaseAndCompleted(keyword, completed);
        }else if(keyword != null){
            return taskRepository.findByTitleContainingIgnoreCase(keyword);
        }else if(completed != null){
            return taskRepository.findByCompleted(completed);
        }else{
            return taskRepository.findAll();
        }
    }
}