/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.io.AbstractChunkHandler;
import com.rameses.io.FileObject;
import com.rameses.rcp.common.FileUploadModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport.PropertyInfo;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIExpression;
import com.rameses.util.BreakException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public class XFileUpload extends JPanel implements UIControl, ActiveControl {
    
    public final static String STAT_READY       = "READY";
    public final static String STAT_PROCESSING  = "PROCESSING"; 
    
    public final static Insets ACTION_DEFAULT_MARGIN = new Insets(1,3,1,3); 
    public final static Insets ACTION_PRESS_MARGIN   = new Insets(2,4,0,2); 

    private final Color DEFAULT_PROGRESS_BAR_COLOR = new Color(176, 197, 227);
    private final Color DEFAULT_FOREGROUND = new Color(30,30,30);
    private final Color CANCELLED_FOREGROUND = new Color(150,150,150);
    
    private JFileChooser fileChooser;
    private BrowseAction cmdBrowse; 
    private CancelAction cmdCancel;
    private String status; 
    
    private FileChunkHandler filehandler;
    private FileUploadModel model; 
    private ProviderImpl provider;
    
    private Color progressBarColor; 
    private ProgressData progressData; 
    private ProgressValue progressValue;
    private FileNameLabel fileNameLabel;
    private FileSizeLabel fileSizeLabel;
    
    private String handler; 
    private String visibleWhen;
    private String disableWhen; 
    
    public XFileUpload() {
        super(); 
        initComponents();
    }
        
    
    // <editor-fold defaultstate="collapsed" desc=" init components ">  

    public void setLayout(LayoutManager mgr) {
        //do nothing 
    } 
    
    private void initComponents() { 
        status = STAT_READY; 
        provider = new ProviderImpl();
        fileChooser = new JFileChooser(); 
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        fileChooser.setMultiSelectionEnabled( false ); 
        progressBarColor = DEFAULT_PROGRESS_BAR_COLOR; 
        progressValue = new ProgressValue(); 
        fileNameLabel = new FileNameLabel();
        fileSizeLabel = new FileSizeLabel();
        
        cmdBrowse = new BrowseAction(); 
        cmdBrowse.init(); 
        cmdCancel = new CancelAction();
        cmdCancel.init();
        
        super.setLayout( new LayoutImpl() );
        setBackground(new Color(255,255,255)); 
        setBorder( BorderFactory.createLineBorder(new Color(180,180,180)) ); 
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showInfo( e ); 
            }
        });
        
        add( fileNameLabel ); 
        add( fileSizeLabel ); 
        add( progressValue ); 
        add( cmdBrowse ); 
        add( cmdCancel ); 
    } 

    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        ProgressData data = progressData; 
        if ( data == null ) return; 
        
        Graphics2D g2 = (Graphics2D) g.create();  
        if ( data.error != null ) {
            g2.setColor( Color.PINK ); 
            g2.fillRect(0, 0, getWidth(), getHeight()); 
            
        } else if ( data.cancelled ) {
            //do nothing 
            
        } else if ( data.success ) {
            //do nothing 
            
        } else {
            double dwidth = getWidth() * 1.0; 
            dwidth = (dwidth * data.percentage); 
            int iwidth = (int)dwidth; 

            Color color = getProgressBarColor(); 
            if ( color == null ) color = DEFAULT_PROGRESS_BAR_COLOR; 
            
            g2.setColor( color ); 
            g2.fillRect(0, 0, iwidth, getHeight()); 
        } 
        g2.dispose(); 
    }
    
    private void showInfo( MouseEvent e ) { 
        if ( progressData == null ) return; 
        
        Throwable error = progressData.error; 
        if ( error == null ) return; 
        
        MsgBox.err( error ); 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" getters/setters ">  
    
    public Color getProgressBarColor() { return progressBarColor; } 
    public void setProgressBarColor( Color progressBarColor ) {
        this.progressBarColor = progressBarColor; 
    }
    
    public String getStatus() { return status; }
    public void setStatus( String status ) {
        if ( STAT_PROCESSING.equals( status )) {
            this.status = status; 
        } else { 
            this.status = STAT_READY; 
        } 
    }
    
    public String getMessage() { 
        return ( fileNameLabel == null? null: fileNameLabel.getText()); 
    }
    public void setMessage( String message ) {
        if ( fileNameLabel != null ) { 
            fileNameLabel.setText( message ); 
        } 
    } 
    
    public FileUploadModel getModel() { return model; } 
    public void setModel( FileUploadModel model ) { 
        if ( this.model != null ) {
            this.model.setProvider( null ); 
        }
        this.model = model; 
        if ( model != null ) { 
            model.setProvider( provider ); 
        } 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler( String handler ) {
        this.handler = handler; 
    }
    
    public String getDisableWhen() { return disableWhen; }
    public void setDisableWhen( String disableWhen ) {
        this.disableWhen = disableWhen; 
    }

    public String getVisibleWhen() { return visibleWhen; }
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen; 
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" Progress ">  
    
    private class ProgressData { 
        
        String message; 
        String message2;
        double percentage; 
        Throwable error; 
        
        boolean success;
        boolean cancelled;
        
        ProgressData( String message ) {
            this( message, 0.0 ); 
        }
        ProgressData( String message, double percentage ) {
            this.message = message; 
            this.percentage = percentage; 
        }
    }
    
    private class FileNameLabel extends JLabel { 
        XFileUpload root = XFileUpload.this;
        
        FileNameLabel() {
            setBorder(BorderFactory.createEmptyBorder(1,5,1,5));
            setText(" Select a file to upload... "); 
        } 
        void update() { 
            ProgressData data = root.progressData;
            if ( data == null ) return; 
            
            String msg = data.message;
            setText( msg==null ? "..." : msg ); 
            
            if ( data.cancelled ) {
                setForeground( CANCELLED_FOREGROUND ); 
            } else {
                setForeground( DEFAULT_FOREGROUND ); 
            }
        }
    }
    private class FileSizeLabel extends JLabel { 
        XFileUpload root = XFileUpload.this;
        
        FileSizeLabel() { 
            super();
            setBorder(BorderFactory.createEmptyBorder(1,0,1,10));
        } 
        void update() { 
            ProgressData data = root.progressData;
            if ( data == null ) return; 
            
            String msg = data.message2;
            setText( msg==null ? "" : msg ); 
            
            if ( data.cancelled ) {
                setForeground( CANCELLED_FOREGROUND ); 
            } else {
                setForeground( DEFAULT_FOREGROUND ); 
            }
        }
    } 
    private class ProgressValue extends JLabel { 
        XFileUpload root = XFileUpload.this;
        
        ProgressValue() { 
            super();
            setBorder(BorderFactory.createEmptyBorder(1,0,1,5));
        } 
        
        void update() { 
            ProgressData data = root.progressData;
            if ( data == null ) return; 
            
            double percentage = data.percentage; 
            if ( percentage > 0.0 ) { 
                double maxwidth = root.getWidth() * 1.0; 
                double dwidth = (maxwidth * percentage); 
                dwidth = (dwidth / maxwidth) * 100.0;
                int iwidth = (int)dwidth; 
                setText(""+ iwidth + "%"); 
            } 
            if ( data.cancelled ) {
                setForeground( CANCELLED_FOREGROUND ); 
            } else {
                setForeground( DEFAULT_FOREGROUND ); 
            }
        }
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" Actions ">  
    
    private abstract class AbstractButton extends JButton { 
        
        XFileUpload root = XFileUpload.this; 
        
        void init() {
            setMargin( ACTION_DEFAULT_MARGIN ); 
            setBorderPainted( false ); 
            setContentAreaFilled( false ); 
            ImageIcon icon = getPreferredIcon(); 
            setIcon( icon ); 
            
            ActionMouseAdapter mouseAdapter = new ActionMouseAdapter();
            mouseAdapter.init(this);
            
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    process( e ); 
                }
            });
        } 

        public boolean isEnabled() { 
            if ( root == null ) {
                return super.isEnabled(); 
            } else {
                return root.isEnabled(); 
            }
        }
        
        ImageIcon getPreferredIcon() { return null; }        
        void process( ActionEvent e ) { }        
    }
    
    private class BrowseAction extends AbstractButton {
        
        ImageIcon getPreferredIcon() {
            return ImageIconSupport.getInstance().getIcon("images/fileupload_browse.png"); 
        }

        void process( ActionEvent e ) { 
            int opt = root.fileChooser.showOpenDialog( root ); 
            if ( opt == JFileChooser.APPROVE_OPTION ) { 
                File file = fileChooser.getSelectedFile(); 
                process( file ); 
            } 
        } 
        
        void process( final File file ) { 
            Runnable proc = new Runnable() {
                public void run() { 
                    filehandler = new FileChunkHandler(); 
                    new FileObject( file ).read( filehandler ); 
                }
            };
            new Thread( proc ).start(); 
        } 
    } 
    
    private class CancelAction extends AbstractButton { 
        
        ImageIcon getPreferredIcon() {
            return ImageIconSupport.getInstance().getIcon("images/fileupload_cancel.png"); 
        }
        
        void process( ActionEvent e ) { 
            if ( MsgBox.confirm("Cancel upload?")) {
                filehandler.cancel(); 
            } 
        } 
    }
    
    private class ActionMouseAdapter extends MouseAdapter { 
        
        private JButton button; 
        private boolean has_pressed;
        
        void init( JButton button ) {
            if ( this.button != null ) {
                this.button.removeMouseListener(this);
            }
            this.has_pressed = false; 
            this.button = button; 
            if ( button != null ) {
                button.removeMouseListener(this);
                button.addMouseListener(this);
            } 
        } 
        
        public void mouseClicked(MouseEvent e) { 
            has_pressed = false; 
            if ( button != null ) {
                button.setMargin( ACTION_DEFAULT_MARGIN ); 
            }
        }
        public void mousePressed(MouseEvent e) {
            has_pressed = true; 
            if ( button != null ) {
                button.setMargin( ACTION_PRESS_MARGIN ); 
            }
        }
        public void mouseReleased(MouseEvent e) { 
            has_pressed = false; 
            if ( button != null ) {
                button.setMargin( ACTION_DEFAULT_MARGIN ); 
            } 
        }
        public void mouseEntered(MouseEvent e) {
            if ( button != null && has_pressed ) {
                button.setMargin( ACTION_PRESS_MARGIN ); 
            } 
        }
        public void mouseExited(MouseEvent e) {
            if ( button != null && has_pressed ) {
                button.setMargin( ACTION_DEFAULT_MARGIN ); 
            }
        }
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" LayoutImpl ">  
    
    private class LayoutImpl implements LayoutManager {

        XFileUpload root = XFileUpload.this; 
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize( parent ); 
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize( parent ); 
        }
        
        private Component[] getActionComponents() {
            return new Component[]{ cmdBrowse, cmdCancel }; 
        }
        private Component getActionComponent() { 
            String stat = root.getStatus(); 
            if ( STAT_PROCESSING.equals( stat ) ) { 
                return root.cmdCancel; 
            } else { 
                return root.cmdBrowse; 
            } 
        } 

        private Dimension getLayoutSize( Container parent ) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = new Component[]{
                    root.fileNameLabel, root.fileSizeLabel, 
                    root.progressValue, getActionComponent() 
                };
                for (int i=0; i<comps.length; i++ ) {
                    Component c = comps[i];
                    if ( c == null || !c.isVisible() ) {
                        continue; 
                    }
                    
                    Dimension dim = c.getPreferredSize(); 
                    h = Math.max(h, dim.height); 
                    w += dim.width; 
                } 
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = getActionComponents(); 
                for ( int i=0; i<comps.length; i++ ) {
                    if ( comps[i] == null ) continue; 
                    
                    comps[i].setBounds(0, 0, 0, 0); 
                } 
                
                Component c = getActionComponent();
                if ( c != null && c.isVisible() ) {
                    Dimension dim = c.getPreferredSize(); 
                    int x1 = (w - dim.width) + margin.left; 
                    int y1 = Math.max((h / 2)-(dim.height / 2), 0);
                    c.setBounds(x1, y+y1, dim.width, dim.height); 
                    w -= dim.width; 
                } 
                
                comps = new Component[]{  
                    root.progressValue, root.fileSizeLabel 
                }; 
                for (int i=0; i<comps.length; i++) {
                    if ( comps[i]==null || !comps[i].isVisible() ) continue; 
                    
                    Dimension dim = comps[i].getPreferredSize();
                    int x1 = (w - dim.width) + margin.left; 
                    comps[i].setBounds(x1, y, dim.width, h); 
                    w -= dim.width; 
                } 
                
                c = root.fileNameLabel; 
                if ( c != null && c.isVisible() ) {
                    Dimension dim = c.getPreferredSize(); 
                    c.setBounds(x, y, w, h); 
                } 
            }
        }
    } 
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">  
    
    private class ProviderImpl implements FileUploadModel.Provider {
        String fileid; 
        String filename;
        String filetype;
        long filesize;
        int chunkcount;
        
        public String getFileId() { return fileid; } 
        public String getFileName() { return filename; }
        public String getFileType() { return filetype; }
        public long getFileSize() { return filesize; }
        public int getChunkCount() { return chunkcount; }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FileChunkHandler ">  
    
    private class FileChunkHandler extends AbstractChunkHandler {

        XFileUpload root = XFileUpload.this; 
        
        Throwable error;
        double filesize; 
        double chunksize;
        
        void refreshComponents() { 
            root.fileNameLabel.update();
            root.fileSizeLabel.update();            
            root.progressValue.update();
            root.revalidate(); 
            root.repaint(); 
        } 
        
        public void start() { 
            root.setStatus( STAT_PROCESSING ); 
            root.progressData = new ProgressData("Reading file information..."); 
            refreshComponents();
                        
            root.provider.fileid = getMeta().getId();             
            root.provider.filename = getMeta().getFileName(); 
            root.provider.filetype = getMeta().getFileType();
            root.provider.filesize = getMeta().getFileSize();
            root.provider.chunkcount = getMeta().getChunkCount();
            
            String stype = null; 
            double dfilesize = getMeta().getFileSize() * 1.0;
            String[] sizetypes = new String[]{ "KB", "MB", "GB" }; 
            for ( int i=0; i<sizetypes.length; i++ ) {
                stype = sizetypes[i]; 
                dfilesize = (dfilesize / 1024.0); 
                if ( dfilesize < 1024.0 ) { 
                    dfilesize = new BigDecimal( dfilesize ).setScale(2, RoundingMode.UP).doubleValue(); 
                    break; 
                } 
            } 
            
            dfilesize = new BigDecimal( dfilesize ).setScale(2, RoundingMode.UP).doubleValue(); 
            root.progressData = new ProgressData( root.provider.filename ); 
            root.progressData.message2 = dfilesize +" "+ stype; 
            refreshComponents(); 

            filesize = root.provider.filesize * 1.0; 
            chunksize = 0.0;             
            fireOnStart();
        }
        
        public void end() { 
            root.setStatus( STAT_READY ); 
            if ( error == null ) { 
                if ( isCancelled() ) { 
                    if ( root.progressData != null ) { 
                        root.progressData.cancelled = true; 
                    } 
                    refreshComponents(); 
                } else { 
                    if ( root.progressData != null ) { 
                        root.progressData.success = true; 
                    } 
                    refreshComponents();
                    fireOnComplete(); 
                } 
            } else { 
                try { 
                    if ( root.progressData != null ) {
                        root.progressData.error = error; 
                        refreshComponents(); 
                    } 
                    
                    fireUpdateBeanValue( null ); 
                    FileUploadModel fum = root.getModel(); 
                    if ( fum != null ) { 
                        fum.onerror( error );
                    } 
                } catch( BreakException be ) { 
                    //do nothing 
                } catch( Exception e ) { 
                    MsgBox.err( e );
                } catch( Throwable t ) {
                    MsgBox.err( t ); 
                } 
            }             
        }
        
        public void handle(int indexno, byte[] bytes) { 
            try { 
                if ( root.progressData != null ) {
                    chunksize += bytes.length; 
                    BigDecimal bd = new BigDecimal(chunksize / filesize); 
                    double value = bd.setScale(2, RoundingMode.HALF_UP).doubleValue(); 
                    root.progressData.percentage = value; 
                } 
                refreshComponents(); 
                
            } catch(Throwable t) {
                error = t; 
                cancel(); 
            } 
        } 
        
        void fireOnStart() {
            FileUploadModel fum = root.getModel(); 
            if ( fum == null ) { return; } 
            
            try {
                fum.onstart(); 
            } catch(Throwable t) { 
                error = t; 
                cancel();
            } 
        } 
        void fireOnComplete() {
            Map data = new HashMap();
            data.put("fileid", root.provider.fileid);
            data.put("filename", root.provider.filename);
            data.put("filetype", root.provider.filetype);
            data.put("filesize", root.provider.filesize);
            data.put("chunkcount", root.provider.chunkcount);
            
            try { 
                fireUpdateBeanValue( data ); 
                
                FileUploadModel fum = root.getModel(); 
                if ( fum != null ) {
                    fum.oncomplete( data );
                } 
            } catch(Throwable t) { 
                MsgBox.err( t ); 
            } 
        } 
        void fireUpdateBeanValue( Map data ) { 
            String name = root.getName(); 
            if ( name != null && name.length() > 0 ) {
                UIControlUtil.setBeanValue(root.getBinding(), name, data); 
            }
        }
        
        LinkedBlockingQueue BLOCKER = new LinkedBlockingQueue();
        void locked() {
            try { 
                BLOCKER.poll(25, TimeUnit.MILLISECONDS);
            } catch (Throwable ex) {
                ;
            }
        }
    } 
        
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl ">  
    
    private Binding binding; 
    private String[] depends; 
    private int index; 
    private int stretchWidth;
    private int stretchHeight;
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { 
        this.binding = binding; 
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex( int index ) {
        this.index = index; 
    } 

    public int getStretchWidth() { return stretchWidth; }
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth;
    }

    public int getStretchHeight() { return stretchHeight; }
    public void setStretchHeight(int stretchHeight) { 
        this.stretchHeight = stretchHeight; 
    }

    public int compareTo( Object o ) { 
        return UIControlUtil.compare(this, o); 
    } 

    public void setPropertyInfo(PropertyInfo info) {
    } 
    
    public void load() { 
    } 

    public void refresh() { 
        Object o = UIControlUtil.getBeanValue(getBinding(), getHandler()); 
        if ( o instanceof FileUploadModel ) {
            setModel( (FileUploadModel) o ); 
        } else if ( o == null ) {
            setModel( null ); 
        } else { 
            System.out.println("[WARN] handler must be an instance of FileUploadModel");
            setModel( null ); 
        }
        
        UIExpression uix = new UIExpression(); 
        uix.disableWhen( this, getDisableWhen() ); 
        uix.visibleWhen( this, getVisibleWhen() ); 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold> 
}
