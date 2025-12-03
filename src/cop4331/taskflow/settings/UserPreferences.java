package cop4331.taskflow.settings;

import cop4331.taskflow.model.SortByDueDateStrategy;
import cop4331.taskflow.model.SortByPriorityStrategy;
import cop4331.taskflow.model.SortByCreationTimeStrategy;
import cop4331.taskflow.model.SortAlphabeticallyStrategy;
import cop4331.taskflow.model.TaskSortStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * Manages user preferences including sort order and default reminder time.
 */
public class UserPreferences {
    
    private static final Path PREFERENCES_FILE = Paths.get("taskflow_preferences.json");
    private static UserPreferences instance;
    
    private String defaultSortStrategy = "Sort by Due Date";
    private String defaultReminderTime = "30 minutes before";
    
    private UserPreferences() {
        loadPreferences(); // Load my preferences (because I'm picky about how things work)
    }
    
    public static UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences(); // Singleton pattern - only one instance of my preferences
        }
        return instance;
    }
    
    public String getDefaultSortStrategy() {
        return defaultSortStrategy;
    }
    
    public void setDefaultSortStrategy(String strategy) {
        this.defaultSortStrategy = strategy;
        savePreferences(); // Save it so I don't have to set it again next time
    }
    
    public String getDefaultReminderTime() {
        return defaultReminderTime; // How early I want to be reminded (usually 30 min, but sometimes I change it)
    }
    
    public void setDefaultReminderTime(String reminderTime) {
        this.defaultReminderTime = reminderTime;
        savePreferences(); // Remember this for next time
    }
    
    public TaskSortStrategy getSortStrategyInstance() {
        switch (defaultSortStrategy) {
            case "Sort by Priority":
                return new SortByPriorityStrategy();
            case "Sort by Creation Time":
                return new SortByCreationTimeStrategy();
            case "Sort Alphabetically":
                return new SortAlphabeticallyStrategy();
            default:
                return new SortByDueDateStrategy();
        }
    }
    
    private void loadPreferences() {
        try {
            if (Files.exists(PREFERENCES_FILE)) {
                String content = Files.readString(PREFERENCES_FILE);
                JSONObject json = new JSONObject(content);
                if (json.has("defaultSortStrategy")) {
                    defaultSortStrategy = json.getString("defaultSortStrategy"); // Load my preferred sort
                }
                if (json.has("defaultReminderTime")) {
                    defaultReminderTime = json.getString("defaultReminderTime"); // Load my reminder preference
                }
            }
        } catch (Exception e) {
            // Use defaults - if loading fails, just use the defaults I set up
        }
    }
    
    private void savePreferences() {
        try {
            JSONObject json = new JSONObject();
            json.put("defaultSortStrategy", defaultSortStrategy);
            json.put("defaultReminderTime", defaultReminderTime);
            Files.writeString(PREFERENCES_FILE, json.toString(2)); // Save my preferences so I don't lose them
        } catch (IOException e) {
            // Silently fail - if saving fails, oh well, I'll just set it again next time
        }
    }
}

