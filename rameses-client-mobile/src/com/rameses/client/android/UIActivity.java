/*
 * UIActivity.java
 *
 * Created on January 28, 2014, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.android;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 * @author wflores
 */
public abstract class UIActivity extends Activity
{
    
    protected final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onCreateProcess(bundle);
        DeviceManager.getInstance().register(this);         
    }

    protected final void onDestroy() {
        onDestroyProcess();
        DeviceManager.getInstance().unregister(this); 
        super.onDestroy();
    }
    
    protected void onCreateProcess(Bundle bundle) {
    }
    
    protected void onDestroyProcess() {
    }
}
