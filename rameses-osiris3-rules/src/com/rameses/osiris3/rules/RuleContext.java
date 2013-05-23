/*
 * RuleContext.java
 *
 * Created on February 12, 2013, 9:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.rules;

import com.rameses.osiris3.core.MainContext;
import com.rameses.osiris3.core.OsirisServer;
import com.rameses.util.Service;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

/**
 *
 * @author Elmo
 */
public class RuleContext {
    
    private MainContext mainContext;
    private KnowledgeBase knowledgeBase;
    private List<RuleResource> providers;
    private String name;
    private KnowledgeBuilderConfiguration conf;
    
    public RuleContext(String name, MainContext m) {
        this.name = name;
        this.mainContext = m;
        
        String langLevel = (String) this.mainContext.getConf().get("drools.langLevel");
        Properties properties = new Properties();
        if(langLevel!=null) {
            properties.setProperty( "drools.dialect.java.compiler.lnglevel",langLevel );
        }
        properties.setProperty( "drools.dialect.java.compiler", "JANINO" );
        conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(properties);
    }
    
    public void start() throws Exception {
        //load the resource providers
        providers = new ArrayList();
        Iterator<RuleResource> iter = Service.providers( RuleResource.class, OsirisServer.class.getClassLoader() );
        while(iter.hasNext()) {
            RuleResource res = iter.next();
            res.setRuleContext( this );
            providers.add( res);
        }
        initKnowledgeBuilder();
    }
    
    public void stop() {
        
    }
    
    //this is called when starting up the rules
    public void initKnowledgeBuilder() throws Exception {
        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        RuleResource.Handler handler = new RuleResource.Handler() {
            public void handle(InputStream is) {
                builder.add( ResourceFactory.newInputStreamResource( is ), ResourceType.DRL );
            }
        };
        //build the facts
        for(RuleResource r: providers) {
            r.collectFactTypes( handler );
        }
        
        //build the rules
        for(RuleResource r: providers) {
            r.collectRules( handler );
        }
        knowledgeBase = builder.newKnowledgeBase();
    }
    
    public String getName() {
        return name;
    }
    
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }
    
    public MainContext getMainContext() {
        return mainContext;
    }
    
    public void addRulePackage(Reader reader) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( knowledgeBase, conf );
        kbuilder.add( ResourceFactory.newReaderResource(reader) , ResourceType.DRL );
        knowledgeBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    }
    
    public void removeRulePackage(String packageName) throws Exception {
        knowledgeBase.removeKnowledgePackage( packageName );
    }   
}
