package cop4331.taskflow.view;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for managing task dependencies.
 */
public class DependenciesDialog extends JDialog {
    
    private final Task task;
    private final TaskModel model;
    private JList<String> availableTasksList;
    private JList<String> dependenciesList;
    private DefaultListModel<String> dependenciesModel;
    private boolean confirmed = false;
    
    public DependenciesDialog(Window parent, Task task, TaskModel model) {
        super(parent, "Manage Dependencies: " + task.getTitle(), ModalityType.APPLICATION_MODAL);
        this.task = task;
        this.model = model;
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        createUI(); // Building the UI for managing which tasks depend on which other tasks
        loadDependencies(); // Load up what I already set up (if anything)
    }
    
    private void createUI() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Available tasks panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Tasks"));
        
        DefaultListModel<String> availableModel = new DefaultListModel<>();
        for (Task t : model.getTasks()) {
            if (!t.getId().equals(task.getId()) && t.getStatus() != cop4331.taskflow.model.TaskStatus.TRASHED) {
                availableModel.addElement(t.getTitle() + " (" + t.getId() + ")");
            }
        }
        availableTasksList = new JList<>(availableModel);
        leftPanel.add(new JScrollPane(availableTasksList), BorderLayout.CENTER);
        
        // Dependencies panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Dependencies"));
        
        dependenciesModel = new DefaultListModel<>();
        dependenciesList = new JList<>(dependenciesModel);
        rightPanel.add(new JScrollPane(dependenciesList), BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addDepBtn = new JButton("Add →");
        addDepBtn.addActionListener(e -> addDependency());
        JButton removeDepBtn = new JButton("Remove ←");
        removeDepBtn.addActionListener(e -> removeDependency());
        buttonPanel.add(addDepBtn);
        buttonPanel.add(removeDepBtn);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // OK/Cancel buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            confirmed = true;
            saveDependencies();
            dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        bottomPanel.add(okBtn);
        bottomPanel.add(cancelBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    private void addDependency() {
        String selected = availableTasksList.getSelectedValue();
        if (selected == null) return; // Nothing selected, nothing to do
        
        // Extract task ID from display string - had to parse this out, was annoying but I got it
        String taskId = extractTaskId(selected);
        if (taskId != null && !dependenciesModel.contains(taskId)) {
            dependenciesModel.addElement(selected); // Add it to the dependency list
        }
    }
    
    private void removeDependency() {
        int selectedIndex = dependenciesList.getSelectedIndex();
        if (selectedIndex >= 0) {
            dependenciesModel.remove(selectedIndex); // Remove it - no longer depends on that task
        }
    }
    
    private void loadDependencies() {
        for (String depId : task.getDependencies()) {
            model.findById(depId).ifPresent(depTask -> {
                dependenciesModel.addElement(depTask.getTitle() + " (" + depId + ")");
            });
        }
    }
    
    private void saveDependencies() {
        List<String> dependencies = new ArrayList<>();
        for (int i = 0; i < dependenciesModel.size(); i++) {
            String display = dependenciesModel.getElementAt(i);
            String taskId = extractTaskId(display);
            if (taskId != null) {
                dependencies.add(taskId);
            }
        }
        task.setDependencies(dependencies);
    }
    
    private String extractTaskId(String display) {
        // Parse out the task ID from the display string - had to figure this out the hard way
        int start = display.lastIndexOf("(");
        int end = display.lastIndexOf(")");
        if (start >= 0 && end > start) {
            return display.substring(start + 1, end); // Got it!
        }
        return null; // Couldn't find it, oh well
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}

