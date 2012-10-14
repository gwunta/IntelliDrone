/*
 * Gauges.class
 * Version 0.2
 */
package intellidrone;

import Intellidrone.myNavDataListener;
import com.codeminders.ardrone.NavData;

/**
 *
 * @author gboxall
 */
public class Gauges
{
    double retval;

    NavData nd;
    public Gauges() 
    {
        //this.nd = new NavData();
        //this.navdat = new myNavDataListener();
    }
        
        public int getProgress(myNavDataListener data,NavData nd)
        {
            Gauges guage = new Gauges();
            data.navDataReceived(nd);
            return nd.getBattery();
        }
        public double getYaw(myNavDataListener data,NavData nd)
        {
            return data.getYaw();
        }
        public double getPitch(myNavDataListener data,NavData nd)
        {
            data.navDataReceived(nd);
            return (nd.getPitch());
        }
        public double getRoll(myNavDataListener data,NavData nd)
        {
            data.navDataReceived(nd);
            return (nd.getRoll());
        }
        public double getAltitude(myNavDataListener data,NavData nd)
        {
            data.navDataReceived(nd);
            return (nd.getAltitude());
        }
}