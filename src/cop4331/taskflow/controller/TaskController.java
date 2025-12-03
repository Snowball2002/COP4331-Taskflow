package cop4331.taskflow.controller;

import cop4331.taskflow.command.AddTaskCommand;
import cop4331.taskflow.command.CommandManager;
import cop4331.taskflow.command.CompleteTaskCommand;
import cop4331.taskflow.command.DeleteTaskCommand;
import cop4331.taskflow.command.EditTaskCommand;
import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskFactory;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing task operations.
 * 
 * <p>This class acts as the controller in the MVC pattern, mediating between
 * the view and model layers. It uses the Command pattern for all operations
 * to support undo/redo functionality.
 * 
 * <p><b>Preconditions:</b> model and commandManager must be non-null
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class TaskController {

    private final TaskModel model;
    private final CommandManager commandManager;
    private final TaskFactory taskFactory = new TaskFactory();

    /**
     * Creates a new TaskController.
     * 
     * <p><b>Preconditions:</b> model and commandManager must be non-null
     * 
     * @param model the task model (required, non-null)
     * @param commandManager the command manager (required, non-null)
     * @throws IllegalArgumentException if model or commandManager is null
     */
    public TaskController(TaskModel model, CommandManager commandManager) {
        if (model == null) {
            throw new IllegalArgumentException("TaskModel must be non-null");
        }
        if (commandManager == null) {
            throw new IllegalArgumentException("CommandManager must be non-null");
        }
        this.model = model;
        this.commandManager = commandManager;
    }

    /**
     * Adds a new task to the model.
     * 
     * <p><b>Preconditions:</b> title must be non-null and non-blank, priority must be non-null
     * 
     * <p><b>Postconditions:</b> Task is created and added via Command pattern
     * 
     * @param title the task title (required, non-null, non-blank)
     * @param description the task description (may be null)
     * @param due the due date/time (may be null)
     * @param priority the task priority (required, non-null)
     * @param dueDateString the raw due date string for display (may be null)
     * @return the ID of the created task
     * @throws IllegalArgumentException if title is null/blank or priority is null
     */
    public String addTask(String title,
                        String description,
                        LocalDateTime due,
                        TaskPriority priority,
                        String dueDateString) {
        return addTask(title, description, due, priority, dueDateString, null);
    }
    
    public String addTask(String title,
                        String description,
                        LocalDateTime due,
                        TaskPriority priority,
                        String dueDateString,
                        String category) {
        return addTask(title, description, due, priority, dueDateString, category, Task.RecurrenceType.NONE);
    }
    
    public String addTask(String title,
                        String description,
                        LocalDateTime due,
                        TaskPriority priority,
                        String dueDateString,
                        String category,
                        Task.RecurrenceType recurrenceType) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority must be non-null");
        }
        Task task = taskFactory.createSimpleTask(title, description, due, priority);
        if (dueDateString != null && !dueDateString.isEmpty()) {
            task.setDueDateString(dueDateString);
        }
        if (category != null && !category.isEmpty()) {
            task.setCategory(category);
        }
        if (recurrenceType != null && recurrenceType != Task.RecurrenceType.NONE) {
            task.setRecurrenceType(recurrenceType);
        }
        commandManager.executeCommand(new AddTaskCommand(model, task));
        return task.getId();
    }

    /**
     * Edits an existing task.
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task is updated via Command pattern
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @param title the new title (required, non-null, non-blank)
     * @param description the new description (may be null)
     * @param due the new due date/time (may be null)
     * @param dueDateString the raw due date string for display (may be null)
     * @throws IllegalArgumentException if id is null/blank or title is null/blank
     */
    public void editTask(String id,
                         String title,
                         String description,
                         LocalDateTime due,
                         String dueDateString) {
        editTask(id, title, description, due, dueDateString, null);
    }
    
    public void editTask(String id,
                         String title,
                         String description,
                         LocalDateTime due,
                         String dueDateString,
                         String category) {
        editTask(id, title, description, due, dueDateString, category, null);
    }
    
    public void editTask(String id,
                         String title,
                         String description,
                         LocalDateTime due,
                         String dueDateString,
                         String category,
                         Task.RecurrenceType recurrenceType) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must be non-null and non-blank");
        }
        commandManager.executeCommand(
                new EditTaskCommand(model, id, title, description, due, dueDateString, category, recurrenceType));
    }
    
    /**
     * Clones a task, creating a duplicate with a new ID.
     * 
     * <p>I added this because sometimes I need to create similar tasks and I'm lazy.
     * 
     * @param taskId the ID of the task to clone
     * @return the ID of the cloned task
     */
    public String cloneTask(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task ID must be non-null and non-blank");
        }
        Task original = model.findById(taskId).orElseThrow(() -> 
            new IllegalArgumentException("Task not found: " + taskId));
        
        // Create a new task with the same properties - basically copy-paste but for tasks
        Task cloned = taskFactory.createSimpleTask(
            original.getTitle() + " (Copy)", // Add "(Copy)" so I know it's a clone
            original.getDescription(),
            original.getDueDateTime(),
            original.getPriority()
        );
        cloned.setDueDateString(original.getDueDateString());
        cloned.setCategory(original.getCategory());
        cloned.setTags(original.getTags());
        cloned.setRecurrenceType(original.getRecurrenceType()); // Copy everything over
        
        commandManager.executeCommand(new AddTaskCommand(model, cloned));
        return cloned.getId();
    }
    
    /**
     * Performs bulk delete operation on multiple tasks.
     * 
     * <p>I made this so I can delete a bunch of tasks at once - saves time when cleaning up!
     * 
     * @param taskIds list of task IDs to delete
     */
    public void bulkDelete(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return; // Nothing to delete, nothing to do
        }
        for (String id : taskIds) {
            if (id != null && !id.isBlank()) {
                deleteTask(id); // Delete each one - mass deletion mode activated
            }
        }
    }
    
    /**
     * Performs bulk complete operation on multiple tasks.
     * 
     * <p>For when I actually finish multiple tasks at once (rare, but it happens!)
     * 
     * @param taskIds list of task IDs to mark as completed
     */
    public void bulkComplete(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return; // Nothing to complete
        }
        for (String id : taskIds) {
            if (id != null && !id.isBlank()) {
                completeTask(id); // Mark each one as done - productivity mode!
            }
        }
    }

    /**
     * Deletes a task (moves to trash).
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task status is set to TRASHED via Command pattern
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void deleteTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        commandManager.executeCommand(
                new DeleteTaskCommand(model, id));
    }

    /**
     * Marks a task as completed.
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task status is set to COMPLETED via Command pattern
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void completeTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        commandManager.executeCommand(
                new CompleteTaskCommand(model, id));
    }

    /**
     * Undoes the last command.
     * 
     * <p><b>Preconditions:</b> canUndo() must return true
     * 
     * <p><b>Postconditions:</b> Last command is undone
     */
    public void undo() {
        commandManager.undo();
    }

    /**
     * Redoes the last undone command.
     * 
     * <p><b>Preconditions:</b> canRedo() must return true
     * 
     * <p><b>Postconditions:</b> Last undone command is re-executed
     */
    public void redo() {
        commandManager.redo();
    }

    /**
     * Gets the task model.
     * 
     * @return the task model (never null)
     */
    public TaskModel getModel() {
        return model;
    }

    /**
     * Checks if undo is possible.
     * 
     * @return true if undo is possible, false otherwise
     */
    public boolean canUndo() {
        return commandManager.canUndo();
    }

    /**
     * Checks if redo is possible.
     * 
     * @return true if redo is possible, false otherwise
     */
    public boolean canRedo() {
        return commandManager.canRedo();
    }
}

