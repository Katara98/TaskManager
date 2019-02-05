package ua.edu.sumdu.j2se.volyk.tasks.models;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The ArrayTaskList class stores the task list in an array.
 */
public class ArrayTaskList extends TaskList {
    private static final int INIT_SIZE = 20;
    private static final int EXTEND = 15;
    private Task[] list;

    /**
     * Constructs an empty ArrayTaskList with an initial capacity.
     */
    public ArrayTaskList() {
        list = new Task[INIT_SIZE];
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(Task task) {
        if (task != null) {
            if (size() == list.length) {
                resize(size() + EXTEND);
            }
            list[size++] = task;
        } else {
            throw new IllegalArgumentException("Can't add null task.");
        }
        return false;
    }

    private void resize(int newSize) {
        Task[] newList = new Task[newSize];
        System.arraycopy(list, 0, newList, 0, size);
        list = newList;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Task task) {
        if (task != null) {
            boolean wasTaskInList = false;
            for (int i = 0; i < size(); i++) {
                if (list[i].equals(task)) {
                    wasTaskInList = true;
                    for (int j = i; j < size() - 1; j++) {
                        list[j] = list[j + 1];
                    }
                    list[--size] = null;
                    break;
                }
            }
            if ((list.length > INIT_SIZE) && (size() < list.length / 4)) {
                resize((int) list.length / 2);
            }
            return wasTaskInList;
        } else {
            throw new IllegalArgumentException("Can't remove null task.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Task getTask(int index) {
        if (index >= 0 && index < size()) {
            return list[index];
        } else {
            throw new IndexOutOfBoundsException("Index out of range.");
        }
    }

    protected TaskList createInstance() {
        return new ArrayTaskList();
    }


    /**
     * {@inheritDoc}
     */
    public Iterator<Task> iterator() {
        return new Iterator<Task>() {
            private int currentIndex = -1;

            public boolean hasNext() {
                return currentIndex < size() - 1;
            }

            public Task next() {
                if (hasNext()) {
                    return list[++currentIndex];
                }
                throw new NoSuchElementException("No more elements in the list.");
            }

            public void remove() {
                if (currentIndex < 0) {
                    throw new IllegalStateException("Can't remove.");
                } else {
                    for (int j = currentIndex; j < size() - 1; j++) {
                        list[j] = list[j + 1];
                    }
                    currentIndex--;
                    list[--size] = null;
                }
            }

        };
    }

    /**
     * {@inheritDoc}
     */
    public ArrayTaskList clone() {
        ArrayTaskList clone = (ArrayTaskList) super.clone();
        clone.list = list.clone();
        return clone;
    }
}