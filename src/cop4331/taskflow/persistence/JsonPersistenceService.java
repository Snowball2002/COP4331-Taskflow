package cop4331.taskflow.persistence;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskPriority;
import cop4331.taskflow.model.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service for persisting tasks to and loading tasks from JSON files.
 * 
 * <p>This service handles the serialization and deserialization of tasks
 * to/from JSON format for local storage.
 * 
 * <p><b>Preconditions:</b> File paths must be valid and writable for save operations.
 * 
 * <p><b>Postconditions:</b> Tasks are saved to or loaded from JSON format.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class JsonPersistenceService {

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Saves a list of tasks to a JSON file.
     * 
     * <p><b>Preconditions:</b>
     * <ul>
     *   <li>tasks must be non-null</li>
     *   <li>filePath must be non-null</li>
     *   <li>Parent directory of filePath must exist or be creatable</li>
     * </ul>
     * 
     * <p><b>Postconditions:</b> Tasks are written to the specified file in JSON format
     * 
     * @param tasks the list of tasks to save (required, non-null)
     * @param filePath the path to the JSON file (required, non-null)
     * @throws IOException if an I/O error occurs while writing
     * @throws IllegalArgumentException if tasks or filePath is null
     */
    public void save(List<Task> tasks, Path filePath) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks list must be non-null");
        }
        if (filePath == null) {
            throw new IllegalArgumentException("File path must be non-null");
        }

        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            JSONObject jsonTask = new JSONObject();
            jsonTask.put("id", task.getId());
            jsonTask.put("title", task.getTitle());
            jsonTask.put("description", task.getDescription() != null ? task.getDescription() : ""); // Save it all, even the empty ones
            
            if (task.getDueDateTime() != null) {
                jsonTask.put("dueDateTime", task.getDueDateTime().format(DATE_FORMATTER));
            } else {
                jsonTask.put("dueDateTime", JSONObject.NULL);
            }
            // Save the raw due date string for display
            if (task.getDueDateString() != null && !task.getDueDateString().isEmpty()) {
                jsonTask.put("dueDateString", task.getDueDateString());
            } else {
                jsonTask.put("dueDateString", JSONObject.NULL);
            }
            
            jsonTask.put("priority", task.getPriority().name());
            jsonTask.put("status", task.getStatus().name());
            
            JSONArray tagsArray = new JSONArray();
            for (String tag : task.getTags()) {
                tagsArray.put(tag);
            }
            jsonTask.put("tags", tagsArray);
            
            // Save category/project
            if (task.getCategory() != null && !task.getCategory().isEmpty()) {
                jsonTask.put("category", task.getCategory());
            } else {
                jsonTask.put("category", JSONObject.NULL);
            }
            
            // Save dependencies
            JSONArray dependenciesArray = new JSONArray();
            for (String depId : task.getDependencies()) {
                dependenciesArray.put(depId);
            }
            jsonTask.put("dependencies", dependenciesArray);
            
            // Save recurrence type
            jsonTask.put("recurrenceType", task.getRecurrenceType().name());
            
            if (task.getCreatedAt() != null) {
                jsonTask.put("createdAt", task.getCreatedAt().format(DATE_FORMATTER));
            } else {
                jsonTask.put("createdAt", JSONObject.NULL);
            }
            
            if (task.getUpdatedAt() != null) {
                jsonTask.put("updatedAt", task.getUpdatedAt().format(DATE_FORMATTER));
            } else {
                jsonTask.put("updatedAt", JSONObject.NULL);
            }
            
            if (task.getReminderTime() != null) {
                jsonTask.put("reminderTime", task.getReminderTime().format(DATE_FORMATTER));
            } else {
                jsonTask.put("reminderTime", JSONObject.NULL);
            }
            
            jsonArray.put(jsonTask);
        }

        // Ensure parent directory exists
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        Files.writeString(filePath, jsonArray.toString(2));
    }

    /**
     * Loads tasks from a JSON file.
     * 
     * <p><b>Preconditions:</b>
     * <ul>
     *   <li>filePath must be non-null</li>
     *   <li>File must exist and be readable</li>
     *   <li>File must contain valid JSON</li>
     * </ul>
     * 
     * <p><b>Postconditions:</b> Tasks are loaded from the JSON file and returned as a list
     * 
     * @param filePath the path to the JSON file (required, non-null)
     * @return a list of tasks loaded from the file (never null, may be empty)
     * @throws IOException if an I/O error occurs while reading
     * @throws IllegalArgumentException if filePath is null or file doesn't exist
     */
    public List<Task> load(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path must be non-null");
        }
        if (!Files.exists(filePath)) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }

        String content = Files.readString(filePath);
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }

        JSONArray jsonArray = new JSONArray(content);
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonTask = jsonArray.getJSONObject(i);
            
            String id = jsonTask.getString("id");
            String title = jsonTask.getString("title");
            String description = jsonTask.optString("description", "");
            if (description.isEmpty()) {
                description = null;
            }
            
            LocalDateTime dueDateTime = null;
            if (!jsonTask.isNull("dueDateTime")) {
                String dueStr = jsonTask.getString("dueDateTime");
                if (dueStr != null && !dueStr.isEmpty()) {
                    dueDateTime = LocalDateTime.parse(dueStr, DATE_FORMATTER);
                }
            }
            
            TaskPriority priority = TaskPriority.valueOf(jsonTask.getString("priority"));
            TaskStatus status = TaskStatus.valueOf(jsonTask.getString("status"));
            
            List<String> tags = new ArrayList<>();
            if (jsonTask.has("tags") && !jsonTask.isNull("tags")) {
                JSONArray tagsArray = jsonTask.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    tags.add(tagsArray.getString(j));
                }
            }
            
            LocalDateTime reminderTime = null;
            if (jsonTask.has("reminderTime") && !jsonTask.isNull("reminderTime")) {
                String reminderStr = jsonTask.getString("reminderTime");
                if (reminderStr != null && !reminderStr.isEmpty()) {
                    reminderTime = LocalDateTime.parse(reminderStr, DATE_FORMATTER);
                }
            }
            
            Task task = new Task(id, title, description, dueDateTime, priority, status, tags, reminderTime);
            
            // Load the raw due date string if available
            if (jsonTask.has("dueDateString") && !jsonTask.isNull("dueDateString")) {
                String dueDateString = jsonTask.optString("dueDateString", null);
                if (dueDateString != null && !dueDateString.isEmpty()) {
                    task.setDueDateString(dueDateString);
                }
            }
            
            // Load category/project if available
            if (jsonTask.has("category") && !jsonTask.isNull("category")) {
                String category = jsonTask.optString("category", null);
                if (category != null && !category.isEmpty()) {
                    task.setCategory(category);
                }
            }
            
            // Load dependencies if available
            if (jsonTask.has("dependencies") && !jsonTask.isNull("dependencies")) {
                JSONArray dependenciesArray = jsonTask.getJSONArray("dependencies");
                List<String> dependencies = new ArrayList<>();
                for (int j = 0; j < dependenciesArray.length(); j++) {
                    dependencies.add(dependenciesArray.getString(j));
                }
                task.setDependencies(dependencies);
            }
            
            // Load recurrence type if available
            if (jsonTask.has("recurrenceType") && !jsonTask.isNull("recurrenceType")) {
                String recurrenceStr = jsonTask.optString("recurrenceType", "NONE");
                try {
                    task.setRecurrenceType(Task.RecurrenceType.valueOf(recurrenceStr));
                } catch (IllegalArgumentException e) {
                    task.setRecurrenceType(Task.RecurrenceType.NONE);
                }
            }
            
            tasks.add(task);
        }

        return tasks;
    }
}

