package cop4331.taskflow.command;

/**
 * Command pattern interface.
 */
public interface Command {

    void execute();

    void undo();
}

