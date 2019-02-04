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

/**
 * Represents a view of the application main window
 */
public class MainWindowView extends View {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean isButtonsDisabled = true;
    private MainWindowController controller;
    private ObservableList<Item> calendarItems;

    /**
     * Class for converting calendar items to items that can be used in TableView
     */
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

    /**
     * Initializes state of the view
     */
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("date"));
        taskTitleColumn.setCellValueFactory(new PropertyValueFactory<Item, String>("set"));
        setTaskAndSaveButtonsDisabled(true);
    }

    /**
     * Sets the individual disabled state for all tasks toolbar buttons, "Save" and "Save as" menu items
     * @param disabled
     */
    public void setTaskAndSaveButtonsDisabled(boolean disabled) {
        ObservableList<Node> taskButtons = tasksToolbar.getItems();
        for (Node n : taskButtons) {
            n.setDisable(disabled);
        }
        saveButton.setDisable(disabled);
        saveAsButton.setDisable(disabled);
    }

    /**
     * Returns selected task in the list
     * @return selected task in the list
     */
    public Task getSelectedTask() {
        return taskList.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns index of the selected task in the list
     * @return index of the selected task in the list
     */
    public int getSelectedIndex() {
        return taskList.getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns the text value of the "From" time field
     * @return the text value of the "From" time field
     */
    public String getFromDate() {
        return fromDateField.getText();
    }

    /**
     * Returns the text value of the "To" time field
     * @return the text value of the "To" time field
     */
    public String getToDate() {
        return toDateField.getText();
    }

    /**
     * Sets the individual disabled state for the "Load from previous session" menu item
     * @param disabled the individual disabled state
     */
    public void setFromPrevSessionButtonDisabled(boolean disabled) {
        fromPrevSessionButton.setDisable(disabled);
    }

    /**
     * Sets task list items for the ListView
     * @param tasks task list items
     */
    public void setTaskListItems(ObservableList<Task> tasks) {
        taskList.setItems(tasks);
    }

    /**
     * Sets calendar items for CalendarTable
     * @param map calendar items
     */
    public void setCalendarItems(SortedMap<Date, Set<Task>> map) {
        calendarItems = FXCollections.observableArrayList();
        for (Map.Entry<Date, Set<Task>> entry : map.entrySet()) {
            calendarItems.add(new Item(entry.getKey(), entry.getValue()));
        }
        calendarTable.setItems(calendarItems);
    }

    /**
     * Event handler for "Add task" button click
     */
    @FXML
    void addTask() {
        controller.addTask();
    }

    /**
     * Event handler for "View task" button click
     */
    @FXML
    void viewTask() {
        controller.viewTask();
    }

    /**
     * Event handler for "Delete task" button click
     */
    @FXML
    void deleteTask() {
        controller.deleteTask();
    }

    /**
     * Event handler for "Edit task" button click
     */
    @FXML
    void editTask() {
        controller.editTask();
    }

    /**
     * Event handler for "Show calendar" button click
     */
    @FXML
    void showCalendar() {
        controller.showCalendar();
    }

    /**
     * Event handler for "Exit" menu item click
     */
    @FXML
    void exit() {
        controller.exit();
    }

    /**
     * Event handler for "Load from file..." menu item click
     */
    @FXML
    void loadListFromFile() {
        controller.loadListFromFile();
    }

    /**
     * Event handler for "Load from previous session" menu item click
     */
    @FXML
    void loadFromPrev() {
        controller.loadFromPrev();
    }

    /**
     * Event handler for "Load new list" menu item click
     */
    @FXML
    void loadNewList() {
        controller.loadNewList();
    }

    /**
     * Event handler for "Save list" menu item click
     */
    @FXML
    void saveList() {
        controller.saveList();
    }

    /**
     * Event handler for "Save as" menu item click
     */
    @FXML
    void saveListAs() {
        controller.saveListAs();
    }
}
