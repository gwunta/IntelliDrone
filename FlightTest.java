/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapper;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.*;
import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavDataListener;
import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.*;



/**
 * The Client class demosntates use of the GPS class
 *
 * @author Audey Isaacs
 * @version 1.0
 *
 */
public class FlightTest implements Runnable
{
	/**
	 * This is the main function. It has a GPS object and loops every
	 * second displaying the latitude and longitude
	 *
	 * @param args The command line arguments(not used)
	 * @throws InterruptedException so I don't have to try/catch sleep
	 */
        ARDrone drone;
        public FlightTest(ARDrone drone)
        {
            this.drone = drone;
        }
	public void run()
	{
        try
        {
            DoTest(drone);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(FlightTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FlightTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        }    
        public void DoTest(ARDrone drone) throws InterruptedException, IOException
        {
                    final long CONNECT_TIMEOUT = 3000; 
        
            GPS gps = null;
            //create a GPS object	
            gps = new GPS();
		//Magnet magnet = new Magnet(gps.getTelint())
            drone.connect();
            drone.clearEmergencySignal();
            drone.waitForReady(CONNECT_TIMEOUT);
            myNavDataListener navdat = new myNavDataListener();
	    drone.addNavDataListener(navdat);
       
            drone.trim();
            System.out.println("Trimmed and waiting for GPS");	
		//wait for gps
		while(gps.getLatitude() == 0)
                {
                    Thread.sleep(1000);
                }
            System.out.println("GPS Acquired, taking off");
        
            Thread.sleep(1000);
            drone.takeOff();
            Thread.sleep(5000);
            Search search = new Search();
            search.orientate(drone,navdat,-32.069724,115.84017,gps);
            search.moveToBearing(-32.069724,115.84017,drone,gps,navdat);
          
            //Thread.sleep(8000);
        
            drone.land();
        
           Thread.sleep(3000);
        //drone.disconnect();	*/
       
            drone.disconnect();
	}
}



