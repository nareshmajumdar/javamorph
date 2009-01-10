package javamorph;

import java.awt.*;
import java.util.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CTriangle.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Data structure for triangulation of the mesh.
 * <br/> 
 * Hint: Relating to picture pixel units.
 */
public class CTriangle {
    /** All three corners of the triangle. */
    private Point p[] = new Point[]{new Point(), new Point(), new Point()};
    /** All pixel points within the triangle. */
    private Point withins[];
    /** Rectangular border of the triangle. */
    private int y_min = Integer.MAX_VALUE;
    /** Rectangular border of the triangle. */
    private int y_max = Integer.MIN_VALUE;
    /** Rectangular border of the triangle. */
    private int x_min;
    /** Rectangular border of the triangle. */
    private int x_max;
    /**
     * Copy constructor.
     * @param _p0 First of the three points.
     * @param _p1 Second of the three points.
     * @param _p2 Third of the three points
     */
    public CTriangle(Point _p0, Point _p1, Point _p2){
       Vector<Point> v = new Vector<Point>();
        p[0].x = _p0.x;
        p[0].y = _p0.y;
        p[1].x = _p1.x;
        p[1].y = _p1.y;
        p[2].x = _p2.x;
        p[2].y = _p2.y;
        /* Calculate the y range. */
        for(int i = 0;i < 3; ++i){
            this.y_min = Math.min(this.y_min, p[i].y);
            this.y_max = Math.max(this.y_max, p[i].y);
        }
        /* For all horizontal lines. */
        for(int y = this.y_min; y <= this.y_max; ++y){
            /* Calculate the x range. */
            this.calculateXBounds(y);
            /* For all points of the horizontal line. */
            for(int x = this.x_min; x <= this.x_max; ++x){
                /* Add within - point. */
                Point pt = new Point();
                pt.x = x;
                pt.y = y;
                v.add(pt);
            }
        }
        this.withins = new Point[v.size()];
        /* Make vector to array. */
        v.toArray(this.withins);
    }
    /**
     * Get function.
     * @return All points within the triangle.
     */
    public Point[] getWithins(){
        return this.withins;
    }    
    /** 
     * Get function.
     * @return All three corner points.
     */
    public Point[] getPoints(){
        return this.p;
    }
    /**
     * Calculate left & right edge point of the scan line. The scan line must 
     * cross two of the three edges. Seldom the scan line crosses one edge
     * + two end points of the other booth edges.
     * @param y Vertical position of the scan line.
     */
    private void calculateXBounds(int y){
        int 
            x1 = getXIntersection(y, p[0], p[1]),
            x2 = getXIntersection(y, p[0], p[2]),
            x3 = getXIntersection(y, p[1], p[2]);
        if(Integer.MIN_VALUE == x1)
        {
            this.x_min = Math.min(x2, x3);
            this.x_max = Math.max(x2, x3);
        }else if(Integer.MIN_VALUE == x2){
            this.x_min = Math.min(x1, x3);
            this.x_max = Math.max(x1, x3);
        }else if(Integer.MIN_VALUE == x3){
            this.x_min = Math.min(x2, x1);
            this.x_max = Math.max(x2, x1);
        }else if(x1 != x2){
            this.x_min = Math.min(x2, x1);
            this.x_max = Math.max(x2, x1);
        }else if(x1 != x3){
            this.x_min = Math.min(x3, x1);
            this.x_max = Math.max(x3, x1);
        }else if(x3 != x2){
            this.x_min = Math.min(x2, x3);
            this.x_max = Math.max(x2, x3);
        }
    }
    /**
     * Test 
     * @param y Vertical position of the scan line.
     * @param p1 First point of the edge line.
     * @param p2 Second point of the edge line.
     * @return X coordinate of the intersection point between edge & scan line.
     */
    private int getXIntersection(int y, Point p1, Point p2){
        int quotient = p2.y - p1.y;
        double param = ((double)y - p1.y) / quotient;
        if(0.0 > param || 1.0 < param || 0 == quotient)return Integer.MIN_VALUE;
        return (int)(p1.x +  param  * ((double)p2.x - p1.x));
    }
}
