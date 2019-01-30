package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.volyk.tasks.models.*;
import ua.edu.sumdu.j2se.volyk.tasks.views.ConfirmationWindow;
import ua.edu.sumdu.j2se.volyk.tasks.views.Window;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainWindowController extends Controller {
    private static final Logger log = Logger.getLogger(MainWindowController.class);
    private TaskList list;
    private ObservableList<Task> tasks;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private File lastFile;
    private boolean isSavedFile = true;
    private boolean isButtonsDisabled = true;
    private String recentFileName = System.getProperty("user.dir") + "\\recentFile.txt";

    @Override
    public void setWindow(Window window) {
        super.setWindow(window);
        window.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                exit();
            }
        });
    }

    public class Item {
        private String date;
        private String set;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSet() {
            return set;
        }

        public void setSet(String set) {
            this.set = set;
        }

        Item(Date date, Set<Task> set) {
            this.date = dateFormat.format(date);
            this.set = setToString(set);
        }

        private String setToString(Set<Task> set) {
            StringBuilder result = new StringBuilder();
            int i = 0;
            for (Task task : set) {
                result.append(task.getTitle());
                if (i == set.size() - 1) {
                    break;
                }
                result.append(", ");
                i++;
            }
            return result.toString();
        }
    }

    ObservableList<Item> calendarItems;

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    @FXML
    private ToolBar tasksToolbar;

    @FXML
    private ListView<Task> taskList;

    @FXML
    private TextField fromDateField;

    @FXML
    private TextField toDateField;

    @FXML
    private TableView<Item> calendarTable;

    @FXML
    private TableColumn<Item, String> dateColumn;

    @FXML
    private TableColumn<Item, String> taskTitleColumn;

    @FXML
    private MenuItem saveButton;

    @FXML
    private MenuItem saveAsButton;

    @FXML
    private MenuItem fromPrevSessionButton;

    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("date"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("set"));
        setTaskAndSaveButtonsDisabled(true);
        try (BufferedReader reader = new BufferedReader(new FileReader(recentFileName))) {
            String fileName = reader.readLine();
            if (fileName == null) {
                fromPrevSessionButton.setDisable(true);
            }
        } catch (FileNotFoundException e) {
            fromPrevSessionButton.setDisable(true);
            File file = new File(recentFileName);
            try {
                if (file.createNewFile()) {
                    log.info("File \"" + recentFileName + "\" was created.");
                }
            } catch (IOException e1) {
                log.error("IOException happened!", e1);
                showErrorWindow("IOException happened!");
            }
        } catch (IOException e) {
            log.error("IOException happened!", e);
            showErrorWindow("IOException happened!");
        }
    }

    private void setTaskAndSaveButtonsDisabled(boolean disabled) {
        ObservableList<Node> taskButtons = tasksToolbar.getItems();
        for (Node n : taskButtons) {
            n.setDisable(disabled);
        }
        saveButton.setDisable(disabled);
        saveAsButton.setDisable(disabled);
    }

    private void initObservableTaskList() {
        tasks = FXCollections.observableArrayList(list);
        tasks.addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Task> c) {
                while (c.next()) {
                    if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (Task t : c.getRemoved()) {
                            list.remove(t);
                        }
                        for (Task t : c.getAddedSubList()) {
                            list.add(t);
                        }
                    }
                }
            }
        });
        taskList.setItems(tasks);
    }

    private void startNotificationThread() {
        Thread notificationThread = new Thread(new NotificationController(this));
        notificationThread.start();
        log.info("Notification thread started");
    }

    @FXML
    void addTask(ActionEvent event) {
        log.info("Adding new task");
        TaskWindowController controller = showTaskWindow(null, WindowType.add);
        if (controller.isOkClicked()) {
            synchronized (tasks) {
                isSavedFile = false;
                tasks.add(controller.getTask());
                log.info("New task is added");
            }
        }
    }

    @FXML
    void viewTask(ActionEvent event) {
        Task selectedTask = taskList.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            log.info("View selected task: " + selectedTask);
            showTaskWindow(selectedTask, WindowType.view);
        }
    }

    @FXML
    void deleteTask(ActionEvent event) {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            log.info("Delete selected task: " + taskList.getSelectionModel().getSelectedItem());
            if (showConfirmationWindow("Are you sure to delete this task?", taskList.getSelectionModel().getSelectedItem().toString())) {
                tasks.remove(selectedIndex);
                isSavedFile = false;
                log.info("Task is deleted");
            }
        }
    }

    @FXML
    void editTask(ActionEvent event) {
        Task selectedTask = taskList.getSelectionModel().getSelectedItem();
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (selectedTask != null) {
            log.info("Editing selected task: " + selectedTask);
            TaskWindowController controller = showTaskWindow(selectedTask, WindowType.edit);
            if (controller.isOkClicked()) {
                tasks.remove(taskList.getSelectionModel().getSelectedItem());
                tasks.add(index, controller.getTask());
                taskList.getSelectionModel().select(index);
                isSavedFile = false;
            }
        }
    }

    @FXML
    void showCalendar(ActionEvent event) {
        try {
            Date fromDate = dateFormat.parse(fromDateField.getText());
            Date toDate = dateFormat.parse(toDateField.getText());
            SortedMap<Date, Set<Task>> map = Tasks.calendar(tasks, fromDate, toDate);
            calendarItems = FXCollections.observableArrayList();
            for (Map.Entry<Date, Set<Task>> entry : map.entrySet()) {
                calendarItems.add(new Item(entry.getKey(), entry.getValue()));
            }
            calendarTable.setItems(calendarItems);
            log.info("Calendar is showed");
        } catch (ParseException e) {
            log.warn("Invalid values in fields 'From' and/or 'To'");
            showWarningWindow("Invalid values in fields 'From' and/or 'To'", "Format for input: yyyy-MM-dd HH:mm:ss");
        }
    }

    private TaskWindowController showTaskWindow(Task task, WindowType type) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(window.getStage());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Window newWindow = null;
        try {
            newWindow = new Window(dialogStage, "/TaskWindow.fxml", "Task");
            TaskWindowController controller = (TaskWindowController) newWindow.getController();
            controller.setWindow(newWindow);

            controller.setTask(task);
            controller.setType(type);
            controller.customizeView();

            dialogStage.showAndWait();
            log.debug("Task window created.");
            return controller;
        } catch (IOException e) {
            log.error("Can't create task window.", e);
            throw new RuntimeException("Can't create task window.");
        }
    }

    @FXML
    void exit() {
        if (list != null && !isSavedFile && showConfirmationWindow("Do you want to save changes?", null)) {
            saveList();
        }
        if (lastFile != null) {
            log.info("Remembering last file.");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(recentFileName)))) {
                writer.write(lastFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("IOException happened!", e);
                showErrorWindow("IOException happened!");
            }
        }

        System.exit(0);
    }

    @FXML
    void loadListFromFile() {
        if (list != null) {
            if (showConfirmationWindow("Do you want to save changes before loading new list?", null)) {
                saveList();
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        lastFile = fileChooser.showOpenDialog(getWindow().getStage());
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
                showErrorWindow("IOException happened!");
            } catch (ParseException e) {
                log.error("The data in the file is not in the appropriate format.");
                showErrorWindow("The data in the file is not in the appropriate format.");
            }
        }
    }

    @FXML
    void loadFromPrev(ActionEvent event) {
        try (BufferedReader reader = new BufferedReader(new FileReader(recentFileName))) {
            String fileName = reader.readLine();
            if (fileName != null) {
                lastFile = new File(fileName);
                if (lastFile.isFile()) {
                    loadFromFile();
                } else {
                    showErrorWindow("File not found!");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(recentFileName))) {
                        writer.flush();
                    }
                    fromPrevSessionButton.setDisable(true);
                }
            }
        } catch (IOException e) {
            log.error("IOException happened!", e);
            showErrorWindow("IOException happened!");
        }
    }

    @FXML
    void loadNewList(ActionEvent event) {
        if (list != null && !isSavedFile) {
            if (showConfirmationWindow("Do you want to save changes before loading new list?", null)) {
                saveList();
            }
        }
        list = new ArrayTaskList();
        initList();
        log.info("New list is loaded.");
    }

    private void initList() {
        initObservableTaskList();
        if (isButtonsDisabled) {
            setTaskAndSaveButtonsDisabled(false);
        }
        startNotificationThread();
    }

    @FXML
    void saveList() {
        if (lastFile == null) {
            saveListAs();
        } else {
            saveToFile();
        }
    }

    @FXML
    void saveListAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        lastFile = fileChooser.showSaveDialog(getWindow().getStage());
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
                showErrorWindow("IOException happened!");
            }
        }
    }
}
