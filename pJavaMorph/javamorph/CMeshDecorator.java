package javamorph;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * @version 1.1
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.1.
 * <br/>
 * Class: CMeshDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Pattern to draw the mesh to CFrame.
 * <br/>
 * Hint: Contains the mesh editing functionality.
 */
public class CMeshDecorator 
    implements IDecorator, MouseListener, MouseMotionListener, Runnable{
    /** Draw color of the mesh's lines. */
    public static final Color MESH_COLOR = Color.magenta;
    /** Draw color of the mesh point move cursor crosss. */
    public static final Color CURSOR_COLOR = Color.yellow;
    /** Parent component to draw the mesh to. */
    private CFrame parent;
    /** Index of those mesh point which is nearest to the mouse pointer. */
    private static int index;
    /** Collection of the mesh's points. */
    private Vector<Point> mesh;
    /** Input picture of the own side. */
    private BufferedImage image;
    /** File to store the mesh to after program shutdown. */
    private File f_mesh;
    /** Fine value of the mouse pointer's position. */
    private Point pos = new Point(0, 0);
    /**
     * Constructor.
     * 
     * @param parent Parent JComponent to paint to.
     * @param mesh Pictures of the mesh.
     * @param image Picture of the own side.
     * @param f_mesh File to store the mesh to after shutdown.
     */
    public CMeshDecorator(
            CFrame parent, 
            Vector<Point> mesh,
            BufferedImage image,
            File f_mesh){
        /* Ensure saving of the mesh after shutdown. */
        Runtime.getRuntime().addShutdownHook(new Thread(this));
        /* Assign arguments. */
        this.parent = parent;
        this.mesh = mesh;
        this.image = image;
        this.f_mesh = f_mesh;
        /* Provide the mesh. */
        if(f_mesh.exists())load();
        else init();
        /* Add listeners. */
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);
    }
    /**
     * Ensure that the cursor is situated inside the bounds of the mesh while
     * drawing.
     */
    public void clip(){
        index = Math.max(0, Math.min(mesh.size() - 1, index));
    }
    /**
     * Decorator API. Draw this mesh to the graphics context of the CFrame.
     */
    public void paint(Graphics g){
        int m = parent.scaleMarkSize();
        clip();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(CMeshDecorator.MESH_COLOR);
        Rectangle bounds = parent.getImageBounds();
        /* Transform coordinates depending on screen & image sizes. */
        g2.translate(bounds.x, bounds.y);
        g2.scale((double)bounds.width / image.getWidth(), 
                (double)bounds.height / image.getHeight());
        /* Draw mesh points. */
        for(Point p: mesh){
            g2.drawLine(p.x - m, p.y, p.x + m, p.y);
            g2.drawLine(p.x, p.y - m, p.x, p.y + m);
        }
        g2.setColor(CMeshDecorator.CURSOR_COLOR);
        Point q = mesh.get(index);
        /* Branch if moving points. */
        if(CConfig.EDIT_MESH_OFF == CConfig.edit_state){
            g2.drawLine(q.x - m, q.y - m, q.x + m, q.y + m);
            g2.drawLine(q.x - m, q.y + m, q.x + m, q.y - m);
            g2.drawOval(q.x - 2 * m, q.y - 2 * m, 4 * m, 4 * m);
        }
        /* Branch if adding points. */
        if(CConfig.EDIT_MESH_ADD == CConfig.edit_state){
            g2.drawOval(pos.x - 2 * m, pos.y - 2 * m, 4 * m, 4 * m);
        }
        /* Branch if removing points. */
        if(CConfig.EDIT_MESH_SUB == CConfig.edit_state){
            g2.drawLine(q.x, q.y - m, q.x, q.y + m);
            g2.drawLine(q.x - m, q.y, q.x + m, q.y);
            g2.drawOval(q.x - 2 * m, q.y - 2 * m, 4 * m, 4 * m);
        }
        /* Reset transformation of coordinates. */
        g2.setTransform(new AffineTransform());
    }
    /**
     * Move a mesh point with the mouse.
     */
    public void mouseDragged(MouseEvent e){
        /* Branch if moving points. */
        if(CConfig.EDIT_MESH_OFF == CConfig.edit_state){
            Point p = new Point(e.getX(), e.getY()), q = mesh.get(index);
            parent.scalePoint(p);
            /* Manipulate coordinates of current mesh point. */
            q.x = p.x;
            q.y = p.y;
            parent.repaint();
        }
    }
    /**
     * Detect where the mouse is before and while moving the mesh point.
     */
    public void mouseMoved(MouseEvent e){
        pos = new Point(e.getX(), e.getY());
        parent.scalePoint(pos);
        /* Branch if moving or removing points. */
        if(CConfig.EDIT_MESH_OFF == CConfig.edit_state ||
            CConfig.EDIT_MESH_SUB == CConfig.edit_state){
            int n = getIndex(pos);
            /* Overwrite the index. */
            if(index != n){
                index = n;
                this.parent.getParent().repaint();
            }
        }
        /* Branch if adding points. */
        if(CConfig.EDIT_MESH_ADD == CConfig.edit_state){
            /* Repaint only. */
            this.parent.getParent().repaint();
        }
    }
    /**
     * Save-on-exit-thread. Save the mesh.
     */
    public void run(){
        try{
            /* Provide the file stream. */
            FileOutputStream out = new FileOutputStream(f_mesh);
            out.write('\n');
            /* For all points of the mesh: Write booth coordinates to file. */
            for(Point p: mesh){
                out.write(("" + p.x + ' ').getBytes());
                out.write(("" + p.y + '\n').getBytes());
            }
            out.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            /* Show error pop up. */
            JOptionPane.showMessageDialog
                (parent, "Can't save mesh. See also console output!");
        }
    }
    /**
     * Load the mesh on startup from file.
     */
    public void load(){
        try{
            /* Make the collection empty. */
            mesh.clear();
            /* Provide the file. */
            FileInputStream in = new FileInputStream(f_mesh);
            BufferedReader read= 
                new BufferedReader(new InputStreamReader(in));
            String line = read.readLine();
            /* Add one mesh point for each file's line. */
            while(null != (line = read.readLine())){
                StringTokenizer st = new StringTokenizer(line, " ");
                Point p = new Point();
                /* Read both coordinates. */
                p.x = Integer.parseInt(st.nextToken());
                p.y = Integer.parseInt(st.nextToken());
                p.x = Math.min(p.x, image.getWidth() - 1);
                p.y = Math.min(p.y, image.getHeight() - 1);
                mesh.add(p);
            }
            read.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            /* Show error pop up. */
            JOptionPane.showMessageDialog
                (parent, "Can't load mesh. See also console output!");
        }
        
    }
    /**
     * If mesh creation from file is not possible or not intended:
     * Create an grid mesh controlled by configuration values.
     */
    public void init(){
        int x, y;
        /* Clear the point collection. */
        mesh.clear();
        /* For a number of rows. */
        for(int c = 0; c <= CConfig.COLUMNS_OF_MESH; ++c){
            /* For a number of columns. */
            for(int r = 0; r <= CConfig.ROWS_OF_MESH; ++r){
                /* Scale & add the point depending on row & column. */
                x = ((image.getWidth() - 1) * c) / CConfig.COLUMNS_OF_MESH;
                y = ((image.getHeight() - 1) * r) / CConfig.ROWS_OF_MESH;
                mesh.add(new Point(x, y));
            }
        }
    }
    /**
     * Due to Java API. Perform mesh point operation depending on edit state.
     */
    public void mouseClicked(MouseEvent e){
        if(MouseEvent.BUTTON1 == e.getButton()){
            switch(CConfig.edit_state){
                /* Add one point. */
                case CConfig.EDIT_MESH_ADD:
                    CConfig.left_mesh.add(new Point(pos.x, pos.y));
                    CConfig.right_mesh.add(new Point(pos.x, pos.y));
                    break;
                /* Delete one point if there are enough remaining points. */
                case CConfig.EDIT_MESH_SUB:
                    if(CConfig.left_mesh.size() > 4){
                        CConfig.left_mesh.remove(index);
                        CConfig.right_mesh.remove(index);
                    }
                    break;
            }
        }
        parent.getParent().repaint();
    }
    /** Due to Java API. */
    public void mouseEntered(MouseEvent e){}
    /** Due to Java API. */
    public void mouseExited(MouseEvent e){}
    /** Due to Java API. */
    public void mousePressed(MouseEvent e){}
    /** Due to Java API. */
    public void mouseReleased(MouseEvent e){}
    /**
     * Seek the index of that one mesh point which is nearest to the mouse
     * cursor.
     * 
     * @param p0 on screen, scaled to picture coordinates.
     * @return Index in point collection.
     */
    private int getIndex(Point p0){
        int dx, dy, d, h = Integer.MAX_VALUE, n = -1;
        /* Seek nearest within all mesh points. */
        for(int i = 0; i < mesh.size(); ++i){
            Point p1 = mesh.get(i);
            dx = p0.x - p1.x;
            dy = p0.y - p1.y;
            d = dx * dx + dy * dy;
            /* Update if nearer. */
            if(d < h){
                n = i;
                h = d;
            }
        }
        return n;
    }
}
