/*
 * TaskInfoSet.java
 *
 * Created on January 30, 2013, 8:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.script.task;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Elmo
 */
public class TaskInfoSet {
    
    private Set<TaskInfo> taskInfos = new HashSet();
    
    public TaskInfoSet() {
    }
    
    public void addTaskInfo(TaskInfo t) {
        taskInfos.add( t );
    }

    public Set<TaskInfo> getTaskInfos() {
        return taskInfos;
    }
    
}
