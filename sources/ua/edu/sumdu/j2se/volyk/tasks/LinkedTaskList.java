package ua.edu.sumdu.j2se.volyk.tasks;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The LinkedTaskList class stores the linked task list.
 */
public class LinkedTaskList extends TaskList {
    private ListItem head;  

    /**
	 * Constructs an empty LinkedTaskList.
	 */
    public LinkedTaskList() {
    }

    private class ListItem implements Cloneable {
        private Task task;  
        private ListItem next;
    
        private ListItem(Task task) {
            this.task = task;
        }
        
        private ListItem(Task task, ListItem next) {
            this.task = task;
            this.next = next;
        }
    
        private Task getTask() {
            return task;
        }
    
        private void setTask(Task task) {
            this.task = task;
        }
    
        private ListItem getNext() {
            return next;
        }
    
        private void setNext(ListItem item) {
            this.next = item;
        }
        
        protected ListItem clone() {
            try {
                ListItem clone = (ListItem) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Clone not supported", e);
            }
        }
    }

    /**
     * {@inheritDoc} 
     */
    public void add(Task task) {
        if (task != null) {
            if (head == null) { 
                head = new ListItem(task); 
                size++;
                return; 
            } 
			ListItem currentItem = head;
            while (currentItem.getNext() != null) {
				currentItem = currentItem.getNext();
            }
            currentItem.setNext(new ListItem(task));
            size++;
        } else {
            throw new IllegalArgumentException("Can't add null task.");
        }
    }

    /**
     * {@inheritDoc} 
     */
    public boolean remove(Task task) {
        if (task != null) {
            if (head == null) { 
                System.out.println("List is empty!");
                return false; 
            } 
            ListItem currentItem = head;
            if (head.getTask().equals(task)) {
                head = head.getNext();
                currentItem = head;
                size--;
                return true;
            } else {
                while (currentItem.getNext() != null && !currentItem.getNext().getTask().equals(task)) {
                    currentItem = currentItem.getNext();
                }
                if (currentItem.getNext() != null) {
                    currentItem.setNext(currentItem.getNext().getNext());
                    size--;
                    return true;
                } else {
                    return false;
                }
            }
            
        } else {
            throw new IllegalArgumentException("Can't remove null task.");
        }
    }

    /**
     * {@inheritDoc} 
     */
    public Task getTask(int index) {
        if (index >= 0 && index < size()) {
            int i = 0;
            ListItem currentItem = head;
            while (currentItem.getNext() != null && i < index) {
                currentItem = currentItem.getNext();
                i++;
            }
            return currentItem.getTask();
        } else {
            throw new IndexOutOfBoundsException("Index out of range.");
        }
    }
    
    protected TaskList createInstance() {
        return new LinkedTaskList();
    }
    
    /**
     * {@inheritDoc} 
     */
    public Iterator<Task> iterator() {
        return new Iterator<Task>() {
            private ListItem prevItem = null;
            private ListItem currentItem = null;
            private ListItem nextItem = head;
            
            public boolean hasNext() {
                return nextItem != null;
            }

            public Task next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in the list.");
                }
                prevItem = currentItem;
                currentItem = nextItem;
                nextItem = currentItem.getNext();
                return currentItem.getTask();
            }
            
            public void remove() {
                if (currentItem == null) {
                    throw new IllegalStateException("Can't remove.");
                } else if (prevItem == null) {
                    head = nextItem;
                } else {
                    prevItem.setNext(nextItem);
                }
                currentItem = null;
                size--;
            }
        };
    }
    
    /**
     * {@inheritDoc} 
     */
    public LinkedTaskList clone() {
        LinkedTaskList clone = (LinkedTaskList) super.clone();
        clone.head = (ListItem) head.clone();
        return clone;
    }
}