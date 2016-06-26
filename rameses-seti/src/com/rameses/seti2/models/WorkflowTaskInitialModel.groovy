package com.rameses.seti2.models;

import com.rameses.seti2.models.CrudFormModel;

public class WorkflowTaskInitialModel extends CrudFormModel  {
    
    public void beforeSave(def mode) {
        entity._wf = true;
    }

    
}

