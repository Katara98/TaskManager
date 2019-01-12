package ua.edu.sumdu.j2se.volyk.tasks;

import java.util.*;

public class Tasks {
    public static Iterable<Task> incoming(Iterable<Task> tasks, Date start, Date end) {
        Date minDate = new Date(0);
        if (start.before(minDate) || end.before(minDate)) {
            throw new IllegalArgumentException("Entered values: from=" + start + ", to=" + end + " - not valid. Have to be >= 0.");
        } else if (start.after(end)) {
            throw new IllegalArgumentException("Value of 'to' is less than value of 'from'.");
        } else {
            List<Task> incTasks = new ArrayList<>();
            //TaskList incomingTasks = createInstance();
            for (Task currentTask: tasks) {
                Date timeAfterStart = currentTask.nextTimeAfter(start);
                if (timeAfterStart != null && timeAfterStart.after(start) && !timeAfterStart.after(end)) {
                    incTasks.add(currentTask);
                }
            }
            return incTasks;
        }
    }

    public static SortedMap<Date, Set<Task>> calendar(Iterable<Task> tasks, Date start, Date end) {
        Iterable<Task> incomingTasks = incoming(tasks, start, end);
        SortedMap<Date, Set<Task>> map = new TreeMap<>();
        for(Task t : incomingTasks) {
            if (t.isRepeated()) {
                long d = t.nextTimeAfter(start).getTime();
                long endD;
                if (t.getEndTime().before(end)) {
                    endD = t.getEndTime().getTime();
                } else {
                    endD = end.getTime();
                }
                for (; d <= endD; d += t.getRepeatInterval()*1000) {
                    Date curDate = new Date(d);
                    addTaskToMap(map, curDate, t);
                }
            } else {
                Date curDate = new Date(t.getTime().getTime());
                addTaskToMap(map, curDate, t);
            }
        }
        return map;
    }

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
