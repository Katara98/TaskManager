package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ua.edu.sumdu.j2se.volyk.tasks.models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskWindowController extends Controller {
    private WindowType type;
    private Task task;
    private boolean okClicked = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean isOkClicked() {
        return okClicked;
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

    @FXML
    private ComboBox<String> taskTypeComboBox;

    @FXML
    private TextField taskTitleField;

    @FXML
    private CheckBox activeCheckBox;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

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

    public void initialize() {
        taskTypeComboBox.getItems().addAll("Unrepeatable", "Repeatable");

    }

    @FXML
    void changeTaskFields() {
        String selected = taskTypeComboBox.getSelectionModel().getSelectedItem();
        if (selected.equals("Unrepeatable")) {
            repeatableTaskFields.setVisible(false);
            startTimeLabel.setText("Time");
        } else {
            repeatableTaskFields.setVisible(true);
            startTimeLabel.setText("Start time");
        }
    }

    @FXML
    void handleCancelClick(ActionEvent event) {
        window.getStage().close();
    }

    @FXML
    void handleOkClick(ActionEvent event) {
        String title = taskTitleField.getText();
        boolean isActive = activeCheckBox.isSelected();
        if (type == WindowType.view) {
            okClicked = true;
            this.getWindow().getStage().close();
        } else if (isValidData()) {
            try {
                Date startDate = dateFormat.parse(startTimeField.getText());
                if (taskTypeComboBox.getSelectionModel().getSelectedItem().equals("Repeatable")) {
                    Date endDate = dateFormat.parse(endTimeField.getText());
                    int interval = Integer.parseInt(repeatIntervalField.getText());
                    task = new Task(title, startDate, endDate, interval);
                } else {
                    task = new Task(title, startDate);
                }
                task.setActive(isActive);
                okClicked = true;
                this.getWindow().getStage().close();
            } catch (ParseException e) {
                okClicked = false;
            }
        } else {
            okClicked = false;
        }
    }

    void customizeView() {
        if (type != WindowType.add) {
            taskTitleField.setText(task.getTitle());
            activeCheckBox.setSelected(task.isActive());
            startTimeField.setText(dateFormat.format(task.getTime()));
            if (task.isRepeated()) {
                taskTypeComboBox.getSelectionModel().select("Repeatable");
                endTimeField.setText(dateFormat.format(task.getEndTime()));
                repeatIntervalField.setText(Integer.toString(task.getRepeatInterval()));
            } else {
                taskTypeComboBox.getSelectionModel().select("Unrepeatable");
            }
            changeTaskFields();
            if (type == WindowType.view) {
                ObservableList<Node> list = window.getStage().getScene().getRoot().getChildrenUnmodifiable();
                for (Node n : list) {
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
            taskTypeComboBox.getSelectionModel().select("Unrepeatable");
            changeTaskFields();
            startTimeField.setText(dateFormat.format(new Date()));
            okButton.setText("Add task");
        }
    }

    private boolean isValidData() {
        try {
            if (taskTypeComboBox.getSelectionModel().getSelectedItem() == null) {
                return false;
            }
            dateFormat.parse(startTimeField.getText());
            if (taskTypeComboBox.getSelectionModel().getSelectedItem().equals("Repeatable")) {
                dateFormat.parse(endTimeField.getText());
                Integer.parseInt(repeatIntervalField.getText());
            }
            return true;
        } catch (ParseException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
