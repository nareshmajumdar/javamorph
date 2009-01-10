package javamorph;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
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
    /** Radio button group a) edit mesh b) edit polygon. */
    private ButtonGroup g_radio_buttons = new ButtonGroup();
    /** Morph command. */
    private JMenuItem m_morph = new JMenuItem("Morph!");
    /** Edit mesh command. */
    private JRadioButtonMenuItem m_edit_mesh = 
        new JRadioButtonMenuItem("Edit mesh.", true);
    /** Edit polygon command. */
    private JRadioButtonMenuItem m_edit_polygon =
        new JRadioButtonMenuItem("Edit polygon.", false);
    /** Edit configuration command. */
    private JMenuItem m_edit_config = new JMenuItem("Edit Config ->");
    /** Delete mesh command. */
    private JMenuItem m_delete_mesh = new JMenuItem("Delete Mesh X");
    /** Delete polygon command. */
    private JMenuItem m_delete_polygon = new JMenuItem("Delete Polygon X");
    /** Show about box command. */
    private JMenuItem m_about = new JMenuItem("Help about?");
    /**
     * Constructor.
     * @param parent The parent JComponent to draw the mouse symbol to.
     */
    public CPopupMenuDecorator(CFrame parent){
        this.parent = parent;
        popup_menu.add(m_morph);
        popup_menu.addSeparator();
        popup_menu.add(m_edit_mesh);
        popup_menu.add(m_edit_polygon);
        popup_menu.addSeparator();
        popup_menu.add(m_edit_config);
        popup_menu.add(m_delete_mesh);
        popup_menu.add(m_delete_polygon);
        popup_menu.addSeparator();
        popup_menu.add(m_about);
        g_radio_buttons.add(m_edit_mesh);
        g_radio_buttons.add(m_edit_polygon);
        m_morph.addActionListener(this);
        m_edit_mesh.addActionListener(this);
        m_edit_polygon.addActionListener(this);
        m_edit_config.addActionListener(this);
        m_delete_mesh.addActionListener(this);
        m_delete_polygon.addActionListener(this);
        m_about.addActionListener(this);
    }
    /**
     * Draw the mouse symbol to the graphics context of the parent JComponent.
     */
    public void paint(Graphics g) {
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
    /** Due to the decorator API. */
    public void setSize(Dimension size){ }
    /** Due to the decorator API. */
    public void setActive(boolean active){}
    /** The user wants to see the pop up menu. */
    public void mouseClicked(MouseEvent e){
        if(MouseEvent.BUTTON3 == e.getButton())
        {
            this.popup_menu.show(parent, e.getX(), e.getY());
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
        if(this.m_morph == e.getSource()){
            File list[] = new File(CStrings.OUTPUTDIR).listFiles();
            for(File f: list)f.delete();
            parent.getParent().save();
        }
        if(this.m_edit_mesh == e.getSource()){
            this.parent.setEditMode(CFrame.EDIT_MESH);
        }
        if(this.m_edit_polygon == e.getSource()){
            this.parent.setEditMode(CFrame.EDIT_POLYGON);
        }
        if(this.m_edit_config == e.getSource()){
            this.parent.getParent().showConfigDialog();
        }
        if(this.m_delete_mesh == e.getSource()){
            parent.deleteMesh();
        }
        if(this.m_delete_polygon == e.getSource()){
            parent.deletePolygon();
        }
        if(this.m_about == e.getSource()){
            parent.getParent().showAboutDialog();
        }
        parent.repaint();
    }
}
