package javamorph;

import javax.swing.*;
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
 * Class: CAbout.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Global info about the JavaMorphProgram (JDialog).
 * <br/>
 * Hint: This class isn't the container but the shown info component.
 */
public class CAbout extends JLabel {
    /** Java API.*/
    private static final long serialVersionUID = 1L;
    /** Preferred size of this dialog. */
    public static final Dimension PREF_SIZE = new Dimension(400, 300);
    /** Title of this dialog. */
    public static final String TITLE = "About";
    /** Background color of this dialog. */
    public static final Color BACKGROUND = Color.black;
    /** Foreground color of this dialog. */
    public static final Color FOREGROUND = Color.red;
    /** Textcolor of the info text (text about the program. */
    public static final Color TEXT = Color.white;
    /** Gui container at window level. */
    private JDialog dialog;
    /** Parent frame to enable modal behavior. */
    private JFrame parent;    
    /** 
     * Constructor.
     * @param parent Applications main JFrame to enable modal behavior.
     */
    public CAbout(JFrame parent){
        this.parent = parent;
        dialog = new JDialog(parent, TITLE, true);
        dialog.getContentPane().add(this);
        dialog.pack();
    }    
    /**
     * Show the about dialog, when requested by user's menu command.
     */
    public void open(){
        dialog.setLocation
        (parent.getLocation().x + 10, parent.getLocation().y + 10);
        dialog.setSize(PREF_SIZE);
        dialog.setResizable(false);
        dialog.setVisible(true);
        dialog.setResizable(true);
    }
    /**
     * Paint the component within the container, showing the info.
     * Fetch data from the global string table.
     */
    public void paint(Graphics g){
        Dimension size = this.getSize();
        ((Graphics2D)g).setRenderingHint
        (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(FOREGROUND);
        g.fillPolygon(
                new int[]{15, size.width - 20, size.width - 10, 5}, 
                new int[]{10, 20, size.height - 20, size.height - 10}, 
                4);
        g.setFont(new Font("Sans Serif", Font.BOLD, 16));
        g.setColor(TEXT);
        g.drawString(CStrings.PROG, 30, 30);
        g.drawString("Author: ", 30, 60);
        g.drawString(CStrings.AUTHOR, 40, 80);
        g.drawString("Version: " + CStrings.VERSION, 30, 110);
        g.drawString("License: " + CStrings.LICENSE, 30, 140);
        g.setFont(new Font("Sans Serif", Font.BOLD, 10));
        g.drawString("Home: " + CStrings.HOME, 20, 170);
        g.drawString
        ("Work: " + "Home" + CStrings.SEP + CStrings.APPDIR, 20, 190);
        g.drawString
        ("Hint: Find a tutorial within the help subdir of the workdir."
                , 20, 210);
    }
    /**
     * Satisfy the java API.
     */
    public void update(Graphics g){
        paint(g);
    }
}
