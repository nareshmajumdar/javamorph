package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;

/**
 * File belongs to javamorph (Merging of human-face-pictures).
 * Copyright (C) 2009 - 2010  Claus Wimmer
 * See file ".../help/COPYING" for details!
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA * 
 *
 * @version 1.5
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph.
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
    /** User is adding mesh points with the GUI. */
    public static final int EDIT_MESH_ADD = 1;
    /** User is removing mesh points with the GUI. */
    public static final int EDIT_MESH_SUB = 2;
    /** User is moving mesh points with the GUI. */
    public static final int EDIT_MESH_OFF = 3;
    /** User is adding polygon points with the GUI. */
    public static final int EDIT_POLYGON_ADD = 4;
    /** User is removing polygon points with the GUI. */
    public static final int EDIT_POLYGON_SUB = 5;
    /** User is moving polygon points with the GUI. */
    public static final int EDIT_POLYGON_OFF = 6;
    /** Rows of the mesh. Count of both windows is identical. */
    public static int ROWS_OF_MESH = 10;
    /** Columns of the mesh. Count of both windows is identical. */
    public static int COLUMNS_OF_MESH = 15;
    /** Points of the left polygon. Appears within the left window. */
    public static int POINTS_OF_POLYGON = 5;
    /** Number of morph steps. Steps between the pictures counted. */
    public static int NUM_OF_MORPH_STEPS = 5;
    /** Smooth radius of the polygon. Fuzzy polygon clipping done. */
    public static int SMOOTH_RADIUS = 19;
    /** Size of the marker points for mesh & polygon. */
    public static int MARK_SIZE = 7;
    /** Collection of points which represent the left picture's mesh. */
    public static final Vector<Point> left_mesh = new Vector<Point>();
    /** Collection of points which represent the right picture's mesh. */
    public static final Vector<Point> right_mesh = new Vector<Point>();
    /** Collection of points which represent the left picture's polygon.*/
    public static final Vector<Point> left_polygon = new Vector<Point>();
    /** Collection of points which represent the right picture's polygon. */
    public static final Vector<Point> right_polygon = new Vector<Point>();
    /** Left input image. */
    public static BufferedImage left_image;
    /** Right input image. */
    public static BufferedImage right_image;
    /** Current result image to be saved to disk. */
    public static BufferedImage result_image;
    /** Smoothed clip polygon matrix of the left picture. */
    public static double left_clip[][];
    /** Smoothed clip polygon matrix of the right picture. */
    public static double right_clip[][];
    /** Equal edit mode of both picture display viewers. */
    public static int edit_state = EDIT_MESH_OFF;
    /** Triangulation of the left picture. */
    public static final Vector<CTriangle> left_triangles 
        = new Vector<CTriangle>();
    /** Triangulation of the right picture. */
    public static final Vector<CTriangle> right_triangles
        = new Vector<CTriangle>();
    /** Current result triangulation. */
    public static final Vector<CTriangle> result_triangles
        = new Vector<CTriangle>();
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
    private CEditField points_of_polygon = new CEditField(
            "Points of polygon : ",
            POINTS_OF_POLYGON,
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
    /** Size of cursor and also point marks. */
    private CEditField mark_size = new CEditField(
            "Mark size : ",
            MARK_SIZE,
            2,
            100,
            true
    );
    /** Display field for the working directory with the same name. */
    private CEditField working_dir = new CEditField(
            "Workdir : ",
            CStrings.WORKDIR,
            false
        );
    /** Static init() reading pictures. */
    static{
        try{
            /* List input directory to fetch image file (not only JPG type). */
            File dir[] = new File(CStrings.INPUTDIR).listFiles();
            /* Choose two files, more files forbidden in directory. */
            for(File f : dir){
                /* Left image. */
                if(f.getName().toLowerCase().startsWith(CStrings.LEFT_PREFIX)){
                    /* Let Java load. */
                    left_image = ImageIO.read(f);
                }
                /* Right image. */
                if(f.getName().toLowerCase().startsWith(CStrings.RIGHT_PREFIX)){
                    /* Let Java load. */
                    right_image = ImageIO.read(f);
                }
            }
            /* Calculate size of result picture. */
            int 
                w = Math.max(left_image.getWidth(), right_image.getWidth()),
                h = Math.max(left_image.getHeight(), right_image.getHeight());
            /* Create empty result picture. */
            result_image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            /* Create left clip matrix. */
            left_clip = 
                new double[left_image.getWidth()][left_image.getHeight()];
            /* Create right clip matrix. */
            right_clip = 
                new double[right_image.getWidth()][right_image.getHeight()];
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            /* Show error pop up. */
            JOptionPane.showMessageDialog
                (null, "Can't load image. See also console output!");
        }
    }
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
        /* Initialize dialog. */
        this.getContentPane().setLayout(new GridLayout(7, 1));
        this.getContentPane().add(this.num_of_morph_steps);
        this.getContentPane().add(this.rows_of_mesh);
        this.getContentPane().add(this.columns_of_mesh);
        this.getContentPane().add(this.points_of_polygon);
        this.getContentPane().add(this.smooth_radius);
        this.getContentPane().add(this.mark_size);
        this.getContentPane().add(this.working_dir);
        this.getContentPane().setSize(this.getContentPane().getPreferredSize());
        this.pack();
        /* Ensure properties save on exit. */
        Runtime.getRuntime().addShutdownHook(new Thread(this));
        /* Load properties. */
        File p = new File(CStrings.PROPS);
        String s;
        /* a) load. */
        if(p.exists()){
            try{
                props.load(new FileInputStream(p));
                s = props.getProperty("ROWS_OF_MESH");
                ROWS_OF_MESH = Integer.parseInt(s);
                s = props.getProperty("COLUMNS_OF_MESH");
                COLUMNS_OF_MESH = Integer.parseInt(s);
                s = props.getProperty("POINTS_OF_POLYGON");
                POINTS_OF_POLYGON = Integer.parseInt(s);
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
        /* b) initialize by default. */ 
        }else{
            props.setProperty("ROWS_OF_MESH", "" + ROWS_OF_MESH);
            props.setProperty("COLUMNS_OF_MESH", "" + COLUMNS_OF_MESH);
            props.setProperty
            ("POINTS_OF_POLYGON", "" + POINTS_OF_POLYGON);
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
        /* Set fixed size. */
        this.setResizable(false);
        /* Position on screen. */
        this.setLocation
        (parent.getLocation().x + 10, parent.getLocation().y + 10);
        /* Show. */
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
            ("POINTS_OF_POLYGON", "" + POINTS_OF_POLYGON);
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
            pg = POINTS_OF_POLYGON != points_of_polygon.getNumber();
        COLUMNS_OF_MESH = columns_of_mesh.getNumber();
        ROWS_OF_MESH = rows_of_mesh.getNumber();
        POINTS_OF_POLYGON = points_of_polygon.getNumber();
        SMOOTH_RADIUS = smooth_radius.getNumber();
        NUM_OF_MORPH_STEPS = num_of_morph_steps.getNumber();
        MARK_SIZE = mark_size.getNumber();
        if(msh){
            left.initMesh();
            right.initMesh();
        }
        if(pg){
            left.initPolygon();
            right.initPolygon();
        }
        parent.repaint();
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
        /* Initialize text fields by actual values. */
        columns_of_mesh.setValue(COLUMNS_OF_MESH);
        rows_of_mesh.setValue(ROWS_OF_MESH);
        points_of_polygon.setValue(POINTS_OF_POLYGON);
        smooth_radius.setValue(SMOOTH_RADIUS);
        num_of_morph_steps.setValue(NUM_OF_MORPH_STEPS);
        mark_size.setValue(MARK_SIZE);
        working_dir.setValue(CStrings.WORKDIR);
    }
}
