package javamorph;

import java.awt.*;
import javax.swing.*;

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
