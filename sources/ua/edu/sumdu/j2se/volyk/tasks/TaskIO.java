package ua.edu.sumdu.j2se.volyk.tasks;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskIO {
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = MINUTES_IN_HOUR * SECONDS_IN_MINUTE;
    private static final int SECONDS_IN_DAY = HOURS_IN_DAY * SECONDS_IN_HOUR;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");

    public static void write(TaskList tasks, OutputStream out) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read(TaskList tasks, InputStream in) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBinary(TaskList tasks, File file) {
        try (DataOutputStream stream = new DataOutputStream(new FileOutputStream(file))) {
            write(tasks, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readBinary(TaskList tasks, File file) {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            read(tasks, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(TaskList tasks, Writer out) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void read(TaskList tasks, Reader in) {
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
                Date start = dateFormat.parse(startTime);

                if (line.indexOf("from", indexAfterTitle) >= 0) {
                    int endTimeBeginIndex = line.indexOf("[", startTimeEndIndex);
                    int endTimeEndIndex = line.indexOf("]", endTimeBeginIndex);
                    String endTime = line.substring(endTimeBeginIndex, endTimeEndIndex + 1);
                    Date end = dateFormat.parse(endTime);

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void writeText(TaskList tasks, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            write(tasks, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readText(TaskList tasks, File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            read(tasks, reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String taskToString(Task task) {
        StringBuilder resString = new StringBuilder();
        String title = task.getTitle().replaceAll("\"", "\"\"");
        String activeStatus = (task.isActive()) ? "" : " inactive";
        resString.append("\"").append(title).append("\"");
        if (task.getRepeatInterval() > 0) {
            resString.append(" from ").append(dateFormat.format(task.getStartTime()))
                    .append(" to ").append(dateFormat.format(task.getEndTime()))
                    .append(" every [").append(intervalToString(task.getRepeatInterval())).append("]");
        } else {
            resString.append(" at ").append(dateFormat.format(task.getTime()));
        }
        resString.append(activeStatus);
        return resString.toString();
    }

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
