package cop4331.taskflow.command;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;
import cop4331.taskflow.model.TaskStatus;

import java.util.Optional;

/**
 * Command for moving a task to trash.
 * 
 * <p><b>Preconditions:</b> model and taskId must be non-null
 * 
 * <p><b>Postconditions:</b> Task status is set to TRASHED on execute, restored on undo
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class DeleteTaskCommand implements Command {

    private final TaskModel model;
    private final String taskId;
    private Task backup;

    public DeleteTaskCommand(TaskModel model, String taskId) {
        this.model = model;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        Optional<Task> opt = model.findById(taskId);
        opt.ifPresent(task -> {
            backup = task;
            model.moveToTrash(taskId);
        });
    }

    @Override
    public void undo() {
        if (backup != null) {
            backup.setStatus(TaskStatus.PENDING);
        }
    }
}

