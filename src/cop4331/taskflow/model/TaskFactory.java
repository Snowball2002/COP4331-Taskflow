package cop4331.taskflow.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Factory for creating different task types.
 */
public class TaskFactory {

    public Task createSimpleTask(String title,
                                 String description,
                                 LocalDateTime dueDateTime,
                                 TaskPriority priority) {
        return new Task(title, description, dueDateTime, priority);
    }

    /**
     * Placeholder for recurring tasks; you can later add a RecurringTask subclass.
     */
    public Task createRecurringTask(String title,
                                    String description,
                                    LocalDateTime firstDueDateTime,
                                    TaskPriority priority,
                                    List<String> tags) {
        Task task = new Task(title, description, firstDueDateTime, priority);
        task.setTags(tags);
        // Add recurrence metadata later as needed
        return task;
    }
}

