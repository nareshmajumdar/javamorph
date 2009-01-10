package javamorph;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
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
    implements IDecorator, MouseMotionListener{
    /** Draw color of the mesh's lines. */
    public static final Color MESH_COLOR = Color.magenta;
    /** Draw color of the mesh point move cursor crosss. */
    public static final Color CURSOR_COLOR = Color.yellow;
    /** Parent component to draw the mesh to. */
    private CFrame parent;
    /** If <code>true</code> then the mesh is editable. */
    private boolean active = true;
    /** Current cursor row of booth meshes at the same time. */
    private static int row;
    /** Current cursor column of booth meshes at the same time. */
    private static int col;
    /**
     * Constructor.
     * @param parent Parent CFrame to draw to.
     */
    public CMeshDecorator(CFrame parent){
        this.parent = parent;
    }
    /**
     * Ensure that the cursor is situated inside the bounds of the mesh while
     * drawing.
     */
    public void clip(){
        row = Math.min(row, parent.getMesh().length - 1);
        col = Math.min(col, parent.getMesh()[0].length - 1);
        row = Math.max(row, 0);
        col = Math.max(col, 0);
    }
    /**
     * Decorator API. Drawing this mesh to the graphics context of the CFrame.
     */
    public void paint(Graphics g){
        clip();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(CMeshDecorator.MESH_COLOR);
        Rectangle bounds = parent.getImageBounds();
        Dimension size = parent.getImageSize();
        g2.translate(bounds.x, bounds.y);
        g2.scale((double)bounds.width / size.width, 
                (double)bounds.height / size.height);
        for(int r = 0; r <= CConfig.ROWS_OF_MESH; ++r){
            for(int c = 0; c <= CConfig.COLUMNS_OF_MESH; ++c){
                if(c > 0){
                    g2.drawLine(
                        parent.getMesh()[r][c - 1].x,
                        parent.getMesh()[r][c - 1].y,
                        parent.getMesh()[r][c].x,
                        parent.getMesh()[r][c].y
                    );
                }
                if(r > 0){
                    g2.drawLine(
                        parent.getMesh()[r - 1][c].x,
                        parent.getMesh()[r - 1][c].y,
                        parent.getMesh()[r][c].x,
                        parent.getMesh()[r][c].y
                    );
                }
                if(active && (col != c || row != r)){
                    g2.fillRect(    
                        parent.getMesh()[r][c].x - CConfig.MARK_SIZE / 2, 
                        parent.getMesh()[r][c].y - CConfig.MARK_SIZE / 2, 
                        CConfig.MARK_SIZE, CConfig.MARK_SIZE);
                }
            }
        }
        if(this.active){
            g2.setColor(CMeshDecorator.CURSOR_COLOR);
            g2.drawLine(
                parent.getMesh()[row][col].x - CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].y - CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].x + CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].y + CConfig.MARK_SIZE 
            );
            g2.drawLine(
                parent.getMesh()[row][col].x - CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].y + CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].x + CConfig.MARK_SIZE, 
                parent.getMesh()[row][col].y - CConfig.MARK_SIZE 
            );
        }
        g2.setTransform(new AffineTransform());
    }
    /**
     * Decorator API.
     */
    public void setSize(Dimension size){}
    /**
     * Move a mesh point with the mouse.
     */
    public void mouseDragged(MouseEvent e){
        if(!active)return;
        Point p = new Point(e.getX(), e.getY());
        parent.scalePoint(p);
        parent.getMesh()[row][col].x = p.x;
        parent.getMesh()[row][col].y = p.y;
        parent.repaint();
    }
    /**
     * Detect where the mouse is before and while moving the mesh point.
     */
    public void mouseMoved(MouseEvent e){
        if(!active)return;
        Point p = new Point(e.getX(), e.getY());
        parent.scalePoint(p);
        int dx, dy, d, h = Integer.MAX_VALUE, nc = -1, nr  = -1;
        for(int r = 0; r <= CConfig.ROWS_OF_MESH; ++r){
            for(int c = 0; c <= CConfig.COLUMNS_OF_MESH; ++c){
                dx = p.x - parent.getMesh()[r][c].x;
                dy = p.y - parent.getMesh()[r][c].y;
                d = dx * dx + dy * dy;
                if(d < h){
                    nr = r;
                    nc = c;
                    h = d;
                }
            }
        }
        if((row != nr) || (col != nc)){
            col = nc;
            row = nr;
            this.parent.getParent().repaint();
        }
    }
    /**
     * Set the edit mode. If the parameter is <code>true</code> then the mesh
     * is editable.   
     */
    public void setActive(boolean active){
        this.active = active;
    }
}
