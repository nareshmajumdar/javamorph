package javamorph;

import java.awt.*;
import java.awt.image.*;

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
 * Class: CPictureDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Draw one of both input pictures into the component.
 * <br/>
 * Hint: Scaling is necessary depending on relation between width & height.
 */
public class CPictureDecorator implements IDecorator{
    /** JComponent to draw the picture to. */
    private CFrame parent;
    /** Image from file to draw. */
    private BufferedImage image;
    /** X position of the top left corner in screen pixel units. */
    private int x;
    /** Y position of the top left corner in screen pixel units. */
    private int y;
    /** Width of the image in screen pixel units. */
    private int width;
    /** Height of the image in screen pixel units. */
    private int height;
    /**
     * Constructor.
     * @param parent Graphical component to draw the picture to.
     * @param image Input picture of the own side.
     */
    public CPictureDecorator(CFrame parent, BufferedImage image){
        this.parent = parent;
        this.image = image;
    }
    /**
     * Draw the picture to the Graphics context of the parent JComponent.
     */
    public void paint(Graphics g) {
        Dimension size = parent.getSize();
        int
            cwidth = image.getWidth(),
            cheight = image.getHeight(),
            w = size.width,
            h = size.height;
        double quotient = (double)cwidth / (double)cheight;
        if(h * quotient <  size.width){
            height = h;
            width = (int)(h * quotient);
        }else{
            width = w;
            height = (int)(w / quotient);
        }
        x = (w - width) / 2;   
        y = (h - height) / 2;
        g.drawImage(image, x, y, width, height, parent);
    }
    /**
     * Get function.
     * @return Image bounds in screen pixel units.
     */
    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}
