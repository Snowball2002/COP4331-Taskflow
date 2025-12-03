package cop4331.taskflow.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorting strategy that sorts tasks alphabetically by title.
 * 
 * <p>Sometimes I just want things in alphabetical order - it's satisfying, okay?
 */
public class SortAlphabeticallyStrategy implements TaskSortStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sorted = new ArrayList<>(tasks);
        // Sort alphabetically - A to Z, because organization is key (or so I tell myself)
        sorted.sort(Comparator.comparing(Task::getTitle, 
            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return sorted;
    }
}

