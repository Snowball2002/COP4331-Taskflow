package cop4331.taskflow.view;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskPriority;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TaskFormDialog extends JDialog {

    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextField dueDateField;
    private final JComboBox<TaskPriority> priorityCombo;
    private final JTextField categoryField;
    private final JComboBox<Task.RecurrenceType> recurrenceCombo;
    private final JCheckBox reminderCheckbox;
    private final JComboBox<String> reminderTimeCombo;
    private boolean confirmed = false;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TaskFormDialog(Window parent, String title) {
        super(parent, title, ModalityType.APPLICATION_MODAL);

        setSize(500, 450); // Increased height to make description field more visible
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        panel.add(titleField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3; // Give it some weight but not too much
        descriptionArea = new JTextArea(4, 20); // Made it 4 rows instead of 5
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(300, 80)); // Set preferred size so it's visible
        descriptionScroll.setMinimumSize(new Dimension(300, 60)); // Set minimum size
        panel.add(descriptionScroll, gbc);

        // Due Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0; // Reset weighty for remaining fields
        panel.add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dueDateField = new JTextField(20);
        panel.add(dueDateField, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        priorityCombo = new JComboBox<>(TaskPriority.values());
        panel.add(priorityCombo, gbc);

        // Category/Project
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Category/Project:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        categoryField = new JTextField(20);
        panel.add(categoryField, gbc);

        // Recurrence
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Recurrence:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        recurrenceCombo = new JComboBox<>(Task.RecurrenceType.values());
        panel.add(recurrenceCombo, gbc);

        // Reminder
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        reminderCheckbox = new JCheckBox("Set Reminder");
        panel.add(reminderCheckbox, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        reminderTimeCombo = new JComboBox<>(new String[]{
            "5 minutes before",
            "30 minutes before",
            "1 hour before",
            "1 day before"
        });
        reminderTimeCombo.setEnabled(false);
        // Load default reminder time preference
        try {
            String defaultReminder = cop4331.taskflow.settings.UserPreferences.getInstance().getDefaultReminderTime();
            reminderTimeCombo.setSelectedItem(defaultReminder);
        } catch (Exception e) {
            // Use default
        }
        panel.add(reminderTimeCombo, gbc);
        
        // Now set up the action listener after reminderTimeCombo is initialized
        reminderCheckbox.addActionListener(e -> {
            reminderTimeCombo.setEnabled(reminderCheckbox.isSelected());
            // Save default reminder time preference when user selects one
            if (reminderCheckbox.isSelected() && reminderTimeCombo.getSelectedItem() != null) {
                cop4331.taskflow.settings.UserPreferences.getInstance()
                    .setDefaultReminderTime((String) reminderTimeCombo.getSelectedItem());
            }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            confirmed = true; // User clicked OK, we good to go!
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false; // User changed their mind, that's okay
            dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    public void setInitialValues(String title, String description, LocalDateTime dueDateTime, TaskPriority priority) {
        titleField.setText(title);
        descriptionArea.setText(description);
        if (dueDateTime != null) {
            dueDateField.setText(FORMATTER.format(dueDateTime));
        }
        priorityCombo.setSelectedItem(priority);
    }
    
    public void setCategory(String category) {
        if (categoryField != null) {
            categoryField.setText(category != null ? category : "");
        }
    }
    
    public String getCategory() {
        return categoryField != null ? categoryField.getText().trim() : null;
    }
    
    public void setRecurrenceType(Task.RecurrenceType recurrenceType) {
        if (recurrenceCombo != null) {
            recurrenceCombo.setSelectedItem(recurrenceType != null ? recurrenceType : Task.RecurrenceType.NONE);
        }
    }
    
    public Task.RecurrenceType getRecurrenceType() {
        return recurrenceCombo != null ? (Task.RecurrenceType) recurrenceCombo.getSelectedItem() : Task.RecurrenceType.NONE;
    }
    
    public LocalDateTime getReminderTime() {
        if (reminderCheckbox == null || !reminderCheckbox.isSelected() || dueDateField == null) {
            return null;
        }
        
        LocalDateTime dueDateTime = getDueDateTime();
        if (dueDateTime == null) {
            return null;
        }
        
        String reminderOption = (String) reminderTimeCombo.getSelectedItem();
        if (reminderOption == null) return null;
        
        switch (reminderOption) {
            case "5 minutes before":
                return dueDateTime.minusMinutes(5);
            case "30 minutes before":
                return dueDateTime.minusMinutes(30);
            case "1 hour before":
                return dueDateTime.minusHours(1);
            case "1 day before":
                return dueDateTime.minusDays(1);
            default:
                return null;
        }
    }
    
    public void setReminderTime(LocalDateTime reminderTime) {
        if (reminderTime != null && reminderCheckbox != null) {
            reminderCheckbox.setSelected(true);
            reminderTimeCombo.setEnabled(true);
            // Try to determine which option matches
            LocalDateTime dueDateTime = getDueDateTime();
            if (dueDateTime != null) {
                long minutesBefore = java.time.Duration.between(reminderTime, dueDateTime).toMinutes();
                if (minutesBefore == 5) {
                    reminderTimeCombo.setSelectedItem("5 minutes before");
                } else if (minutesBefore == 30) {
                    reminderTimeCombo.setSelectedItem("30 minutes before");
                } else if (minutesBefore == 60) {
                    reminderTimeCombo.setSelectedItem("1 hour before");
                } else if (minutesBefore == 1440) {
                    reminderTimeCombo.setSelectedItem("1 day before");
                }
            }
        }
    }

    public void setDueDateString(String dueDateString) {
        if (dueDateString != null) {
            dueDateField.setText(dueDateString);
        }
    }

    public String getTitleField() {
        return titleField.getText();
    }

    public String getDescriptionField() {
        return descriptionArea.getText();
    }

    public LocalDateTime getDueDateTime() {
        String text = dueDateField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        // Try to parse, but if it fails, return null (we'll store the string separately)
        try {
            return LocalDateTime.parse(text, FORMATTER);
        } catch (DateTimeParseException e) {
            return null; // Can't parse, but we'll store the string
        }
    }

    public String getDueDateString() {
        return dueDateField.getText().trim();
    }

    public TaskPriority getPriority() {
        return (TaskPriority) priorityCombo.getSelectedItem();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

