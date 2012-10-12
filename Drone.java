/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapper;

import com.codeminders.ardrone.ARDrone;
import java.net.UnknownHostException;

/**
 *
 * @author gboxall
 */
public class Drone
{
    ARDrone drone;
    GPS gps;
    Magnet magnet;
    Coord coord;
    
    public Drone() throws UnknownHostException, InterruptedException
    {
        this.drone = new ARDrone();
        this.gps = new GPS();
        this.magnet = new Magnet();
        this.coord = new Coord();
    }
    private void setDrone(ARDrone droner)
    {
        this.drone = droner;
    }
    private ARDrone getdrone()
    {
        return this.drone;
    }
    private void setGps(GPS gpss)
    {
        this.gps = gpss;
    }
    private GPS getGps()
    {
        return this.gps;
    }
    private void setMagnet(Magnet mag)
    {
        this.magnet = mag;
    }
    private Magnet getMagnet()
    {
        return this.magnet;
    }
    private void setCoord(Coord crd)
    {
        this.coord = crd;
    }
    private Coord getCoord()
    {
        return this.coord;
    }
}






