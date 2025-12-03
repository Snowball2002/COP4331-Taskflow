package cop4331.taskflow.command;

import java.util.Stack;

/**
 * Manages undo/redo stacks using the Command pattern.
 * 
 * <p>This class implements the Singleton pattern to ensure a single instance
 * manages all command execution, undo, and redo operations throughout the application.
 * 
 * <p><b>Preconditions:</b> Commands passed to executeCommand must be non-null and executable.
 * 
 * <p><b>Postconditions:</b> All executed commands are added to the undo stack.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    private CommandManager() {
    }

    /**
     * Gets the singleton instance of CommandManager.
     * 
     * @return the CommandManager instance (never null)
     */
    public static CommandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Executes a command and adds it to the undo stack.
     * 
     * <p><b>Preconditions:</b> command must be non-null
     * 
     * <p><b>Postconditions:</b> Command is executed, added to undo stack, and redo stack is cleared
     * 
     * @param command the command to execute (required, non-null)
     * @throws IllegalArgumentException if command is null
     */
    public void executeCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command must be non-null");
        }
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Can't redo after a new command - time travel doesn't work that way
    }

    /**
     * Checks if undo is possible.
     * 
     * @return true if there are commands to undo, false otherwise
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if redo is possible.
     * 
     * @return true if there are commands to redo, false otherwise
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Undoes the last executed command.
     * 
     * <p><b>Preconditions:</b> canUndo() must return true
     * 
     * <p><b>Postconditions:</b> Last command is undone and moved to redo stack
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo(); // Ctrl+Z in real life (well, in code)
            redoStack.push(cmd);
        }
    }

    /**
     * Redoes the last undone command.
     * 
     * <p><b>Preconditions:</b> canRedo() must return true
     * 
     * <p><b>Postconditions:</b> Last undone command is re-executed and moved to undo stack
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }
}

