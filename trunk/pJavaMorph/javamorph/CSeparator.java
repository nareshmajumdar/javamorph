package javamorph;

import java.awt.*;
import javax.swing.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CSeparator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Display layout helper.
 * <br/>
 * Hint: Splits left image & right image when the application displays them
 * on the screen.
 */
public class CSeparator extends JComponent{
    /** Due to java API. */
    private static final long serialVersionUID = 1L;
    /** Define the width of the vertical rectangle on the screen. */
    public static final Dimension PREF_SIZE = new Dimension(3,1);
    /** Define the 1st color of the separator. */
    public static final Color BACKGROUND = Color.black;
    /** Define the 2nd color of the separator. */
    public static final Color FOREGROUND = Color.white;
    
    /** Constructor. */
    public CSeparator(){
        this.setBackground(CSeparator.BACKGROUND);
        this.setForeground(FOREGROUND);
    }
    /**
     * Java API.
     */
    public void paint(Graphics g){
        Dimension size = this.getSize();
        int i = 0;
        do{
            if(0 == ((i / 5) % 2)){
                g.setColor(this.getBackground());
            }else{
                g.setColor(this.getForeground());
            }
            g.fillRect(0, i, size.width, 5);
        }while((i += 5) < size.height);
    }
    /** Java API. */
    public Dimension getPreferredSize(){
        return CSeparator.PREF_SIZE;
    }
}
