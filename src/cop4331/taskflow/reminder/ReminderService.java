package cop4331.taskflow.reminder;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;

import javax.swing.*;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Service for managing task reminders using Swing Timer.
 * 
 * <p>This service checks for tasks with reminder times and displays
 * popup notifications when reminders are due. It also tracks missed
 * reminders that occurred while the application was closed.
 * 
 * <p><b>Preconditions:</b> TaskModel must be non-null and properly initialized.
 * 
 * <p><b>Postconditions:</b> Reminders are checked periodically and notifications are shown.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class ReminderService {

    private final TaskModel model;
    private final Timer reminderTimer;
    private final Set<String> shownReminders;
    private static final int CHECK_INTERVAL_MS = 30000; // 30 seconds

    /**
     * Creates a new ReminderService.
     * 
     * <p><b>Preconditions:</b> model must be non-null
     * 
     * <p><b>Postconditions:</b> Reminder service is initialized and timer is started
     * 
     * @param model the task model to monitor (required, non-null)
     * @throws IllegalArgumentException if model is null
     */
    public ReminderService(TaskModel model) {
        if (model == null) {
            throw new IllegalArgumentException("TaskModel must be non-null");
        }
        this.model = model;
        this.shownReminders = new HashSet<>();
        
        this.reminderTimer = new Timer(CHECK_INTERVAL_MS, e -> checkReminders());
        this.reminderTimer.start();
    }

    /**
     * Checks for tasks with due reminders and displays notifications.
     * 
     * <p><b>Postconditions:</b> Popup notifications are shown for tasks with due reminders
     */
    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = model.getTasks();
        
        for (Task task : tasks) {
            if (task.getReminderTime() != null && 
                task.getStatus() != cop4331.taskflow.model.TaskStatus.COMPLETED &&
                task.getStatus() != cop4331.taskflow.model.TaskStatus.TRASHED) {
                
                LocalDateTime reminderTime = task.getReminderTime();
                String reminderKey = task.getId() + "_" + reminderTime.toString();
                
                // Check if reminder is due and hasn't been shown yet
                // (Don't spam the user with the same reminder 1000 times - I learned that lesson)
                if (reminderTime.isBefore(now) || reminderTime.isEqual(now)) {
                    if (!shownReminders.contains(reminderKey)) {
                        showReminder(task); // Time to annoy myself with a reminder!
                        shownReminders.add(reminderKey); // Remember that I already showed this one
                    }
                }
            }
        }
    }

    /**
     * Displays a reminder popup for a task.
     * 
     * <p><b>Preconditions:</b> task must be non-null
     * 
     * <p><b>Postconditions:</b> A popup dialog is displayed to the user
     * 
     * @param task the task to remind about (required, non-null)
     */
    private void showReminder(Task task) {
        if (task == null) {
            return;
        }
        
        String message = String.format("Reminder: %s is due soon!", task.getTitle());
        if (task.getDueDateTime() != null) {
            message += "\nDue: " + task.getDueDateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        
        // Try to show system tray notification first (works even when minimized)
        // I added this so reminders work even when the app is hidden - pretty useful!
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage("");
                TrayIcon trayIcon = new TrayIcon(image, "TaskFlow");
                trayIcon.setImageAutoSize(true);
                if (tray.getTrayIcons().length == 0) {
                    tray.add(trayIcon); // Add to system tray if not already there
                }
                trayIcon.displayMessage("Task Reminder", message, TrayIcon.MessageType.INFO);
                // This shows a notification even when the app is minimized - neat!
            } catch (Exception e) {
                // Fall back to dialog if tray notification fails - sometimes things go wrong
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Task Reminder",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } else {
            // Fall back to dialog if system tray is not supported - some systems don't have it
            JOptionPane.showMessageDialog(
                null,
                message,
                "Task Reminder",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Checks for missed reminders (tasks with reminder times in the past).
     * 
     * <p><b>Postconditions:</b> Returns a list of tasks with missed reminders
     * 
     * @return a list of tasks with missed reminders (never null, may be empty)
     */
    public List<Task> getMissedReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> missed = new ArrayList<>();
        List<Task> tasks = model.getTasks();
        
        for (Task task : tasks) {
            if (task.getReminderTime() != null && 
                task.getReminderTime().isBefore(now) &&
                task.getStatus() != cop4331.taskflow.model.TaskStatus.COMPLETED &&
                task.getStatus() != cop4331.taskflow.model.TaskStatus.TRASHED) {
                missed.add(task);
            }
        }
        
        return missed;
    }

    /**
     * Displays missed reminders in a popup dialog on startup.
     * 
     * <p><b>Postconditions:</b> A popup dialog is shown if there are missed reminders
     */
    public void showMissedReminders() {
        List<Task> missed = getMissedReminders();
        if (!missed.isEmpty()) {
            StringBuilder message = new StringBuilder("You have " + missed.size() + " missed reminder(s):\n\n");
            for (Task task : missed) {
                message.append("• ").append(task.getTitle());
                if (task.getDueDateTime() != null) {
                    message.append(" (Due: ")
                           .append(task.getDueDateTime().format(
                               java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                           .append(")");
                }
                message.append("\n");
            }
            
            JOptionPane.showMessageDialog(
                null,
                message.toString(),
                "Missed Reminders",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    /**
     * Stops the reminder service and its timer.
     * 
     * <p><b>Postconditions:</b> Timer is stopped and service is shut down
     */
    public void stop() {
        if (reminderTimer != null) {
            reminderTimer.stop();
        }
    }
}

