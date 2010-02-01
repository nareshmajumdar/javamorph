package javamorph;

import java.awt.*;
import java.awt.event.*;
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
 * Class: CProgress.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Shows a dialog with the progress bar during morphing.
 * <br/>
 * Hint: Dialog blocks the caller, that's why morph process is an own thread.
 */
public class CProgress extends JDialog 
    implements ActionListener{
    /** Due to java API. */
    private static final long serialVersionUID = 1L;
    /** Size of the progrssbar on screen. */
    public static final Dimension PREF_SIZE = new Dimension(250, 25);
    /** Color of the elapsed time. */
    public static final Color FOREGROUND = Color.red;
    /** Color of the remaining time. */
    public static final Color BACKGROUND = Color.white;
    /** Parent JFrame to enable modal behavior. */
    private JFrame parent;
    /** Break button for user abort. */
    private JButton b_break = new JButton("Break!");
    /** Progress bar JComponent. */
    private JProgressBar b_progress = new JProgressBar();
    /**
     * Constructor.
     * @param parent Parent JFrame to enable modal behavior.
     */
    public CProgress(JFrame parent){
        super(parent, "Morph Progress", true);
        this.parent = parent;
        this.b_break.addActionListener(this);
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add("Center", b_progress);
        this.add("East", b_break);
        this.b_progress.setForeground(FOREGROUND);
        this.b_progress.setBackground(BACKGROUND);
        this.setUndecorated(true);
        this.pack();
    }
    /**
     * Show the progress bar.
     */
    public void open(){
        this.b_progress.setValue(0);
        this.setLocation
        (parent.getLocation().x + 10, parent.getLocation().y + 10);
        this.setSize(PREF_SIZE);
        this.setResizable(false);
        this.setVisible(true);
        this.setResizable(true);
    }
    /**
     * Close the progress bar when morphing is finnished.
     */
    public void close(){
        this.setVisible(false);
    }
    /**
     * Set function.
     * @param progress Elapsed steps.
     * @param min Offset. Normally zero.
     * @param max Number of steps.
     */
    public void setProgress(int progress, int min, int max){
        this.b_progress.setValue(progress);
        this.b_progress.setMinimum(min);
        this.b_progress.setMaximum(max);
    }
    /**
     * User has clicked the abort button.
     */
    public void actionPerformed(ActionEvent e) {
        if(this.b_break == e.getSource()){
            CMorphOperator.doBreak();
            this.close();
        }
    }
}
