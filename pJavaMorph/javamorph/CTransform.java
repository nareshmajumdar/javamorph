package javamorph;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CTransform.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Affine transform matrix.
 * <br/>
 * Hint: Example: a_rc -> r = row_in_matrix, c = column in matrix. Row != Column
 * because inversion is not necessary.
 * <br/>
 * Usage: M * P' = P (M...Matrix, P'...Result Point, P...Origin Point).
 */
public class CTransform {
    /** Matrix factor. Row = 1, Column = 1.*/
    double a_11;
    /** Matrix factor. Row = 1, Column = 2.*/
    double a_12;
    /** Matrix factor. Row = 1, Column = 3.*/
    double a_13;
    /** Matrix factor. Row = 2, Column = 1.*/
    double a_21;
    /** Matrix factor. Row = 2, Column = 2.*/
    double a_22;
    /** Matrix factor. Row = 2, Column = 3.*/
    double a_23;
}
