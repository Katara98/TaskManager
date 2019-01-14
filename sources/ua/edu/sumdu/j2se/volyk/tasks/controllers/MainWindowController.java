package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.volyk.tasks.models.Task;
import ua.edu.sumdu.j2se.volyk.tasks.models.Tasks;
import ua.edu.sumdu.j2se.volyk.tasks.views.Window;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainWindowController extends Controller {
    ObservableList<Task> tasks = FXCollections.observableArrayList();

    private class Item {
        Date date;
        Set<Task> set;
        Item (Date date, Set<Task> set) {
            this.date = date;
            this.set = set;
        }
    }

    ObservableList<Item> calendarItems;

    @FXML
    private Button addButton;

    @FXML
    private Button viewButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ListView<Task> taskList;

    @FXML
    private TextField fromDateField;

    @FXML
    private TextField  toDateField;

    @FXML
    private Button showCalendarButton;

    @FXML
    private TableView<Item> calendarTable;

    @FXML
    private TableColumn<Item, Date> dateColumn;

    @FXML
    private TableColumn<Item, String> taskTitleColumn;

    public void initialize() {
        taskList.setItems(tasks);

    }

    @FXML
    void addTask(ActionEvent event) {
        TaskWindowController controller = showTaskWindow(null, WindowType.add);
        if (controller.isOkClicked()) {
            tasks.add(controller.getTask());
        }
    }

    @FXML
    void viewTask(ActionEvent event) {
        Task selectedTask = taskList.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            showTaskWindow(selectedTask, WindowType.view);
        }
    }

    @FXML
    void deleteTask(ActionEvent event) {
        int selectedIndex = taskList.getSelectionModel().getSelectedIndex();
        if (selectedIndex>=0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Are you sure to delete this task?");
            alert.setContentText(taskList.getSelectionModel().getSelectedItem().toString());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                tasks.remove(selectedIndex);
            }
        }
    }

    @FXML
    void editTask(ActionEvent event) {
        Task selectedTask = taskList.getSelectionModel().getSelectedItem();
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (selectedTask != null) {
            TaskWindowController controller = showTaskWindow(selectedTask, WindowType.edit);
            if (controller.isOkClicked()) {
                tasks.remove(taskList.getSelectionModel().getSelectedItem());
                tasks.add(index, controller.getTask());
                taskList.getSelectionModel().select(index);
            }
        }
    }

    @FXML
    void showCalendar(ActionEvent event) {
        try {
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            taskTitleColumn.setCellValueFactory(new PropertyValueFactory<>("set"));
            Date fromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fromDateField.getText());
            Date toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toDateField.getText());
            SortedMap<Date, Set<Task>> map = Tasks.calendar(tasks, fromDate, toDate);
            //ObservableMap<Date, Set<Task>> observableMap = FXCollections.observableMap(map);
            calendarItems  = FXCollections.observableArrayList();
            for (Map.Entry<Date, Set<Task>> entry : map.entrySet()) {
                calendarItems.add(new Item(entry.getKey(), entry.getValue()));
            }
            for (Item i : calendarItems) {
                System.out.println(i.date + " " + i.set);
            }
            calendarTable.setItems(calendarItems);
            System.out.println(calendarTable.getItems());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private TaskWindowController showTaskWindow(Task task, WindowType type) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(window.getStage());
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Window newWindow = null;
        try {
            newWindow = new Window(dialogStage, "TaskWindow.fxml", "Task");
            TaskWindowController controller = (TaskWindowController) newWindow.getController();
            controller.setWindow(newWindow);

            controller.setTask(task);
            controller.setType(type);
            controller.customizeView();

            dialogStage.showAndWait();
            return controller;
        } catch (IOException e) {
            throw new RuntimeException("Can't read fxml file.");
        }
    }
}
