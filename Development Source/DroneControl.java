package Intellidrone;

import java.util.regex.*;
import java.util.*;
import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavDataListener;
import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * 
 * @version 0.2
 * @since 2012-10-13
 */
public class DroneControl implements Drone, NavDataListener, DroneVideoListener
{

        private Magnet magnet;
	private Gps gps;
	private ARDrone drone;


    
	private List<DroneVideoListener>        image_listeners   = new LinkedList<DroneVideoListener>();

	/**
	 * The constructor should do everything needed
	 * to initialize the drone and make it ready to takeoff
	 */
	public DroneControl() throws InterruptedException, IOException, UnknownHostException
	{
		//define the timeout value
		final long CONNECT_TIMEOUT = 3000;
	
                Telint telnet = new Telint("192.168.1.1", 23);
                 
		//create a GPS object
		gps = new Gps(telnet);
	
		//create a magnet object
		magnet = new Magnet(telnet);
	
		//create an ardrone object
		drone = new ARDrone();
                
                        
                // Create the video frame data and set the input format
                m_videoFrameData = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
	
		//connect to the drone and initialise
		try
		{
			drone.connect();
			drone.clearEmergencySignal();
			drone.waitForReady(CONNECT_TIMEOUT);
		}
		catch(IOException e) 
		{
			System.out.println("Error connecting: " + e.toString());			
		}
		drone.trim();
	
		//add a navdata listener
		//navdat = new myNavDataListener();
		//drone.addNavDataListener(navdat);
			
			
	}
        
        /**
         * Makes the drone hover
         */
        public void hover() throws IOException
        {
            drone.hover();
        }
	
	/**
	 * Move should move the drone
	 * @param roll the left-right tilt. -1.0 being full left? and +1.0 being full right
	 * @param pitch the front-back tilt. -1.0 being full ? and +1.0 being full ?
	 * @param thrust the lift force. ?
	 * @param yaw the rotational translation. -1.0 being full anti-clockwise, +1.0 being full clockwise
	 */
	public void move(double roll, double pitch, double thrust, double yaw) throws IOException
	{
		
		drone.move((float) roll, (float) pitch, (float) thrust, (float) yaw);
		
	}
	
	/**
	 * Method should give the drone an automated takeoff command.
	 */
	public void takeOff()
	{
		try
		{
			drone.takeOff();
		}
		catch(IOException e) {}
		
		//wait for the drone to takeoff
		try
		{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
	}
	
	/**
	 * Method should give the drone an automated takeoff command.
	 */
	public void land()
	{
		try
		{
			drone.land();
		}
		catch(IOException e) {}
		
		//wait for the drone to land
		try
		{
			Thread.sleep(2000);
		}
		catch (Exception e) {}
	}
	
	/**
	 * Method should return the latest coordinates of the drone.
	 */
	public Coord getCoord()
	{
		return gps.getCoord();
	}
	
	
	/**
	 * Method should return the compass bearing of the drone, in degrees from north.
	 */
	public double getHeading()
	{
		//note, we will change this if/when the magnetometer arrives
		return yaw;
	}
	
	/**
	 * Method should tell the drone to pick up a package.
	 */
	public void pickUp()
	{
		magnet.pickUp();
	}
	
	/**
	 * Method should tell the drone to put down a package.
	 */
	public void putDown()
	{
		magnet.putDown();
	}
	
	public void addImageListener(DroneVideoListener l)
	{
		synchronized(image_listeners)
		{
			image_listeners.add(l);
		}
	}
	
         /**
         * Method to disconnect from the drone
         */
        public void disconnect() throws IOException
        {
            drone.disconnect();
        }
        
        
        
        //---------------------------- Nav Data Listener code
        
        // Stores the yaw value in degrees
	private float yaw;
        private float altitude;

	//listener method for NavDataListener, accepts navdata and stores it in yaw
	public void navDataReceived(NavData nd) 
        {
		yaw = nd.getYaw();
                altitude = nd.getAltitude();
	}

	//allows for accessing yaw value
	public float getYaw() { return yaw; }
        
        public float getAltitude()
        {
            return altitude;
        }

        

        
        
        //VideoListener Code
        
        
            private BufferedImage m_videoFrameData = null;
            private boolean m_videoFrameUpdateLock;
            public BufferedImage GetVideoFrame()
            {
                return(m_videoFrameData);
            }

            public synchronized void LockVideoFrameUpdate()
            {
                m_videoFrameUpdateLock = true;
            }

            public synchronized void UnlockVideoFrameUpdate()
            {
                m_videoFrameUpdateLock = false;
            }

            public synchronized void frameReceived(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
            {
                // If video frame updating is not locked
                if (!m_videoFrameUpdateLock)
                {
                    // Convert the recieved video frame to a BufferedImage
                    m_videoFrameData.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
                }
            }
}


