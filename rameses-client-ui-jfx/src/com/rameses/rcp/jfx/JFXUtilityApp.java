/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author wflores
 */
public class JFXUtilityApp extends Application {

    public void start(Stage stage) throws Exception {
        stage.setWidth(1);
        stage.setHeight(1);
        stage.setX(-200);
        stage.setY(-200);
        stage.initStyle(StageStyle.UTILITY); 
        stage.show(); 
    } 
    
    public static void main(String[] args) { 
        String[] fonts = new String[]{
            "fonts/OpenSans-Bold-webfont.ttf", 
            "fonts/OpenSans-Regular-webfont.ttf"
        };
        for ( String str : fonts ) { 
            try { 
                InputStream inp = JFXUtilityApp.class.getClassLoader().getResourceAsStream( str ); 
                if ( inp != null ) Font.loadFont(inp, 11); 
            } catch(Throwable t) {
                //t.printStackTrace();
            }
        }
        
        System.out.println("launch jfx application...");
        Application.launch(args); 
    }    
}
