package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.volyk.tasks.models.*;
import ua.edu.sumdu.j2se.volyk.tasks.views.DialogWindow;
import ua.edu.sumdu.j2se.volyk.tasks.views.MainWindowView;
import ua.edu.sumdu.j2se.volyk.tasks.views.TaskWindowView;
import ua.edu.sumdu.j2se.volyk.tasks.views.View;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

/**
 * Main controller of the application
 */
public class MainWindowController extends Application {
    private static final Logger log = Logger.getLogger(MainWindowController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private TaskList list;
    private ObservableList<Task> tasks;
    private File lastFile;
    private boolean isSavedFile = true;
    private String recentFileName = System.getProperty("user.dir") + "\\recentFile.txt";
    private TaskWindowView taskWindowView;
    private MainWindowView mainWindowView;

    public MainWindowController() {
    }

    /**
     * Creates new main controller with the view.
     * If there is no file from previous session "Load from previous session" menu item is set disabled.
     * If file where the path to the file from previous session is not found, new file is created
     * @param view
     */
    public MainWindowController(MainWindowView view) {
        mainWindowView = view;
        try (BufferedReader reader = new BufferedReader(new FileReader(recentFileName))) {
            String fileName = reader.readLine();
            if (fileName == null) {
                mainWindowView.setFromPrevSessionButtonDisabled(true);
            }
        } catch (FileNotFoundException e) {
            mainWindowView.setFromPrevSessionButtonDisabled(true);
            File file = new File(recentFileName);
            try {
                if (file.createNewFile()) {
                    log.info("File \"" + recentFileName + "\" was created.");
                }
            } catch (IOException e1) {
                log.error("IOException happened!", e1);
                DialogWindow.showErrorWindow("IOException happened!");
            }
        } catch (IOException e) {
            log.error("IOException happened!", e);
            DialogWindow.showErrorWindow("IOException happened!");
        }

        view.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                exit();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting TaskManager application");
        log.debug("Creating main view window from: /MainWindow.fxml");
        MainWindowView view = (MainWindowView) View.loadViewFromFxml(primaryStage, "/MainWindow.fxml", "TaskManager");
        MainWindowController controller = new MainWindowController(view);
        view.setController(controller);
        view.showStage();
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    /**
     * Initiates observable task list
     */
    private void initObservableTaskList() {
        tasks = FXCollections.observableArrayList(list);
        tasks.addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Task> c) {
                while (c.next()) {
                    for (Task t : c.getRemoved()) {
                        list.remove(t);
                    }
                    /*for (Task t : c.getAddedSubList()) {
                        list.add(t);
                    }*/
                    list.addAll(c.getAddedSubList());
                }
            }
        });
        mainWindowView.setTaskListItems(tasks);
    }

    /**
     * Starts notification thread
     */
    private void startNotificationThread() {
        Thread notificationThread = new Thread(new NotificationController(this));
        notificationThread.start();
        log.info("Notification thread started");
    }

    /**
     * Shows task window for adding new task to the list
     */
    public void addTask() {
        log.info("Adding new task");
        showTaskWindow(null, WindowType.add);
        if (taskWindowView.isOkClicked()) {
            synchronized (tasks) {
                isSavedFile = false;
                tasks.add(taskWindowView.getTask());
                log.info("New task is added");
            }
        }
    }

    /**
     * Shows task window for viewing selected task from the list
     */
    public void viewTask() {
        Task selectedTask = mainWindowView.getSelectedTask();
        if (selectedTask != null) {
            log.info("View selected task: " + selectedTask);
            showTaskWindow(selectedTask, WindowType.view);
        }
    }

    /**
     * Deletes selected task from the list
     */
    public void deleteTask() {
        int selectedIndex = mainWindowView.getSelectedIndex();
        if (selectedIndex >= 0) {
            log.info("Delete selected task: " + mainWindowView.getSelectedTask());
            if (DialogWindow.showConfirmationWindow("Are you sure to delete this task?", mainWindowView.getSelectedTask().toString())) {
                tasks.remove(selectedIndex);
                isSavedFile = false;
                log.info("Task is deleted");
            }
        }
    }

    /**
     * Shows task window for editing selected task from the list
     */
    public void editTask() {
        Task selectedTask = mainWindowView.getSelectedTask();
        int index = mainWindowView.getSelectedIndex();
        if (selectedTask != null) {
            log.info("Editing selected task: " + selectedTask);
            showTaskWindow(selectedTask, WindowType.edit);
            if (taskWindowView.isOkClicked()) {
                tasks.remove(mainWindowView.getSelectedTask());
                tasks.add(index, taskWindowView.getTask());
                //taskList.getSelectionModel().select(index);
                isSavedFile = false;
            }
        }
    }

    /**
     * Shows a calendar of tasks for a given period
     */
    public void showCalendar() {
        try {
            Date fromDate = DATE_FORMAT.parse(mainWindowView.getFromDate());
            Date toDate = DATE_FORMAT.parse(mainWindowView.getToDate());
            SortedMap<Date, Set<Task>> map = Tasks.calendar(tasks, fromDate, toDate);
            mainWindowView.setCalendarItems(map);
            log.info("Calendar is showed");
        } catch (ParseException e) {
            log.warn("Invalid values in fields 'From' and/or 'To'");
            DialogWindow.showWarningWindow("Invalid values in fields 'From' and/or 'To'", "Format for input: yyyy-MM-dd HH:mm");
        }
    }

    /**
     * Shows task window and waits for user to close it
     * @param task task to be displayed in task window
     * @param type window type
     */
    private void showTaskWindow(Task task, WindowType type) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(mainWindowView.getStage());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        try {
            taskWindowView = (TaskWindowView) View.loadViewFromFxml(dialogStage, "/TaskWindow.fxml", "Task");
            taskWindowView.setController(this);
            taskWindowView.setTask(task);
            taskWindowView.setType(type);
            taskWindowView.customizeView();
            dialogStage.showAndWait();
            log.debug("Task window created.");
        } catch (IOException e) {
            log.error("Can't create task window.", e);
            throw new RuntimeException("Can't create task window.");
        }
    }

    /**
     * Remembers last opened or saved file and ends the program
     */
    public void exit() {
        if (list != null && !isSavedFile && DialogWindow.showConfirmationWindow("Do you want to save changes before exit?", null)) {
            saveList();
        }
        if (lastFile != null) {
            log.info("Remembering last file.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(recentFileName)))) {
                writer.write(lastFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("IOException happened!", e);
                DialogWindow.showErrorWindow("IOException happened!");
            }
        }
        System.exit(0);
    }

    /**
     * Loads task list from file that is chosen in FileChooser open dialog by user
     */
    public void loadListFromFile() {
        if (list != null) {
            if (DialogWindow.showConfirmationWindow("Do you want to save changes before loading new list?", null)) {
                saveList();
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        lastFile = fileChooser.showOpenDialog(mainWindowView.getStage());
        loadFromFile();
    }

    /**
     * Loads task list from the file that was last opened, saved or chosen in FileChooser open dialog
     */
    private void loadFromFile() {
        list = new ArrayTaskList();
        if (lastFile != null) {
            try {
                TaskIO.readText(list, lastFile);
                initList();
                isSavedFile = true;
                log.info("List is loaded from file: " + lastFile.getPath());
            } catch (IOException e) {
                log.error("IOException happened!", e);
                DialogWindow.showErrorWindow("IOException happened!");
            } catch (ParseException e) {
                log.error("The data in the file is not in the appropriate format.");
                DialogWindow.showErrorWindow("The data in the file is not in the appropriate format.");
            }
        }
    }

    /**
     * Loads task list fro previous session
     */
    public void loadFromPrev() {
        try (BufferedReader reader = new BufferedReader(new FileReader(recentFileName))) {
            String fileName = reader.readLine();
            if (fileName != null) {
                lastFile = new File(fileName);
                if (lastFile.isFile()) {
                    loadFromFile();
                } else {
                    DialogWindow.showErrorWindow("File not found!");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(recentFileName))) {
                        writer.flush();
                    }
                    mainWindowView.setFromPrevSessionButtonDisabled(true);
                }
            }
        } catch (IOException e) {
            log.error("IOException happened!", e);
            DialogWindow.showErrorWindow("IOException happened!");
        }
    }

    /**
     * Loads new task list
     */
    public void loadNewList() {
        if (list != null && !isSavedFile) {
            if (DialogWindow.showConfirmationWindow("Do you want to save changes before loading new list?", null)) {
                saveList();
            }
        }
        list = new ArrayTaskList();
        initList();
        log.info("New list is loaded.");
    }

    private void initList() {
        initObservableTaskList();
        if (mainWindowView.isTaskAndSaveButtonsDisabled()) {
            mainWindowView.setTaskAndSaveButtonsDisabled(false);
        }
        startNotificationThread();
    }

    /**
     * Saves task list. If there is no last last opened or saved file, list is saved as a new file
     */
    public void saveList() {
        if (lastFile == null) {
            saveListAs();
        } else {
            saveToFile();
        }
    }

    /**
     * Saves list in a new file, that is chosen by user
     */
    public void saveListAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        lastFile = fileChooser.showSaveDialog(mainWindowView.getStage());
        saveToFile();
    }

    /**
     * Saves task list to the file that was last opened, saved or chosen in FileChooser save dialog
     */
    private void saveToFile() {
        if (lastFile != null) {
            try {
                TaskIO.writeText(list, lastFile);
                isSavedFile = true;
                log.info("List is saved in file: " + lastFile.getPath());
            } catch (IOException e) {
                log.error("IOException happened!", e);
                DialogWindow.showErrorWindow("IOException happened!");
            }
        }
    }

    /**
     * Function for handling click event for "OK" button from TaskWindowView.
     * If window type - view, task window view is closed.
     * Else input data from task window view is validates, then parsed and new task is created.
     */
    public void handleOkClick() {
        String title = taskWindowView.getTaskTitle();
        boolean isActive = taskWindowView.isActiveTask();
        if (taskWindowView.getType() == WindowType.view) {
            taskWindowView.setOkClicked(true);
            taskWindowView.close();
        } else if (isValidData()) {
            try {
                Date startDate = DATE_FORMAT.parse(taskWindowView.getStartTime());
                if (taskWindowView.isRepeatedTask()) {
                    Date endDate = DATE_FORMAT.parse(taskWindowView.getEndTime());
                    int interval = Integer.parseInt(taskWindowView.getRepeatInterval());
                    taskWindowView.setTask(new Task(title, startDate, endDate, interval));
                } else {
                    taskWindowView.setTask(new Task(title, startDate));
                }
                taskWindowView.getTask().setActive(isActive);
                taskWindowView.setOkClicked(true);
                taskWindowView.close();
            } catch (ParseException e) {
                taskWindowView.setOkClicked(false);
            } catch (IllegalArgumentException e) {
                DialogWindow.showWarningWindow("Invalid values in fields", e.getMessage());
                taskWindowView.setOkClicked(false);
            }
        } else {
            DialogWindow.showWarningWindow("Invalid values in fields", "Format for date input: yyyy-MM-dd HH:mm.\nRepeat interval must be integer and >=0");
            taskWindowView.setOkClicked(false);
        }
    }

    /**
     * Returns whether the entered data in TaskWindowView is valid
     * @return whether the entered data in TaskWindowView is valid
     */
    private boolean isValidData() {
        try {
            if (taskWindowView.getTaskType() == null) {
                return false;
            }
            DATE_FORMAT.parse(taskWindowView.getStartTime());
            if (taskWindowView.isRepeatedTask()) {
                DATE_FORMAT.parse(taskWindowView.getEndTime());
                Integer.parseInt(taskWindowView.getRepeatInterval());
            }
            return true;
        } catch (ParseException | NumberFormatException e) {
            return false;
        }
    }
}
