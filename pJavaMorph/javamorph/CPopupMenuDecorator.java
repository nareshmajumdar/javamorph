package javamorph;

import java.io.*;
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
 * Class: CPopupMenuDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Handles the user's pop up menu.
 * <br/>
 * Hint: Draw the hint symbol & process the menu commands.
 */
public class CPopupMenuDecorator 
    implements IDecorator, MouseListener, ActionListener{
    /** One of the colors of the mouse symbol (area). */
    public static final Color MOUSE = new Color(255, 0, 0, 150);
    /** One of the colors of the mouse symbol (buttons). */
    public static final Color BUTTON = new Color(0, 0, 255, 100);
    /** One of the colors of the mouse symbol (border line). */
    public static final Color FRAME = new Color(0, 0, 0, 200);
    /** One of the colors of the mouse symbol. (connection cable fragment) */
    public static final Color CABLE = new Color(0, 128, 0, 200);
    /** Parent JComponent to draw the mouse symbol to. */
    private CFrame parent;
    /** Pop up menu object with application control commands. */
    private JPopupMenu popup_menu = new JPopupMenu();
    /** Sub menu, edit medh points. */
    private JMenu mesh_menu = new JMenu("Mesh Points /");
    /** Add mesh points command. */
    private JRadioButtonMenuItem m_add_mesh_points = 
        new JRadioButtonMenuItem("Add Points +");
    /** Remove mesh points command. */ 
    private JRadioButtonMenuItem m_sub_mesh_points = 
        new JRadioButtonMenuItem("Sub Points -");
    /** Move mesh points command. */
    private JRadioButtonMenuItem m_off_mesh_points = 
        new JRadioButtonMenuItem("Off Points %");
    /** Delete whole mesh command. */
    private JMenuItem m_delete_mesh = new JMenuItem("Delete Mesh X");
    /** Sub menu, edit polygon points. */
    private JMenu polygon_menu = new JMenu("Polygon Points /");
    /** Delete mesh command. */
    /** Add polygon points command. */
    private JRadioButtonMenuItem m_add_polygon_points = 
        new JRadioButtonMenuItem("Add Points +");
    /** Remove polygon points command. */
    private JRadioButtonMenuItem m_sub_polygon_points = 
        new JRadioButtonMenuItem("Sub Points -");
    /** Move polygon points command. */
    private JRadioButtonMenuItem m_off_polygon_points = 
        new JRadioButtonMenuItem("Off Points %");
    /** Delete the whole polygon command. */
    private JMenuItem m_delete_polygon = new JMenuItem("Delete Polygon X");
    /** Radio button group a) edit mesh b) edit polygon. */
    private ButtonGroup g_radio_buttons = new ButtonGroup();
    /** Group of the mesh sub menu radio buttons. */
    private ButtonGroup g_mesh_buttons = new ButtonGroup();
    /* Group of the polygon sub menu radio buttons. */
    private ButtonGroup g_polygon_buttons = new ButtonGroup();
    /** Morph command. */
    private JMenuItem m_morph = new JMenuItem("Morph!");
    /** Edit mesh command. */
    private JRadioButtonMenuItem m_edit_mesh = 
        new JRadioButtonMenuItem("Edit mesh.");
    /** Edit polygon command. */
    private JRadioButtonMenuItem m_edit_polygon =
        new JRadioButtonMenuItem("Edit polygon.");
    /** Edit configuration command. */
    private JMenuItem m_edit_config = new JMenuItem("Edit Config ->");
    /** Show about box command. */
    private JMenuItem m_about = new JMenuItem("Help about?");
    /**
     * Constructor.
     * @param parent The parent JComponent to draw the mouse symbol to.
     */
    public CPopupMenuDecorator(CFrame parent){
        this.parent = parent;
        /* Initialize the whole pop up menu. */
        popup_menu.add(m_morph);
        popup_menu.addSeparator();
        popup_menu.add(m_edit_mesh);
        popup_menu.add(m_edit_polygon);
        popup_menu.addSeparator();
        popup_menu.add(m_edit_config);
        popup_menu.add(this.mesh_menu);
        popup_menu.add(this.polygon_menu);
        popup_menu.addSeparator();
        popup_menu.add(m_about);
        mesh_menu.add(m_add_mesh_points);
        mesh_menu.add(m_sub_mesh_points);
        mesh_menu.add(m_off_mesh_points);
        mesh_menu.add(m_delete_mesh);
        polygon_menu.add(m_add_polygon_points);
        polygon_menu.add(m_sub_polygon_points);
        polygon_menu.add(m_off_polygon_points);
        polygon_menu.add(m_delete_polygon);
        g_radio_buttons.add(m_edit_mesh);
        g_radio_buttons.add(m_edit_polygon);
        g_mesh_buttons.add(m_add_mesh_points);
        g_mesh_buttons.add(m_sub_mesh_points);
        g_mesh_buttons.add(m_off_mesh_points);
        g_polygon_buttons.add(m_add_polygon_points);
        g_polygon_buttons.add(m_sub_polygon_points);
        g_polygon_buttons.add(m_off_polygon_points);
        /* Add the listeners. */
        m_morph.addActionListener(this);
        m_edit_mesh.addActionListener(this);
        m_edit_polygon.addActionListener(this);
        m_edit_config.addActionListener(this);
        m_add_mesh_points.addActionListener(this);
        m_sub_mesh_points.addActionListener(this);
        m_off_mesh_points.addActionListener(this);
        m_delete_mesh.addActionListener(this);
        m_add_polygon_points.addActionListener(this);
        m_sub_polygon_points.addActionListener(this);
        m_off_polygon_points.addActionListener(this);
        m_delete_polygon.addActionListener(this);
        m_about.addActionListener(this);
        parent.addMouseListener(this);
    }
    /**
     * Draw the mouse symbol to the graphics context of the parent JComponent.
     */
    public void paint(Graphics g){
        g.setColor(MOUSE);
        g.fillRect(10, 10, 30, 30);
        g.setColor(FRAME);
        g.drawRect(10, 10, 30, 30);
        g.setColor(CABLE);
        g.fillRect(24, 41, 2, 15);
        g.fillPolygon(new int[]{24, 34, 36, 26}, new int[]{55, 65, 65, 55}, 4);
        g.setColor(BUTTON);
        g.fillRect(10, 10, 10, 10);
        g.fillRect(30, 10, 10, 10);
    }
    /** The user wants to see the pop up menu. */
    public void mouseClicked(MouseEvent e){
        if(MouseEvent.BUTTON3 == e.getButton())
        {
            switch(CConfig.edit_state){
                case CConfig.EDIT_MESH_ADD : 
                    m_edit_mesh.setSelected(true);
                    m_add_mesh_points.setSelected(true);
                    m_off_polygon_points.setSelected(true);
                    break;
                case CConfig.EDIT_MESH_SUB : 
                    m_edit_mesh.setSelected(true);
                    m_sub_mesh_points.setSelected(true);
                    m_off_polygon_points.setSelected(true);
                    break;
                case CConfig.EDIT_MESH_OFF : 
                    m_edit_mesh.setSelected(true);
                    m_off_mesh_points.setSelected(true);
                    m_off_polygon_points.setSelected(true);
                    break;
                case CConfig.EDIT_POLYGON_ADD : 
                    m_edit_polygon.setSelected(true);
                    m_add_polygon_points.setSelected(true);
                    m_off_mesh_points.setSelected(true);
                    break;
                case CConfig.EDIT_POLYGON_SUB : 
                    m_edit_polygon.setSelected(true);
                    m_sub_polygon_points.setSelected(true);
                    m_off_mesh_points.setSelected(true);
                    break;
                case CConfig.EDIT_POLYGON_OFF :
                    m_edit_polygon.setSelected(true);
                    m_off_polygon_points.setSelected(true);
                    m_off_mesh_points.setSelected(true);
                    break;
            }
            popup_menu.show(parent, e.getX(), e.getY());
        }
    }
    /** Due to the event API. */
    public void mouseEntered(MouseEvent e){}
    /** Due to the event API. */
    public void mouseExited(MouseEvent e){ }
    /** Due to the event API. */
    public void mousePressed(MouseEvent e){}
    /** Due to the event API. */
    public void mouseReleased(MouseEvent e){}
    /**
     * The user has entered a menu command by clicking the menu item.
     */
    public void actionPerformed(ActionEvent e){
        if(m_morph == e.getSource()){
            File list[] = new File(CStrings.OUTPUTDIR).listFiles();
            for(File f: list)f.delete();
            parent.getParent().morph();
        }
        if(m_edit_mesh == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_MESH_OFF;
        }
        if(m_edit_polygon == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_POLYGON_OFF;
        }
        if(m_edit_config == e.getSource()){
            parent.getParent().showConfigDialog();
        }
        if(m_about == e.getSource()){
            parent.getParent().showAboutDialog();
        }
        
        if(m_add_mesh_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_MESH_ADD;
        }
        if(m_sub_mesh_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_MESH_SUB;
        }
        if(m_off_mesh_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_MESH_OFF;
        }
        if(m_delete_mesh == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_MESH_OFF;
            parent.getParent().initMesh();
        }
        
        if(m_add_polygon_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_POLYGON_ADD;
        }
        if(m_sub_polygon_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_POLYGON_SUB;
        }
        if(m_off_polygon_points == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_POLYGON_OFF;
        }
        if(m_delete_polygon == e.getSource()){
            CConfig.edit_state = CConfig.EDIT_POLYGON_OFF;
            parent.initPolygon();
        }        
        parent.getParent().repaint();
    }
}
