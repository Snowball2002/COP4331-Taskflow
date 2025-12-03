package cop4331.taskflow.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortByDueDateStrategy implements TaskSortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> copy = new ArrayList<>(tasks);
        copy.sort(Comparator.comparing(Task::getDueDateTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return copy;
    }
}

