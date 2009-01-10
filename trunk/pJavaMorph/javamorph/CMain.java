package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CMain.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Main entry of the morph application.
 * <br/>
 * Hint: Merging two pictures (for example human faces) with intermediate steps.
 */
public class CMain extends JPanel 
    implements WindowListener, LayoutManager{
    /** Java API. */
    private static final long serialVersionUID = 1L;
    /** Minimum size of this application on the screen. */
    public static final Dimension MIN_SIZE = new Dimension(100,200);
    /** Left picture's display. */
    private CFrame left = new CFrame(this, true);
    /** Right picture's display. */
    private CFrame right = new CFrame(this, false);
    /** Separator line between booth displays. */
    private CSeparator sep = new CSeparator();
    /** JFrame, as top level window on the screen. */
    private JFrame frame = new JFrame(CStrings.PROG + ", " + CStrings.VERSION);
    /** Global info window of this program. */
    private CAbout about = new CAbout(frame);
    /** Progress bar, shown when morphing a number of intermediate pictures. */
    private CProgress progress = new CProgress(frame);
    /** Configuration dialog with file storing functionality. */
    private CConfig config = new CConfig(frame, left, right);
    /**
     *  Constructor. Laying out the applicaiton window.
     */
    public CMain(){
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        InputStream in = loader.getResourceAsStream("JavaMorph.png");
        try{
            BufferedImage icon = ImageIO.read(in);
            this.frame.setIconImage(icon);
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        this.setLayout(this);
        this.add(left);
        this.add(right);
        this.add(sep);
        this.frame.setLocation(50, 50);
        this.frame.getContentPane().add(this);
        this.frame.addWindowListener(this);
        this.frame.pack();
        this.left.load(
                CStrings.LEFT_INPUT, 
                CStrings.LEFT_MESH, 
                CStrings.LEFT_POLYGON);
        this.right.load(
                CStrings.RIGHT_INPUT, 
                CStrings.RIGHT_MESH, 
                CStrings.RIGHT_POLYGON);
        this.frame.setVisible(true);
    }
    /**
     * Main entry.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        if(CStrings.initialize()){
            CMain prog = new CMain();
            System.out.println("Main class = " +prog + '.');
        }
    }
    /**
     * Show the about global info.
     */
    public void showAboutDialog(){
        about.open();
    }
    /**
     * Show the configuration dialog.
     */
    public void showConfigDialog(){
        config.open();
    }
    /** Event API. */
    public void windowActivated(WindowEvent e){}
    /** Event API. */
    public void windowClosed(WindowEvent e) {}
    /** Event API. Saving also the meshes on program exit.*/
    public void windowClosing(WindowEvent e){
        this.left.save(CStrings.LEFT_MESH, CStrings.LEFT_POLYGON);
        this.right.save(CStrings.RIGHT_MESH, CStrings.RIGHT_POLYGON);
        System.exit(0);
    }
    /** Event API. */
    public void windowDeactivated(WindowEvent e) {}
    /** Event API. */
    public void windowDeiconified(WindowEvent e) {}
    /** Event API. */
    public void windowIconified(WindowEvent e) {}
    /** Event API. */
    public void windowOpened(WindowEvent e) {}
    /** Layout manager API. */
    public void addLayoutComponent(String name, Component comp){}
    /** 
     * Setting the bounds of the application's main frame's componentes.
     */
    public void layoutContainer(Container parent) {
        Dimension size = parent.getSize();
        Rectangle bounds = new Rectangle();
        int s = this.sep.getPreferredSize().width;
        bounds.x = 0;
        bounds.y = 0;
        bounds .width = size.width / 2 - s / 2;
        bounds .height = size.height;
        this.left.setBounds(bounds);
        bounds.x = bounds.width;
        bounds.width = s;
        this.sep.setBounds(bounds);
        bounds.x += bounds.width;
        bounds.width = 
            size.width - 
            this.sep.getSize().width / 2 -
            this.left.getSize().width;
        this.right.setBounds(bounds);
    }
    /** 
     * Provide the minimum layout size of the JFrame.
     */
    public Dimension minimumLayoutSize(Container parent) {
        return CMain.MIN_SIZE;
    }
    /**
     * Ask nested components to calculate the JFrame window size on screen.
     */
    public Dimension preferredLayoutSize(Container parent) {
        int 
            width = 
                this.left.getPreferredSize().width +
                this.right.getPreferredSize().width +
                this.sep.getPreferredSize().width,
            height = Math.max(
                this.left.getPreferredSize().height,
                this.right.getPreferredSize().height
                );
                
        Dimension size = new Dimension(width, height);
        return size;
    }
    /** Layout manager API. */
    public void removeLayoutComponent(Component comp){}
    /**
     * Perform the morph operation. Show the progress bar during rendering.
     */
    public void save(){
        /* Store system time to calculate the duration. */
        long time = System.currentTimeMillis();
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        CMorphOperator morph = new CMorphOperator(this, 
            this.left.getMesh(),
            this.right.getMesh(),
            this.left.getClip(new File(CStrings.LEFT_DEBUG)),
            this.right.getClip(new File(CStrings.RIGHT_DEBUG)),
            this.left.getContent(),
            this.right.getContent(),
            this.progress);
        /* Not blocking. */
        new Thread(morph).start();
        /* Blocking made here! */
        progress.open(morph);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        time = System.currentTimeMillis() - time;
        time /= 1000;
        /* Print duration to the console. */
        System.out.println("Duration = " + time + " seconds.");
    }
    /**
     * Get function.
     * @return Top level window JFrame.
     */
    public JFrame getFrame(){
        return this.frame;
    }
}
