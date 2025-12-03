package cop4331.taskflow.view;

import cop4331.taskflow.controller.TaskController;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.settings.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window for TaskFlow.
 * 
 * <p>This class creates and manages the main UI including toolbar, menu bar,
 * and the task list view. It also handles window close events for saving data.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class MainFrame extends JFrame {

    private final TaskListView taskListView;
    private final CalendarView calendarView;
    private final TaskController controller;
    private final TaskModel model;
    private final Runnable onCloseCallback;
    private boolean trashViewMode = false;
    private boolean calendarViewMode = false;
    private JPanel contentPanel;

    /**
     * Creates a new MainFrame.
     * 
     * <p><b>Preconditions:</b> controller and model must be non-null
     * 
     * <p><b>Postconditions:</b> Main window is initialized and displayed
     * 
     * @param controller the task controller (required, non-null)
     * @param model the task model (required, non-null)
     * @param onCloseCallback callback to execute when window closes (may be null)
     */
    public MainFrame(TaskController controller, TaskModel model, Runnable onCloseCallback) {
        super("TaskFlow");
        this.controller = controller;
        this.model = model;
        this.onCloseCallback = onCloseCallback;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);

        // Apply theme
        ThemeManager.getInstance().applyTheme();

        this.taskListView = new TaskListView(controller, model);
        this.calendarView = new CalendarView(controller, model);
        SummaryPanel summaryPanel = new SummaryPanel(model);

        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        add(createToolbar(controller), BorderLayout.NORTH);
        
        // Content panel for switching between views
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(taskListView, "LIST");
        contentPanel.add(calendarView, "CALENDAR");
        add(contentPanel, BorderLayout.CENTER);
        
        // Add summary panel to the right side
        add(summaryPanel, BorderLayout.EAST);
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts(controller);

        // Handle window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                dispose();
                System.exit(0);
            }
        });
    }

    /**
     * Creates the menu bar with View menu.
     * 
     * @return the menu bar (never null)
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu viewMenu = new JMenu("View");
        
        JMenuItem themeToggle = new JMenuItem("Toggle Theme");
        themeToggle.addActionListener(e -> {
            ThemeManager.getInstance().toggleTheme();
            ThemeManager.getInstance().applyTheme();
            SwingUtilities.updateComponentTreeUI(this); // Refresh everything (dark mode activated!)
        });
        viewMenu.add(themeToggle);
        
        JMenuItem listViewItem = new JMenuItem("List View");
        listViewItem.addActionListener(e -> switchToListView());
        viewMenu.add(listViewItem);
        
        JMenuItem calendarViewItem = new JMenuItem("Calendar View");
        calendarViewItem.addActionListener(e -> switchToCalendarView());
        viewMenu.add(calendarViewItem);
        
        viewMenu.addSeparator();
        
        JMenuItem analyticsItem = new JMenuItem("Analytics");
        analyticsItem.addActionListener(e -> {
            AnalyticsDialog dialog = new AnalyticsDialog(this, model);
            dialog.setVisible(true);
        });
        viewMenu.add(analyticsItem);
        
        menuBar.add(viewMenu);
        
        // File menu for import/export
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem exportItem = new JMenuItem("Export Tasks...");
        exportItem.addActionListener(e -> {
            cop4331.taskflow.persistence.ExportImportService service = 
                new cop4331.taskflow.persistence.ExportImportService();
            service.showExportDialog(this, model.getTasks());
        });
        fileMenu.add(exportItem);
        
        JMenuItem importItem = new JMenuItem("Import Tasks...");
        importItem.addActionListener(e -> {
            cop4331.taskflow.persistence.ExportImportService service = 
                new cop4331.taskflow.persistence.ExportImportService();
            java.util.List<cop4331.taskflow.model.Task> imported = service.showImportDialog(this);
            for (cop4331.taskflow.model.Task task : imported) {
                model.addTask(task);
            }
        });
        fileMenu.add(importItem);
        
        menuBar.add(fileMenu);
        
        return menuBar;
    }

    /**
     * Creates the toolbar with action buttons.
     * 
     * @param controller the task controller (required, non-null)
     * @return the toolbar (never null)
     */
    private JToolBar createToolbar(TaskController controller) {
        JToolBar bar = new JToolBar();

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton completeBtn = new JButton("Complete");
        JButton undoBtn = new JButton("Undo");
        JButton redoBtn = new JButton("Redo");
        JButton trashBtn = new JButton("View Trash");
        JButton restoreBtn = new JButton("Restore");
        JButton cloneBtn = new JButton("Clone");
        JButton bulkDeleteBtn = new JButton("Bulk Delete");
        JButton bulkCompleteBtn = new JButton("Bulk Complete");
        JButton dependenciesBtn = new JButton("Dependencies");
        JButton analyticsBtn = new JButton("Analytics");

        addBtn.addActionListener(e -> taskListView.showAddDialog());
        editBtn.addActionListener(e -> taskListView.showEditDialog());
        deleteBtn.addActionListener(e -> taskListView.deleteSelectedTask());
        completeBtn.addActionListener(e -> taskListView.completeSelectedTask());
        undoBtn.addActionListener(e -> {
            controller.undo();
            taskListView.refresh();
        });
        redoBtn.addActionListener(e -> {
            controller.redo();
            taskListView.refresh();
        });
        
        trashBtn.addActionListener(e -> {
            trashViewMode = !trashViewMode;
            taskListView.setTrashView(trashViewMode);
            trashBtn.setText(trashViewMode ? "View All" : "View Trash");
            restoreBtn.setEnabled(trashViewMode);
        });
        
        restoreBtn.addActionListener(e -> taskListView.restoreSelectedTask());
        restoreBtn.setEnabled(false);
        
        cloneBtn.addActionListener(e -> taskListView.cloneSelectedTask());
        cloneBtn.setToolTipText("Clone selected task (Ctrl+D)");
        
        bulkDeleteBtn.addActionListener(e -> taskListView.bulkDeleteSelected());
        bulkDeleteBtn.setToolTipText("Delete multiple selected tasks");
        
        bulkCompleteBtn.addActionListener(e -> taskListView.bulkCompleteSelected());
        bulkCompleteBtn.setToolTipText("Mark multiple selected tasks as completed");
        
        dependenciesBtn.addActionListener(e -> taskListView.showDependenciesDialog());
        dependenciesBtn.setToolTipText("Manage task dependencies");
        
        analyticsBtn.addActionListener(e -> {
            AnalyticsDialog dialog = new AnalyticsDialog(this, model);
            dialog.setVisible(true);
        });
        analyticsBtn.setToolTipText("View task statistics and analytics");

        bar.add(addBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(completeBtn);
        bar.addSeparator();
        bar.add(undoBtn);
        bar.add(redoBtn);
        bar.addSeparator();
        bar.add(trashBtn);
        bar.add(restoreBtn);
        bar.addSeparator();
        bar.add(cloneBtn);
        bar.add(bulkDeleteBtn);
        bar.add(bulkCompleteBtn);
        bar.addSeparator();
        bar.add(dependenciesBtn);
        bar.addSeparator();
        bar.add(analyticsBtn);
        
        // Add tooltips to all buttons
        addBtn.setToolTipText("Add new task (Ctrl+N)");
        editBtn.setToolTipText("Edit selected task (Ctrl+E)");
        deleteBtn.setToolTipText("Delete selected task (Delete)");
        completeBtn.setToolTipText("Mark task as completed (Ctrl+Enter)");
        undoBtn.setToolTipText("Undo last action (Ctrl+Z)");
        redoBtn.setToolTipText("Redo last undone action (Ctrl+Y)");
        trashBtn.setToolTipText("Toggle trash view");
        restoreBtn.setToolTipText("Restore task from trash");

        return bar;
    }
    
    private void switchToListView() {
        calendarViewMode = false;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "LIST");
    }
    
    private void switchToCalendarView() {
        calendarViewMode = true;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "CALENDAR");
    }
    
    /**
     * Sets up keyboard shortcuts for common actions.
     * 
     * <p>I added these because I'm lazy and don't want to click buttons all the time.
     */
    private void setupKeyboardShortcuts(TaskController controller) {
        // Get root pane for key bindings - this is how I set up keyboard shortcuts
        JRootPane rootPane = getRootPane();
        
        // Ctrl+N: Add task - standard "new" shortcut, because why not?
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "addTask");
        rootPane.getActionMap().put("addTask", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                taskListView.showAddDialog();
            }
        });
        
        // Ctrl+E: Edit task - "E" for edit, makes sense to me
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "editTask");
        rootPane.getActionMap().put("editTask", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                taskListView.showEditDialog();
            }
        });
        
        // Delete: Delete task - obvious choice, everyone expects this to work
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteTask");
        rootPane.getActionMap().put("deleteTask", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                taskListView.deleteSelectedTask();
            }
        });
        
        // Ctrl+Enter: Complete task - because Enter alone would be too easy to hit by accident
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "completeTask");
        rootPane.getActionMap().put("completeTask", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                taskListView.completeSelectedTask();
            }
        });
        
        // Ctrl+Z: Undo - standard undo shortcut, everyone knows this one
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "undo");
        rootPane.getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.undo();
                taskListView.refresh();
            }
        });
        
        // Ctrl+Y: Redo - standard redo shortcut (though some apps use Ctrl+Shift+Z)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), "redo");
        rootPane.getActionMap().put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                controller.redo();
                taskListView.refresh();
            }
        });
        
        // Ctrl+D: Clone task - "D" for duplicate, I guess? (Actually I just picked a key that wasn't taken)
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "cloneTask");
        rootPane.getActionMap().put("cloneTask", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                taskListView.cloneSelectedTask();
            }
        });
    }
}

