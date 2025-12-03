package cop4331.taskflow.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Model for managing tasks. Notifies views when data changes.
 * 
 * <p>This class implements the Model component of the MVC pattern and uses
 * the Observer pattern to notify registered listeners of changes. It also
 * uses the Strategy pattern for sorting tasks.
 * 
 * <p><b>Preconditions:</b> All public methods that accept IDs require non-null, non-blank IDs.
 * 
 * <p><b>Postconditions:</b> All mutating operations notify registered listeners.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class TaskModel {

    private final List<Task> tasks = new ArrayList<>();
    private final List<ModelListener> listeners = new ArrayList<>();
    private TaskSortStrategy sortStrategy = new SortByDueDateStrategy();

    /**
     * Registers a listener to be notified of model changes.
     * 
     * <p><b>Preconditions:</b> listener must be non-null
     * 
     * <p><b>Postconditions:</b> listener is added to the notification list
     * 
     * @param listener the listener to register (required, non-null)
     * @throws IllegalArgumentException if listener is null
     */
    public void addListener(ModelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must be non-null");
        }
        listeners.add(listener);
    }

    /**
     * Removes a listener from the notification list.
     * 
     * <p><b>Preconditions:</b> listener must be non-null
     * 
     * <p><b>Postconditions:</b> listener is removed from the notification list
     * 
     * @param listener the listener to remove (required, non-null)
     * @throws IllegalArgumentException if listener is null
     */
    public void removeListener(ModelListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener must be non-null");
        }
        listeners.remove(listener);
    }

    /**
     * Notifies all registered listeners of model changes.
     * 
     * <p><b>Postconditions:</b> All registered listeners receive modelChanged() call
     */
    private void notifyListeners() {
        for (ModelListener l : new ArrayList<>(listeners)) {
            l.modelChanged();
        }
    }

    /**
     * Sets the sorting strategy for tasks.
     * 
     * <p><b>Preconditions:</b> strategy must be non-null
     * 
     * <p><b>Postconditions:</b> Sort strategy is updated and listeners are notified
     * 
     * @param strategy the sorting strategy to use (required, non-null)
     * @throws IllegalArgumentException if strategy is null
     */
    public void setSortStrategy(TaskSortStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy must be non-null");
        }
        this.sortStrategy = strategy;
        notifyListeners();
    }

    /**
     * Gets all tasks sorted according to the current sort strategy.
     * 
     * <p><b>Postconditions:</b> Returns a new list (defensive copy) sorted by current strategy
     * 
     * @return a sorted list of all tasks (never null, may be empty)
     */
    public List<Task> getTasks() {
        return sortStrategy.sort(tasks);
    }

    /**
     * Adds a new task to the model.
     * 
     * <p><b>Preconditions:</b> task must be non-null
     * 
     * <p><b>Postconditions:</b> Task is added to the model and listeners are notified
     * 
     * @param task the task to add (required, non-null)
     * @throws IllegalArgumentException if task is null
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must be non-null");
        }
        tasks.add(task);
        notifyListeners(); // (Observer pattern doing its thing)
    }

    /**
     * Finds a task by its unique identifier.
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @return an Optional containing the task if found, empty otherwise
     * @throws IllegalArgumentException if id is null or blank
     */
    public Optional<Task> findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    /**
     * Permanently deletes a task from the model.
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank
     * 
     * <p><b>Postconditions:</b> Task is removed from the model and listeners are notified
     * 
     * @param id the task identifier to delete (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void deleteTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        tasks.removeIf(t -> t.getId().equals(id));
        notifyListeners();
    }

    /**
     * Moves a task to the trash (sets status to TRASHED).
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task status is set to TRASHED and listeners are notified
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void moveToTrash(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        findById(id).ifPresent(t -> t.setStatus(TaskStatus.TRASHED));
        notifyListeners();
    }

    /**
     * Marks a task as completed.
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task status is set to COMPLETED and listeners are notified
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void markCompleted(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        findById(id).ifPresent(t -> t.setStatus(TaskStatus.COMPLETED));
        notifyListeners();
    }

    /**
     * Reopens a task (sets status to PENDING).
     * 
     * <p><b>Preconditions:</b> id must be non-null and non-blank, task must exist
     * 
     * <p><b>Postconditions:</b> Task status is set to PENDING and listeners are notified
     * 
     * @param id the task identifier (required, non-null, non-blank)
     * @throws IllegalArgumentException if id is null or blank
     */
    public void reopenTask(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must be non-null and non-blank");
        }
        findById(id).ifPresent(t -> t.setStatus(TaskStatus.PENDING));
        notifyListeners();
    }

    /**
     * Gets all tasks with a specific status.
     * 
     * <p><b>Preconditions:</b> status must be non-null
     * 
     * @param status the status to filter by (required, non-null)
     * @return a list of tasks with the specified status (never null, may be empty)
     * @throws IllegalArgumentException if status is null
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must be non-null");
        }
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(java.util.stream.Collectors.toList());
    }
}

