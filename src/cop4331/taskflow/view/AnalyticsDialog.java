package cop4331.taskflow.view;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog showing task analytics and statistics.
 * 
 * <p>Displays completion statistics, task counts by status, and
 * completion percentage.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class AnalyticsDialog extends JDialog {

    /**
     * Creates a new AnalyticsDialog.
     * 
     * <p><b>Preconditions:</b> parent and model must be non-null
     * 
     * @param parent the parent window (required, non-null)
     * @param model the task model to analyze (required, non-null)
     */
    public AnalyticsDialog(Window parent, TaskModel model) {
        super(parent, "Task Analytics", ModalityType.APPLICATION_MODAL);
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        List<Task> allTasks = model.getTasks();
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();
        long pendingTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .count();
        long trashedTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.TRASHED)
                .count();
        
        double completionPercentage = totalTasks > 0 
                ? (completedTasks * 100.0 / totalTasks) 
                : 0.0;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Task Statistics");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Total Tasks (let's see how many tasks we're dealing with)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Total Tasks:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(totalTasks)), gbc);
        
        // Completed Tasks (the ones we actually finished - good job!)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Completed:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(completedTasks)), gbc);
        
        // Pending Tasks
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Pending:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(pendingTasks)), gbc);
        
        // Trashed Tasks
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Trashed:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(trashedTasks)), gbc);
        
        // Completion Percentage
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Completion %:"), gbc);
        gbc.gridx = 1;
        JLabel percentageLabel = new JLabel(String.format("%.1f%%", completionPercentage));
        percentageLabel.setFont(percentageLabel.getFont().deriveFont(Font.BOLD));
        panel.add(percentageLabel, gbc);
        
        // Close Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton, gbc);
        
        add(panel);
    }
}

