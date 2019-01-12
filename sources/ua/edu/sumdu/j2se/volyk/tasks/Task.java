package ua.edu.sumdu.j2se.volyk.tasks;

import java.io.Serializable;
import java.util.Date;

/**
 * The Task class represents the “task” data type, 
 * which contains information about the essence of the task, 
 * its status (active / inactive), the notification time, 
 * the time interval through which the notification about it should be repeated.
 */
public class Task implements Cloneable, Serializable {
	private String title;
	private boolean active;
	private Date startTime;
	private Date endTime;
	private int repeatInterval;
	private boolean repeated;
	
	/**
	 * Constructs a new Task with the specified title and time for one-time task.
	 * New created task is considered inactive.
	 * @param title new task title
	 * @param time the task notification time
	 */
	public Task(String title, Date time) {
		setTitle(title);
		setTime(time);
		setActive(false);
	}


	/**
	 * Constructs a new Task with the specified title, start time, end time and
     * repeated interval for repetitive task.
	 * New created task is considered inactive.
	 * @param title new task title
	 * @param start the start time of the task alert
	 * @param end  the time when the task alert is terminated
	 * @param interval the time interval through which the task alert must be repeated
	 */
	public Task(String title, Date start, Date end, int interval) {
		setTitle(title);
		setTime(start, end, interval);
		setActive(false);
	}

	/**
	 * Returns current title of the task
	 * @return current title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets new title of the task
	 * @param title new task title
	 */
	public void setTitle(String title) {
		if (title.isEmpty()) {
			throw new IllegalArgumentException("Entered value of 'title' is not valid. Can't be empty.");
		} else {
			this.title = title;
		}
		
	}

	/**
	 * Returns {@code true}  if task is active, otherwise {@code false} .
	 * @return  {@code true}  if task is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets whether task is active
	 * @param active whether task is active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Sets time of the alert for the one-time task
	 * @param time the task notification time
	 */
	public void setTime(Date time) {
		if (time.before(new Date(0))) {
			throw new IllegalArgumentException("Entered value: time=" + time + " - not valid. Has to be >= 0.");
		} else {
			startTime = time;
			endTime = time;
			repeatInterval = 0;
			repeated = false;
		}
	}

	/**
	 * Sets time of the alert for the repetitive task
	 * @param start the start time of the task alert
	 * @param end  the time when the task alert is terminated
	 * @param interval the time interval through which the task alert must be repeated
	 */
	public void setTime(Date start, Date end, int interval) {
		Date minDate = new Date(0);
		if (start.before(minDate) || end.before(minDate) || interval <= 0) {
			throw new IllegalArgumentException("Entered values: start=" + start 
                                + ", end=" + end 
                                + ", interval=" + interval 
                                + " - not valid. Have to be >= 0.");
		} else {
			startTime = start;
			if (start.after(end)) {
				throw new IllegalArgumentException("Value of 'end' is less than value of 'start'.");
			} else {
				endTime = end;
				if (start == end) {
					repeatInterval = 0;
					repeated = false;	
				} else {
					repeatInterval = interval;
					repeated = true;
				}
			}
		}
	}

	/**
	 * Returns the start time of the alert (for a repetitive task) or the time of
     * a single alert (for a one-time task)
	 * @return start time of the alert for a task
	 */
	public Date getTime() {
		return startTime;
	}

	/**
	 * Returns the start time of the alert (for a repetitive task) or the time of
     * a single alert (for a one-time task)
	 * @return start time of the alert for a task
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Returns the end time of the alert (for a repetitive task) or the time of
     * a single alert (for one-time task)
	 * @return end time of the alert for a task
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Returns the time interval after which it is necessary to repeat the task
     * alert (for a repetitive task) or 0 (for a one-time task)
	 * @return the time interval after which it is necessary to repeat the task alert
	 */
	public int getRepeatInterval() {
		return repeatInterval;
	}

	/**
	 * Returns {@code true}  if task is repeatable, otherwise {@code false} .
	 * @return  {@code true}  if task is repeatable
	 */
	public boolean isRepeated() {
		return repeated;
	}
	
	/**
	 * Gets time of the alert after the specified time(not including it).
	 * If there are no more alerts after the specified time or the task 
     * is inactive, the result would be -1.
	 * @param current the specified time after which it is needed to find 
     * time of the alert
	 * @return time of tne next alert
	 */
	public Date nextTimeAfter(Date current) {
		if (current.before(new Date(0))) {
			throw new IllegalArgumentException("Entered value: current=" + current + " - not valid. Has to be >= 0.");
		} else if (!current.before(endTime) || !isActive()) {
			return null;
		} else if (current.before(startTime)) {
			return startTime;
		} else {
			Date nextTime = new Date(current.getTime() + repeatInterval * 1000 - (current.getTime() - startTime.getTime()) % (repeatInterval * 1000));
			return (!nextTime.after(endTime)) ? nextTime : null;
		}

		/*if (current < 0) {
			throw new IllegalArgumentException("Entered value: current=" + current + " - not valid. Has to be >= 0.");
		} else if (current >= endTime || !isActive()) {
			return -1;
		} else if (current < startTime) {
			return startTime;
		} else {
			int nextTime = current + repeatInterval - (current - startTime) % repeatInterval;
			return (nextTime <= endTime) ? nextTime : -1;
		}*/
	}
    
    /**
     * Returns a hash code for this task.
	 * @return a hash code value for this task.
     */
	public int hashCode() {
		int result = 1;
		result = 31 * result + startTime.hashCode();
		result = 31 * result + endTime.hashCode();
		result = 31 * result + repeatInterval;
		result = 31 * result + (repeated ? 1 : 0);
		result = 31 * result + (active ? 1 : 0);
		result = 31 * result + title.hashCode();
		return result;
	}

	/**
     * Indicates whether some other object is "equal to" this one.
     * Follows the contract specified by {@link java.lang.Object#equals(Object)}
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
        Task other = (Task) obj;
		if (!title.equals(other.title) 
			|| (active != other.active) 
			|| (!startTime.equals(other.startTime))
			|| (!endTime.equals(other.endTime))
			|| (repeatInterval != other.repeatInterval) 
			|| (repeated != other.repeated)) {
			return false;
		}
        return true;
    }
    
    /**
     * Creates and returns a copy of this task. 
     * @return a clone of this task instance.
     */
	public Task clone() {
		try {
            return (Task) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
	}
    
    /**
	 * Returns a String object representing this Task.
	 * @return a string representation of this task.
	 */
	public String toString() {
		if (isActive()) {
			if (isRepeated()) {
				return "Task \"" + getTitle() + "\" from " + getStartTime() + " to " + getEndTime() + " every " + getRepeatInterval() + " seconds";
			} else {
				return "Task \"" + getTitle() + "\" at " + getStartTime();
			}
		} else {
			return "Task \"" + getTitle() + "\" is inactive";
		}
	}
}