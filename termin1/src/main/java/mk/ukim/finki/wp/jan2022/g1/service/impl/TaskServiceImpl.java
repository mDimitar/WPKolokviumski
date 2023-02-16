package mk.ukim.finki.wp.jan2022.g1.service.impl;

import mk.ukim.finki.wp.jan2022.g1.model.Task;
import mk.ukim.finki.wp.jan2022.g1.model.TaskCategory;
import mk.ukim.finki.wp.jan2022.g1.model.User;
import mk.ukim.finki.wp.jan2022.g1.repository.TaskRepository;
import mk.ukim.finki.wp.jan2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.jan2022.g1.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Task> listAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public Task create(String title, String description, TaskCategory category, List<Long> assignees, LocalDate dueDate) {
        List<User> list = new ArrayList<>();
        for(Long a : assignees){
            User user = userRepository.findById(a).get();
            list.add(user);
        }
        return taskRepository.save(new Task(title,description,category,list,dueDate));
    }

    @Override
    public Task update(Long id, String title, String description, TaskCategory category, List<Long> assignees) {
        Task task = taskRepository.findById(id).get();
        task.setTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        List<User> list = new ArrayList<>();
        for(Long a : assignees){
            User user = userRepository.findById(a).get();
            list.add(user);
        }
        task.setAssignees(list);
        return taskRepository.save(task);
    }

    @Override
    public Task delete(Long id) {
        Task task = taskRepository.findById(id).get();
        taskRepository.delete(task);
        return task;
    }

    @Override
    public Task markDone(Long id) {
        Task task = taskRepository.findById(id).get();
        task.setDone(true);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> filter(Long assigneeId, Integer lessThanDayBeforeDueDate) {
        if(assigneeId == null && lessThanDayBeforeDueDate == null){
            return taskRepository.findAll();
        }
        if(assigneeId != null && lessThanDayBeforeDueDate == null){
            User user = userRepository.findById(assigneeId).get();
            return taskRepository.findAllByAssignees(user);
        }
        if(assigneeId == null && lessThanDayBeforeDueDate != null){
            return taskRepository.findAllByDueDateBefore(LocalDate.now().plusDays(lessThanDayBeforeDueDate));
        }
        if(assigneeId != null && lessThanDayBeforeDueDate != null){
            User user = userRepository.findById(assigneeId).get();
            return taskRepository.findAllByAssigneesAndDueDateBefore(user,LocalDate.now().plusDays(lessThanDayBeforeDueDate));
        }
        return null;
    }
}
