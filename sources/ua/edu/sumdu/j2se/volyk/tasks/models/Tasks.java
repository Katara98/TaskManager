package ua.edu.sumdu.j2se.volyk.tasks.models;

import java.util.*;

/**
 * Class for work with task collections
 */
public class Tasks {
    /**
     * Returns tasks from the Iterable task collection, the notification time of which is
     * between {@code start} (exclusive) and {@code end} (inclusive).
     * @param tasks task collection for which it is needed to find incoming tasks
     * @param start start time of the interval in which it is needed to find tasks with notification
     * @param end end time of the interval in which it is needed to find tasks with notification
     * @return array of tasks from the list in specified interval
     */
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end) {
        Date minDate = new Date(0);
        if (start.before(minDate) || end.before(minDate)) {
            throw new IllegalArgumentException("Entered values: from=" + start + ", to=" + end + " - not valid. Have to be >= 0.");
        } else if (start.after(end)) {
            throw new IllegalArgumentException("Value of 'to' is less than value of 'from'.");
        } else {
            List<Task> incTasks = new ArrayList<>();
            for (Task currentTask: tasks) {
                Date timeAfterStart = currentTask.nextTimeAfter(start);
                if (timeAfterStart != null && timeAfterStart.after(start) && !timeAfterStart.after(end)) {
                    incTasks.add(currentTask);
                }
            }
            return incTasks;
        }
    }

    /**
     * Builds a calendar of tasks for a given period - a table where each date corresponds to the set of tasks
     * that must be performed at that time, at which one task can meet in accordance with several dates
     * if it has to be executed several times during the specified period.
     * @param tasks task collection for which it is needed to find incoming tasks
     * @param start start time of the interval in which it is needed to find tasks with notification
     * @param end end time of the interval in which it is needed to find tasks with notification
     * @return a table where each date corresponds to the set of tasks that must be performed at that time
     */
    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end) {
        Iterable<Task> incomingTasks = incoming(tasks, start, end);
        SortedMap<Date, Set<Task>> map = new TreeMap<>();
        for(Task t : incomingTasks) {
            addTaskToMap(map, t.nextTimeAfter(start), t);
        }
        return map;
    }

    /**
     * Adds task to a map
     * @param map where to add task
     * @param date key of the map for which it is needed to add a task
     * @param t task to be added
     */
    private static void addTaskToMap(SortedMap<Date, Set<Task>> map, Date date, Task t) {
        Set<Task> taskSet = map.get(date);
        if (taskSet == null) {
            taskSet = new HashSet<>();
            taskSet.add(t);
            map.put(date, taskSet);
        } else {
            taskSet.add(t);
        }
    }
}
