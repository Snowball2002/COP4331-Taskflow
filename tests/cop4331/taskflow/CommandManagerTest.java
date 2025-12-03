package cop4331.taskflow;

import cop4331.taskflow.command.*;
import cop4331.taskflow.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * JUnit tests for CommandManager.
 */
public class CommandManagerTest {

    private TaskModel model;
    private CommandManager commandManager;

    @BeforeEach
    public void setUp() {
        model = new TaskModel();
        commandManager = CommandManager.getInstance();
    }

    @Test
    public void testSingleton() {
        CommandManager instance1 = CommandManager.getInstance();
        CommandManager instance2 = CommandManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testExecuteCommand() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        Command addCommand = new AddTaskCommand(model, task);
        
        commandManager.executeCommand(addCommand);
        
        assertEquals(1, model.getTasks().size());
        assertTrue(commandManager.canUndo());
    }

    @Test
    public void testUndo() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        Command addCommand = new AddTaskCommand(model, task);
        
        commandManager.executeCommand(addCommand);
        commandManager.undo();
        
        assertEquals(0, model.getTasks().size());
        assertFalse(commandManager.canUndo());
        assertTrue(commandManager.canRedo());
    }

    @Test
    public void testRedo() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        Command addCommand = new AddTaskCommand(model, task);
        
        commandManager.executeCommand(addCommand);
        commandManager.undo();
        commandManager.redo();
        
        assertEquals(1, model.getTasks().size());
        assertTrue(commandManager.canUndo());
        assertFalse(commandManager.canRedo());
    }

    @Test
    public void testCanUndo() {
        assertFalse(commandManager.canUndo());
        
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        commandManager.executeCommand(new AddTaskCommand(model, task));
        
        assertTrue(commandManager.canUndo());
    }

    @Test
    public void testCanRedo() {
        assertFalse(commandManager.canRedo());
        
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), TaskPriority.MEDIUM);
        commandManager.executeCommand(new AddTaskCommand(model, task));
        commandManager.undo();
        
        assertTrue(commandManager.canRedo());
    }
}

