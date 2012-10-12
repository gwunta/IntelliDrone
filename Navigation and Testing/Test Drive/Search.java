/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testdrive;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavData;
import java.io.IOException;

/**
 *
 * @author gboxall
 */
public class Search
{
    private double latitude;
    private double longitude;
    private double bearing;
    private NavData data;
    private Coord coords;
    final int ERROR_RATE=5;   //The margin of error allowed when controlling the drone
    public Search(){}
    public void orientate(double yaw, ARDrone drone, NavData data, double lat, double longi) throws IOException
    {
        //get current bearing
        double heading;
        do
        {
            //If the current position is to the right of where the drone needs to be then spin the drone left, otherwise spin it right 
            heading = coords.headingTo(new Coord(lat,longi));
            if((yaw - heading) < 0)
            {
                drone.move(0,0,0,-50);
            }
            else
            {
                drone.move(0,0,0,50);
            }
            //yaw = data.getYaw();
        }while((yaw < heading + 10) && (yaw > heading - 10));
        //this line may not be needed
        drone.hover();
    }
    //Move forward until the required latitude and longitude are reached
    /********** NOTE: This might need to be modified to include a 'track'.  The drone's progress ***********
    *********** can then be tracked and modifications to its track can then me made ***********************/
    public void moveToBearing(double lat, double lng, ARDrone drone) throws IOException
    {
        //double timer = System.currentTimeMillis();
        do
        {
            drone.move(0,-50,0,0);
        }while((lat < lat + 0.00002) && (lat > lat - 0.00002));// && (lng != longitude));
        drone.hover();
        
        do
        {
            drone.move(0,-50,0,0);
        }while((lng < lng + 0.00002) && (lng > lng - 0.00002));// && (lng != longitude));
        drone.hover();
    }
}
