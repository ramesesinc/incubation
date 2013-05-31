package com.rameses.osiris2;

import com.rameses.common.ExpressionResolver;
import com.rameses.common.MethodResolver;
import com.rameses.osiris2.flow.AbstractNode;
import com.rameses.osiris2.flow.EndNode;
import com.rameses.osiris2.flow.PageFlow;
import com.rameses.osiris2.flow.PageNode;
import com.rameses.osiris2.flow.ProcessNode;
import com.rameses.osiris2.flow.StartNode;
import com.rameses.osiris2.flow.Transition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageFlowInstance {
    
    private AbstractNode currentNode;
    private String name;
    private WorkUnitInstance workunit;
    
    PageFlowInstance( WorkUnitInstance wui ) {
        this.workunit = wui;
        currentNode = wui.getPageFlow().getStart();
    }
    
    public PageFlow getPageFlow() {
        return workunit.getPageFlow();
    }
    
    public void signal() {
        signal( null );
    }
    
    public void signal(String n ){
        currentNode = signalNode( currentNode, n );
    }
    

    
    private AbstractNode signalNode(AbstractNode prevNode, String n )
    {
        try 
        {
            Object data = workunit.getController();
            Transition t = findTransition(prevNode, n, data);
            fireTransitionAction(t);
            AbstractNode tempNode = findNode(t.getTo());
            fireNodeAction(tempNode);
            if ( tempNode instanceof ProcessNode ) {
                return signalNode( tempNode, null );
            } else {
                return tempNode;
            }
        }
        catch(RuntimeException re) {
            throw re;
        }
        catch(Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
    
    
    private Transition findTransition(AbstractNode node, String name,Object data) {
        Iterator iter = node.getTransitions().iterator();
        if( name !=null && name.length() > 0 ) {
            while(iter.hasNext()) {
                Transition t = (Transition)iter.next();
                if( isRendered(t, data) && name.equals(t.getName())) {
                    return t;
                }
            }
        } else {
            while(iter.hasNext()) {
                Transition t = (Transition)iter.next();
                if( isRendered(t, data) && isMatchedCondition(t,data) ) {
                    return t;
                }
            }
        }
        return null;
    }
    
    
    private boolean isMatchedCondition(Transition t, Object data) {
        if( t.getCond() != null ) {
            try {
                return ExpressionResolver.getInstance().evalBoolean(t.getCond(), data);
            }
            catch(Exception ex) {
                System.out.println("error pageflow rendered expr " + t.getName() + "->" + ex.getMessage());
                return false;
            }
        }
        return true;
    }
    
    private boolean isRendered(Transition t, Object data) {
        //do not render if it does not match the permissions
        if( t.getRendered() != null ) {
            try {
                return ExpressionResolver.getInstance().evalBoolean(t.getRendered(), data);
            }
            catch(Exception ex) {
                System.out.println("error pageflow rendered expr " + t.getName() + "->" + ex.getMessage());
                return false;
            }
        }
        return true;
    }
    
    
    
    private AbstractNode findNode(String n) {
        Iterator iter = getPageFlow().getNodes().iterator();
        while(iter.hasNext()) {
            AbstractNode anode = (AbstractNode)iter.next();
            if( anode.getName().equals(n)) {
                return anode;
            }
        }
        return null;
    }
    
    private void fireTransitionAction(Transition t) throws Exception{
        if( t.getAction() != null ) {
            MethodResolver.getInstance().invoke(workunit.getController(), t.getAction(), null );
        }
    }
    
    private void fireNodeAction(AbstractNode aNode) throws Exception {
        String act = null;
        if(aNode instanceof PageNode) {
            act = ((PageNode)aNode).getAction();
        } else if (aNode instanceof ProcessNode )  {
            act = ((ProcessNode)aNode).getAction();
        }
        if( act != null ) {
            MethodResolver.getInstance().invoke(workunit.getController(), act, null );
        }
    }

    
    public List getTransitions() {
        //return only transitions that are rendered
        Object bean = workunit.getController();
        List trans = new ArrayList();
        Iterator iter = currentNode.getTransitions().iterator();
        while(iter.hasNext()) {
            Transition t = (Transition)iter.next();
            if( isRendered( t, bean ) == true ) trans.add(t);
        }
        return trans;
    }
    
    public AbstractNode getCurrentNode() {
        return currentNode;
    }
    
    public boolean isEnded() {
        return (currentNode instanceof EndNode);
    }
    
    public boolean isStarted() {
        return !(currentNode instanceof StartNode);
    }
    
}