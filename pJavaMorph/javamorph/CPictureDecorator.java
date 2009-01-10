package javamorph;

import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @version 1.0
 * <br/>
 * @author claus.erhard.wimmer@googlemail.com
 * <br/>
 * Program: JavaMorph V 1.0.
 * <br/>
 * Class: CPictureDecorator.
 * <br/>
 * License: GPLv2.
 * <br/>
 * Description: Draw one of booth input pictures into the component.
 * <br/>
 * Hint: Scaling is necessary depending on relation between width & height.
 */
public class CPictureDecorator implements IDecorator{
    /** JComponent to draw the picture to. */
    private CFrame parent;
    /** Image from file to draw. */
    private BufferedImage content;
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
     */
    public CPictureDecorator(CFrame parent){
        this.parent = parent;
    }
    /**
     * Draw the picture to the Graphics context of the parent JComponent.
     */
    public void paint(Graphics g) {
        g.drawImage(this.content, x, y, width, height, this.parent);
    }
    /**
     * Notify this decorator of the available drawing space.
     * Bounds are calculated.
     */
    public void setSize(Dimension size) {
        int
        cwidth = this.content.getWidth(),
        cheight = this.content.getHeight();
        double quotient = (double)cwidth / (double)cheight;
        if(size.height * quotient <  size.width){
            height = size.height;
            width = (int)(size.height * quotient);
        }else{
            width = size.width;
            height = (int)(size.width / quotient);
        }
        x = (size.width - width) / 2;   
        y = (size.height - height) / 2;
    }
    /**
     * Load the image date from file.
     * @param f Filename of the file.
     */
    public void load(File f){
        try{
            this.content = ImageIO.read(f);
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog
                (this.parent, "Can't load image. See also console output!");
        }
    }
    /**
     * Get function.
     * @return Image raster data.
     */
    public BufferedImage getContent(){
        return this.content;
    }
    /**
     * Get function.
     * @return Image bounds in screen pixel units.
     */
    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
    /**
     * Due to decorator API.
     */
    public void setActive(boolean active){
    }
}
