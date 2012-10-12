/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapper;

/**
 *
 * @author Grant Boxall
 */
public class ImageRec
{
    private int posx;
    private int posy;
    boolean match;

    public int GetObjectPositionX()
    {
        return posx;
    }
    public int GetObjectPositionY()
    {
        return posy;
    }
    public boolean isMatch()
    {
        return match;
    }    
}
