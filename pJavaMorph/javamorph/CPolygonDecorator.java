package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

/**
 * @version 1.1
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.1.
 * <br/>
 * Class: CPolygonDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Draw the polygon to the parent component.
 * <br/>
 * Hint: User can move polygon points by mouse. Used to merge the pictures
 * selectively. Pixel operations performed on one of the both ratio matrixes.
 */
public class CPolygonDecorator 
    implements IDecorator, MouseListener, MouseMotionListener, Runnable{
    /** Color of the polygon lines. */
    public static final Color POLYGON_COLOR = Color.cyan;
    /** Color of the polygon point move cursor. */
    public static final Color CURSOR_COLOR= Color.red;
    /** Parent component to draw the polygon to. */
    private CFrame parent;
    /** Index of the current polygon point which has the cursor. */
    private int index = 0;
    /** Collection of the polygon's points. */
    private Vector<Point> polygon;
    /** Picture of the own side. */
    private BufferedImage image;
    /** Smoothed clip polygon matrix. */
    private double clip[][];
    /** File to save the polygon to after shutdown. */
    private File f_poly;
    /** File to save a debug copy of the clip matrix to. */
    private File f_clip;
    /** Fine position of the mouse pointer in picture pixels. */
    private Point pos = new Point(0, 0);
    /**
     * Constructor.
     * 
     * @param parent Parent JComponent to paint to.
     * @param polygon Points of the clip polygon.
     * @param image Picture of the own side.
     * @param f_poly File to save the polygon to after shutdown.
     * @param clip Smoothed clip matrix.
     * @param f_clip File to store a debug copy of the to.
     */
    public CPolygonDecorator(
            CFrame parent,
            Vector<Point> polygon,
            BufferedImage image,
            File f_poly,
            double clip[][],
            File f_clip){
        Runtime.getRuntime().addShutdownHook(new Thread(this));
        /* Assign parameters. */
        this.parent = parent;
        this.polygon = polygon;
        this.image = image;
        this.clip = clip;
        this.f_poly = f_poly;
        this.f_clip = f_clip;
        /* Provide the polygon points. */
        if(f_poly.exists())load();
        else init();
        /* Add listeners. */
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);
    }
    /**
     * Ensures that the cursor points to a valid polygon point.
     */
    public void clip(){
        index = Math.max(0, Math.min(polygon.size() - 1, index));
    }
    /**
     * Draw the polygon to the parent JComponent.
     */
    public void paint(Graphics g){
        int m = parent.scaleMarkSize();
        clip();
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(POLYGON_COLOR);
        Rectangle bounds = parent.getImageBounds();
        /* Transform coordinates to screen pixels. */
        g2.translate(bounds.x, bounds.y);
        g2.scale((double)bounds.width / image.getWidth(), 
                (double)bounds.height / image.getHeight());
        /* Draw point marks. */
        for(int i = 0; i < polygon.size(); ++i){
            Point p = polygon.get(i), q = polygon.get((i + 1) % polygon.size());
            g2.drawLine(p.x, p.y, q.x, q.y);
        }
        g2.setColor(CPolygonDecorator.CURSOR_COLOR);
        Point q = polygon.get(index);
        /* Branch when moving points. */
        if(CConfig.EDIT_POLYGON_OFF == CConfig.edit_state){
            g2.drawLine(q.x - m, q.y - m, q.x + m, q.y + m);
            g2.drawLine(q.x - m, q.y + m, q.x + m, q.y - m);
            g2.drawOval(q.x - 2 * m, q.y - 2 * m, 4 * m, 4 * m);
        }
        /* Branch when adding points. */
        if(CConfig.EDIT_POLYGON_ADD == CConfig.edit_state){
            g2.drawOval(pos.x - 2 * m, pos.y - 2 * m, 4 * m, 4 * m);
        }
        /* Branch when removing points. */
        if(CConfig.EDIT_POLYGON_SUB == CConfig.edit_state){
            g2.drawLine(q.x, q.y - m, q.x, q.y + m);
            g2.drawLine(q.x - m, q.y, q.x + m, q.y);
            g2.drawOval(q.x - 2 * m, q.y - 2 * m, 4 * m, 4 * m);
        }
        /* Remove coordinates transformation. */
        g2.setTransform(new AffineTransform());
    }
    /**
     * The user is moving a polygon point.
     */
    public void mouseDragged(MouseEvent e){
        /* Branch when moving points. */
        if(CConfig.EDIT_POLYGON_OFF == CConfig.edit_state){
            Point p = new Point(e.getX(), e.getY()), q = polygon.get(index);
            parent.scalePoint(p);
            q.x = p.x;
            q.y = p.y;
            parent.repaint();
        }
    }
    /**
     * Detect the current edit able polygon point before & during moving.
     */
    public void mouseMoved(MouseEvent e){
        pos = new Point(e.getX(), e.getY());
        parent.scalePoint(pos);
        /* Branch when moving or removing points. */
        if(CConfig.EDIT_POLYGON_OFF == CConfig.edit_state ||
            CConfig.EDIT_POLYGON_SUB == CConfig.edit_state){
            int ni = getIndex(pos);
            /* Branch & update index when changed. */
            if((index != ni)){
                index = ni;
                this.parent.getParent().repaint();
            }
        }
        /* Branch when adding points. */
        if(CConfig.EDIT_POLYGON_ADD == CConfig.edit_state){
            this.parent.getParent().repaint();
        }
    }
    /**
     * Provide the clip matrix.
     * Calculates Ratio matrix of row / column.
     */
    public void genClip(){
        /* Set all clip matrix pixels to zero. */
        for(int x = 0; x < clip.length; ++x){
            for(int y = 0; y < clip[0].length; ++y){
                clip[x][y] = 0.0;
            }
        }
        /* Draw lines connecting the polygon pixels. */
        for(int i = 0; i < polygon.size(); ++i){
            drawLine(polygon.get(i), polygon.get((i +1) % polygon.size()));
        }
        /* Fill the polygon. */
        fill();
        /* Smooth the edges of the polygon depending on the configuration. */
        for(int i = 0; i < polygon.size(); ++i){
            smoothLine(polygon.get(i), polygon.get((i +1) % polygon.size()));
        }
        /* Save the debug copy. */
        try{
            BufferedImage im = new BufferedImage(clip.length,clip[0].length, 
                        BufferedImage.TYPE_INT_ARGB);
            for(int x = 0; x < clip.length; ++x){
                for(int y = 0; y < clip[0].length; ++y){
                    int rgb =
                        0xff000000 |
                        (int)(clip[x][y] * 255) << 16 |
                        (int)(clip[x][y] * 255) << 8 |
                        (int)(clip[x][y] * 255);
                    im.setRGB(x, y, rgb);
                }
            }
            ImageIO.write(im, "png", f_clip);
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Save-on-shutdown-thread.
     */
    public void run(){
        try{
            FileOutputStream out = new FileOutputStream(f_poly);
            out.write('\n');
            /* Write one line for each point of the polygon. */
            for(Point p: polygon){
                out.write(("" + p.x + ' ').getBytes());
                out.write(("" + p.y + '\n').getBytes());
            }
            out.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            /* Show error pop up. */
            JOptionPane.showMessageDialog
                (parent, "Can't save polygon. See also console output!");
        }
    }
    /**
     * Load the polygon from file.
     */
    public void load(){
        try{
            /* Clear the point collection. */
            polygon.clear();
            FileInputStream in = new FileInputStream(f_poly);
            BufferedReader read= 
                new BufferedReader(new InputStreamReader(in));
            String line = read.readLine();
            /* Add one point for each line of the file. */
            while(null != (line = read.readLine())){
                StringTokenizer st = new StringTokenizer(line, " ");
                Point p = new Point();
                p.x = Integer.parseInt(st.nextToken());
                p.y = Integer.parseInt(st.nextToken());
                p.x = Math.min(p.x, image.getWidth() - 1);
                p.y = Math.min(p.y, image.getHeight() - 1);
                polygon.add(p);
            }
            read.close();
           read.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            /* Show error pop up. */
            JOptionPane.showMessageDialog
                (parent, "Can't load polygon. See also console output!");
        }
    }
    /**
     * Create a default polygon, depending on configuration coordinates,
     * when initialization by file isn't possible  of isn't intended.
     */
    public void init(){
        int 
            w = image.getWidth(), 
            h = image.getHeight(), 
            pts = CConfig.POINTS_OF_POLYGON;
        /* Clear the point collection. */
        polygon.clear();
        for(int i = 0; i < pts; ++i){
            Point p = new Point();
            /* Set all points into a circle. */
            double r = Math.sqrt(w * w + h * h) / 4.0;
            p.x = (int)(Math.cos(2.0 * Math.PI * i / (double)pts) * r); 
            p.y = (int)(Math.sin(2.0 * Math.PI * i / (double)pts) * r); 
            p.x += w / 2;
            p.y += h / 2;
            polygon.add(p);
        }
    }
    /**
     * Fill the inner of the polygon with ratio 1.0.
     */
    private void fill(){
        int state, min = 0, max = 0;
        boolean set;
        /* Scan line state machine. */
        for(int x = 0; x < clip.length - 1; ++x){
            state = 0;
            for(int y = 0; y < clip[0].length - 1; ++y){
                set = 0.0 < clip[x][y];
                /* Seek pattern: 1-0-1. */
                switch(state){
                    case 0: if(set)state = 1; break;
                    case 1: 
                        if(!set){
                            state = 2;
                            min = max = y;
                        }
                        break;
                    case 2: 
                        if(set){
                            state = 3;
                        }else{
                            max = y;
                        }
                        break;
                    default: break;
                }
            }
            /* Fill line when pattern of the state machine has been detected. */
            for(int y = min; y <= max && 3 == state; ++y){
                clip[x][y] = 1.0;
            }
        }
    }
    /**
     * Draw a line between two polygon points into the clip matrix.
     * @param p1 First point.
     * @param p2 Second point.
     * 
     * <code>
     * 
     * |x|   |x1|       |x2 - x1|  0.0 <= p <= 1.0
     * | | = |  | + p * |       |  resolution depends on longer
     * |y|   |y1|       |y2 - y1|  orthogonal difference.
     * 
     * </code>
     * 
     * Line is orthogonal pixel proof.
     * 
     */
    private void drawLine(Point p1, Point p2){
        if(p1.equals(p2))return;
        int 
            x1 = p1.x,
            x2 = p2.x,
            y1 = p1.y,
            y2 = p2.y,
            dx = x2 - x1,
            dy = y2 - y1,
            param = Math.max(Math.abs(dx), Math.abs(dy)),
            x,
            y;
        for(int p = 0; p <= param; ++p){
            x = (int)(x1 + (double)p * dx / (double)param);
            y = (int)(y1 + (double)p * dy / (double)param);
            clip
                [Math.max(0, Math.min(x, clip.length) - 1)]
                [Math.max(0, Math.min(y, clip[0].length) - 1)] = 1.0;
        }
    }    
    /**
     * Smooth the contour of the clip matrix as dilatation. Work on the contour
     * of clip & write to shadow.
     * @param p1 First point.
     * @param p2 Second point.
     */
    private void smoothLine(Point p1, Point p2){
        if(p1.equals(p2))return;
            int 
            x1 = p1.x,
            x2 = p2.x,
            y1 = p1.y,
            y2 = p2.y,
            dx = x2 - x1,
            dy = y2 - y1,
        param = Math.max(Math.abs(dx), Math.abs(dy));
        Point pt = new Point();
        for(int p = 0; p <= param; ++p){
            pt.x = (int)(x1 + (double)p * dx / (double)param);
            pt.y = (int)(y1 + (double)p * dy / (double)param);
            smoothCircle(pt);
        }
    }
    /**
     * Perform actually smooth for one pixel
     * @param p The current pixel.
     */
    private void smoothCircle(Point p){
        int r = CConfig.SMOOTH_RADIUS , dx, dy;
        double d;
        for(int x = p.x - r; x <= p.x + r; ++x){
            for(int y = p.y -r; y <= p.y +r; ++y){
                dx = p.x - x;
                dy = p.y - y;
                d = Math.sqrt(dx * dx + dy * dy);
                if(
                    x >= 0 &&
                    y >= 0 &&
                    x < image.getWidth() &&
                    y < image.getHeight() &&
                    r >= d
                ){
                    clip[x][y] = Math.max(clip[x][y], (r - d) / r);
                }
            }
        }
    }
    /**
     * Due to Java API. Perform polygon point operation depending on edit state.
     */
    public void mouseClicked(MouseEvent e){
        if(MouseEvent.BUTTON1 == e.getButton()){
            switch(CConfig.edit_state){
                /* Add one point. */
                case CConfig.EDIT_POLYGON_ADD:
                    int i = (index + 1) % polygon.size();
                    polygon.insertElementAt(new Point(pos.x, pos.y), i);
                    break;
                /* Delete one point if there are enough remaining points. */
                case CConfig.EDIT_POLYGON_SUB:
                    if(polygon.size() > 3){
                        polygon.remove(index);
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
     * @param p on screen, scaled to picture coordinates.
     * @return Index in point collection.
     */    
    private int getIndex(Point p){
        int dx, dy, d, h = Integer.MAX_VALUE, ni = -1;
        /* Seek nearest within all mesh points. */
        for(int i = 0; i < polygon.size(); ++i){
            Point p1 = polygon.get(i);
            dx = p.x - p1.x;
            dy = p.y - p1.y;
            d = dx * dx + dy * dy;
            /* Update if nearer. */
            if(d < h){
                ni = i;
                h = d;
            }
        }
        return ni;
    }
}
