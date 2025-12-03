package cop4331.taskflow;

import cop4331.taskflow.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JUnit tests for TaskModel.
 */
public class TaskModelTest {

    private TaskModel model;

    @BeforeEach
    public void setUp() {
        model = new TaskModel();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        model.addTask(task);
        
        List<Task> tasks = model.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    public void testFindById() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        model.addTask(task);
        
        assertTrue(model.findById(task.getId()).isPresent());
        assertEquals("Test Task", model.findById(task.getId()).get().getTitle());
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        model.addTask(task);
        model.deleteTask(task.getId());
        
        assertEquals(0, model.getTasks().size());
    }

    @Test
    public void testMarkCompleted() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        model.addTask(task);
        model.markCompleted(task.getId());
        
        assertEquals(TaskStatus.COMPLETED, model.findById(task.getId()).get().getStatus());
    }

    @Test
    public void testMoveToTrash() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        model.addTask(task);
        model.moveToTrash(task.getId());
        
        assertEquals(TaskStatus.TRASHED, model.findById(task.getId()).get().getStatus());
    }

    @Test
    public void testSortStrategy() {
        Task task1 = new Task("Task 1", "Description", LocalDateTime.now().plusDays(2), TaskPriority.LOW);
        Task task2 = new Task("Task 2", "Description", LocalDateTime.now().plusDays(1), TaskPriority.HIGH);
        
        model.addTask(task1);
        model.addTask(task2);
        
        model.setSortStrategy(new SortByDueDateStrategy());
        List<Task> tasks = model.getTasks();
        assertEquals("Task 2", tasks.get(0).getTitle());
    }
}

