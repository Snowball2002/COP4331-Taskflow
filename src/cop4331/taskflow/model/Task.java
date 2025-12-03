package cop4331.taskflow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a task entity in TaskFlow.
 * 
 * <p>A task contains all information needed to track a user's task including
 * title, description, due date, priority, status, and optional reminder time.
 * Tasks support snapshot functionality for undo/redo operations.
 * 
 * <p><b>Preconditions:</b> Title must be non-null and non-blank when creating a task.
 * 
 * <p><b>Postconditions:</b> All setter methods update the task and set updatedAt timestamp.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class Task {

    private final String id;
    private String title;
    private String description;
    private LocalDateTime dueDateTime;
    private String dueDateString; // Store raw string for display
    private TaskPriority priority;
    private TaskStatus status;
    private final List<String> tags;
    private String category; // Project/category name
    private List<String> dependencies; // IDs of tasks this task depends on
    private RecurrenceType recurrenceType; // Daily, Weekly, Monthly, or None
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reminderTime;
    
    /**
     * Enum for recurring task types.
     */
    public enum RecurrenceType {
        NONE, DAILY, WEEKLY, MONTHLY
    }

    /**
     * Creates a new task with the specified properties.
     * 
     * <p><b>Preconditions:</b>
     * <ul>
     *   <li>title must be non-null and non-blank</li>
     *   <li>priority must be non-null</li>
     * </ul>
     * 
     * <p><b>Postconditions:</b>
     * <ul>
     *   <li>A new task is created with a unique UUID</li>
     *   <li>Status is set to PENDING</li>
     *   <li>createdAt and updatedAt are set to current time</li>
     * </ul>
     * 
     * @param title the task title (required, non-null, non-blank)
     * @param description the task description (may be null or empty)
     * @param dueDateTime the due date and time (may be null)
     * @param priority the task priority (required, non-null)
     * @throws IllegalArgumentException if title is null or blank, or priority is null
     */
    public Task(String title,
                String description,
                LocalDateTime dueDateTime,
                TaskPriority priority) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must be non-null and non-blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority must be non-null");
        }
        this.id = UUID.randomUUID().toString(); // Give it a unique ID (like a social security number but for taskssssssss)
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.dueDateString = null; // Will be set via setter
        this.priority = priority;
        this.status = TaskStatus.PENDING; // Everyone starts as pending............................
        this.tags = new ArrayList<>();
        this.category = null;
        this.dependencies = new ArrayList<>();
        this.recurrenceType = RecurrenceType.NONE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reminderTime = null;
    }

    /**
     * Creates a task with all properties specified (used for loading from persistence).
     * 
     * <p><b>Preconditions:</b>
     * <ul>
     *   <li>id must be non-null and non-blank</li>
     *   <li>title must be non-null and non-blank</li>
     *   <li>priority must be non-null</li>
     *   <li>status must be non-null</li>
     * </ul>
     * 
     * <p><b>Postconditions:</b>
     * <ul>
     *   <li>Task is created with all specified properties</li>
     *   <li>createdAt and updatedAt are set to current time</li>
     * </ul>
     * 
     * @param id the unique task identifier (required, non-null)
     * @param title the task title (required, non-null, non-blank)
     * @param description the task description (may be null)
     * @param dueDateTime the due date and time (may be null)
     * @param priority the task priority (required, non-null)
     * @param status the task status (required, non-null)
     * @param tags the list of tags (may be null, will be converted to empty list)
     * @param reminderTime the reminder time (may be null)
     * @throws IllegalArgumentException if required parameters are null or blank
     */
    public Task(String id,
                String title,
                String description,
                LocalDateTime dueDateTime,
                TaskPriority priority,
                TaskStatus status,
                List<String> tags,
                LocalDateTime reminderTime) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must be non-null and non-blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority must be non-null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status must be non-null");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.dueDateString = null; // Will be set via setter if needed
        this.priority = priority;
        this.status = status;
        this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
        this.category = null;
        this.dependencies = new ArrayList<>();
        this.recurrenceType = RecurrenceType.NONE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.reminderTime = reminderTime;
    }

    /**
     * Gets the unique identifier of this task.
     * 
     * @return the task ID (never null)
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the title of this task.
     * 
     * @return the task title (never null)
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this task.
     * 
     * <p><b>Preconditions:</b> title must be non-null and non-blank
     * 
     * <p><b>Postconditions:</b> title is updated and updatedAt timestamp is set
     * 
     * @param title the new title (required, non-null, non-blank)
     * @throws IllegalArgumentException if title is null or blank
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must be non-null and non-blank");
        }
        this.title = title;
        touch();
    }

    /**
     * Gets the description of this task.
     * 
     * @return the task description (may be null)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this task.
     * 
     * <p><b>Postconditions:</b> description is updated and updatedAt timestamp is set
     * 
     * @param description the new description (may be null)
     */
    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    /**
     * Gets the due date and time of this task.
     * 
     * @return the due date/time (may be null if not set)
     */
    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    /**
     * Sets the due date and time of this task.
     * 
     * <p><b>Postconditions:</b> dueDateTime is updated and updatedAt timestamp is set
     * 
     * @param dueDateTime the new due date/time (may be null)
     */
    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
        touch();
    }

    /**
     * Gets the raw due date string for display.
     * 
     * @return the due date string (may be null)
     */
    public String getDueDateString() {
        return dueDateString;
    }

    /**
     * Sets the raw due date string for display.
     * 
     * <p><b>Postconditions:</b> dueDateString is updated and updatedAt timestamp is set
     * 
     * @param dueDateString the new due date string (may be null)
     */
    public void setDueDateString(String dueDateString) {
        this.dueDateString = dueDateString;
        touch();
    }

    /**
     * Gets the priority of this task.
     * 
     * @return the task priority (never null)
     */
    public TaskPriority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this task.
     * 
     * <p><b>Preconditions:</b> priority must be non-null
     * 
     * <p><b>Postconditions:</b> priority is updated and updatedAt timestamp is set
     * 
     * @param priority the new priority (required, non-null)
     * @throws IllegalArgumentException if priority is null
     */
    public void setPriority(TaskPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority must be non-null");
        }
        this.priority = priority;
        touch();
    }

    /**
     * Gets the status of this task.
     * 
     * @return the task status (never null)
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this task.
     * 
     * <p><b>Preconditions:</b> status must be non-null
     * 
     * <p><b>Postconditions:</b> status is updated and updatedAt timestamp is set
     * 
     * @param status the new status (required, non-null)
     * @throws IllegalArgumentException if status is null
     */
    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must be non-null");
        }
        this.status = status;
        touch();
    }

    /**
     * Gets a copy of the tags list for this task.
     * 
     * @return a new list containing all tags (never null, may be empty)
     */
    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Sets the tags for this task.
     * 
     * <p><b>Postconditions:</b> tags are replaced with the new list and updatedAt timestamp is set
     * 
     * @param tags the new list of tags (may be null, will be treated as empty list)
     */
    public void setTags(List<String> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
        touch();
    }

    /**
     * Gets the creation timestamp of this task.
     * 
     * @return the creation date/time (never null)
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the last update timestamp of this task.
     * 
     * @return the last update date/time (never null)
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Gets the reminder time for this task.
     * 
     * @return the reminder time (may be null if not set)
     */
    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    /**
     * Sets the reminder time for this task.
     * 
     * <p><b>Postconditions:</b> reminderTime is updated and updatedAt timestamp is set
     * 
     * @param reminderTime the new reminder time (may be null)
     */
    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
        touch();
    }

    /**
     * Gets the category/project of this task.
     * 
     * @return the category/project name (may be null)
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category/project of this task.
     * 
     * <p><b>Postconditions:</b> category is updated and updatedAt timestamp is set
     * 
     * @param category the new category/project name (may be null)
     */
    public void setCategory(String category) {
        this.category = category;
        touch();
    }

    /**
     * Gets a copy of the dependency list for this task.
     * 
     * @return a new list containing all dependency task IDs (never null, may be empty)
     */
    public List<String> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    /**
     * Sets the dependencies for this task.
     * 
     * <p><b>Postconditions:</b> dependencies are replaced with the new list and updatedAt timestamp is set
     * 
     * @param dependencies the new list of dependency task IDs (may be null, will be treated as empty list)
     */
    public void setDependencies(List<String> dependencies) {
        this.dependencies.clear();
        if (dependencies != null) {
            this.dependencies.addAll(dependencies);
        }
        touch();
    }

    /**
     * Adds a dependency to this task.
     * 
     * <p><b>Preconditions:</b> taskId must be non-null and non-blank
     * 
     * <p><b>Postconditions:</b> dependency is added and updatedAt timestamp is set
     * 
     * @param taskId the ID of the task this task depends on (required, non-null, non-blank)
     * @throws IllegalArgumentException if taskId is null or blank
     */
    public void addDependency(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("Task ID must be non-null and non-blank");
        }
        if (!this.dependencies.contains(taskId)) {
            this.dependencies.add(taskId);
            touch();
        }
    }

    /**
     * Removes a dependency from this task.
     * 
     * <p><b>Postconditions:</b> dependency is removed if it exists and updatedAt timestamp is set
     * 
     * @param taskId the ID of the task to remove from dependencies (may be null)
     */
    public void removeDependency(String taskId) {
        if (this.dependencies.remove(taskId)) {
            touch();
        }
    }

    /**
     * Gets the recurrence type of this task.
     * 
     * @return the recurrence type (never null)
     */
    public RecurrenceType getRecurrenceType() {
        return recurrenceType != null ? recurrenceType : RecurrenceType.NONE;
    }

    /**
     * Sets the recurrence type of this task.
     * 
     * <p><b>Postconditions:</b> recurrenceType is updated and updatedAt timestamp is set
     * 
     * @param recurrenceType the new recurrence type (may be null, will default to NONE)
     */
    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType != null ? recurrenceType : RecurrenceType.NONE;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a snapshot of the current task state for undo/redo operations.
     * 
     * <p><b>Postconditions:</b> A new EditSnapshot is created containing current task state
     * 
     * @return a snapshot of the current task state (never null)
     */
    public EditSnapshot createSnapshot() {
        return new EditSnapshot(title, description, dueDateTime, priority, status);
    }

    /**
     * Restores the task state from a snapshot.
     * 
     * <p><b>Preconditions:</b> snapshot must be non-null
     * 
     * <p><b>Postconditions:</b> Task state is restored and updatedAt timestamp is set
     * 
     * @param snapshot the snapshot to restore from (required, non-null)
     * @throws IllegalArgumentException if snapshot is null
     */
    public void restore(EditSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot must be non-null");
        }
        this.title = snapshot.title;
        this.description = snapshot.description;
        this.dueDateTime = snapshot.dueDateTime;
        this.priority = snapshot.priority;
        this.status = snapshot.status;
        // Note: dueDateString is not in snapshot, will be preserved or can be regenerated
        touch();
    }

    public static class EditSnapshot {
        private final String title;
        private final String description;
        private final LocalDateTime dueDateTime;
        private final TaskPriority priority;
        private final TaskStatus status;

        private EditSnapshot(String title,
                             String description,
                             LocalDateTime dueDateTime,
                             TaskPriority priority,
                             TaskStatus status) {
            this.title = title;
            this.description = description;
            this.dueDateTime = dueDateTime;
            this.priority = priority;
            this.status = status;
        }
    }

    @Override
    public String toString() {
        return title;
    }
}

