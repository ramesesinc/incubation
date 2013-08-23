/*
 * XActionBarBeanInfo.java
 *
 * Created on October 8, 2010, 1:37 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfo;
import com.rameses.beaninfo.editor.TextAlignmentPropertyEditor;
import com.rameses.beaninfo.editor.UIConstantsOrientationPropertyEditor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class XActionBarBeanInfo extends ComponentBeanInfo.Support 
{
    private Class beanClass; 
    
    public Class getBeanClass() 
    { 
        if (beanClass == null)  beanClass = XActionBar.class; 
        
        return beanClass; 
    }
    
    protected PropertyDescriptor[] createPropertyDescriptors() throws IntrospectionException 
    {
        return new PropertyDescriptor[] 
        {
            new PropertyDescriptor("border", getBeanClass()),
            
            new PropertyDescriptor("buttonAsHyperlink", getBeanClass(), "isButtonAsHyperlink", "setButtonAsHyperlink"),
            new PropertyDescriptor("buttonBorderPainted", getBeanClass(), "isButtonBorderPainted", "setButtonBorderPainted"),
            new PropertyDescriptor("buttonCaptionOrientation", getBeanClass()),
            new PropertyDescriptor("buttonContentAreaFilled", getBeanClass(), "isButtonContentAreaFilled", "setButtonContentAreaFilled"),
            new PropertyDescriptor("buttonFont", getBeanClass()),
            new PropertyDescriptor("buttonForeground", getBeanClass()),
            new PropertyDescriptor("buttonPreferredSize", getBeanClass()),
            new PropertyDescriptor("buttonTextInHtml", getBeanClass(), "isButtonTextInHtml", "setButtonTextInHtml"),
            
            new PropertyDescriptor("depends", getBeanClass()),
            new PropertyDescriptor("dynamic", getBeanClass(), "isDynamic", "setDynamic"),
            new PropertyDescriptor("index", getBeanClass()),
            new PropertyDescriptor("formName", getBeanClass()),

            installEditor(new PropertyDescriptor("textAlignment", getBeanClass()), TextAlignmentPropertyEditor.class),
            installEditor(new PropertyDescriptor("textPosition", getBeanClass()), TextAlignmentPropertyEditor.class), 
            installEditor(new PropertyDescriptor("orientation", getBeanClass()), UIConstantsOrientationPropertyEditor.class), 
            
            new PropertyDescriptor("horizontalAlignment", getBeanClass()),
            new PropertyDescriptor("orientationHAlignment", getBeanClass()),
            new PropertyDescriptor("orientationVAlignment", getBeanClass()),
            
            new PropertyDescriptor("padding", getBeanClass()),
            new PropertyDescriptor("showCaptions", getBeanClass(), "isShowCaptions", "setShowCaptions"),
            new PropertyDescriptor("spacing", getBeanClass()),
            new PropertyDescriptor("target", getBeanClass()),
            new PropertyDescriptor("useToolBar", getBeanClass(), "isUseToolBar", "setUseToolBar") 
        };        
    }    


    /*
    public void property(String propertyName, PropertyDescriptor desc) {
        if ( "orientation".equals(propertyName) )
            desc.setPropertyEditorClass(OrientationPropertyEditor.class);
        else if ( "orientationHAlignment".equals(propertyName) )
            desc.setPropertyEditorClass(HAlignmentPropertyEditor.class);
        else if ( "orientationVAlignment".equals(propertyName) )
            desc.setPropertyEditorClass(VAlignmentPropertyEditor.class);
        else if ( "buttonTemplate".equals(propertyName) )
            desc.setPropertyEditorClass(ButtonTemplatePropertyEditor.class);
        else if ( "buttonCaptionOrientation".equals(propertyName) )
            desc.setPropertyEditorClass(SwingCaptionOrientation.class);

    }
    */

    
}
