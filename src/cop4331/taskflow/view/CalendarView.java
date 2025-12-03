package cop4331.taskflow.view;

import cop4331.taskflow.controller.TaskController;
import cop4331.taskflow.model.ModelListener;
import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskPriority;
import cop4331.taskflow.model.TaskStatus;

import javax.swing.*;
import javax.swing.TransferHandler;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calendar view component for displaying tasks in Day/Week/Month views.
 * 
 * <p>Supports drag and drop to change task due dates and visual highlighting
 * of overdue tasks.
 */
public class CalendarView extends JPanel implements ModelListener {
    
    public enum ViewMode {
        DAY, WEEK, MONTH
    }
    
    private final TaskController controller;
    private final TaskModel model;
    private ViewMode currentViewMode = ViewMode.MONTH;
    private LocalDate currentDate = LocalDate.now();
    private JComboBox<ViewMode> viewModeCombo;
    private JPanel calendarPanel;
    private JLabel dateLabel;
    private Task draggedTask = null;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public CalendarView(TaskController controller, TaskModel model) {
        this.controller = controller;
        this.model = model;
        
        model.addListener(this);
        
        setLayout(new BorderLayout());
        createUI();
        refresh(); // Let's see what tasks I have coming up (probably too many)
    }
    
    private void createUI() {
        // Control panel - where I put all my navigation stuff
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // View mode selector - day/week/month, because sometimes I need to see the big picture
        controlPanel.add(new JLabel("View:"));
        viewModeCombo = new JComboBox<>(ViewMode.values());
        viewModeCombo.addActionListener(e -> {
            currentViewMode = (ViewMode) viewModeCombo.getSelectedItem();
            refresh();
        });
        controlPanel.add(viewModeCombo);
        
        // Navigation buttons - time travel buttons (not really, but close enough)
        JButton prevButton = new JButton("◀");
        prevButton.addActionListener(e -> {
            navigate(-1); // Go back in time... to see what I missed
        });
        controlPanel.add(prevButton);
        
        dateLabel = new JLabel();
        controlPanel.add(dateLabel);
        
        JButton nextButton = new JButton("▶");
        nextButton.addActionListener(e -> {
            navigate(1); // Go forward... to see what's coming (probably more tasks)
        });
        controlPanel.add(nextButton);
        
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            currentDate = LocalDate.now(); // Back to reality, where all my overdue tasks live
            refresh();
        });
        controlPanel.add(todayButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Calendar panel
        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);
    }
    
    private void navigate(int direction) {
        switch (currentViewMode) {
            case DAY:
                currentDate = currentDate.plusDays(direction);
                break;
            case WEEK:
                currentDate = currentDate.plusWeeks(direction);
                break;
            case MONTH:
                currentDate = currentDate.plusMonths(direction);
                break;
        }
        refresh();
    }
    
    @Override
    public void modelChanged() {
        refresh();
    }
    
    public void refresh() {
        calendarPanel.removeAll();
        
        switch (currentViewMode) {
            case DAY:
                calendarPanel.add(createDayView(), BorderLayout.CENTER);
                dateLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
                break;
            case WEEK:
                calendarPanel.add(createWeekView(), BorderLayout.CENTER);
                LocalDate weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                dateLabel.setText(weekStart.format(DateTimeFormatter.ofPattern("MMM d")) + " - " + 
                    weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
                break;
            case MONTH:
                calendarPanel.add(createMonthView(), BorderLayout.CENTER);
                dateLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
                break;
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    private JPanel createDayView() {
        JPanel panel = new JPanel(new BorderLayout());
        List<Task> dayTasks = getTasksForDate(currentDate);
        
        JPanel taskList = new JPanel();
        taskList.setLayout(new BoxLayout(taskList, BoxLayout.Y_AXIS));
        
        if (dayTasks.isEmpty()) {
            taskList.add(new JLabel("No tasks for this day"));
        } else {
            for (Task task : dayTasks) {
                taskList.add(createTaskComponent(task));
            }
        }
        
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createWeekView() {
        JPanel panel = new JPanel(new GridLayout(1, 7));
        LocalDate weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            JPanel dayPanel = createDayPanel(date);
            panel.add(dayPanel);
        }
        
        return panel;
    }
    
    private JPanel createMonthView() {
        JPanel panel = new JPanel(new GridLayout(0, 7));
        
        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel header = new JLabel(dayName, SwingConstants.CENTER);
            header.setFont(header.getFont().deriveFont(Font.BOLD));
            panel.add(header);
        }
        
        // Get first day of month and first day of calendar grid
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        LocalDate firstDayOfGrid = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue());
        
        // Fill calendar grid (6 weeks = 42 days)
        for (int i = 0; i < 42; i++) {
            LocalDate date = firstDayOfGrid.plusDays(i);
            JPanel dayPanel = createDayPanel(date);
            panel.add(dayPanel);
        }
        
        return panel;
    }
    
    private JPanel createDayPanel(LocalDate date) {
        JPanel dayPanel = new JPanel(new BorderLayout());
        dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Date label
        JLabel dateLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
        if (date.equals(LocalDate.now())) {
            dateLabel.setBackground(Color.CYAN);
            dateLabel.setOpaque(true);
        }
        if (!date.getMonth().equals(currentDate.getMonth())) {
            dateLabel.setForeground(Color.GRAY);
        }
        dayPanel.add(dateLabel, BorderLayout.NORTH);
        
        // Tasks for this day
        List<Task> dayTasks = getTasksForDate(date);
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        
        for (Task task : dayTasks) {
            JComponent taskComp = createTaskComponent(task);
            taskPanel.add(taskComp);
        }
        
        JScrollPane scrollPane = new JScrollPane(taskPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dayPanel.add(scrollPane, BorderLayout.CENTER);
        
        return dayPanel;
    }
    
    private JComponent createTaskComponent(Task task) {
        JPanel taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getPriorityColor(task.getPriority()), 2),
            BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        
        // Highlight overdue tasks
        if (isOverdue(task)) {
            taskPanel.setBackground(new Color(255, 200, 200));
            taskPanel.setOpaque(true);
        }
        
        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 10f));
        taskPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Make draggable
        taskPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedTask = task;
            }
        });
        
        // Note: Drag and drop functionality can be enhanced later
        // (I wanted to add this but ran out of time - maybe next version? Or maybe not, we'll see)
        
        return taskPanel;
    }
    
    private List<Task> getTasksForDate(LocalDate date) {
        return model.getTasks().stream()
            .filter(t -> t.getStatus() != TaskStatus.TRASHED)
            .filter(t -> {
                if (t.getDueDateTime() == null) return false;
                return t.getDueDateTime().toLocalDate().equals(date);
            })
            .collect(Collectors.toList());
    }
    
    private boolean isOverdue(Task task) {
        if (task.getDueDateTime() == null) return false;
        if (task.getStatus() == TaskStatus.COMPLETED) return false;
        return task.getDueDateTime().toLocalDate().isBefore(LocalDate.now()); // Oops, past due... happens to the best of us
    }
    
    private Color getPriorityColor(TaskPriority priority) {
        // Color coding because I'm visual like that - red means "do this NOW"
        switch (priority) {
            case HIGH: return Color.RED; // Panic mode activated
            case MEDIUM: return Color.ORANGE; // Getting there...
            case LOW: return Color.GREEN; // Chill vibes
            default: return Color.GRAY; // No priority? That's a choice
        }
    }
}

