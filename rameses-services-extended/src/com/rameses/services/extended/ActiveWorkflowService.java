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
import com.rameses.common.ExpressionResolver;
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
    
    protected String getWorkitemTablename() {
        return getProcessname().toLowerCase()+"_workitem";
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
    
    //overridable
    public void beforeCreateTask(Object o) {;}
    public void afterCreateTask(Object o) {;}
    public void beforeCloseTask(Object o) {;}
    public void afterCloseTask(Object o) {;}
    public void beforeOpenTask(Object o) {;}
    public void afterOpenTask(Object o) {;}
    public void beforeSignal(Object params) {;}
    public void afterSignal(Object result) {;}
    public void afterLoadTask(Object newTask) {;}
    public void onEndTask() {;}
    public void loadWorkitem( Object workitem, Object task ) {;}
    public void loadTransition( Object transition, Object task ) {;}
    
    public boolean checkTaskOwner( Map task ) {return true; }

    protected Map createTaskInstance(Map t) throws Exception {
        Map m = new HashMap();
        m.put("objid", "TSK"+new UID());
        m.put("startdate", dateSvc.getServerDate());
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        
        //from parameters
        m.put("message", env.get("message"));
        m.put("assignee", env.get("assignee"));
        m.putAll( findNodeInfo(t.get("state").toString()) );
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
        
        //check first if there are open workitems
        List openWorkitems = getOpenWorkitemList(r);
        if(openWorkitems.size()>0)
            throw new Exception("There are still open work items for " + t.get("state"));
        
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
        } else {
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
    public List getOpenWorkitemList( Map parm ) throws Exception {
        parm.put( "workitemTablename",getWorkitemTablename() );
        parm.put( "processname",getProcessname() );
        return (List)getWfObj().invokeMethod("getOpenWorkitemList", new Object[]{parm} );
    }
    
    private Map findNodeInfo(  String state ) {
        Map parm = new HashMap();
        parm.put( "state", state );
        parm.put( "processname",getProcessname() );
        return (Map)getWfObj().invokeMethod("findNodeInfo", new Object[]{parm} );
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
            String action = (String)env.get("ACTION");
            if( action!=null &&  !action.equals(o.get("action"))) continue;
            if(!fireAll) breakTransition = true;
            if( "fork".equals( o.get("tonodetype") )) {
                //create fork instance
                Map z = new HashMap();
                z.put("state", o.get("to"));
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                Map p = createTaskInstance( z );
                String forkId = (String)p.get("objid");
                
                //create subsequent fork children
                Map param = new HashMap();
                param.put("nodename", o.get("to"));
                param.put("parentprocessid", forkId);
                param.put( "refid", r.get("refid") );
                findNextTransition(param, true, collector);
            } else if( "join".equals(o.get("tonodetype")) ) {
                String parentProcessId = (String)r.get("parentprocessid");
                List pendingList = getOpenForkList( parentProcessId, r );
                if(pendingList.size()==0) {
                    //close the main fork
                    Map z = new HashMap();
                    z.put("taskid", parentProcessId);
                    z.put("message", r.get("message"));
                    closeTaskInstance(z);
                    
                    Map zz = new HashMap();
                    zz.put( "refid", r.get("refid") );
                    zz.put( "nodename", o.get("to") );
                    
                    findNextTransition( zz, false, collector );
                }
            } else if( "end".equals(o.get("to"))) {
                onEndTask();
                break;
            } else {
                Map z = new HashMap();
                z.put("refid", r.get("refid"));
                z.put("parentprocessid", r.get("parentprocessid"));
                z.put("state", o.get("to"));
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
        
        env.put("data", r.get("data"));
        env.put("action", r.get("action"));
        env.put("message", r.get("message"));
        env.put("assignee", r.get("assignee"));
        
        beforeSignal(r);
        
        //if there is an assignee, move it out from previous task and place it in the new task
        Map t = closeTaskInstance( r );
        env.put("prevtask", t );
        
        //close the existing task and find the next instance
        Map m = new HashMap();
        m.put("state", t.get("state"));
        m.put("refid", t.get("refid"));
        m.put("taskid", t.get("objid"));
        m.put("parentprocessid", t.get("parentprocessid"));
        m.put("salience", t.get("salience")); //this is very impt. for checking salience
        
        //get possible concurrent tasks
        List<Map> tsks = new ArrayList();
        findNextTransition( m, false, tsks );
        Map newTask = null;
        List newTasks = new ArrayList();
        for( Map tk : tsks ) {
            if( isTaskOwner(tk) ) {
                newTasks.add(  tk );
                tk.put("taskid", tk.get("objid"));
                if(newTask==null) {
                    newTask = getTaskInfo(tk);
                    newTask.put("owner",true);
                }
            }
        }
        
        Map result = new HashMap();
        result.put("tasks", newTasks);
        result.put("task", newTask );
        
        if(newTask!=null) {
            loadTask(newTask);
        }
        afterSignal(result);
        return result;
    }
    
    @ProxyMethod
    public Map addWorkitem( Map t ) throws Exception {
        if( t.get("taskid") == null ) throw new Exception("taskid is required in WorkflowService.addWorkitem");
        
        //check first if task is already closed, you cannot add a workitem to it.
        Map tsk =  findTask( t.get("taskid").toString() );
        
        if(tsk.get("enddate")!=null) throw new Exception("Task has already ended");
        
        if( t.get("assignee") == null ) throw new Exception("assignee is required in WorkflowService.addWorkitem");
        Map m = new HashMap();
        m.put("objid", "WFST"+new UID());
        m.put("taskid", t.get("taskid"));
        m.put("refid", t.get("refid"));
        m.put("workitemid", t.get("workitemid"));
        m.put("action", t.get("action"));
        m.put("message", t.get("message"));
        m.put("startdate", dateSvc.getServerDate());
        m.put("assignee", t.get("assignee"));
        getTaskObj().invokeMethod( "create", new Object[]{  m, "workitem"} );
        return m;
    }
    
    @ProxyMethod
    public Map closeWorkitem(Map r) throws Exception {
        if( r.get("objid") == null ) throw new Exception("objid is required in WorkflowService.addWorkitem");
        Map t = (Map)getTaskObj().invokeMethod( "read", new Object[]{r,"workitem"}  );
        if(t.get("enddate")!=null) throw new Exception("workitem is already closed");
        t.put("enddate", dateSvc.getServerDate());
        t.put("remarks", r.get("remarks"));
        Map actor = new HashMap();
        actor.put("objid", env.get("USERID"));
        actor.put("name", env.get("FULLNAME"));
        actor.put("title", env.get("JOBTITLE"));
        t.put("actor", actor);
        getTaskObj().invokeMethod("update", new Object[]{t, "workitem"} );
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
        beforeOpenTask(map);
        Map t = getTaskInfo(map);
        isTaskOwner(t);
        afterOpenTask(t);
        
        //place data in env;
        env.put("data", t.get("data"));
        
        loadTask(t);
        return t;
    }
    
    private void loadTask( Map task ) throws Exception {
        //check assignees. if a task has assignee, do not display
        Map data =(Map)env.get("data");
        Map prevTask = (Map)env.get("prevtask");
        
        List<Map> transitions = getTaskInfoTransitions(task);
        List xtransitions = new ArrayList();
        if( transitions.size() > 0 ) {
            for(Map m: transitions) {
                loadTransition( m, task );
                xtransitions.add(m);
            }
        }
        task.put("transitions", xtransitions );
        
        //attach also workitem types
        List<Map> workitemTypes = getWorkitemTypes( task );
        List xworkitemTypes = new ArrayList();
        
        if( workitemTypes.size() > 0 ) {
            Map parm = new HashMap();
            parm.put("data", data);
            parm.put("task", task);
            parm.put("prevtask", prevTask);
            for(Map m: workitemTypes) {
                boolean addIt = true;
                String expr = (String)m.get("expr");
                if(expr !=null) {
                    try {addIt = ExpressionResolver.getInstance().evalBoolean( expr, parm );} catch(Exception e){;}
                }
                if(addIt) {
                    loadWorkitem( m, task );
                    xworkitemTypes.add( m );
                }
            }
        }
        task.put("workitemtypes", xworkitemTypes );
        afterLoadTask( task );
    }
    
    
    private Map getTaskInfo( Map map ) throws Exception {
        if(map.get("taskid") == null) throw new Exception("taskid is required in getTaskInfo");
        String taskId = map.get("taskid").toString();
        return findTask(taskId);
    }
    
    @ProxyMethod
    public List getOpenTasks( Map map ) throws Exception {
        if(map.get("refid") == null) throw new Exception("refid is required in getTasks");
        map.put("objid", map.get("refid"));
        List<Map> tsks = getOpenTaskList(map);
        List tskList = new ArrayList();
        Map parm = new HashMap();
        for(Map t: tsks) {
            if( isTaskOwner(t)) { 
                parm.put( "taskid", t.get("objid") );
                Map eTsk = getTaskInfo( parm );
                eTsk.put("owner", true);
                tskList.add( eTsk );
            }
        }
        return tskList;
    }
    
    @ProxyMethod
    public List getWorkitemTypes( Map map ) throws Exception {
        if(!map.containsKey( "state")) throw new Exception("state is required");
        map.put("processname", getProcessname() );
        return (List)getWfObj().invokeMethod("getWorkitemTypes", new Object[]{map} );
    }
    
    //overridable
    
    private boolean isTaskOwner( Map task ) throws Exception {
        String userId = (String) env.get("USERID");
        if( userId == null ) 
            throw new Exception("USERID is null. Please check if you have logged in");
        Map assignee = (Map)task.get("assignee");
        if(assignee!=null && assignee.get("objid") != null) {
            String assigneeId = (String)assignee.get("objid");
            if(assigneeId == null) return true;
            if(userId.equals(assigneeId)) {
                task.put("owner", true);
                return true;
            }
        } 
        boolean test = checkTaskOwner( task );
        if(test==true) {
            task.put("owner", true);
            return true;
        }
        task.put("owner", false);
        return false;
    }
    
}