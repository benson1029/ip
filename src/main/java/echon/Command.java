package echon;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a command that can be executed by the user.
 */
public abstract class Command {
    /**
     * Executes the command.
     *
     * @param ui The user interface where the command is executed.
     * @throws EchonException If an error occurs while executing the command.
     */
    public abstract void execute(EchonUi ui) throws EchonException;
}

class ByeCommand extends Command {
    static final String BYE_MESSAGE = "Bye. Hope to see you again soon!";

    public ByeCommand() {
    }

    @Override
    public void execute(EchonUi ui) {
        ui.displayEchonMessage(BYE_MESSAGE);
    }
}

class EchoCommand extends Command {
    private String message;

    public EchoCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(EchonUi ui) {
        ui.displayEchonMessage(message);
    }
}

abstract class AddTaskCommand extends Command {
    private static final String EMPTY_DESCRIPTION_MESSAGE =
            "OOPS!!! The description of a todo cannot be empty.";

    protected String description;
    private TaskList taskList;

    public AddTaskCommand(String description, TaskList taskList) {
        this.description = description;
        this.taskList = taskList;
    }

    protected abstract Task createTask() throws EchonException;

    @Override
    public void execute(EchonUi ui) throws EchonException {
        if (this.description.equals("")) {
            throw new EchonException(EMPTY_DESCRIPTION_MESSAGE);
        }
        Task task;
        try {
            task = this.createTask();
        } catch (EchonException e) {
            throw new EchonException(e.getMessage());
        }
        this.taskList.addTask(task);
        ArrayList<String> messages = new ArrayList<String>(Arrays.asList(
                "Got it. I've added this task:", "  " + task.toString(),
                String.format("Now you have %d tasks in the list.",
                        this.taskList.getSize())));
        ui.displayEchonMessages(messages);
    }
}

class AddTodoCommand extends AddTaskCommand {
    public AddTodoCommand(String description, TaskList taskList) {
        super(description, taskList);
    }

    @Override
    protected Task createTask() {
        return new Todo(this.description);
    }
}

class AddDeadlineCommand extends AddTaskCommand {
    private String byDate;

    public AddDeadlineCommand(String description, String byDate,
            TaskList taskList) {
        super(description, taskList);
        this.byDate = byDate;
    }

    @Override
    protected Task createTask() throws EchonException {
        return new Deadline(this.description, this.byDate);
    }
}

class AddEventCommand extends AddTaskCommand {
    private String fromDate;
    private String toDate;

    public AddEventCommand(String description, String fromDate, String toDate,
            TaskList taskList) {
        super(description, taskList);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    protected Task createTask() throws EchonException {
        return new Event(this.description, this.fromDate, this.toDate);
    }
}

class ListCommand extends Command {
    private TaskList taskList;

    public ListCommand(TaskList taskList) {
        this.taskList = taskList;
    }

    @Override
    public void execute(EchonUi ui) {
        ArrayList<String> messages = new ArrayList<String>(
                Arrays.asList("Here are the tasks in your list:"));
        messages.addAll(this.taskList.listTasks());
        ui.displayEchonMessages(messages);
    }
}

class MarkAsDoneCommand extends Command {
    private int index;
    private TaskList taskList;

    public MarkAsDoneCommand(int index, TaskList taskList) {
        this.index = index;
        this.taskList = taskList;
    }

    @Override
    public void execute(EchonUi ui) {
        Task task = this.taskList.getTask(this.index);
        task.markAsDone();
        ArrayList<String> messages = new ArrayList<String>(
                Arrays.asList("Nice! I've marked this task as done:",
                        "  " + task.toString()));
        ui.displayEchonMessages(messages);
    }
}

class UnmarkAsDoneCommand extends Command {
    private int index;
    private TaskList taskList;

    public UnmarkAsDoneCommand(int index, TaskList taskList) {
        this.index = index;
        this.taskList = taskList;
    }

    @Override
    public void execute(EchonUi ui) {
        Task task = this.taskList.getTask(this.index);
        task.unmarkAsDone();
        ArrayList<String> messages = new ArrayList<String>(
                Arrays.asList("OK, I've marked this task as not done yet:",
                        "  " + task.toString()));
        ui.displayEchonMessages(messages);
    }
}

class DeleteTaskCommand extends Command {
    private int index;
    private TaskList taskList;

    public DeleteTaskCommand(int index, TaskList taskList) {
        this.index = index;
        this.taskList = taskList;
    }

    @Override
    public void execute(EchonUi ui) {
        Task task = this.taskList.getTask(index);
        this.taskList.deleteTask(index);
        ArrayList<String> messages = new ArrayList<String>(
                Arrays.asList("Noted. I've removed this task:", "  "
                        + task.toString(),
                        String.format("Now you have %d tasks in the list.",
                                this.taskList.getSize())));
        ui.displayEchonMessages(messages);
    }
}

class FindTaskCommand extends Command {
    private String keyword;
    private TaskList taskList;

    public FindTaskCommand(String keyword, TaskList taskList) {
        this.keyword = keyword;
        this.taskList = taskList;
    }

    @Override
    public void execute(EchonUi ui) {
        ArrayList<String> messages = new ArrayList<String>(
                Arrays.asList("Here are the matching tasks in your list:"));
        for (int i = 0; i < this.taskList.getSize(); i++) {
            Task task = this.taskList.getTask(i);
            if (task.getDescription().contains(this.keyword)) {
                messages.add(String.format("%d.%s", i + 1, task.toString()));
            }
        }
        ui.displayEchonMessages(messages);
    }
}
