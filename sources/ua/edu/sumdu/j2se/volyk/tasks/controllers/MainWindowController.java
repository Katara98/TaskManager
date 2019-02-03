package ua.edu.sumdu.j2se.volyk.tasks.controllers;

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
import java.util.*;

public class MainWindowController {
    private static final Logger log = Logger.getLogger(MainWindowController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private TaskList list;
    private ObservableList<Task> tasks;
    private File lastFile;
    private boolean isSavedFile = true;
    private String recentFileName = System.getProperty("user.dir") + "\\recentFile.txt";
    private TaskWindowView taskWindowView;
    private MainWindowView mainWindowView;

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

    public ObservableList<Task> getTasks() {
        return tasks;
    }

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

    private void startNotificationThread() {
        Thread notificationThread = new Thread(new NotificationController(this));
        notificationThread.start();
        log.info("Notification thread started");
    }

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

    public void viewTask() {
        Task selectedTask = mainWindowView.getSelectedTask();
        if (selectedTask != null) {
            log.info("View selected task: " + selectedTask);
            showTaskWindow(selectedTask, WindowType.view);
        }
    }

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

    public void saveList() {
        if (lastFile == null) {
            saveListAs();
        } else {
            saveToFile();
        }
    }

    public void saveListAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        lastFile = fileChooser.showSaveDialog(mainWindowView.getStage());
        saveToFile();
    }

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
