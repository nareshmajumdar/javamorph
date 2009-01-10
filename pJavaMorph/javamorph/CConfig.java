package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CConfig.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Data list of the application's global data (shown as JDialog).
 * <br/>
 * Hint: Loading and saving to the propertie's file.
 */
public class CConfig extends JDialog implements Runnable, WindowListener{
    /** Java API. */
    private static final long serialVersionUID = 1L;
    /** Rows of the mesh. Count of both windows is identical. */
    public static int ROWS_OF_MESH = 10;
    /** Columns of the mesh. Count of both windows is identical. */
    public static int COLUMNS_OF_MESH = 15;
    /** Points of the left polygon. Appears within the left window. */
    public static int POINTS_OF_LEFT_POLYGON = 4;
    /** Points of the right  polygon. Appears within the right window. */
    public static int POINTS_OF_RIGHT_POLYGON = 5;
    /** Number of morph steps. Steps between the pictures counted. */
    public static int NUM_OF_MORPH_STEPS = 5;
    /** Smooth radius of the polygon. Fuzzy polygon clipping done. */
    public static int SMOOTH_RADIUS = 19;
    /** Size of the marker points for mesh & polygon. */
    public static int MARK_SIZE = 2;
    /** Property object. Can load and store the numerical data from file. */
    private Properties props = new Properties();
    /** Parent JFrame to enable modal behavior. */
    private JFrame parent;
    /** Left morph picture componenent. */
    private CFrame left;
    /** Right morph picture component. */
    private CFrame right;
    /** Edit field for the property with the same name. */
    private CEditField rows_of_mesh = new CEditField(
            "Rows of mesh : ",
            ROWS_OF_MESH,
            1,
            99,
            true
        );
    /** Edit field for the property with the same name. */
    private CEditField columns_of_mesh = new CEditField(
            "Columns of mesh : ",
            COLUMNS_OF_MESH,
            1,
            99,
            true
        );
    /** Edit field for the property with the same name. */
    private CEditField points_of_left_polygon = new CEditField(
            "Points of left polygon : ",
            POINTS_OF_LEFT_POLYGON,
            3,
            99,
            true
        );
    /** Edit field for the property with the same name. */
    private CEditField points_of_right_polygon = new CEditField(
            "Points of right polygon : ",
            POINTS_OF_RIGHT_POLYGON,
            3,
            99,
            true
        );
    /** Edit field for the property with the same name. */
    private CEditField num_of_morph_steps = new CEditField(
            "Num of morph steps : ",
            NUM_OF_MORPH_STEPS,
            1,
            999,
            true
        );
    /** Edit field for the property with the same name. */
    private CEditField smooth_radius = new CEditField(
            "Smooth radius : ",
            SMOOTH_RADIUS,
            1,
            999,
            true
    );
    /** Display field for the working directory with the same name. */
    private CEditField working_dir = new CEditField(
            "Workdir : ",
            CStrings.WORKDIR,
            false
        );
    /**
     * Constructor.
     * @param parent The parent JFrame.
     * @param left Left picture displaying component.
     * @param right Right picture displaying component.
     */
    public CConfig(JFrame parent, CFrame left, CFrame right){
        super(parent, "Config", true);
        this.left = left;
        this.right = right;
        this.parent = parent;
        this.getContentPane().setLayout(new GridLayout(7, 1));
        this.getContentPane().add(this.num_of_morph_steps);
        this.getContentPane().add(this.rows_of_mesh);
        this.getContentPane().add(this.columns_of_mesh);
        this.getContentPane().add(this.points_of_left_polygon);
        this.getContentPane().add(this.points_of_right_polygon);
        this.getContentPane().add(this.smooth_radius);
        this.getContentPane().add(this.working_dir);
        this.getContentPane().setSize(this.getContentPane().getPreferredSize());
        this.pack();
        Runtime.getRuntime().addShutdownHook(new Thread(this));
        File p = new File(CStrings.PROPS);
        String s;
        if(p.exists()){
            try{
                props.load(new FileInputStream(p));
                s = props.getProperty("ROWS_OF_MESH");
                ROWS_OF_MESH = Integer.parseInt(s);
                s = props.getProperty("COLUMNS_OF_MESH");
                COLUMNS_OF_MESH = Integer.parseInt(s);
                s = props.getProperty("POINTS_OF_LEFT_POLYGON");
                POINTS_OF_LEFT_POLYGON = Integer.parseInt(s);
                s = props.getProperty("POINTS_OF_RIGHT_POLYGON");
                POINTS_OF_RIGHT_POLYGON = Integer.parseInt(s);
                s = props.getProperty("NUM_OF_MORPH_STEPS");
                NUM_OF_MORPH_STEPS = Integer.parseInt(s);
                s = props.getProperty("SMOOTH_RADIUS");
                SMOOTH_RADIUS = Integer.parseInt(s);
                s = props.getProperty("MARK_SIZE");
                MARK_SIZE = Integer.parseInt(s);
            }catch(Exception e){
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }else{
            props.setProperty("ROWS_OF_MESH", "" + ROWS_OF_MESH);
            props.setProperty("COLUMNS_OF_MESH", "" + COLUMNS_OF_MESH);
            props.setProperty
            ("POINTS_OF_LEFT_POLYGON", "" + POINTS_OF_LEFT_POLYGON);
            props.setProperty
            ("POINTS_OF_RIGHT_POLYGON", "" + POINTS_OF_RIGHT_POLYGON);
            props.setProperty("NUM_OF_MORPH_STEPS", "" + NUM_OF_MORPH_STEPS);
            props.setProperty("SMOOTH_RADIUS", "" + SMOOTH_RADIUS);
            props.setProperty("MARK_SIZE", "" + MARK_SIZE);
        }
        this.addWindowListener(this);
    }
    /**
     * Show the config dialog when requested by user's menu.
     */
    public void open(){        
        this.setResizable(false);
        this.setLocation
        (parent.getLocation().x + 10, parent.getLocation().y + 10);
        this.setVisible(true);
    }
    /**
     * Shutdown hook.
     * On shutdown save the configuration data into the property file.
     */
    public void run(){
        File p = new File(CStrings.PROPS);
        try{
            props.setProperty("ROWS_OF_MESH", "" + ROWS_OF_MESH);
            props.setProperty("COLUMNS_OF_MESH", "" + COLUMNS_OF_MESH);
            props.setProperty
            ("POINTS_OF_LEFT_POLYGON", "" + POINTS_OF_LEFT_POLYGON);
            props.setProperty
            ("POINTS_OF_RIGHT_POLYGON", "" + POINTS_OF_RIGHT_POLYGON);
            props.setProperty("NUM_OF_MORPH_STEPS", "" + NUM_OF_MORPH_STEPS);
            props.setProperty("SMOOTH_RADIUS", "" + SMOOTH_RADIUS);
            props.setProperty("MARK_SIZE", "" + MARK_SIZE);
            props.store(new FileOutputStream(p), CStrings.PROG);
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /** Event API. */
    public void windowActivated(WindowEvent e){}
    /** Event API. */
    public void windowClosed(WindowEvent e){}
    /**
     * When the user closes the dialog -> assign the dialog content to this
     * program's global configuration. Replace mesh & polygon separately if
     * necessary.
     */
    public void windowClosing(WindowEvent e){
        boolean
            msh = COLUMNS_OF_MESH != columns_of_mesh.getNumber() ||
                ROWS_OF_MESH != rows_of_mesh.getNumber(),
            pgl = POINTS_OF_LEFT_POLYGON != points_of_left_polygon.getNumber(),
            pgr = POINTS_OF_RIGHT_POLYGON !=
                points_of_right_polygon.getNumber();
        COLUMNS_OF_MESH = columns_of_mesh.getNumber();
        ROWS_OF_MESH = rows_of_mesh.getNumber();
        POINTS_OF_LEFT_POLYGON = points_of_left_polygon.getNumber();
        POINTS_OF_RIGHT_POLYGON = points_of_right_polygon.getNumber();
        SMOOTH_RADIUS = smooth_radius.getNumber();
        NUM_OF_MORPH_STEPS = num_of_morph_steps.getNumber();
        if(msh){
            left.deleteMesh();
            right.deleteMesh();
        }
        if(pgl){
            left.deletePolygon();
        }
        if(pgr){
            right.deletePolygon();
        }
    }
    /** Event API. */
    public void windowDeactivated(WindowEvent e){}
    /** Event API. */
    public void windowDeiconified(WindowEvent e){}
    /** Event API. */
    public void windowIconified(WindowEvent e){}
    /** 
     * When the user opens the dialog then read this application's global
     * data into the shown dialog.
     */
    public void windowOpened(WindowEvent e){
        this.columns_of_mesh.setValue(COLUMNS_OF_MESH);
        this.rows_of_mesh.setValue(ROWS_OF_MESH);
        this.points_of_left_polygon.setValue(POINTS_OF_LEFT_POLYGON);
        this.points_of_right_polygon.setValue(POINTS_OF_RIGHT_POLYGON);
        this.smooth_radius.setValue(SMOOTH_RADIUS);
        this.num_of_morph_steps.setValue(NUM_OF_MORPH_STEPS);
        this.working_dir.setValue(CStrings.WORKDIR);
    }
}
