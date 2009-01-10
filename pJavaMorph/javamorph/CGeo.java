package javamorph;

import java.awt.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CGeo.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Helper class for geometric calculations.
 * <br/>
 * Hint: a) Split mesh into triangles. b) Handle interpolation - transformation.
 */
public class CGeo {
    /**
     * Make list of triangles from the quad mesh.
     * @param mesh Quad mesh.
     * @return Array of triangles.
     */
    public static CTriangle[] getTriangles(Point[][] mesh){
        int r = mesh.length - 1, c = mesh[0].length - 1;
        CTriangle[] result = new CTriangle[c * r * 2];
        CTriangle t;
        for(int row = 0; row < r; ++ row){
            for(int col = 0; col < c; ++ col){
                t = new CTriangle(
                        mesh[row][col],
                        mesh[row][col + 1],
                        mesh[row + 1][col]
                    );
                result[(row * c + col) * 2] = t;
                t = new CTriangle(
                        mesh[row + 1][col + 1],
                        mesh[row][col + 1],
                        mesh[row + 1][col]
                    );
                result[(row * c + col) * 2 + 1] = t;
            }
        }
        return result;
    }
    /**
     * Provide reverse transformation matrix from one result triangle to one
     * input picture triangle. All three points of the two triangles
     * correspond.
     * @param origin_ One triangle of the source picture. 
     * @param result One triangle of the result picture.
     * @return Floating point transformation matrix. Indexes are row / column.
     */
    public static CTransform getTrafo(CTriangle origin_, CTriangle result){
        int
            x1_ = origin_.getPoints()[0].x,
            x2_ = origin_.getPoints()[1].x,
            x3_ = origin_.getPoints()[2].x,
            y1_ = origin_.getPoints()[0].y,
            y2_ = origin_.getPoints()[1].y,
            y3_ = origin_.getPoints()[2].y,
            x1 = result.getPoints()[0].x,
            x2 = result.getPoints()[1].x,
            x3 = result.getPoints()[2].x,
            y1 = result.getPoints()[0].y,
            y2 = result.getPoints()[1].y,
            y3 = result.getPoints()[2].y;
        CTransform trafo = new CTransform();
        double t, u, d;
        if(x1 != x3){
            d = (double)x1 - x3;
            t = ((double)x1 - x2) / d;
            u = ((double)y1 - y2) - ((double)y1 - y3) * t;
            trafo.a_12 = (((double)x1_ - x2_) - ((double)x1_ - x3_) * t) / u; 
            trafo.a_22 = (((double)y1_ - y2_) - ((double)y1_ - y3_) * t) / u;
            trafo.a_11 = (((double)x1_ - x3_) - trafo.a_12 * ((double)y1 - y3)) 
                / d;
            trafo.a_21 = (((double)y1_ - y3_) - trafo.a_22 * ((double)y1 - y3)) 
                / d;
        }else{
            d = (double)y1 - y3;
            t = ((double)y1 - y2) / d;
            u = ((double)x1 - x2) - ((double)x1 - x3) * t;
            trafo.a_11 = (((double)x1_ - x2_) - ((double)x1_ - x3_) * t) / u; 
            trafo.a_21 = (((double)y1_ - y2_) - ((double)y1_ - y3_) * t) / u;
            trafo.a_12 = (((double)x1_ - x3_) - trafo.a_11 * ((double)x1 - x3)) 
                / d;
            trafo.a_22 = (((double)y1_ - y3_) - trafo.a_21 * ((double)x1 - x3)) 
                / d;
        }
        trafo.a_13 = (double)x1_ - trafo.a_11 * x1 - trafo.a_12 * y1;
        trafo.a_23 = (double)y1_ - trafo.a_21 * x1 - trafo.a_22 * y1;
        return trafo;
    }
    /**
     * Transform one point from the result matrix to the one point of the input
     * matrix.
     * @param result Point of the result picture.
     * @param trafo Transformation matrix.
     * @return Corresponding point of the input picture.
     */
    public static Point getOrigin_(Point result, CTransform trafo){
        Point origin_ = new Point();
        origin_.x = (int)
            (result.x * trafo.a_11 + result.y * trafo.a_12 + trafo.a_13);
        origin_.y = (int)
            (result.x * trafo.a_21 + result.y * trafo.a_22 + trafo.a_23);
        return origin_;
    }
}
