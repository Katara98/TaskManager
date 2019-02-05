package ua.edu.sumdu.j2se.volyk.tasks.models;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Date;
import java.util.Iterator;

/**
 * The TaskList class represents an ordered collection of tasks.
 * The user can access elements by their integer index (position in the list).
 * To implement a modifiable task list, the programmer needs to extend
 * this class and provide implementations for the getTask(int),
 * add(Task) and remove(Task) methods.
 */
public abstract class TaskList extends AbstractCollection<Task> implements Cloneable, Iterable<Task>, Serializable {
    /**
     * Count of elements of the list.
     */
    protected int size;

    /**
     * Appends the specified task to the end of this list.
     *
     * @param task task to be appended to this list
     */
    public abstract boolean add(Task task);

    /**
     * Removes the specified task from this list.
     *
     * @param task task to be removed from the list
     * @return if the task was deleted
     */
    public abstract boolean remove(Task task);

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Returns the task at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the task at the specified position in this list
     */
    public abstract Task getTask(int index);

    /**
     * Returns array of tasks from the list, the notification time of which is
     * between {@code from} (exclusive) and {@code to} (inclusive).
     *
     * @param from start time of the interval in which it is needed to find tasks with notification
     * @param to   end time of the interval in which it is needed to find tasks with notification
     * @return Returns array of tasks from the list in specified interval
     */
    public TaskList incoming(Date from, Date to) {
        Date minDate = new Date(0);
        if (from.before(minDate) || to.before(minDate)) {
            throw new IllegalArgumentException("Entered values: from=" + from + ", to=" + to + " - not valid. Have to be >= 0.");
        } else if (from.after(to)) {
            throw new IllegalArgumentException("Value of 'to' is less than value of 'from'.");
        } else {
            TaskList incomingTasks = createInstance();
            for (int i = 0; i < size(); i++) {
                Task currentTask = this.getTask(i);
                if (currentTask.nextTimeAfter(from).after(from) && currentTask.nextTimeAfter(from).before(to)) {
                    incomingTasks.add(currentTask);
                }
            }
            return incomingTasks;
        }
    }

    protected abstract TaskList createInstance();

    /**
     * Returns an iterator over the elements in this list
     * in proper sequence.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
    public abstract Iterator<Task> iterator();

    /**
     * Returns a hash code for this task list.
     *
     * @return a hash code value for this task list.
     */
    public int hashCode() {
        int result = 1;
        result = 31 * result + size;
        for (Task task : this) {
            result = 31 * result + task.hashCode();
        }
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Follows the contract specified by {@link java.lang.Object#equals(Object)}
     *
     * @param obj the object to compare with
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TaskList other = (TaskList) obj;
        if (size != other.size) {
            return false;
        }
        Iterator<Task> itr = iterator();
        Iterator<Task> otherItr = other.iterator();
        while (itr.hasNext() && otherItr.hasNext()) {
            if (!itr.next().equals(otherItr.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates and returns a copy of this task list.
     *
     * @return a clone of this task list instance.
     */
    public TaskList clone() {
        try {
            return (TaskList) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }

    /**
     * Returns a String object representing this task list.
     *
     * @return a string representation of this task list.
     */
    public String toString() {
        StringBuilder result = new StringBuilder(this.getClass().getSimpleName() + " [");
        int i = 0;
        for (Task task : this) {
            result.append(task.getTitle());
            if (i == size - 1) {
                break;
            }
            result.append(", ");
            i++;
        }
        result.append("]");
        return result.toString();
    }
}