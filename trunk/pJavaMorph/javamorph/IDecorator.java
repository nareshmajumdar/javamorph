package javamorph;

import java.awt.*;

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
 * Class: IDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Interface to draw some layers to a JComponent.
 * <br/> 
 * Hint: Consider the order of the several drawing steps!
 */
public interface IDecorator {
    /**
     * Draw the content.
     * @param g Graphics context of the JComponent.
     */
    public void paint(Graphics g);
    /** 
     * Notify the decorator implementation of the available size to draw to.
     * @param size Size in screen pixel units.
     */
}
