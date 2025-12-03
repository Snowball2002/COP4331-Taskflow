package cop4331.taskflow.view;

import cop4331.taskflow.controller.TaskController;
import cop4331.taskflow.model.ModelListener;
import cop4331.taskflow.model.SortByDueDateStrategy;
import cop4331.taskflow.model.SortByPriorityStrategy;
import cop4331.taskflow.model.SortByCreationTimeStrategy;
import cop4331.taskflow.model.SortAlphabeticallyStrategy;
import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskPriority;
import cop4331.taskflow.model.TaskStatus;
import cop4331.taskflow.settings.UserPreferences;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View component for displaying and managing tasks in a table.
 * 
 * <p>Implements the Observer pattern to automatically refresh when the model changes.
 * Supports filtering by status (all tasks or trash only) and sorting strategies.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class TaskListView extends JPanel implements ModelListener {

    private final TaskController controller;
    private final TaskModel model;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private boolean showTrashOnly = false;
    private JComboBox<String> sortComboBox;
    private JTextField searchField;
    private JComboBox<String> filterCategoryCombo;
    private JComboBox<TaskPriority> filterPriorityCombo;
    private JComboBox<String> filterTagCombo;
    private JTextField filterDueDateField;
    private String searchQuery = "";
    private String filterCategory = null;
    private TaskPriority filterPriority = null;
    private String filterTag = null;
    private LocalDateTime filterDueDate = null;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Creates a new TaskListView.
     * 
     * <p><b>Preconditions:</b> controller and model must be non-null
     * 
     * <p><b>Postconditions:</b> View is initialized and registered as a model listener
     * 
     * @param controller the task controller (required, non-null)
     * @param model the task model (required, non-null)
     */
    public TaskListView(TaskController controller, TaskModel model) {
        this.controller = controller;
        this.model = model;

        model.addListener(this);

        setLayout(new BorderLayout());
        
        // Initialize table and tableModel FIRST before any action listeners that call refresh()
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Title", "Due", "Priority", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        // Create control panel with search and filters
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Search field
        controlPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchField.addActionListener(e -> {
            searchQuery = searchField.getText().toLowerCase();
            refresh();
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            private void updateSearch() {
                searchQuery = searchField.getText().toLowerCase();
                refresh();
            }
        });
        controlPanel.add(searchField);
        
        // Filter by category
        controlPanel.add(new JLabel("Category:"));
        filterCategoryCombo = new JComboBox<>(new String[]{"All", ""});
        filterCategoryCombo.addActionListener(e -> {
            String selected = (String) filterCategoryCombo.getSelectedItem();
            filterCategory = "All".equals(selected) ? null : selected;
            refresh();
        });
        controlPanel.add(filterCategoryCombo);
        
        // Filter by priority
        controlPanel.add(new JLabel("Priority:"));
        filterPriorityCombo = new JComboBox<>(new TaskPriority[]{null, TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH});
        filterPriorityCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "All" : value.toString());
                return this;
            }
        });
        filterPriorityCombo.addActionListener(e -> {
            filterPriority = (TaskPriority) filterPriorityCombo.getSelectedItem();
            refresh();
        });
        controlPanel.add(filterPriorityCombo);
        
        // Filter by tag
        controlPanel.add(new JLabel("Tag:"));
        filterTagCombo = new JComboBox<>(new String[]{"All", ""});
        filterTagCombo.addActionListener(e -> {
            String selected = (String) filterTagCombo.getSelectedItem();
            filterTag = "All".equals(selected) ? null : selected;
            refresh();
        });
        controlPanel.add(filterTagCombo);
        
        // Filter by due date
        controlPanel.add(new JLabel("Due Date:"));
        filterDueDateField = new JTextField(12);
        filterDueDateField.setToolTipText("Enter date (yyyy-MM-dd) or leave empty for all");
        filterDueDateField.addActionListener(e -> {
            String text = filterDueDateField.getText().trim();
            if (text.isEmpty()) {
                filterDueDate = null;
            } else {
                try {
                    filterDueDate = LocalDateTime.parse(text + " 00:00", FORMATTER);
                } catch (Exception ex) {
                    filterDueDate = null;
                }
            }
            refresh();
        });
        controlPanel.add(filterDueDateField);
        
        // Sort combo box (Strategy pattern in action - pretty cool if you ask me)
        controlPanel.add(new JLabel("Sort:"));
        sortComboBox = new JComboBox<>(new String[]{
            "Sort by Due Date", 
            "Sort by Priority", 
            "Sort by Creation Time",
            "Sort Alphabetically"
        });
        // Load saved preference
        String savedSort = UserPreferences.getInstance().getDefaultSortStrategy();
        sortComboBox.setSelectedItem(savedSort);
        model.setSortStrategy(UserPreferences.getInstance().getSortStrategyInstance());
        
        sortComboBox.addActionListener(e -> {
            String selected = (String) sortComboBox.getSelectedItem();
            if ("Sort by Due Date".equals(selected)) {
                model.setSortStrategy(new SortByDueDateStrategy());
            } else if ("Sort by Priority".equals(selected)) {
                model.setSortStrategy(new SortByPriorityStrategy());
            } else if ("Sort by Creation Time".equals(selected)) {
                model.setSortStrategy(new SortByCreationTimeStrategy());
            } else if ("Sort Alphabetically".equals(selected)) {
                model.setSortStrategy(new SortAlphabeticallyStrategy());
            }
            // Save preference
            UserPreferences.getInstance().setDefaultSortStrategy(selected);
            refresh(); // Update the table with new sorting
        });
        controlPanel.add(sortComboBox);
        
        add(controlPanel, BorderLayout.NORTH);

        // Table was already initialized above, just add it to the panel
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    /**
     * Refreshes the table display with current tasks.
     * 
     * <p><b>Postconditions:</b> Table is updated to reflect current model state
     */
    public void refresh() {
        // Safety check - don't refresh if tableModel isn't initialized yet
        // (Learned this the hard way when I got a NullPointerException - fun times!)
        if (tableModel == null) {
            return;
        }
        
        List<Task> tasks = model.getTasks(); // Get all my tasks
        
        // Filter by view mode
        if (showTrashOnly) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.TRASHED)
                    .collect(Collectors.toList());
        } else {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() != TaskStatus.TRASHED)
                    .collect(Collectors.toList());
        }
        
        // Apply search filter
        if (searchQuery != null && !searchQuery.isEmpty()) {
            final String query = searchQuery;
            tasks = tasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(query) ||
                            (t.getDescription() != null && t.getDescription().toLowerCase().contains(query)) ||
                            (t.getCategory() != null && t.getCategory().toLowerCase().contains(query)) ||
                            t.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }
        
        // Apply category filter
        if (filterCategory != null && !filterCategory.isEmpty()) {
            final String category = filterCategory;
            tasks = tasks.stream()
                    .filter(t -> category.equals(t.getCategory()))
                    .collect(Collectors.toList());
        }
        
        // Apply priority filter
        if (filterPriority != null) {
            final TaskPriority priority = filterPriority;
            tasks = tasks.stream()
                    .filter(t -> priority == t.getPriority())
                    .collect(Collectors.toList());
        }
        
        // Apply tag filter
        if (filterTag != null && !filterTag.isEmpty()) {
            final String tag = filterTag;
            tasks = tasks.stream()
                    .filter(t -> t.getTags().contains(tag))
                    .collect(Collectors.toList());
        }
        
        // Apply due date filter
        if (filterDueDate != null) {
            final LocalDateTime filterDate = filterDueDate;
            tasks = tasks.stream()
                    .filter(t -> {
                        if (t.getDueDateTime() == null) return false;
                        LocalDateTime taskDate = t.getDueDateTime();
                        return taskDate.toLocalDate().equals(filterDate.toLocalDate());
                    })
                    .collect(Collectors.toList());
        }
        
        // Update category and tag filter options - keep the dropdowns fresh
        updateCategoryFilterOptions();
        updateTagFilterOptions();
        
        tableModel.setRowCount(0); // Clear the table first
        for (Task t : tasks) {
            // Display the raw string if available, otherwise try to format LocalDateTime, otherwise empty
            // (I made it flexible so users can type whatever they want for due dates)
            String dueDisplay = "";
            if (t.getDueDateString() != null && !t.getDueDateString().isEmpty()) {
                dueDisplay = t.getDueDateString(); // Use the raw string they typed
            } else if (t.getDueDateTime() != null) {
                dueDisplay = FORMATTER.format(t.getDueDateTime()); // Format the date properly
            }
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    dueDisplay,
                    t.getPriority(),
                    t.getStatus()
            }); // Add each task to the table
        }
    }

    /**
     * Sets the view mode to show all tasks or trash only.
     * 
     * <p><b>Postconditions:</b> View mode is updated and table is refreshed
     * 
     * @param trashOnly true to show only trashed tasks, false to show all non-trashed tasks
     */
    public void setTrashView(boolean trashOnly) {
        this.showTrashOnly = trashOnly;
        refresh();
    }
    
    /**
     * Updates the category filter combo box with available categories from tasks.
     */
    private void updateCategoryFilterOptions() {
        List<String> categories = model.getTasks().stream()
                .map(Task::getCategory)
                .filter(cat -> cat != null && !cat.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        String currentSelection = (String) filterCategoryCombo.getSelectedItem();
        filterCategoryCombo.removeAllItems();
        filterCategoryCombo.addItem("All");
        for (String cat : categories) {
            filterCategoryCombo.addItem(cat);
        }
        if (currentSelection != null && categories.contains(currentSelection)) {
            filterCategoryCombo.setSelectedItem(currentSelection);
        } else {
            filterCategoryCombo.setSelectedItem("All");
        }
    }
    
    /**
     * Updates the tag filter combo box with available tags from tasks.
     */
    private void updateTagFilterOptions() {
        List<String> tags = model.getTasks().stream()
                .flatMap(t -> t.getTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        String currentSelection = (String) filterTagCombo.getSelectedItem();
        filterTagCombo.removeAllItems();
        filterTagCombo.addItem("All");
        for (String tag : tags) {
            filterTagCombo.addItem(tag);
        }
        if (currentSelection != null && tags.contains(currentSelection)) {
            filterTagCombo.setSelectedItem(currentSelection);
        } else {
            filterTagCombo.setSelectedItem("All");
        }
    }

    @Override
    public void modelChanged() {
        refresh();
    }

    private String getSelectedTaskId() {
        int row = table.getSelectedRow();
        if (row == -1) return null;
        return (String) tableModel.getValueAt(row, 0);
    }

    public void showAddDialog() {
        TaskFormDialog dialog = new TaskFormDialog(SwingUtilities.getWindowAncestor(this), "Add Task");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                String dueDateString = dialog.getDueDateString();
                String taskId = controller.addTask(
                        dialog.getTitleField(),
                        dialog.getDescriptionField(),
                        dialog.getDueDateTime(),
                        dialog.getPriority(),
                        dueDateString,
                        dialog.getCategory(),
                        dialog.getRecurrenceType()
                );
                // Set reminder time if specified
                if (dialog.getReminderTime() != null) {
                    model.findById(taskId).ifPresent(task -> task.setReminderTime(dialog.getReminderTime()));
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showEditDialog() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to edit.");
            return;
        }

        Task task = model.findById(id).orElse(null);
        if (task == null) return;

        TaskFormDialog dialog = new TaskFormDialog(SwingUtilities.getWindowAncestor(this), "Edit Task");
        dialog.setInitialValues(task.getTitle(), task.getDescription(), task.getDueDateTime(), task.getPriority());
        // If we have a raw string, use it instead
        if (task.getDueDateString() != null && !task.getDueDateString().isEmpty()) {
            dialog.setDueDateString(task.getDueDateString());
        }
        if (task.getCategory() != null) {
            dialog.setCategory(task.getCategory());
        }
        dialog.setRecurrenceType(task.getRecurrenceType());
        if (task.getReminderTime() != null) {
            dialog.setReminderTime(task.getReminderTime());
        }
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String dueDateString = dialog.getDueDateString();
            controller.editTask(
                    id,
                    dialog.getTitleField(),
                    dialog.getDescriptionField(),
                    dialog.getDueDateTime(),
                    dueDateString,
                    dialog.getCategory(),
                    dialog.getRecurrenceType()
            );
            // Update reminder time
            if (dialog.getReminderTime() != null) {
                model.findById(id).ifPresent(t -> t.setReminderTime(dialog.getReminderTime()));
            } else {
                model.findById(id).ifPresent(t -> t.setReminderTime(null));
            }
        }
    }

    public void deleteSelectedTask() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to delete.");
            return;
        }
        int res = JOptionPane.showConfirmDialog(this,
                "Move selected task to Trash?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            controller.deleteTask(id);
        }
    }

    public void completeSelectedTask() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to complete.");
            return;
        }
        controller.completeTask(id);
    }

    /**
     * Restores the selected task from trash.
     * 
     * <p><b>Postconditions:</b> Task status is changed from TRASHED to PENDING
     */
    public void restoreSelectedTask() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to restore.");
            return;
        }
        model.reopenTask(id);
    }
    
    /**
     * Clones the selected task.
     */
    public void cloneSelectedTask() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to clone.");
            return;
        }
        try {
            controller.cloneTask(id);
            JOptionPane.showMessageDialog(this, "Task cloned successfully!");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gets all selected task IDs from the table.
     * 
     * @return list of selected task IDs
     */
    public List<String> getSelectedTaskIds() {
        List<String> ids = new ArrayList<>();
        int[] selectedRows = table.getSelectedRows();
        for (int row : selectedRows) {
            String id = (String) tableModel.getValueAt(row, 0);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }
    
    /**
     * Performs bulk delete on selected tasks.
     */
    public void bulkDeleteSelected() {
        List<String> ids = getSelectedTaskIds();
        if (ids.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select tasks to delete.");
            return;
        }
        int res = JOptionPane.showConfirmDialog(this,
                "Move " + ids.size() + " selected task(s) to Trash?", "Confirm Bulk Delete",
                JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            controller.bulkDelete(ids);
        }
    }
    
    /**
     * Performs bulk complete on selected tasks.
     */
    public void bulkCompleteSelected() {
        List<String> ids = getSelectedTaskIds();
        if (ids.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select tasks to complete.");
            return;
        }
        controller.bulkComplete(ids);
    }
    
    /**
     * Shows the dependencies dialog for the selected task.
     */
    public void showDependenciesDialog() {
        String id = getSelectedTaskId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Select a task to manage dependencies.");
            return;
        }
        
        Task task = model.findById(id).orElse(null);
        if (task == null) return;
        
        DependenciesDialog dialog = new DependenciesDialog(
            SwingUtilities.getWindowAncestor(this), task, model);
        dialog.setVisible(true);
    }
}

