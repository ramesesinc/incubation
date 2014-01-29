/*
 * TaskManager.java
 *
 * Created on January 27, 2014, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author wflores
 */
public class TaskManager 
{
    private static TaskManager current;
    static void setCurrent(TaskManager newInstance) {
        if (current != null) current.close(); 
        
        current = newInstance;
    } 
    
    
    private Timer timer; 
    
    TaskManager() {
        timer = new Timer();
    }
    
    void close() {
        if (timer != null) { 
            try { timer.cancel(); } catch(Throwable t){;} 
            try { timer.purge(); } catch(Throwable t){;} 
            try { timer = null; } catch(Throwable t){;} 
        } 
    }
    
    public void schedule(Runnable runnable, long delay) {
        schedule(new TaskRunnableProxy(runnable), delay);
    } 
    
    public void schedule(Runnable runnable, long delay, long period) {
        schedule(new TaskRunnableProxy(runnable), delay, period);
    } 

    public void schedule(Task task, long delay) {
        schedule(task, delay, 0);
    } 
    
    public void schedule(Task task, long delay, long period) {
        TimerTaskImpl impl = new TimerTaskImpl(task); 
        if (period <= 0)  
            timer.schedule(impl, delay); 
        else 
            timer.schedule(impl, delay, period); 
    } 
    
    
    
    private class TaskRunnableProxy extends Task 
    {
        private Runnable runnable;
        
        TaskRunnableProxy(Runnable runnable) {
            this.runnable = runnable; 
        }

        public void run() { 
            runnable.run(); 
        } 
    }
    
    private class TimerTaskImpl extends TimerTask 
    {
        private Task task; 
        
        TimerTaskImpl(Task task) {
            this.task = task; 
        }

        public void run() {
            task.run(); 
        }
    }
}
