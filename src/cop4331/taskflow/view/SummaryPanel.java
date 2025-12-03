package cop4331.taskflow.view;

import cop4331.taskflow.model.ModelListener;
import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskStatus;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Summary panel showing "Today" and "This Week" tasks.
 */
public class SummaryPanel extends JPanel implements ModelListener {
    
    private final TaskModel model;
    private JPanel todayPanel;
    private JPanel weekPanel;
    
    public SummaryPanel(TaskModel model) {
        this.model = model;
        model.addListener(this);
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Upcoming Tasks")); // AKA "Things I should probably do"
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Today section - the tasks that are probably overdue already
        JLabel todayLabel = new JLabel("Today");
        todayLabel.setFont(todayLabel.getFont().deriveFont(Font.BOLD, 14f));
        content.add(todayLabel);
        todayPanel = new JPanel();
        todayPanel.setLayout(new BoxLayout(todayPanel, BoxLayout.Y_AXIS));
        content.add(todayPanel);
        
        content.add(Box.createVerticalStrut(10));
        
        // This Week section - future me's problem
        JLabel weekLabel = new JLabel("This Week");
        weekLabel.setFont(weekLabel.getFont().deriveFont(Font.BOLD, 14f));
        content.add(weekLabel);
        weekPanel = new JPanel();
        weekPanel.setLayout(new BoxLayout(weekPanel, BoxLayout.Y_AXIS));
        content.add(weekPanel);
        
        add(new JScrollPane(content), BorderLayout.CENTER);
        
        refresh(); // Let's see what I'm avoiding today
    }
    
    @Override
    public void modelChanged() {
        refresh();
    }
    
    public void refresh() {
        todayPanel.removeAll();
        weekPanel.removeAll();
        
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);
        
        List<Task> todayTasks = model.getTasks().stream()
            .filter(t -> t.getStatus() != TaskStatus.TRASHED && t.getStatus() != TaskStatus.COMPLETED)
            .filter(t -> {
                if (t.getDueDateTime() == null) return false;
                return t.getDueDateTime().toLocalDate().equals(today);
            })
            .collect(Collectors.toList());
        
        List<Task> weekTasks = model.getTasks().stream()
            .filter(t -> t.getStatus() != TaskStatus.TRASHED && t.getStatus() != TaskStatus.COMPLETED)
            .filter(t -> {
                if (t.getDueDateTime() == null) return false;
                LocalDate dueDate = t.getDueDateTime().toLocalDate();
                return dueDate.isAfter(today) && dueDate.isBefore(weekEnd) || dueDate.equals(weekEnd);
            })
            .collect(Collectors.toList());
        
        if (todayTasks.isEmpty()) {
            todayPanel.add(new JLabel("No tasks due today")); // Rare but beautiful moment
        } else {
            for (Task task : todayTasks) {
                todayPanel.add(createTaskLabel(task)); // Here's what I need to do... eventually
            }
        }
        
        if (weekTasks.isEmpty()) {
            weekPanel.add(new JLabel("No tasks due this week")); // Living the dream
        } else {
            for (Task task : weekTasks) {
                weekPanel.add(createTaskLabel(task)); // Future me will handle these
            }
        }
        
        todayPanel.revalidate();
        weekPanel.revalidate();
    }
    
    private JLabel createTaskLabel(Task task) {
        JLabel label = new JLabel(task.getTitle());
        if (isOverdue(task)) {
            label.setForeground(Color.RED); // Red = panic mode, I should probably do this
            label.setFont(label.getFont().deriveFont(Font.BOLD)); // Make it stand out so I feel guilty
        }
        return label;
    }
    
    private boolean isOverdue(Task task) {
        if (task.getDueDateTime() == null) return false;
        return task.getDueDateTime().toLocalDate().isBefore(LocalDate.now()); // Yep, I missed the deadline... again
    }
}

