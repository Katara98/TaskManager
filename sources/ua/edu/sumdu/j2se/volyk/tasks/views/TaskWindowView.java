package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.WindowType;
import ua.edu.sumdu.j2se.volyk.tasks.models.Task;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskWindowView extends View {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private WindowType type;
    private Task task;
    private boolean okClicked = false;
    private final String unrepeatableStr = "Unrepeatable";
    private final String repeatableStr = "Repeatable";
    private MainWindowController controller;

    @FXML
    private ComboBox<String> taskTypeComboBox;
    @FXML
    private TextField taskTitleField;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button okButton;
    @FXML
    private AnchorPane repeatableTaskFields;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField repeatIntervalField;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TextField startTimeField;

    public void setController(MainWindowController controller) {
        this.controller = controller;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setOkClicked(boolean okClicked) {
        this.okClicked = okClicked;
    }

    public void setType(WindowType type) {
        this.type = type;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public WindowType getType() {
        return type;
    }

    public String getTaskTitle() {
        return taskTitleField.getText();
    }

    public boolean isActiveTask() {
        return activeCheckBox.isSelected();
    }

    public String getStartTime() {
        return startTimeField.getText();
    }

    public String getEndTime() {
        return endTimeField.getText();
    }

    public String getRepeatInterval() {
        return repeatIntervalField.getText();
    }

    public String getTaskType() {
        return taskTypeComboBox.getSelectionModel().getSelectedItem();
    }

    public boolean isRepeatedTask() {
        return taskTypeComboBox.getSelectionModel().getSelectedItem().equals(repeatableStr);
    }

    public void close() {
        this.getStage().close();
    }

    public void customizeView() {
        if (type != WindowType.add) {
            taskTitleField.setText(task.getTitle());
            activeCheckBox.setSelected(task.isActive());
            startTimeField.setText(DATE_FORMAT.format(task.getTime()));
            if (task.isRepeated()) {
                taskTypeComboBox.getSelectionModel().select(repeatableStr);
                endTimeField.setText(DATE_FORMAT.format(task.getEndTime()));
                repeatIntervalField.setText(Integer.toString(task.getRepeatInterval()));
            } else {
                taskTypeComboBox.getSelectionModel().select(unrepeatableStr);
            }
            changeTaskFields();
            if (type == WindowType.view) {
                ObservableList<Node> list = this.getStage().getScene().getRoot().getChildrenUnmodifiable();
                ObservableList<Node> list2 = repeatableTaskFields.getChildrenUnmodifiable();
                ObservableList<Node> list3 = FXCollections.concat(list, list2);
                for (Node n : list3) {
                    if (n instanceof TextField) {
                        ((TextField)n).setEditable(false);
                        n.setFocusTraversable(false);
                    }
                    if (n instanceof CheckBox || n instanceof ComboBox) {
                        n.setDisable(true);
                    }
                }
            } else {
                okButton.setText("Save changes");
            }
        } else {
            taskTypeComboBox.getSelectionModel().select(unrepeatableStr);
            changeTaskFields();
            startTimeField.setText(DATE_FORMAT.format(new Date()));
            okButton.setText("Add task");
        }
    }

    public void initialize() {
        taskTypeComboBox.getItems().addAll(unrepeatableStr, repeatableStr);
    }

    @FXML
    void changeTaskFields() {
        String selected = taskTypeComboBox.getSelectionModel().getSelectedItem();
        if (selected.equals(unrepeatableStr)) {
            repeatableTaskFields.setVisible(false);
            startTimeLabel.setText("Time");
        } else {
            repeatableTaskFields.setVisible(true);
            startTimeLabel.setText("Start time");
        }
    }

    @FXML
    void handleCancelClick(ActionEvent event) {
        close();
    }

    @FXML
    void handleOkClick() {
        controller.handleOkClick();
    }
}
