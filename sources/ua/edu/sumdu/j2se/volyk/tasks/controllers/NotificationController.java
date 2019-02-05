package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.application.Platform;
import ua.edu.sumdu.j2se.volyk.tasks.models.Task;
import ua.edu.sumdu.j2se.volyk.tasks.views.DialogWindow;

import java.util.Date;
import java.util.List;

/**
 * Represents a notification thread for the application
 */
public class NotificationController implements Runnable {
    private MainWindowController mainWindowController;
    private long curTime = 0;

    public NotificationController(MainWindowController controller) {
        mainWindowController = controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            List<Task> list = mainWindowController.getTasks();
            Date curDate = new Date();
            long localCurTime = curDate.getTime() / 1000;

            if (localCurTime > curTime) {
                synchronized (list) {
                    if (list != null) {
                        for (Task task : list) {
                            if (task != null) {
                                Date almostCurDate = new Date(curDate.getTime() - 1);
                                if (task.nextTimeAfter(almostCurDate) != null && task.nextTimeAfter(almostCurDate).equals(curDate)) {
                                    notifyTask(task);
                                }
                            }
                        }
                    }
                }
                curTime = localCurTime;
            }

        }
    }

    /**
     * Shows window with notification for some task
     *
     * @param task task for which notification is shown
     */
    private void notifyTask(Task task) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DialogWindow.showInfoWindow("Time to do: " + task.toString());
            }
        });
    }
}
