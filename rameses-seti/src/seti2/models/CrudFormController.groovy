package seti2.models;
 
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.common.*;
        
public class CrudFormController {
        
    @Binding
    def binding;

    @Controller
    def workunit;
        
    @Invoker
    def invoker;
    
    @Service("PersistenceService")
    def service;

    @Service("SchemaService")
    def schemaService;
    
    @Caller
    def caller;

    def selectedEntity;
    def schemaName;
    def adapter;
    def schema;
    def entity;
    
    String role;
    String domain;
    String permission;
    
    @FormTitle
    def title;
    
    def mode;
    def formControls = [];
    def secProvider = ClientContext.getCurrentContext().getSecurityProvider();
    
    boolean isCreateAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".create" );
    }
        
    boolean isOpenAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".open" );
    }

    boolean isDeleteAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".delete" );
    }
                
    boolean isEditAllowed() { 
        if( !role ) return true;
        return secProvider.checkPermission( domain, role, schemaName+".edit" );
    }

    void initSchema() {
        if( !schema ) {
            schema = schemaService.getSchema( [name: schemaName, adapter: adapter]  );
        }   
    }
    
    void init() {
        initSchema();
        buildFormInfos();
    }
    
    def create() {
        mode = "create";
        entity = [:];
        def  _sname = workunit.info.workunit_properties.schemaName;
        if( _sname ) schemaName = _sname;
        entity._schemaname = schemaName;
        init();
        return "create";
    }
    
    def open() {
        mode = "read";
        def  _sname = workunit.info.workunit_properties.schemaName;
        if( _sname ) schemaName = _sname;
        //we need to set the schemaname that will be used for open
        entity._schemaname = schemaName;
        entity = service.read( entity );
        
        //we need to set the schema name for update.
        entity._schemaname = schemaName;
        init();
        return "read";
    }
    
    def edit() {
        mode = "edit";
        entity = new DataMap( entity );
        return "edit";
    }
    
    def unedit() {
        mode = "read";
        entity.unedit();
        entity = entity.data();
        return "read";
    }
    
    def save() {
        if(MsgBox.confirm('You are about to save this record. Proceed?')) {
            if( mode == 'create' ) {
                entity = service.create( entity );
            }
            else {
                //extract from the DataMap
                def e = entity.data();
                entity = service.update( e );
            }
        }
        mode = "read";
        try {
            caller?.refresh();
        }
        catch(ign){;}
        return "read";
    }
    
    void undo() {
        def v = entity.undo();
        //formPanel.reload();
    }
    
    def formPanel = [
        getCategory: { key->
           return "";
        },
        updateBean: {name,value,item->
            item.bean.value = value;
        },
        getControlList: {
            return formControls;
        }
    ] as FormPanelModel;

    
    /*
    def sortInfos(sinfos) {
        def list = sinfos.findAll{it.lob?.objid==null && it.attribute.category==null}?.sort{it.attribute.sortorder};
        def catGrp = sinfos.findAll{it.lob?.objid==null && it.attribute.category!=null};
        if(catGrp) {
            def grpList = catGrp.groupBy{ it.attribute.category };
            grpList.each { k,v->
                v.sort{ it.attribute.sortorder }.each{z->
                    list.add( z );
                }
            }
        }
        list = list + sinfos.findAll{ it.lob?.objid!=null }?.sort{ [it.lob.name, it.attribute.sortorder] }; 
        return list; 
    }
    */

    /*
    def findValue( info ) {
        if(info.lob?.objid!=null) {
            def filter = existingInfos.findAll{ it.lob?.objid!=null };
            def m = filter.find{ it.lob.objid==info.lob.objid && it.attribute.objid == info.attribute.objid };
            if(m) return m.value;
        }
        else {
            def filter = existingInfos.findAll{ it.lob?.objid==null };
            def m = filter.find{ it.attribute.objid == info.attribute.objid };
            if(m) return m.value;
        }
        return null;
     }
     */
    
     def buildFormInfos() {
        formControls.clear();
        def infos = schema.columns;
        //infos = sortInfos( infos );
        for( x in infos ) {
            if( x.primary && !x.visible ) continue;
            def i = [
                caption:x.caption, 
                name:'entity.'+x.name,
                value: entity.get( x.name )
            ];
            //fix the datatype
            if( !x.type ) {
                i.type = "text";
            }
            else {
                i.type = x.type;
            }
            /*
            x.datatype = x.attribute.datatype;
            if(x.datatype.indexOf("_")>0) {
                x.datatype = x.datatype.substring(0, x.datatype.indexOf("_"));
            }
            if(i.type == "boolean") {
                i.type = "subform";
                i.handler = "business_application:yesno";
                i.properties = [item:x];
            }
            else if(i.type == "string_array") {
                i.type = "combo";
                i.preferredSize = '150,20';
                i.itemsObject = x.attribute.arrayvalues;
            }
            else if( i.type == 'decimal' ) {
                i.preferredSize = '150,20';
            }
            else if( i.type == "string" ) {
                i.type = "text";
            }
            else if( i.type == "info") {
                i.type = "subform";
                i.properties = [item:i.bean];
                i.showCaption = false;
            }
            */
            i.required = x.required;
            formControls << i;
        }
     }

    def showDropdownMenu() {
        def op = new PopupMenuOpener();
        op.add( new Opener(caption:'New', action:'create', target:'process') );
        
        //op.addAll( Inv.lookupOpeners("xxx:sample") );
        op.add( new Opener(caption:'Close', action:'_close', target:'process') );
        return op;
    }
        
}