package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;
import ua.edu.sumdu.j2se.volyk.tasks.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class MainWindowView extends View {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean isButtonsDisabled = true;
    private MainWindowController controller;
    private ObservableList<Item> calendarItems;

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
            this.date = DATE_FORMAT.format(date);
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

    public void setController(MainWindowController controller) {
        this.controller = controller;
    }

    public boolean isTaskAndSaveButtonsDisabled() {
        return isButtonsDisabled;
    }

    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("date"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("set"));
        setTaskAndSaveButtonsDisabled(true);
    }

    public void setTaskAndSaveButtonsDisabled(boolean disabled) {
        ObservableList<Node> taskButtons = tasksToolbar.getItems();
        for (Node n : taskButtons) {
            n.setDisable(disabled);
        }
        saveButton.setDisable(disabled);
        saveAsButton.setDisable(disabled);
    }

    public Task getSelectedTask() {
        return taskList.getSelectionModel().getSelectedItem();
    }

    public int getSelectedIndex() {
        return taskList.getSelectionModel().getSelectedIndex();
    }

    public String getFromDate() {
        return fromDateField.getText();
    }

    public String getToDate() {
        return toDateField.getText();
    }

    public void setFromPrevSessionButtonDisabled(boolean disabled) {
        fromPrevSessionButton.setDisable(disabled);
    }

    public void setTaskListItems(ObservableList<Task> tasks) {
        taskList.setItems(tasks);
    }

    public void setCalendarItems(SortedMap<Date, Set<Task>> map) {
        calendarItems = FXCollections.observableArrayList();
        for (Map.Entry<Date, Set<Task>> entry : map.entrySet()) {
            calendarItems.add(new Item(entry.getKey(), entry.getValue()));
        }
        calendarTable.setItems(calendarItems);
    }

    @FXML
    void addTask(ActionEvent event) {
        controller.addTask();
    }

    @FXML
    void viewTask(ActionEvent event) {
        controller.viewTask();
    }

    @FXML
    void deleteTask(ActionEvent event) {
        controller.deleteTask();
    }

    @FXML
    void editTask(ActionEvent event) {
        controller.editTask();
    }

    @FXML
    void showCalendar(ActionEvent event) {
        controller.showCalendar();
    }

    @FXML
    void exit() {
        controller.exit();
    }

    @FXML
    void loadListFromFile() {
        controller.loadListFromFile();
    }

    @FXML
    void loadFromPrev(ActionEvent event) {
        controller.loadFromPrev();
    }

    @FXML
    void loadNewList(ActionEvent event) {
        controller.loadNewList();
    }

    @FXML
    void saveList() {
        controller.saveList();
    }

    @FXML
    void saveListAs() {
        controller.saveListAs();
    }
}
