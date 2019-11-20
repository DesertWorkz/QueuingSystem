package org.desertworkz;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Regular queue data structure hacked to accommodate system requirements
 */
public class MyQueue {

    private ArrayList<HashMap<String, HashMap<String, String>>> elements;

    MyQueue() {
        elements = new ArrayList<>();
    }

    /**
     * @param data adds the data to end of queue
     */
    void enqueue(HashMap<String, HashMap<String, String>> data) {
        elements.add(data);
    }

    /**
     * returns queue head and remove it
     */
    HashMap<String, HashMap<String, String>> dequeue() {
        if (!(elements.isEmpty())) {

            HashMap<String, HashMap<String, String>> toReturn = elements.get(0);
            elements.remove(0);
            return toReturn;

        } else {
            return null;
        }
    }

    /**
     * return head of queue but no removal
     */
    HashMap<String, HashMap<String, String>> peek() {
        if (!(elements.isEmpty())) {
            return elements.get(0);
        } else {
            return null;
        }
    }

    /**
     * clears the queue
     */
    void clear() {
        elements.clear();
    }

    int size() {
        return elements.size();
    }

    /**
     * @param counterId to be searched
     * @return ticket map; null if no left
     */
    HashMap<String, HashMap<String, String>> specialTraverse(String counterId) {
        if (!(elements.isEmpty())) {
            for (HashMap<String, HashMap<String, String>> e : elements) {
                if (e != null) {
                    String key = (String) e.keySet().toArray()[0];
                    String possibleCountersRaw = e.get(key).get("possibleCounters");
                    String[] counterList = possibleCountersRaw.split("|"); // | is delimiter
                    for (String c :
                            counterList) {
//                        System.out.println("checking matches in special");

                        if (c.equals(counterId)) {
                            System.out.println("found match in special");
                            // remove ticket from queue
                            elements.remove(e);
//                            System.out.println("remove from ArrayList complete");
                            return e; // ticket map
                        }
                    }
                }
            }
        }
        System.out.println("queue is empty; special not attempted");
        return null;
    }

    @Override
    public String toString() {
        return "Queue1{" + "elements=" + elements + '}';
    }

}