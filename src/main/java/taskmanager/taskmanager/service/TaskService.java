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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import taskmanager.taskmanager.dto.TaskRequestDTO;
import taskmanager.taskmanager.dto.TaskResponseDTO;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public TaskService(TaskRepository taskRepository, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(task -> modelMapper.map(task, TaskResponseDTO.class))
                .toList();
    }

    public Optional<TaskResponseDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(task -> modelMapper.map(task, TaskResponseDTO.class));
    }

    public TaskResponseDTO createTask(TaskRequestDTO taskRequest) {
        Task task = modelMapper.map(taskRequest, Task.class);
        Task saved = taskRepository.save(task);
        return modelMapper.map(saved, TaskResponseDTO.class);
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequest) {
        return taskRepository.findById(id)
                .map(existing -> {
                    modelMapper.map(taskRequest, existing);
                    return modelMapper.map(taskRepository.save(existing), TaskResponseDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id " + id);
        }
        taskRepository.deleteById(id);
    }
}
