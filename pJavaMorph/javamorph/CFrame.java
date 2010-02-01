package javamorph;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

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
    private CMeshDecorator dmesh;
    /** Decorator to draw & edit the picture as subject of this program. */
    private CPictureDecorator dpicture;
    /** Decorator to draw & edit the clip polygon. */
    private CPolygonDecorator dpolygon;
    /** Decorator to provide the pop up menu. */
    private CPopupMenuDecorator dpopup;
    /** Double buffer image to avoid flickering effects. */
    private BufferedImage moffline;  
    /** Picture's mesh. */
    protected Vector<Point> mesh;
    /** Picture's clip polygon points. */
    protected Vector<Point> polygon;
    /** Picture itself. */
    private BufferedImage image;
    /** Polygon's smoothed clip matrix. */
    protected double clip[][];
    /** File to store a debug copy of the clip matrix. */
    protected File f_clip;
    /** 
     * Constructor.
     * 
     * @param parent Main JFrame.
     * @param mesh Picture's mesh.
     * @param polygon Clip polygon points.
     * @param image Picture itself.
     * @param clip Smoothed clip matrix.
     * @param f_mesh File to store to mesh to.
     * @param f_polygon File to store the polygon to.
     * @param f_clip File to debug the smoothed clip matrix to.
     */
    public CFrame(CMain parent, 
            Vector<Point> mesh,
            Vector<Point> polygon,
            BufferedImage image,
            double clip[][],
            File f_mesh,
            File f_polygon,
            File f_clip){
        /* Assign the arguments. */
        this.parent = parent;
        this.mesh = mesh;
        this.polygon = polygon;
        this.image = image;
        this.clip = clip;
        this.f_clip = f_clip;
        /* Init decorators. */
        dpolygon = 
            new CPolygonDecorator(this, polygon, image, f_polygon, clip, f_clip);
        dmesh = new CMeshDecorator(this, mesh, image, f_mesh);
        dpopup = new CPopupMenuDecorator(this);
        dpicture = new CPictureDecorator(this, image);
        /* Set color. */
        setBackground(CFrame.BACKGROUND);
        /* Set crosshair cursor. */
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
    /**
     * Paint this component to the screen. Call the decorators therefore.
     */
    public void paint(Graphics g){
        int r = 0, c = 0;
        Dimension size = this.getSize();
        /* Init double buffer. */
        this.moffline = new BufferedImage(
                size.width, 
                size.height, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics o = this.moffline.getGraphics();
        o.setColor(this.getBackground());
        /* Draw checkerboard pattern. */
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
        /* Set anit-alias. */
        ((Graphics2D)o).setRenderingHint
        (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        /* Call decorators. */
        dpicture.paint(o);
        dmesh.paint(o);
        dpolygon.paint(o);
        dpopup.paint(o);
        /* Draw onto screen. */
        g.drawImage(moffline, 0, 0, this);
    }
    /**
     * Java API.
     */
    public void update(Graphics g){
        this.paint(g);
    }
    /**
     * Java API. Constant used as return.
     */
    public Dimension getPreferredSize(){
        return CFrame.PREF_SIZE;
    }
    /**
     * Return the position & the size of the drawn image within the component.   
     * @return Bounds in screen pixel units relating to this component.
     */
    public Rectangle getImageBounds(){
        return this.dpicture.getBounds();
    }
    /**
     * Obtain the instance of the application's main class.
     */
    public CMain getParent(){
        return this.parent;
    }

    public void genClip(){
        this.dpolygon.genClip();
    }
    /**
     * Scale a screen point to a picture point.
     * @param p Point in screen resolution units.
     */
    public void scalePoint(Point p){
        /* Picture area on JComponent. */
        Rectangle bounds = getImageBounds();
        /* Picture's own size. */
        int w = image.getWidth(), h = image.getHeight();
        /* Scale. */
        double x, y;
        x = (p.x - bounds.x) * w / bounds.width;
        y = (p.y - bounds.y) * h / bounds.height;
        x = Math.max(0, Math.min(w, x));
        y = Math.max(0, Math.min(h, y));
        p.x = (int)x;
        p.y = (int)y;
        /* Limit relating the picture's own size. */
        p.x = Math.max(0, Math.min(image.getWidth() - 1, p.x));
        p.y = Math.max(0, Math.min(image.getHeight() - 1, p.y));
    }
    /** 
     * Scale mark & cursor size simultan.
     * (Regression.)
     * 
     * @return Mark size to be draw by scaling decorator.
     */
    public int scaleMarkSize(){
        /* Bounds = screen pixel size.*/
        /* Image = origin pixel size.*/
        return 
            CConfig.MARK_SIZE * 
            image.getWidth() /
            getImageBounds().width;
    }
    /** 
     * Delete content of mesh.
     * Initialize matrix controlled by configuration_s number of rows & columns.
     */
    public void initMesh(){
        dmesh.init();
    }
    /** 
     * Delete polygon.
     * Initialize controlled by configuration's number of points.
     */
    public void initPolygon(){
        dpolygon.init();
    }
}
