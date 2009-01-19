package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.*;

/**
 * @version 1.1
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.1.
 * <br/>
 * Class: CTriangulation.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Groups a set of points within one picture to a
 * DELAUNAY triangulation.
 * <br/> 
 * Hint: Not more than 500 points expected.
 */
public class CTriangulation{
    /** Width of the picture. */
    private static int width;
    /** Height of the picture. */
    private static int height;
    /** Collection of all input points, order not modified. */
    private static final Vector<Point> order = new Vector<Point>();
    /** 
     * Collection of all unique input points. No point occurs twice. 
     * Order not original.
     */
    private static final HashSet<Point> points = new HashSet<Point>();
    /** Collection of lines which have already been processed. */
    private static final HashSet<CLine> used = new HashSet<CLine>();
    /** Queue of explored points which have to be processed. */
    private static final Queue<Point[]> queue= new ArrayDeque<Point[]>();
    /** Result of the process is this triangulation. */
    private static final HashSet<CTriangle> triangles = 
        new HashSet<CTriangle>();
    /** Due to DELAUNAY. Circumcircle of the triangle to test. */
    private static double circle_radius;
    /** Center x of the circumcircle of the triangle to test. */
    private static double center_x;
    /** Center y of the circumcircle of the triangle to test. */
    private static double center_y;
    /** First point of the line to explore. */
    private static Point p1;
    /** Second point of the line to explore. */
    private static Point p2;
    /** Point to test relating to the line: No other point shall lay nearer. */
    private static Point pnew;
    /** 
     * Count of found new point relating to the line. Max. two points 
     * can be found.
     */
    private static int count;
    
    /**
     * Perform complete operation.
     */
    public static void triangulate(){
        Point p, l, r;
        clear();
        points.clear();
        width = CConfig.left_image.getWidth() + CConfig.right_image.getWidth();
        height = 
            CConfig.left_image.getHeight() + CConfig.right_image.getHeight();
        width /= 2;
        height /= 2;
        for(int i = 0; i < CConfig.left_mesh.size(); ++i)
        {
            l = CConfig.left_mesh.get(i);
            r = CConfig.right_mesh.get(i);
            p = new Point((l.x + r.x) / 2, (l.y + r.y) / 2);
            points.add(p);
            order.add(p);
        }
        work();
        debug();
    }
    /**
     * Clear all permanent date of the collections.
     */
    private static void clear(){
        used.clear();
        triangles.clear();
        queue.clear();
        CConfig.left_triangles.clear();
        CConfig.right_triangles.clear();
        CConfig.result_triangles.clear();
    }
    /**
     * Calculate the triangles.
     */
    private static void work(){
        clear();
        if(3 > points.size())return;
        Iterator<Point> it= points.iterator();
        p1 = it.next();
        p2 = findNearest(p1);
        used.add(new CLine(p1, p2));
        queue.add(new Point[]{p1, p2});
        while(0 < queue.size())findPoint();
    }
    /**
     * Explore the third points for one line.
     */
    private static void findPoint(){
        Point a[] = queue.poll();
        p1 = a[0];
        p2 = a[1];
        count = 0;
        for(Point p: points){
            pnew = p;
            if(circle()){
                if(delaunayCond()){
                    add(new CTriangle(p1, p2, pnew));
                    if(used.add(new CLine(p1, pnew))){
                        queue.add(new Point[]{p1, pnew});
                    }
                    if(used.add(new CLine(p2, pnew))){
                        queue.add(new Point[]{p2, pnew});
                    }
                    if(1 == count++){
                        return;
                    }
                }
            }
        }
    }
    /**
     * Add one left & one right triangle.
     * Points are fetched ordered from left & right mesh them self.
     *
     * @param temp Input triangle.
     */
    private static void add(CTriangle temp){
        if(triangles.add(temp)){
            Point 
                l0 = CConfig.left_mesh.get(indexOf(temp.getPoints()[0])),
                l1 = CConfig.left_mesh.get(indexOf(temp.getPoints()[1])),
                l2 = CConfig.left_mesh.get(indexOf(temp.getPoints()[2])),
                r0 = CConfig.right_mesh.get(indexOf(temp.getPoints()[0])),
                r1 = CConfig.right_mesh.get(indexOf(temp.getPoints()[1])),
                r2 = CConfig.right_mesh.get(indexOf(temp.getPoints()[2]));
            CConfig.left_triangles.add(new CTriangle(l0, l1, l2));
            CConfig.right_triangles.add(new CTriangle(r0, r1, r2));
        }
    }
    /**
     * Seek one point within the ordered input list.
     * @param p Point to seek.
     * @return Index of the point in the ordered input list.
     */
    private static int indexOf(Point p){
        return order.indexOf(p);
    }
    /**
     * Check the DELAUNAY condition of P1 P2 and PNEW.
     * No other point shall be within the circumcircle of the three points. 
     * 
     * @return <code>true<code> if DELAUNAY condition is satisfied.
     */
    private static boolean delaunayCond(){
        CLine 
            l1 = new CLine(p1, pnew), 
            l2 = new CLine(p2, pnew);
        for(Point p: points){
            double d = distance(p);
            if(!p.equals(pnew) && !p.equals(p1) && !p.equals(p2)){
                if(d < circle_radius){
                    return false;
                }
            }
        }
        for(CLine l: used){
            if(l.cross(l1) || l.cross(l2)){
                return false;
            }
        }
        return true;
    }
    /** 
     * Find the nearest neighbor of one mesh point.
     * 
     * @param p1 First point.
     * @return Nearest point to p1.
     */
    private static Point findNearest(Point p1){
        double dist = Double.MAX_VALUE, d;
        Point result = null;
        for(Point p: points){
            d = p.distance(p1);
            if(d < dist && d > 0.0){
                dist = d;
                result = p;
            }
        }
        return result;
    }
    /**
     * Calculates the circumcircle of the current test triangle's points.
     * 
     * @return <code>true</code> if such a circle can be found.
     */
    private static boolean circle(){
        double x1, y1, x2, y2, x3, y3, q, n;
        x1 = (p1.x + pnew.x) / 2.0;
        y1 = (p1.y + pnew.y) / 2.0;
        x3 = (p2.x + pnew.x) / 2.0;
        y3 = (p2.y + pnew.y) / 2.0;
        x2 = pnew.x;
        y2 = pnew.y;
        q = (y2 - y1) * (y3 - y1) - (-x2 + x1) * (x3 - x1);
        n = (y2 - y3) * (-x2 + x1) - (-x2 + x3) * (y2 - y1);
        if(0.0 == n){
            return false;
        }
        q /= n;
        center_x = x3 + q * (y2 - y3);
        center_y = y3 + q * (-x2 + x3);
        circle_radius = distance(p1);
        return true;
    }
    /**
     * Distance of one point to the center of the circumcircle.
     * 
     * @param p Point to be explored.
     * @return Distance.
     */
    private static double distance(Point p){
        double dx = p.x - center_x, dy = p.y -center_y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    /**
     * Write left right and 50% triangulation into the debug directory.
     */
    private static void debug(){
        BufferedImage image;
        try{
            image = new BufferedImage(CConfig.left_image.getWidth(), 
                    CConfig.left_image.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            for(CTriangle t: CConfig.left_triangles){
                t.debug(image);
            }
            ImageIO.write(image, "png", new File(CStrings.LEFT_TRI));
            image = new BufferedImage(
                    CConfig.right_image.getWidth(),
                    CConfig.right_image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
                    );
            for(CTriangle t: CConfig.right_triangles){
                t.debug(image);
            }
            ImageIO.write(image, "png", new File(CStrings.RIGHT_TRI));

            image = new BufferedImage(
                    width,
                    height,
                    BufferedImage.TYPE_INT_RGB
                    );
            for(CTriangle t: triangles){
                t.debug(image);
            }
            ImageIO.write(image, "png", new File(CStrings.MIDDLE_TRI));
        }catch(Exception e){
            System.out.println("Can't debug triangles.");
            e.printStackTrace();
        }
    }
 }
