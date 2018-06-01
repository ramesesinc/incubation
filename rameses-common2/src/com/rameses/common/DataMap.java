/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author dell This is used for managing data changes similar to SETI To set
 * the value without firing datachange, use set instead of put
 */
public class DataMap extends HashMap implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean editing = false;
    private Stack<Entry> changeLog;
    //this map contains the data before any changes were made.
    private Map originalData;
    private UndoListener undoListener;

    private Map data;
    
    public DataMap(Map d) {
        this.data = d;
        edit();
    }

    public Object set(Object key, Object value) {
        return data.put(key, value);
    }

    public void edit() {
        this.editing = true;
        changeLog = new Stack();
        originalData = new HashMap();
    }

    public void unedit() {
        undoAll();
    }

    public Object put(Object key, Object value) {
        //check and compare first the old value and new value.
        //check first if the same value do not proceed.
        Object prevValue = data.get(key);
        if( isEqual(prevValue,value) ) return value;
        
        data.put(key, value);
        if (!editing) {
            return value;
        }

        if (value instanceof Map) {
            return value;
        } else if (value instanceof List) {
            return value;
        } else {
            //we should push the previous value. 
            ValueEntry ve = new ValueEntry(key.toString(), prevValue);
            changeLog.push(ve);

            //log only the original data for fo
            if (!originalData.containsKey(key)) {
                //put also an entry in the map similar to the name but with underscores
                originalData.put(key, prevValue);
            }
            return value;
        }
    }

    public Map data() {
        return data;
    }

    public static interface Entry {
    }

    public static class ValueEntry implements Entry {

        private String key;
        private Object value;

        public ValueEntry(String k, Object v) {
            this.key = k;
            this.value = v;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
        
        public String toString() {
            return key + "=" + value;
        }
    }

    public void undoAll() {
        changeLog.clear();
        for (Object e : originalData.entrySet()) {
            Map.Entry me = (Map.Entry) e;
            data.put(me.getKey(), me.getValue());
        }
    }

    //undo a field will return the latest data.
    public Object undo() {
        System.out.println("fire undo");
        if (changeLog == null || changeLog.empty()) {
            return null;
        }
        Entry entry = changeLog.pop();
        if (undoListener == null) {
            undoListener = new UndoListener() {
                public void undoValue(ValueEntry ve) {;}
            };
        };
        System.out.println("entry is " + entry );
        if (entry instanceof ValueEntry) {
            ValueEntry ve = (ValueEntry) entry;
            data.put(ve.getKey(), ve.getValue());
            undoListener.undoValue(ve);
        };
        System.out.println("change log is ->"+changeLog.size());
        return entry;
    }

    public static interface UndoListener {
        void undoValue(ValueEntry ve);
    }

    private final boolean isEqual(Object oldValue, Object newValue) {
        if (newValue == null && oldValue != null) {
            return false;
        } else if (newValue != null && oldValue == null) {
            return false;
        } else if (newValue != null && !newValue.equals(oldValue)) {
            return false;
        }
        return true;
    }

    public Object get(Object key) {
        return data.get(key);
    }

    
    
    //returns a new map of updated values or the changes.
    public Map diff() {
        Map newMap = new HashMap();
        if (originalData != null) {
            //loop each data in original and compare with the current data
            for (Object k : originalData.entrySet()) {
                Map.Entry orig = (Map.Entry) k;
                Object newVal = data.get(orig.getKey());
                if (!isEqual(orig.getValue(), newVal)) {
                    newMap.put("_" + orig.getKey(), orig.getValue());
                    newMap.put(orig.getKey(), newVal);
                }
            }
        }
        return newMap;
    }
}
