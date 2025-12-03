package cop4331.taskflow.command;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskStatus;

import java.util.Optional;

/**
 * Command for marking a task as completed.
 * 
 * <p><b>Preconditions:</b> model and taskId must be non-null
 * 
 * <p><b>Postconditions:</b> Task status is set to COMPLETED on execute, restored on undo
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class CompleteTaskCommand implements Command {

    private final TaskModel model;
    private final String taskId;
    private TaskStatus previousStatus;

    public CompleteTaskCommand(TaskModel model, String taskId) {
        this.model = model;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        Optional<Task> opt = model.findById(taskId);
        opt.ifPresent(task -> {
            previousStatus = task.getStatus();
            model.markCompleted(taskId);
        });
    }

    @Override
    public void undo() {
        if (previousStatus != null) {
            model.findById(taskId).ifPresent(task -> task.setStatus(previousStatus));
        }
    }
}

