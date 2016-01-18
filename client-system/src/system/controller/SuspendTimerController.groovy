package system.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.util.*;
import com.rameses.osiris2.client.*;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import com.rameses.rcp.framework.ClientContext;

public class SuspendTimerController extends CountDownTimer implements AWTEventListener 
{
    long _maxSeconds;

    public def init() {
        _maxSeconds = 300;
        ClientContext.currentContext.taskManager.addTask( this );
        Toolkit.defaultToolkit.addAWTEventListener( this, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK );
        return "_close";
    }

    public long getMaxSeconds() {
        return _maxSeconds;
    }

    public void onProgress() {;}

    def resetHandler = {
        super.reset();
        ClientContext.currentContext.taskManager.addTask( this );
    }

    public void onTimeout() {
        try {
            String sessionId = OsirisContext.env.SESSIONID;
            if (sessionId == null) {
                //This is the fixed when the user log offs the system 
                //If no sessionid is specified then do nothing
                return; 
            } 

            def invoker = InvokerUtil.lookup("system:suspend")[0];
            InvokerUtil.invoke(invoker, [resetHandler: resetHandler]);
        }
        catch(Warning w) {
            OsirisContext.mainWindowListener.clear();
            ClientContext.currentContext.platform.logoff();
        }
    }

    public void eventDispatched(AWTEvent event) {
        super.reset();
    }
}
