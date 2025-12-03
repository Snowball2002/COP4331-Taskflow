package cop4331.taskflow.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorting strategy that sorts tasks by creation time (newest first).
 * 
 * <p>I made this so I can see my newest tasks first - sometimes I forget what I just added!
 */
public class SortByCreationTimeStrategy implements TaskSortStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sorted = new ArrayList<>(tasks);
        // Sort by creation time, newest first - because I want to see what I just added
        sorted.sort(Comparator.comparing(Task::getCreatedAt, 
            Comparator.nullsLast(Comparator.reverseOrder())));
        return sorted;
    }
}

