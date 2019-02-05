package ua.edu.sumdu.j2se.volyk.tasks.models;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Realizes methods for writing and reading task lists in different formats.
 * The format of the list of tasks in binary form:
 * - Number of tasks
 * Then for each task:
 * - Length of the name
 * - Name
 * - Activity: 0/1
 * - Repetition interval
 * If repeating
 * - The start time
 * - The end time
 * If not repeated
 *  - The time of execution
 *
 *  The text format looks like this:
 * "Task title" at [06/24/2014 18: 00: 13000];
 * "Very" "Good" "title" at [2013-05-10 20: 31: 20.001] inactive;
 * "Other task" from [2010-06-01 08: 00: 00.000] to [2010-09-01 00: 00: 00.000] every [1 day].
 * 1. Each task is placed on a separate line.
 * 2. The title of the task is contained in double quotes if there are double quotes in the title
 * double It is believed that the task name always consists of one line.
 * 3. The dates are formatted as [RETURN-MONDAY-DAY HOURS: MINUTE: SECOND.MILISKUNDA].
 * 4. The repetition interval is formatted as an integer of days, hours, minutes, and seconds
 * using the word day (s), hour (s), minute (s), second (s) (s is added if number
 * more than 1) through a space. For example, 10000 seconds will look like
 * [2 hours 46 minutes 40 seconds].
 * 5. After each task there is a semicolon, after the last - a point.
 */
public class TaskIO {
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = MINUTES_IN_HOUR * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = HOURS_IN_DAY * SECONDS_IN_HOUR;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");

    /**
     * Writes tasks from the list into a binary stream
     *
     * @param tasks list of tasks to be written
     * @param out   output stream for list of tasks
     * @throws IOException if an I/O error occurs.
     */
    public static void write(TaskList tasks, OutputStream out) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(out)) {
            stream.write(tasks.size());
            for (Task t : tasks) {
                stream.write(t.getTitle().length());
                stream.writeChars(t.getTitle());
                stream.writeBoolean(t.isActive());
                stream.writeInt(t.getRepeatInterval());
                if (t.isRepeated()) {
                    stream.writeLong(t.getStartTime().getTime());
                    stream.writeLong(t.getEndTime().getTime());
                } else {
                    stream.writeLong(t.getTime().getTime());
                }
            }
        }
    }

    /**
     * Reads tasks from a binary stream in task list
     *
     * @param tasks list of tasks to be read
     * @param in    input stream for list of tasks
     * @throws IOException if an I/O error occurs.
     */
    public static void read(TaskList tasks, InputStream in) throws IOException {
        try (DataInputStream stream = new DataInputStream(in)) {
            int size = stream.read();
            for (int i = 0; i < size; i++) {
                StringBuilder title = new StringBuilder();
                int titleLength = stream.read();
                for (int j = 0; j < titleLength; j++) {
                    title.append(stream.readChar());
                }
                boolean isActive = stream.readBoolean();
                int interval = stream.readInt();
                Task task;
                if (interval > 0) {
                    Date start = new Date(stream.readLong());
                    Date end = new Date(stream.readLong());
                    task = new Task(title.toString(), start, end, interval);
                } else {
                    Date time = new Date(stream.readLong());
                    task = new Task(title.toString(), time);
                }
                task.setActive(isActive);
                tasks.add(task);
            }
        }
    }

    /**
     * Writes tasks from a list to a binary file
     *
     * @param tasks list of tasks to be written
     * @param file  output file
     * @throws IOException if an I/O error occurs.
     */
    public static void writeBinary(TaskList tasks, File file) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(file))) {
            write(tasks, stream);
        }
    }

    /**
     * Reads tasks from a binary file in task list
     *
     * @param tasks list of tasks to be read
     * @param file  input file
     * @throws IOException if an I/O error occurs.
     */
    public static void readBinary(TaskList tasks, File file) throws IOException {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            read(tasks, stream);
        }
    }

    /**
     * Writes tasks from the list to the character stream
     *
     * @param tasks list of tasks to be written
     * @param out   output character stream
     * @throws IOException if an I/O error occurs.
     */
    public static void write(TaskList tasks, Writer out) throws IOException {
        try (BufferedWriter bf = new BufferedWriter(out)) {
            int i = 0;
            for (Task t : tasks) {
                String s = taskToString(t) + ((i < tasks.size() - 1) ? ";" : ".");
                bf.write(s);
                if (i < tasks.size() - 1) {
                    bf.newLine();
                }
                i++;
            }
        }
    }

    /**
     * Reads tasks from a character stream in task list
     *
     * @param tasks list of tasks to be read
     * @param in    input character stream
     * @throws IOException    if an I/O error occurs
     * @throws ParseException if date cannot be parsed
     */
    public static void read(TaskList tasks, Reader in) throws IOException, ParseException {
        try (BufferedReader bf = new BufferedReader(in)) {
            String line;
            while ((line = bf.readLine()) != null) {
                String title = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                boolean isActive = !line.contains("inactive");
                Task task;
                int indexAfterTitle = title.length() + 2;
                int startTimeBeginIndex = line.indexOf("[", indexAfterTitle);
                int startTimeEndIndex = line.indexOf("]", indexAfterTitle);
                String startTime = line.substring(startTimeBeginIndex, startTimeEndIndex + 1);
                Date start = DATE_FORMAT.parse(startTime);

                if (line.indexOf("from", indexAfterTitle) >= 0) {
                    int endTimeBeginIndex = line.indexOf("[", startTimeEndIndex);
                    int endTimeEndIndex = line.indexOf("]", endTimeBeginIndex);
                    String endTime = line.substring(endTimeBeginIndex, endTimeEndIndex + 1);
                    Date end = DATE_FORMAT.parse(endTime);

                    int intervalBeginIndex = line.lastIndexOf("[");
                    int intervalEndIndex = line.lastIndexOf("]");
                    String intervalStr = line.substring(intervalBeginIndex + 1, intervalEndIndex);
                    int interval = stringToInterval(intervalStr);

                    task = new Task(title, start, end, interval);
                } else {
                    task = new Task(title, start);
                }
                task.setActive(isActive);
                tasks.add(task);
            }
        }
    }

    /**
     * Writes tasks from the list to the character file
     *
     * @param tasks list of tasks to be written
     * @param file  output character file
     * @throws IOException if an I/O error occurs
     */
    public static void writeText(TaskList tasks, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            write(tasks, writer);
        }
    }

    /**
     * Reads tasks from a character file in task list
     *
     * @param tasks list of tasks to be read
     * @param file  input character file
     * @throws IOException    if an I/O error occurs
     * @throws ParseException if date cannot be parsed
     */
    public static void readText(TaskList tasks, File file) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            read(tasks, reader);
        }
    }

    /**
     * Returns a String object representing a task
     *
     * @param task task to be converted to a String
     * @return a String object representing a task
     */
    private static String taskToString(Task task) {
        StringBuilder resString = new StringBuilder();
        String title = task.getTitle().replaceAll("\"", "\"\"");
        String activeStatus = (task.isActive()) ? "" : " inactive";
        resString.append("\"").append(title).append("\"");
        if (task.getRepeatInterval() > 0) {
            resString.append(" from ").append(DATE_FORMAT.format(task.getStartTime()))
                    .append(" to ").append(DATE_FORMAT.format(task.getEndTime()))
                    .append(" every [").append(intervalToString(task.getRepeatInterval())).append("]");
        } else {
            resString.append(" at ").append(DATE_FORMAT.format(task.getTime()));
        }
        resString.append(activeStatus);
        return resString.toString();
    }

    /**
     * Returns a String object representing interval
     *
     * @param intervalInSec interval to be converted to a String
     * @return a String object representing interval
     */
    private static String intervalToString(int intervalInSec) {
        int seconds = intervalInSec % SECONDS_IN_MINUTE;
        int intervalInMin = (int) intervalInSec / SECONDS_IN_MINUTE;
        int minutes = intervalInMin % MINUTES_IN_HOUR;
        int intervalInHours = (int) intervalInMin / MINUTES_IN_HOUR;
        int hours = intervalInHours % HOURS_IN_DAY;
        int days = (int) intervalInHours / HOURS_IN_DAY;

        StringBuilder s = new StringBuilder();
        if (days > 0) {
            s.append(days).append(" day");
            if (days > 1) {
                s.append("s");
            }
            s.append(" ");
        }
        if (hours > 0) {
            s.append(hours).append(" hour");
            if (hours > 1) {
                s.append("s");
            }
            s.append(" ");
        }
        if (minutes > 0) {
            s.append(minutes).append(" minute");
            if (minutes > 1) {
                s.append("s");
            }
            s.append(" ");
        }
        if (seconds > 0) {
            s.append(seconds).append(" second");
            if (seconds > 1) {
                s.append("s");
            }
            s.append(" ");
        }
        return s.toString().trim();
    }

    /**
     * Converts string value of interval to an int
     *
     * @param s string value of interval
     * @return int value of interval
     */
    private static int stringToInterval(String s) {
        Pattern pattern = Pattern.compile("^((\\d+) days?)?\\s*((\\d+) hours?)?\\s*((\\d+) minutes?)?\\s*((\\d+) seconds?)?$");
        Matcher m = pattern.matcher(s);
        if (m.find()) {
            int days = (m.group(2) != null) ? Integer.valueOf(m.group(2)) : 0;
            int hours = (m.group(4) != null) ? Integer.valueOf(m.group(4)) : 0;
            int minutes = (m.group(6) != null) ? Integer.valueOf(m.group(6)) : 0;
            int seconds = (m.group(8) != null) ? Integer.valueOf(m.group(8)) : 0;
            return days * SECONDS_IN_DAY + hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds;
        }
        return 0;
    }
}
