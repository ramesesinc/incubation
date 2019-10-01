/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.io.FileUtil;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author ramesesinc
 */
public class TestImportRuleData extends TestCase {
    
    public TestImportRuleData(String testName) {
        super(testName);
    }

    public void test() throws Exception {
        String path = "C:/Users/ramesesinc/Downloads/Compressed/email_from_nabunturan/rule/vehicleassessment_rule";
        File f = new File( path );
        Object o = FileUtil.readObject(f);
        List<Map> rules = (List) o; 
        
        for (Map rule : rules) {
            System.out.println("***************");
            System.out.println("> "+ rule);
        }
        if ( true ) return;
        
    }   
    
    
    private String buildFact( Map data ) {
        String[] names = new String[]{ 
            "objid", "name", "title", "factclass", "sortorder",
            "handler", "defaultvarname", "dynamic", "lookuphandler",
            "lookupkey", "lookupvalue", "lookupdatatype", "dynamicfieldname",
            "builtinconstraints", "domain", "factsuperclass" 
        };
        return buildInsertScript("sys_rule_fact", names, data);
    }
    
    private String buildFactField( Map data ) {
        String[] names = new String[]{
            "objid", "parentid", "name", "title", "datatype", "sortorder", "handler", 
            "lookuphandler", "lookupkey", "lookupvalue", "lookupdatatype", 
            "multivalued", "required", "vardatatype", "lovname" 
        }; 
        return buildInsertScript("sys_rule_fact_field", names, data);
    }
    
    private String buildActionDef( Map data ) {
        String[] names = new String[]{ 
            "objid", "name", "title", "sortorder", "actionname", "domain", "actionclass"
        }; 
        return buildInsertScript("sys_rule_actiondef", names, data);
    }

    private String buildActionDefParam( Map data ) {
        String[] names = new String[]{ 
            "objid", "parentid", "name", "sortorder", "title", "datatype", "handler", 
            "lookuphandler", "lookupkey", "lookupvalue", "vardatatype", "lovname" 
        }; 
        return buildInsertScript("sys_rule_actiondef_param", names, data);
    }
    
    private String buildRuleset( Map data ) {
        String[] names = new String[]{ 
            "name", "title", "packagename", "domain", "role", "permission" 
        }; 
        return buildInsertScript("sys_ruleset", names, data);
    }
    
    private String buildRulegroup( Map data ) {
        String[] names = new String[]{ 
            "name", "ruleset", "title", "sortorder" 
        }; 
        return buildInsertScript("sys_rulegroup", names, data);
    }
    
    private String buildRulesetFact( Map data ) {
        String[] names = new String[]{ 
            "ruleset", "rulefact" 
        }; 
        return buildInsertScript("sys_ruleset_fact", names, data);
    }
    
    private String buildRulesetActionDef( Map data ) {
        String[] names = new String[]{ 
            "ruleset", "actiondef" 
        }; 
        return buildInsertScript("sys_ruleset_actiondef", names, data);
    }
    
    private String buildInsertScript( String tableName, String[] fieldNames, Map data ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (String name : fieldNames) {
            if ( fields.length() > 0 ) fields.append(", "); 
            if ( values.length() > 0 ) values.append(", ");
            
            fields.append( name ); 
            
            Object val = data.get(name); 
            if ( val == null ) {
                values.append("null");
            }
            else if ( val instanceof String ) {
                values.append("'"+ val +"'"); 
            }
            else if (val instanceof Date) {
                values.append("'"+ sdf.format(val) +"'");
            }
            else {
                values.append( val.toString()); 
            }
        }
        
        return "INSERT INTO "+ tableName +" (" + fields +") VALUES ("+ values +");";
    }
    
}
