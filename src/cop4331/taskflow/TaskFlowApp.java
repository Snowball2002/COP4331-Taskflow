package cop4331.taskflow;

import cop4331.taskflow.command.CommandManager;
import cop4331.taskflow.controller.TaskController;
import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.ModelListener;
import cop4331.taskflow.persistence.JsonPersistenceService;
import cop4331.taskflow.reminder.ReminderService;
import cop4331.taskflow.view.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main application entry point for TaskFlow.
 * 
 * <p>This class initializes the MVC architecture, loads persisted data,
 * starts the reminder service, and launches the Swing UI.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class TaskFlowApp {

    private static final Path DATA_FILE = Paths.get("taskflow_data.json");
    private static JsonPersistenceService persistenceService;
    private static ReminderService reminderService;
    private static TaskModel model;

    /**
     * Entry point for TaskFlow.
     * 
     * <p>Launches the Swing UI on the Event Dispatch Thread, loads persisted data,
     * and initializes all services.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize services
                persistenceService = new JsonPersistenceService();
                model = new TaskModel();
                
                // Load persisted data (hopefully my tasks are still there!)
                loadData();
                
                // Initialize reminder service
                reminderService = new ReminderService(model);
                
                // Show missed reminders on startup
                reminderService.showMissedReminders();
                
                // Set up auto-save on model changes
                model.addListener(() -> saveData());
                
                CommandManager commandManager = CommandManager.getInstance();
                TaskController controller = new TaskController(model, commandManager);

                MainFrame frame = new MainFrame(controller, model, TaskFlowApp::saveData);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    /**
     * Loads tasks from the persistence file.
     * 
     * <p><b>Postconditions:</b> Tasks are loaded into the model if file exists
     */
    private static void loadData() {
        try {
            List<Task> tasks = persistenceService.load(DATA_FILE);
            for (Task task : tasks) {
                model.addTask(task);
            }
        } catch (IOException e) {
            // File doesn't exist or can't be read - start with empty model
            // (This is fine, we all start somewhere... like my task list)
            System.out.println("No existing data file found, starting with empty model");
        }
    }

    /**
     * Saves tasks to the persistence file.
     * 
     * <p><b>Postconditions:</b> All tasks are saved to JSON file
     */
    private static void saveData() {
        try {
            List<Task> tasks = model.getTasks();
            persistenceService.save(tasks, DATA_FILE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error saving data: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

