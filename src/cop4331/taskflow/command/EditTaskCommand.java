package cop4331.taskflow.command;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Command for editing an existing task.
 * 
 * <p>Uses the snapshot pattern to support undo/redo operations.
 * 
 * <p><b>Preconditions:</b> model, taskId, and new values must be non-null
 * 
 * <p><b>Postconditions:</b> Task is updated on execute, restored on undo
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class EditTaskCommand implements Command {

    private final TaskModel model;
    private final String taskId;

    private final String newTitle;
    private final String newDescription;
    private final LocalDateTime newDue;
    private final String newDueDateString;
    private final String newCategory;
    private final Task.RecurrenceType newRecurrenceType;

    private Task.EditSnapshot oldSnapshot;
    private String oldCategory;
    private Task.RecurrenceType oldRecurrenceType;

    public EditTaskCommand(TaskModel model,
                           String taskId,
                           String newTitle,
                           String newDescription,
                           LocalDateTime newDue,
                           String newDueDateString) {
        this(model, taskId, newTitle, newDescription, newDue, newDueDateString, null, null);
    }
    
    public EditTaskCommand(TaskModel model,
                           String taskId,
                           String newTitle,
                           String newDescription,
                           LocalDateTime newDue,
                           String newDueDateString,
                           String newCategory) {
        this(model, taskId, newTitle, newDescription, newDue, newDueDateString, newCategory, null);
    }
    
    public EditTaskCommand(TaskModel model,
                           String taskId,
                           String newTitle,
                           String newDescription,
                           LocalDateTime newDue,
                           String newDueDateString,
                           String newCategory,
                           Task.RecurrenceType newRecurrenceType) {
        this.model = model;
        this.taskId = taskId;
        this.newTitle = newTitle;
        this.newDescription = newDescription;
        this.newDue = newDue;
        this.newDueDateString = newDueDateString;
        this.newCategory = newCategory;
        this.newRecurrenceType = newRecurrenceType;
    }

    @Override
    public void execute() {
        Optional<Task> opt = model.findById(taskId);
        opt.ifPresent(task -> {
            oldSnapshot = task.createSnapshot();
            oldCategory = task.getCategory();
            oldRecurrenceType = task.getRecurrenceType();
            task.setTitle(newTitle);
            task.setDescription(newDescription);
            task.setDueDateTime(newDue);
            task.setDueDateString(newDueDateString);
            if (newCategory != null) {
                task.setCategory(newCategory);
            }
            if (newRecurrenceType != null) {
                task.setRecurrenceType(newRecurrenceType);
            }
        });
    }

    @Override
    public void undo() {
        if (oldSnapshot == null) {
            return;
        }
        model.findById(taskId).ifPresent(task -> {
            task.restore(oldSnapshot);
            task.setCategory(oldCategory);
            task.setRecurrenceType(oldRecurrenceType);
        });
    }
}

