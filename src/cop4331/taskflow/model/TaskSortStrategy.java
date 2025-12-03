package cop4331.taskflow.model;

import java.util.List;

public interface TaskSortStrategy {
    List<Task> sort(List<Task> tasks);
}

