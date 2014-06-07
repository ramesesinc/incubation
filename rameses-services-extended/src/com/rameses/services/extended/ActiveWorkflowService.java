/*
 * ActiveWorkflowService.java
 *
 * Created on June 3, 2014, 8:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.services.extended;

import com.rameses.annotations.ActiveDB;
import com.rameses.annotations.Env;
import com.rameses.annotations.ProxyMethod;
import com.rameses.annotations.Service;
import com.rameses.util.ObjectDeserializer;
import groovy.lang.GroovyObject;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public abstract class ActiveWorkflowService {
    
    @ActiveDB("wf")
    private Object wf;
    
    protected abstract Object getTask();
    protected abstract String getProcessname();
    
    public Object getWf() {
        return wf;
    }

    public void setWf(Object wf) {
        this.wf = wf;
    }
    
    protected String getTaskTablename() {
        return getProcessname().toLowerCase()+"_task";
    }

    protected String getSubtaskTablename() {
        return getProcessname().toLowerCase()+"_subtask";
    }
    
    private GroovyObject getTaskObj() {
        return (GroovyObject)getTask();
    }
    
    private GroovyObject getWfObj() {
        return (GroovyObject)wf;
    }
     
    @Service(value="DateService", localInterface=DateService.class)
    protected DateService dateSvc;
    
    @Env
    protected Map env;
    
    @ProxyMethod
    public Object start( Map r ) throws Exception {
        if( r.get("refid") == null ) throw new Exception("refid is required in WorkflowService.start");
        r.put("nodename", "start");
        r.put("prevtask", new HashMap());
        List list = new ArrayList();
        findNextTransition(r, false, list);
        return list;
    }
    
    public void beforeCreateTask(Object o) {;}
    public void afterCreateTask(Object o) {;}
    public void beforeCloseTask(Object o) {;}
    public void afterCloseTask(Object o) {;}

    protected Map createTaskInstance(Map t) throws Exception {
        Map m = new HashMap();
        m.put("objid", "TSK"+new UID());
        m.put("startdate", dateSvc.getServerDate());
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        m.put("assignee", t.get("assignee"));
        m.put("data", t.get("data"));
        m.put("prevtask", t.get("prevtask"));
        m.put("message", t.get("message"));
        Object extended = t.get("extended");
        if( extended!=null && (extended instanceof Map)) {
            m.putAll( (Map)extended);
        }
        beforeCreateTask( m );
        getTaskObj().invokeMethod( "create", new Object[]{m} );
        afterCreateTask( m );
        return m;
    }
    
    private Map closeTaskInstance(Map r) throws Exception {
        String taskId = (String) r.get("taskid");
        if(taskId==null)
            throw new Exception("closeNodeInstance error. taskid is required");
        Map t = findTask( taskId );
        if(t.get("enddate")!=null) throw new Exception("Task has already ended");
        t.put("enddate", dateSvc.getServerDate());
        
        //check first if there are open subtasks
        List openSubTask = getOpenSubtaskList(r);
        if(openSubTask.size()>0) 
            throw new Exception("There are still open subtasks for " + t.get("state"));
        
        Map actor = new HashMap();
        actor.put("objid", env.get("USERID"));
        actor.put("name", env.get("FULLNAME"));
        actor.put("title", env.get("JOBTITLE"));
        t.put("actor", actor);
        beforeCloseTask(t);
        getTaskObj().invokeMethod("update", new Object[]{t} );
        afterCloseTask(t);
        return t;
    }
    
    public List getOpenForkList( String parentProcessId, Map currentTask ) throws Exception {
        Map parm = new HashMap();
        parm.put( "parentprocessid",parentProcessId );
        parm.put( "taskTablename",getTaskTablename() );
        parm.put( "processname",getProcessname() );
        List<Map> list = (List)getWfObj().invokeMethod("getOpenForkList", new Object[]{parm} );    
        if( currentTask.get("salience")!=null) {
            int sal = Integer.parseInt( currentTask.get("salience").toString());
            StringBuilder sb = new StringBuilder();
            //check if current task salience must be greater than existing.
            boolean passed =false;
            for( Map st: list ) {
                //compare the saliences
                int isal = -1;
                if( st.get("salience")!=null ) isal = Integer.parseInt(st.get("salience").toString());
                if( isal < sal ) {
                    if(passed) 
                        sb.append(",");
                    else 
                        passed = true;
                    sb.append(st.get("title")+"");
                }
            }
            String err = sb.toString();
            if(err.length()>0) {
                throw new Exception("Cannot proceed because the following tasks must be completed: \n"+err);
            }
        }
        return list;
    }
            
    @ProxyMethod
    public List getOpenTaskList( Map parm ) throws Exception {
        if(parm.get("refid")==null) throw new Exception("refid is required in getOpenTaskList");
        parm.put( "taskTablename",getTaskTablename() );
        parm.put( "processname",getProcessname() );
        
        List<Map> list =(List)getWfObj().invokeMethod("getOpenTaskList", new Object[]{parm} );
        String state = (String)parm.get("state");
        List mlist = new ArrayList();
        if( state !=null) {
            for(Map m: list) {
                String mstate = (String)m.get("state");
                if( state.equals(mstate) ) {
                    mlist.add( m );
                    break;
                }
            }
        }
        else {
            for(Map m: list) {
                String ntype = (String)m.get("nodetype");
                if( !ntype.equals("fork")) {
                    //do not include forked states
                    mlist.add( m );
                }
            }
        }
        
        if(mlist.size()==0) throw new Exception("No open tasks for document with state " + state);
        return mlist;
    }

    @ProxyMethod
    public List getOpenSubtaskList( Map parm ) throws Exception {
         parm.put( "subtaskTablename",getSubtaskTablename() );
         parm.put( "processname",getProcessname() );
         return (List)getWfObj().invokeMethod("getOpenSubtaskList", new Object[]{parm} );
    }
    
    @ProxyMethod
    public List getTransitionList( Map r ) throws Exception {
        if( r.get("state") == null ) throw new Exception("state is required in WorkflowService.getTransitionList");
        Map pr = new HashMap();
        pr.put("nodename", r.get("state"));
        pr.put("processname", getProcessname() );
        return (List)getWfObj().invokeMethod( "getTransitionList", new Object[]{ pr } );
    }
    
    private Map findTask(String taskId) throws Exception {
        Map prm = new HashMap();
        prm.put( "taskTablename",getTaskTablename() );
        prm.put( "processname",getProcessname() );
        prm.put( "objid", taskId );
        Map m = (Map)getWfObj().invokeMethod( "findTask", new Object[]{prm}  );
        if(m==null)
            throw new Exception("Cannot find task with id " + taskId);
        return m;
    }
    
    private void findNextTransition( Map r, boolean fireAll, List collector ) throws Exception {
        Map pr = new HashMap();
        String nodeName = (String)r.get("nodename");
        if(nodeName==null) nodeName = (String)r.get("state");
        if(nodeName==null) throw new Exception("state or nodename is required for nextTransition");
        pr.put( "nodename", nodeName);
        pr.put("processname", getProcessname());
        
        //this assures only the first transition that matches will be executed. except for forks
        boolean breakTransition = false;
        List<Map> transitions = (List)getWfObj().invokeMethod( "getTransitionList", new Object[]{ pr } );
        for(Map o : transitions) {
            if( breakTransition ) break;
            String action = (String)r.get("action");
            if( action!=null &&  !action.equals(o.get("action"))) continue;
            if(!fireAll) breakTransition = true;
            if( "fork".equals( o.get("tonodetype") )) {
                //create fork instance
                Map z = new HashMap();
                z.put("state", o.get("to"));
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                z.put("prevtask", r.get("prevtask"));
                Map p = createTaskInstance( z );
                String forkId = (String)p.get("objid");
                
                //create subsequent fork children 
                Map param = new HashMap();
                param.put("nodename", o.get("to"));
                param.put("parentprocessid", forkId);
                param.put( "data", r.get("data") );
                param.put( "extended", r.get("extended") );
                param.put( "refid", r.get("refid") );
                param.put("prevtask", r.get("prevtask"));
                param.put("message", r.get("message"));
                findNextTransition(param, true, collector);
            } 
            else if( "join".equals(o.get("tonodetype")) ) {
                String parentProcessId = (String)r.get("parentprocessid");
                List pendingList = getOpenForkList( parentProcessId, r );
                if(pendingList.size()==0) {
                    //close the main fork
                    Map z = new HashMap();
                    z.put("taskid", parentProcessId);
                    z.put("message", r.get("message"));
                    z.put("prevtask", r.get("prevtask"));
                    closeTaskInstance(z);
                    
                    
                    Map zz = new HashMap();
                    zz.put( "refid", r.get("refid") );
                    zz.put( "nodename", o.get("to") );
                    zz.put( "data", r.get("data") );
                    zz.put( "extended", r.get("extended") );
                    zz.put("prevtask", r.get("prevtask"));
                    zz.put("message", r.get("message"));
                    findNextTransition( zz, false, collector );
                }
            } 
            else if( "end".equals(o.get("to"))) {
                break;
            }
            else {
                Map z = new HashMap();
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                z.put("assignee", r.get("assignee"));
                z.put("data", r.get("data"));
                z.put("extended", r.get("extended"));
                z.put("state", o.get("to"));
                z.put("prevtask", r.get("prevtask"));
                z.put("message", r.get("message"));
                Map tsk = createTaskInstance( z );
                collector.add( tsk );
            }
        }
    }
    
    @ProxyMethod
    public Map signal( Map r ) throws Exception {
        if( r.get("taskid")==null && r.get("refid")==null ) 
                throw new Exception("taskid or refid is required in WorkflowService.signal");
        if(r.get("taskid")!=null && r.get("state")==null)
            throw new Exception("Please specify a state");
        if( r.get("taskid")==null)  {
            List openList = this.getOpenTaskList( r );
            if(openList.size()>1) throw new Exception("There are more than 1 open tasks for this document. Please specify a state");
            Map ff = (Map)openList.iterator().next();
            r.put("taskid", ff.get("objid"));
        }
        Map t = closeTaskInstance( r );
        //close the existing task and find the next instance
        Map m = new HashMap();
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("taskid", t.get("objid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        m.put("salience", t.get("salience")); //this is very impt. for checking salience
        m.put("action", r.get("action"));
        m.put("message", r.get("message"));
        m.put("prevtask", t);
        m.put("data", r.get("task"));
         
        List<Map> coll = new ArrayList();
        findNextTransition( m, false, coll );
        for(Map t1: coll) {
            t1.put("transitions", getTaskInfoTransitions(t1));
        }
        Map result = new HashMap();
        result.put("tasks", coll);
        return result;
    }

    @ProxyMethod
    public Map addSubtask( Map t ) throws Exception {
        if( t.get("taskid") == null ) throw new Exception("taskid is required in WorkflowService.addSubtask");
        
        //check first if task is already closed, you cannot add a subtask to it.
        Map tsk =  findTask( t.get("taskid").toString() );
        
        if(tsk.get("enddate")!=null) throw new Exception("Task has already ended");
        
        if( t.get("assignee") == null ) throw new Exception("assignee is required in WorkflowService.addSubtask");
        Map m = new HashMap();
        m.put("objid", "WFST"+new UID());
        m.put("taskid", t.get("taskid"));
        m.put("action", t.get("action"));
        m.put("message", t.get("message"));
        m.put("startdate", dateSvc.getServerDate());
        m.put("assignee", t.get("assignee"));
        getTaskObj().invokeMethod( "create", new Object[]{  m, "subtask"} );
        return m;
    }
    
    @ProxyMethod
    public Map closeSubtask(Map r) throws Exception {
        if( r.get("objid") == null ) throw new Exception("objid is required in WorkflowService.addSubtask");
        Map t = (Map)getTaskObj().invokeMethod( "read", new Object[]{r,"subtask"}  );
        if(t.get("enddate")!=null) throw new Exception("subtask is already closed");
        t.put("enddate", dateSvc.getServerDate());
        t.put("remarks", r.get("remarks"));
        Map actor = new HashMap();
        actor.put("objid", env.get("USERID"));
        actor.put("name", env.get("FULLNAME"));
        actor.put("title", env.get("JOBTITLE"));
        t.put("actor", actor);
        getTaskObj().invokeMethod("update", new Object[]{t, "subtask"} );
        return t;
    }
    
    public static interface DateService {
        Object getServerDate();
    }

    @ProxyMethod
    public List getStates() {
        Map r = new HashMap();
        r.put("processname", getProcessname());
        return (List)getWfObj().invokeMethod( "getStates", new Object[]{r}  );
    }
    
    private List getTaskInfoTransitions( Map task ) throws Exception {
        List<Map> transitions = getTransitionList(task);
        for(Map x: transitions) {
            String sprop = (String)x.get("properties");
            if( sprop!=null) {
                ObjectDeserializer dr= new ObjectDeserializer();
                Map pmap = (Map)dr.read( sprop );
                x.put("properties", pmap );
            }
        } 
        return transitions;
    }
    
    
    /****
     * openTask is the one that is hooked on.
     */
    @ProxyMethod
    public Map openTask( Map map ) throws Exception {
        return getTaskInfo(map);
    }
    
    private Map getTaskInfo( Map map ) throws Exception {
        if(map.get("taskid") == null) throw new Exception("taskid is required in getTaskInfo");
        String taskId = map.get("taskid").toString();
        Map task = findTask(taskId);
        
        //check assignees. if a task has assignee, do not display 
        List transitions = getTaskInfoTransitions(task);
        task.put("transitions", transitions );
        return task;
    }
    
    @ProxyMethod
    public List getOpenTasks( Map map ) throws Exception {
        if(map.get("refid") == null) throw new Exception("refid is required in getTasks");
        map.put("objid", map.get("refid"));
        List<Map> tsks = getOpenTaskList(map);
        List tskList = new ArrayList();
        Map parm = new HashMap();
        for(Map t: tsks) {
            parm.put( "taskid", t.get("objid") );
            tskList.add( getTaskInfo( parm ) );
        }
        return tskList;
    }
}
