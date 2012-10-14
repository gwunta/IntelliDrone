/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Intellidrone;

import java.awt.image.BufferedImage;

/**
 *
 * @author Chris Courtis
 */
public class Target 
{
    protected Coord coord;
    protected BufferedImage pic;
    
    public Target()
    {
        coord = new Coord();
        pic = null; //fix it later
        
    }
    
    public void setImage(BufferedImage temp)
    {
        pic = temp;
    }
    
    public BufferedImage getImage()
    {
        return pic;
    }
}
