package javamorph;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CFrame.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: JComponent to show either the left picture of the right one.
 * <br/>
 * Hint: Paint operation also performed by the four decorators (a pattern).
 */
public class CFrame extends JLabel {
    /** Java API. */
    private static final long serialVersionUID = 1L;
    /** Preferred paint size of this component. */ 
    public static final Dimension PREF_SIZE = new Dimension(400,300);
    /** Background color of this component. */
    public static final Color BACKGROUND = Color.black;
    /** Constant for pop up menu edit command: Edit the mesh. */
    public static final int EDIT_MESH = 1;
    /** Constant for pop up menu edit command: Edit the polygon. */ 
    public static final int EDIT_POLYGON = 2;
    /** Parent frame which lays out this component. */
    private CMain parent;
    /** Decorator to draw & edit the morph mesh. */
    private CMeshDecorator dmesh = new CMeshDecorator(this);
    /** Decorator to draw & edit the picture as subject of this program. */
    private CPictureDecorator dpicture = new CPictureDecorator(this);
    /** Decorator to draw & edit the clip polygon. */
    private CPolygonDecorator dpolygon;
    /** Decorator to provide the pop up menu. */
    private CPopupMenuDecorator dpopup = new CPopupMenuDecorator(this);
    /** Double buffer image to avoid flickering effects. */
    private BufferedImage moffline;  
    /**
     * Mesh for the picture to process. Dimensions are global. 
     * First index is row, second index is column.
     */
    private Point mesh[][];
    /** If <code>true</code> then the instance is the left picture. */
    private boolean left;
    /**
     * Constructor.
     * @param parent Parent JFrame.
     * @param left If <code>true</code> then left instance.
     */
    public CFrame(CMain parent, boolean left){
        this.parent = parent;
        this.left = left;
        dpolygon = new CPolygonDecorator(this, left);
        this.setBackground(CFrame.BACKGROUND);
        this.addMouseListener(this.dpopup);
        this.addMouseMotionListener(this.dmesh);
        this.addMouseMotionListener(this.dpolygon);
    }
    /**
     * Paint this component to the screen. Call the decorators therefore.
     */
    public void paint(Graphics g){
        int r = 0, c = 0;
        Dimension size = this.getSize();
        this.moffline = new BufferedImage(
                size.width, 
                size.height, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics o = this.moffline.getGraphics();
        o.setColor(this.getBackground());
        for(int i = 0; i < size.width; i += 20, c += 1, r = 0){
            for(int j = 0; j < size.height; j +=  20, r += 1){
                if(0 == (c + r) % 2){
                    o.setColor(Color.lightGray);
                }else{
                    o.setColor(Color.gray);
                }
                o.fillRect(i, j, 20, 20);
            }
        }
        ((Graphics2D)o).setRenderingHint
        (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.drawDeco(this.dpicture, o);
        this.drawDeco(this.dmesh, o);
        this.drawDeco(this.dpolygon, o);
        this.drawDeco(this.dpopup, o);
        g.drawImage(moffline, 0, 0, this);
    }
    /**
     * Java API.
     */
    public void update(Graphics g){
        this.paint(g);
    }
    /**
     * Draw one of the decorators wihtin the paint function.
     * @param d Decorator to draw.
     * @param g Graphics context to draw the decorator to.
     */
    private void drawDeco(IDecorator d, Graphics g){
        d.setSize(this.getSize());
        d.paint(g);
    }
    /**
     * Java API. Constant used as return.
     */
    public Dimension getPreferredSize(){
        return CFrame.PREF_SIZE;
    }
    /**
     * Load picture mesh & polygon from the working directorie's files.
     * @param pict Picture path.
     * @param msh Mesh path.
     * @param pol Polygon path.
     */
    public void load(String pict, String msh, String pol){
        this.dpicture.load(new File(pict));
        this.dpicture.setSize(this.getSize());
        this.loadMesh(new File(msh));
        this.loadPolygon(new File(pol));
    }
    /**
     * Return the position & the size of the drawn image within the component.   
     * @return Bounds in screen pixel units relating to this component.
     */
    public Rectangle getImageBounds(){
        return this.dpicture.getBounds();
    }
    /**
     * Size of the image relating to file properties.
     * @return With & height of the raster image.
     */
    public Dimension getImageSize(){
        Dimension size = new Dimension();
        size.width = dpicture.getContent().getWidth();
        size.height = dpicture.getContent().getHeight();
        return size;
    }
    /**
     * Save mesh & polygon to the files within the working directory.
     * @param mesh Filename of the mesh file.
     * @param polygon Filename of the polygon file.
     */
    public void save(String mesh, String polygon){
        this.saveMesh(new File(mesh));
        this.savePolygon(new File(polygon));
    }
    /**
     * Obtain the instance of the application's main class.
     */
    public CMain getParent(){
        return this.parent;
    }
    /**
     * Get function.
     * @return The mapping mesh of this instance.
     */
    public Point[][] getMesh(){
        return this.mesh;
    }
    /**
     * Get function.
     * @return The clipping polygon of this instance.
     */
    public Point[] getPolygon(){
        return this.dpolygon.getPolygon();
    }
    /**
     * Get function.
     * @return The image to process.
     */
    public BufferedImage getContent(){
        return this.dpicture.getContent();
    }
    /**
     * Clip picture. Contains the clip ratio for every picture.
     * If the clip ratio is 1.0 the the pixel is valid when the morph ratio
     * for this picture is also 1.0.
     * @param f File to save the clip picture to (not used, only for debug view.
     * @return The clip picture. First index is row, second index is column.
     */
    public double[][] getClip(File f){
        double[][] result = this.dpolygon.getClip();
        dpolygon.debugClip(f);
        return result;
    }
    /**
     * Delete the old mesh file. Provide a new mesh with the dimensions stored
     * within the configuration.
     */
    public void deleteMesh(){
        File f;
        if(left){
            f = new File(CStrings.LEFT_MESH);
        }else{
            f = new File(CStrings.RIGHT_MESH);
        }
        f.delete();
        this.loadMesh(f);
        repaint();
    }
    /**
     * Delete the old polygon file. Provide a new polygon with the number of
     * points stored within the configuration.
     */
    public void deletePolygon(){
        File f;
        if(left){
            f = new File(CStrings.LEFT_POLYGON);
        }else{
            f = new File(CStrings.RIGHT_POLYGON);
        }
        f.delete();
        this.loadPolygon(f);
        repaint();
    }
    /**
     * Save the mesh.    
     * @param f File to save the mesh to.
     */
    private void saveMesh(File f){
        try{
            FileOutputStream out = new FileOutputStream(f);
            out.write(("" +  mesh.length + ' ').getBytes());
            out.write(("" +  mesh[0].length + '\n').getBytes());
            for(int r = 0; r < mesh.length; ++r){
                for(int c = 0; c < mesh[0].length; ++c){
                    out.write(("" + mesh[r][c].x + ' ').getBytes());
                    out.write(("" + mesh[r][c].y + '\n').getBytes());
                }
            }
            out.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog
                (parent, "Can't save mesh. See also console output!");
        }
    }
    /**
     * Load mesh from file if the file exists. Generate a new mesh otherwise.
     * @param f Filename of the mesh file.
     */
    private void loadMesh(File f){
        if(f.exists()){
            try{
                FileInputStream in = new FileInputStream(f);
                BufferedReader read= 
                    new BufferedReader(new InputStreamReader(in));
                String line = read.readLine();
                StringTokenizer st = new StringTokenizer(line, " ");
                CConfig.ROWS_OF_MESH = Integer.parseInt(st.nextToken()) - 1;
                CConfig.COLUMNS_OF_MESH = Integer.parseInt(st.nextToken()) - 1;
                mesh = new Point 
                    [CConfig.ROWS_OF_MESH + 1]
                    [CConfig.COLUMNS_OF_MESH + 1];
                for(int r = 0; r < mesh.length; ++r){
                    for(int c = 0; c < mesh[0].length; ++c){
                        Point p = new Point();
                        line = read.readLine();
                        st = new StringTokenizer(line, " ");
                        p.x = Integer.parseInt(st.nextToken());
                        p.y = Integer.parseInt(st.nextToken());
                        p.x = Math.min(p.x, this.getImageSize().width - 1);
                        p.y = Math.min(p.y, this.getImageSize().height - 1);
                        mesh[r][c] = p;
                    }
                }
                read.close();
            }catch(Exception e){
                System.err.println(e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog
                    (parent, "Can't load mesh. See also console output!");
            }
        }else{
            Dimension size = this.getImageSize();
            mesh = new Point
            [CConfig.ROWS_OF_MESH + 1]
            [CConfig.COLUMNS_OF_MESH + 1];
            for(int r = 0; r <= CConfig.ROWS_OF_MESH; ++r){
                for(int c = 0; c <= CConfig.COLUMNS_OF_MESH; ++c){
                    Point p = new Point();
                    p.x = (size.width * c) / CConfig.COLUMNS_OF_MESH;
                    p.y = (size.height * r) / CConfig.ROWS_OF_MESH;
                    mesh[r][c] = p;
                }
            }
        }
    }
    /**
     * Save the polygon to the file.
     * @param f Filename of the file.
     */
    private void savePolygon(File f){
        try{
            Point[] polygon = dpolygon.getPolygon();
            FileOutputStream out = new FileOutputStream(f);
            out.write(("" +  polygon.length + '\n').getBytes());
            for(int i = 0; i < polygon.length; ++i){
                out.write(("" + polygon[i].x + ' ').getBytes());
                out.write(("" + polygon[i].y + '\n').getBytes());
            }
            out.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog
                (parent, "Can't save polygon. See also console output!");
        }
    }
    /**
     * Load the polygon from the file if the file exists. Provide a new polygon
     * otherwise.
     * @param f Filename of the file.
     */
    private void loadPolygon(File f){
        if(f.exists()){
            try{
                FileInputStream in = new FileInputStream(f);
                BufferedReader read= 
                    new BufferedReader(new InputStreamReader(in));
                String line = read.readLine();
                StringTokenizer st = new StringTokenizer(line, " ");
                int num = Integer.parseInt(st.nextToken());
                if(left)CConfig.POINTS_OF_LEFT_POLYGON = num;
                else CConfig.POINTS_OF_RIGHT_POLYGON = num;
                Point polygon[] = new Point[num];
                dpolygon.setPolygon(polygon);
                for(int i = 0; i < polygon.length; ++i){
                    Point p = new Point();
                    line = read.readLine();
                    st = new StringTokenizer(line, " ");
                    p.x = Integer.parseInt(st.nextToken());
                    p.y = Integer.parseInt(st.nextToken());
                    p.x = Math.min(p.x, this.getImageSize().width - 1);
                    p.y = Math.min(p.y, this.getImageSize().height - 1);
                    polygon[i] = p;
                }
                read.close();
            }catch(Exception e){
                System.err.println(e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog
                    (parent, "Can't load polygon. See also console output!");
            }
        }else{
            int points;
            Dimension size = this.getImageSize();
            if(left)points = CConfig.POINTS_OF_LEFT_POLYGON;
            else points = CConfig.POINTS_OF_RIGHT_POLYGON;
            dpolygon.setPolygon(new Point[points]);
            for(int i = 0; i < points; ++i){
                Point p = new Point();
                double r = Math.sqrt(
                        size.width * size.width + 
                        size.height * size.height) / 4.0;
                p.x = (int)(Math.cos(2.0 * Math.PI * i / points) * r); 
                p.y = (int)(Math.sin(2.0 * Math.PI * i / points) * r); 
                p.x += size.width / 2;
                p.y += size.height / 2;
                dpolygon.getPolygon()[i] = p;
            }
        }
    }
    /**
     * Switch between editing of mesh or editing of polygon.
     * @param mode Edit mode, see EDIT constants.
     */
    public void setEditMode(int mode){
        if(EDIT_POLYGON == mode){
            this.dmesh.setActive(false);
            this.dpolygon.setActive(true);
        }
        if(EDIT_MESH == mode){
            this.dmesh.setActive(true);
            this.dpolygon.setActive(false);
        }
    }
    /**
     * Scale a screen point to a picture point.
     * @param p Point in screen resolution units.
     */
    public void scalePoint(Point p){
        Rectangle bounds = getImageBounds();
        Dimension size = getImageSize();
        double x, y;
        x = (p.x - bounds.x) * size.width / bounds.width;
        y = (p.y - bounds.y) * size.height / bounds.height;
        x = Math.max(0, Math.min(size.width, x));
        y = Math.max(0, Math.min(size.height, y));
        p.x = (int)x;
        p.y = (int)y;
    }
}
