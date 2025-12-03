package cop4331.taskflow.command;

import cop4331.taskflow.model.Task;
import cop4331.taskflow.model.TaskModel;

/**
 * Command for adding a new task to the model.
 * 
 * <p><b>Preconditions:</b> model and task must be non-null
 * 
 * <p><b>Postconditions:</b> Task is added to model on execute, removed on undo
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class AddTaskCommand implements Command {

    private final TaskModel model;
    private final Task task;

    /**
     * Creates a new AddTaskCommand.
     * 
     * @param model the task model (required, non-null)
     * @param task the task to add (required, non-null)
     */
    public AddTaskCommand(TaskModel model, Task task) {
        this.model = model;
        this.task = task;
    }

    /**
     * Executes the command by adding the task to the model.
     * 
     * <p><b>Postconditions:</b> Task is added to the model
     */
    @Override
    public void execute() {
        model.addTask(task); // Add the task to the model (one more task to the pile!)
        // Note: dueDateString should be set before creating the command
    }

    /**
     * Undoes the command by removing the task from the model.
     * 
     * <p><b>Postconditions:</b> Task is removed from the model
     */
    @Override
    public void undo() {
        model.deleteTask(task.getId()); // Oops, take that back! (Undo functionality)
    }
}

