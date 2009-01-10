package javamorph;

import java.io.*;
import java.awt.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.image.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CMorphOperator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Morph the result from left input to right input depending on
 * the ratio parameter.
 * <br/>
 * Hint: Writes the result to the working directory.
 */
public class CMorphOperator implements Runnable{
    /** Application's main class. */
    private CMain parent;
    /** Mesh of the left picture. */
    private Point left_mesh[][];
    /** Mesh of the right picture. */
    private Point right_mesh[][];
    /** Mesh of the result picture. */
    private Point result_mesh[][];
    /** Left input image. */
    private BufferedImage left_image;
    /** Right input image. */
    private BufferedImage right_image;
    /** Current result image. */
    private BufferedImage result_image;
    /** Size of the left input image. */
    private Dimension left_size;
    /** Size of the right input image. */
    private Dimension right_size;
    /** Size of the current result image. */
    private Dimension result_size;
    /** 
     * If <code>0.0</code then output is the left image, if <code>1.0</code>
     * then output is the right image. Every value between them leads to a
     * merged image.
     */
    private double ratio;
    /** Current point coordinates of the left image. */
    private Point left_point = new Point();
    /** Current point coordinates of the right image. */
    private Point right_point = new Point();
    /** Current point coordinates of the result image. */
    private Point result_point = new Point();
    /** RGB value of the current left pixel. */
    private int left_pixel;
    /** RGB value of the current right pixel. */
    private int right_pixel;
    /** RGB value of the current result pixel. */
    private int result_pixel;
    /** Current row of the mesh. */
    private int r;
    /** Current column of the mesh. */
    private int c;
    /** Transformation matrix from result to left point. */
    private CTransform left_trafo;
    /** Transformation matrix from result to right point. */
    private CTransform right_trafo;
    /** Triangulation of the left picture. */
    private CTriangle left_triangles[];
    /** Triangulation of the right picture. */
    private CTriangle right_triangles[];
    /** Triangulation of the current result picture. */
    private CTriangle result_triangles[];
    /** Index of the current triangle within all three lists. */
    private int t_idx;
    /** List of result points situated within the current result triangle. */
    private Point withins[];
    /** Polygon clip matrix of the left picture. */
    private double left_clip[][]; 
    /** Polygon clip matrix of the right picture. */
    private double right_clip[][];
    /** Polygon clip ratio of the current left pixel. */
    private double left_ratio;
    /** Polygon clip ratio of the current right pixel. */
    private double right_ratio;
    /** If <code>true</code> the user forces the morph process to abort. */
    private boolean f_break;
    /** Instance of the progress bar. */
    private CProgress progress;
    /**
     * Constructor.
     * @param parent Application's main class.
     * @param left_mesh Mesh of left input picture.
     * @param right_mesh Mesh of right input picture.
     * @param left_clip Left polygon clip matrix.
     * @param right_clip Right polygon clip matrix.
     * @param left_image Left input image raster.
     * @param right_image Right input image raster.
     * @param progress Graphical progress bar.
     */
    public CMorphOperator(
            CMain parent,
            Point left_mesh[][],
            Point right_mesh[][],
            double left_clip[][],
            double right_clip[][],
            BufferedImage left_image,
            BufferedImage right_image,
            CProgress progress){
        this.parent = parent;
        this.left_mesh = left_mesh;
        this.right_mesh = right_mesh;
        this.left_clip = left_clip;
        this.right_clip = right_clip;
        this.left_image = left_image;
        this.right_image = right_image;
        this.progress = progress;
        this.left_triangles = CGeo.getTriangles(this.left_mesh);
        this.right_triangles = CGeo.getTriangles(this.right_mesh);
    }
    /**
     * Enable abort of the morph process forced by user.
     */
    public void doBreak(){
        this.f_break = true;
    }
    /**
     * Thread API. Starts morph batch for a number of intermediate pictures
     * with increasing ratio value.
     */
    public void run(){
        this.f_break = false;
        try{
            for(int i = 0;
                (i <= CConfig.NUM_OF_MORPH_STEPS) && (!f_break); 
                ++i){
                /* Calculate ratio. */
                this.ratio = ((double)i / CConfig.NUM_OF_MORPH_STEPS);
                /* Work. */
                this.morph();
                File f = new File(CStrings.getOutput(i));
                /* Save image into workdir. */
                ImageIO.write(this.result_image, "jpg",f);
                /* Show progress. */
                progress.setProgress(i, 0, CConfig.NUM_OF_MORPH_STEPS);
                Thread.sleep(1);
            }
            progress.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, 
                    "Can't save result. Please see console output!");
            
        }
    }
    /**
     * Create one intermediate picture.
     */
    private void morph(){
        left_size = new Dimension(
                left_image.getWidth(),
                left_image.getHeight());
        right_size = new Dimension(
                right_image.getWidth(),
                right_image.getHeight());
        this.result_size = new Dimension(
                Math.max(left_size.width, right_size.width),
                Math.max(left_size.height, right_size.height));
        this.result_image = 
            new BufferedImage(result_size.width, 
            result_size.height,
            BufferedImage.TYPE_INT_RGB);
        /* Depends on current ratio. */
        genMesh();
        /* Depends on current ratio. */
        this.result_triangles = CGeo.getTriangles(this.result_mesh);
        /* Iterate through the triangles. */
        for(t_idx = 0; t_idx < result_triangles.length; ++t_idx){
            this.triangle();
        }
    }
    /**
     * Make a weighted average mesh depending on the current ratio.
     */
    private void genMesh(){
        this.result_mesh = new Point[CConfig.ROWS_OF_MESH + 1]
                                     [CConfig.COLUMNS_OF_MESH + 1];
        /* For all mesh crosses. */
        for(r = 0; r <= CConfig.ROWS_OF_MESH; ++r){
            for(c = 0; c <= CConfig.COLUMNS_OF_MESH; ++ c){
                double
                    lx = left_mesh[r][c].x * (1.0 - ratio),
                    rx = right_mesh[r][c].x* ratio,
                    ly = left_mesh[r][c].y * (1 - ratio),
                    ry = right_mesh[r][c].y * ratio,
                    x = lx + rx,
                    y = ly + ry;
                this.result_mesh[r][c] = new Point((int)x, (int)y);
            }
        }
    }
    /**
     * Merge all points of a triangle.
     */
    private void triangle(){
        CTriangle result = this.result_triangles[this.t_idx];
        /* Left transformation matrix. */
        this.left_trafo = CGeo.getTrafo(left_triangles[t_idx], result);
        /* Right transformation matrix. */
        this.right_trafo = CGeo.getTrafo(right_triangles[t_idx], result);
        /* For all target points. */
        this.withins = result.getWithins();
        for(int i = 0; i < this.withins.length; ++i){
            this.result_point = this.withins[i];
            /* Transform left. */
            this.left_point = CGeo.getOrigin_(result_point, left_trafo);
            /* Transform right. */
            this.right_point = CGeo.getOrigin_(result_point, right_trafo);
            /* Merge booth pixels. */
            this.merge();
        }   
    }
    /**
     * Merge (left.pixel, right.pixel)->(result.pixel). Result depends on
     * ratio value & booth polygon matrixes.
     */
    private void merge(){
        try{
            left_pixel = left_image.getRGB(left_point.x, left_point.y);
            right_pixel = right_image.getRGB(right_point.x, right_point.y);
            left_ratio = left_clip[left_point.x][left_point.y];
            right_ratio = right_clip[right_point.x][right_point.y];
            /* Unify all 3 ratios. */
            double
                t1 = left_ratio,
                t2 = 1.0 - left_ratio,
                t3 = 1.0 - right_ratio,
                t4 = right_ratio,
                fl = t3 + (1.0 - ratio) * (t1 - t3),
                fr = t2 + ratio * (t4 - t2);
            /* For each color in 32 bit color value. */
            int 
                l_r = ( left_pixel & 0xffff0000) >> 16,
                r_r = (right_pixel & 0xffff0000) >> 16,
                l_g = ( left_pixel & 0xff00ff00) >> 8,
                r_g = (right_pixel & 0xff00ff00) >> 8,
                l_b =   left_pixel & 0xff0000ff,
                r_b =  right_pixel & 0xff0000ff,
                r = (int)(l_r * fl + r_r * fr),
                g = (int)(l_g * fl + r_g * fr),
                b = (int)(l_b * fl + r_b * fr);
            result_pixel = (0xff000000) | (r << 16) | (g << 8) | b;    
            result_image.setRGB(result_point.x, result_point.y, result_pixel);
        }catch(Exception e){}
     }
}
