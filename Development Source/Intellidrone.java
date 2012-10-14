package Intellidrone;

import Intellidrone.Coord;
import Intellidrone.Magnet;
import Intellidrone.Gps;
import Intellidrone.ImageRecognition;
import com.codeminders.ardrone.ARDrone; 
import com.codeminders.ardrone.NavData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the primary class that is visible to the user of the API. 
 * It is the single point of import (import Org.Intellidrone.*).
 * All functionality in the Intellidrone project can be accessed through this class. (Eg intellidrone_Object.subclass.function())
 * Instantiate the Intellidrone class by creating on object of it.
 * @author Chris Courtis
 * @author Grant Boxall
 * @author Audey Isaacs
 * @author Michael Staunton
 * @version 0.1 First compiling version
 * @version 0.2 Mission Command implemented
 * @version 0.3 New Structure implemented
 * @since 13/10/12
 */
public class Intellidrone 
{
    //protected Magnet magObject;
    //protected Gps gpsObject;
    protected ImageRecognition irObject;
    // protected ARDrone drone; // The drone control object
    //protected NavData data; // The drone nav data object
    protected Coord home;
    protected TaskList mission;
    protected DroneControl droneControl;
    
    /*
     * Default constructor. Sets up everything the user will need to run the drone.
     * @throws UnknownHostException if it can't contact the drone.
     * @throws InterruptedException if the telnet session fails
     * @throws IOException if the drone is connected, but cannot send or recieve data
     * 
     */
    public Intellidrone() throws UnknownHostException, InterruptedException, IOException
    {
       
        
        /* Abandoncode
        magObject = new Magnet(telnet);
        gpsObject = new Gps(telnet);


        drone = new ARDrone();
        data = new NavData();
        */
        
        droneControl = new DroneControl();
        
        home = new Coord();
        mission = new TaskList();
        //targets = new ArrayList();
        //currentTarget = 0;
        //some sort of variable to hold the mission file
        //some sort of variable to hold the log
    }
    
    /**
     * The purpose of this method is to process tasks from the TaskList class and execute them.
     */
    public void missionCommand()
    {
        int size = mission.getSize();
        int i = 0;
        
        while(i<size)
        {
            if(mission.getTask(i).getAction().equals("goto"))
            {
                goTo(mission.getTask(i).getLat(), mission.getTask(i).getLong());
            } 
            else
            {
          
                if(mission.getTask(i).getAction().equals("pickup"))
                {
                    goTo(mission.getTask(i).getLat(), mission.getTask(i).getLong());
                   
                        //imgSearch(drone, mission.getTask(i).getFilename());
                        //TODO: Make this work
              
                    pickup();
                }
                else 
                {    

                    if(mission.getTask(i).getAction().equals("dropoff"))
                    {
                        goTo(mission.getTask(i).getLat(), mission.getTask(i).getLong());
                        dropoff();
                    }
                    else
                    {

                        if(mission.getTask(i).getAction().equals("return"))
                        {
                            goTo(home.getLat(), home.getLon());
                        }
                        else
                        {
                            System.err.println("Invalid action.");
                        }
                            
                    }
                }
            }
            i++;
        }
        
    }
    
    /**
     * The purpose of this method is to get the drone move to a set of GPS coordinates.
     * 
     * @param lat latitude of target location
     * @param lon longitude of target location
     * @return true if the Drone has reached its destination.
     * @bug If too many IOExceptions occur, this could cause this method to loop infinitely.
     */
    public boolean goTo(double lat, double lon)
    {
        Coord target = new Coord(lat, lon); // where its going
        Coord start = droneControl.getCoord();
        Coord current = start; // where it is currently
        double currentDistance = current.distTo(target);
        int speed = 0; // as a percentage
        double currentHeading = droneControl.getHeading(); // in degrees, may change to radians
        double targetHeading = 0;
        
        while(currentDistance>1) // While the target is more than 1 metre away
        {
            current = (droneControl.getCoord());
            
            if(currentDistance > 10)
            {
                speed = 100;
            }
            else
            {
                if (currentDistance > 5)
                {
                    speed = 50;
                }
                else
                {
                    if (currentDistance > 2)
                    {
                        speed = 25;
                    }
                    else
                    {
                        speed = 10;
                    }     
                }
            }
            
            if(Math.abs(currentHeading - targetHeading) > 15)
            {
                try
                {
                    orientate(current, target, 5);
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try
            {
                droneControl.move(0,speed,0,0);
            }
            catch (IOException ex)
            {
                Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            currentDistance = current.distTo(target);
            
            if(currentDistance < 1)
            {
                try
                {
                    droneControl.hover();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }
    
    /**
     * Determine which way to rotate, then rotate until the drone is oriented in that direction.
     * 
     * @param current The drones current coordinates
     * @param target The target coordinates
     * @param drone The drone object
     * @param margin The margin for error
     * @throws IOException if the control station loses IO with the Drone
     */
    public boolean orientate(Coord current, Coord target, int margin) throws IOException
    {
        // get current bearing
        double yaw = droneControl.getYaw();
        double heading;
        
        do
        {
            //If the current position is to the right of where the drone needs to be then spin the drone left, otherwise spin it right 
            heading = current.headingTo(target);
            if((yaw - heading) < 0)
            {
                droneControl.move(0, 0, 0, -50);
            }
            else
            {
                droneControl.move(0, 0, 0, 50);
            }
            
            yaw = droneControl.getYaw();
            
        } while(Math.abs(yaw-heading) > margin); // While more then margin degrees from being the correct bearing
        
        //this line may not be needed
        droneControl.hover();
        return true;
    }
    
    /**
     * Method to find a target on the ground based on a target image
     * 
     * @param drone The drone object
     * @param filename The path to the image
     * @return true if it finds the target object, false if it fails
     * @throws IOException (TODO: Document this!)
     */
    public boolean imgSearch(int hueMin, int hueMax, int satMin, int satMax, int valMin, int valMax) throws IOException, InvalidImageException, NumberOutOfRangeException
    {
        irObject = new ImageRecognition();
        long startTime = System.currentTimeMillis();
        

        final int limit = 45000; // 45 second timer
     
        // Set outer circle color values
        irObject.SetHueRange(hueMin, hueMax);
        irObject.SetSaturationRange(satMin, satMax);
        irObject.SetValueRange(valMin, valMax);

        // Set inner circle color values TODO
        irObject.SetInnerHueRange(80, 100);
        irObject.SetInnerSaturationRange(0, 255);
        irObject.SetInnerValueRange(0, 255);

        // Set other values
        irObject.SetSmoothType(ImageRecognition.BLUR_GAUSSIAN);
        irObject.SetMorphCloseEnabled(true);
        irObject.SetMorphIntensity(14);
        irObject.SetSmoothIntensity(11);
        irObject.SetInnerSmoothIntensity(5);
        irObject.SetCircleDiameterRange(1, 3);
 
        
        do
        {
            droneControl.move(0, 0, -50, 0);
            //altitude = data.getAltitude();
        } while(droneControl.getAltitude() > 0.5); // Drop to less than 500mm
        
        droneControl.hover();
        
        //Begin spiral search
        do
        {
            if(System.currentTimeMillis() > startTime + limit) // if the time expires
            {
                return false;
            }
            
            
            droneControl.move(50, -50, 0, 0);
            droneControl.LockVideoFrameUpdate();
            irObject.SetImage(droneControl.GetVideoFrame());
            droneControl.UnlockVideoFrameUpdate();
            
        } while(!irObject.Detect());

        droneControl.hover();
        
        // TODO Call function that centres drone on the image
        Centralise(irObject, 5);
        
        return true;
    }
    
    /**
     * Centralise takes the output from an Image Recognition Object and uses it to centralise the drone over the object
     * @param image Image Recognition Object
     * @param ERROR_RATE How close the drone can be to the object to be deemed to be on it
     * @return True if centralisation was successful
     * @throws IOException if communication with the drone is lost
     */
    
    public boolean Centralise(ImageRecognition image, final int ERROR_RATE) throws IOException
    {
        int dposx = image.GetObjectPositionX();
        int dposy = image.GetObjectPositionY();
        int square1 = 0;
        int square2 = 0;
        int pickup = 0;
        
        // Do this until the x and y coordinates of the image are central and the drone is at most 1cm from the item
        while((square1 != 1) && (square2 != 1) && (pickup != 1))
        {
            // If the drone is within the specified margin of error on the x axis
            if (dposx <= ERROR_RATE)
            {
                square1 = 1;
            }
            else
            {
                while(dposx <= ERROR_RATE)
                {
                    // If the drone is right of the item then move left
                    if(dposx + ERROR_RATE < 0)
                    {
                        droneControl.move(-10,0,0,0);
                    }
                    
                    // If the drone is left of the item then move right
                    if(dposx > ERROR_RATE)
                    {
                        droneControl.move(10,0,0,0);    
                    }
                }
            }
            
            // If the drone is within the specified margin of error on the y axis
            if (dposy <= ERROR_RATE)
            {
                square2 = 1;
            }
            else
            {
                while(dposy <= ERROR_RATE)
                {
                    //If the drone is foward of the item then move backward
                    if(dposy + ERROR_RATE < 0)
                    {
                        droneControl.move(0, 0, 0, 10);
                    }
                    //If the drone is rearward of the item then move forward
                    if(dposy > ERROR_RATE)
                    {
                        droneControl.move(0, 0, 0, -10);    
                    }
                }              
            }
            
            // Lowering to pickup an object placed in here, because as the drone gets lower, the turbulence will probably throw out the drone out of 'square'
            // so we need to constantly need to adjust to ensure the drone is square           
            if (droneControl.getAltitude() <= 0.01)
            {
                pickup = 1;
            }
            else
            {
                while(droneControl.getAltitude() > 0.5)
                {
                        droneControl.move(0, -10, 0, 0);
                }              
            }
        }
        
        //drone.hover() may not be needed
        droneControl.hover();
        
        return(true);
    }    
    
    /**
     * Takes the drone to the specified altitude
     * @return true once the altitude is reached
     * @throws IOException if it loses IO with the drone
     */
    public boolean pickupAltitude() throws IOException
    {
        while(droneControl.getAltitude() > 0.01)
        {
            droneControl.move(0, -10, 0, 0);
        }
        
        return true;
    }
    
    /**
     * Determines the compass bearing to the provided lat and long from the current position
     * Does not take the curvature of the Earth into consideration
     * 
     * @param lat1 TODO DOCUMENT
     * @param lon1 TODO DOCUMENT
     * @param lat2 TODO DOCUMENT
     * @param lon2 TODO DOCUMENT
     * @return TODO DOCUMENT
     */
    public double getSimpleBearing(double lat1, double lon1, double lat2, double lon2)
    { 
         // difference in longitudinal coordinates
         double dLon = Math.toRadians(lon2) - Math.toRadians(lon1);

         // difference in the phi of latitudinal coordinates
         double dPhi = Math.log(Math.tan(Math.toRadians(lat2) / 2 + Math.PI / 4) / Math.tan(Math.toRadians(lat1) / 2 + Math.PI));

         // we need to recalculate dLon if it is greater than pi
         if(Math.abs(dLon) > Math.PI)
         {
              if(dLon > 0)
              {
                   dLon = (2 * Math.PI - dLon) * -1;
              }
              else
              {
                   dLon = 2 * Math.PI + dLon;
              }
         }
         
         // return the angle, normalized
         return ((Math.toDegrees(Math.atan2(dLon, dPhi)) + 360) % 360);
    }
    
    public void pickup()
    {
        droneControl.pickUp();
    }
    
    public void dropoff()
    {
        droneControl.putDown();
    }
    
    /**
     * 
     * @return 
     */
   
    public DroneControl getDroneControl()
    {
        return droneControl;
    }
    
    /**
     * Test program. DO NOT SHIP THIS CODE!
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, InterruptedException 
    {
        try
        {
            // TODO code application logic here
            // test main, real code loop is abstract
            Intellidrone test = new Intellidrone();
            DroneControl temp;
            
           
            
            System.out.println("Connecting");
            //test.drone.clearEmergencySignal();

            System.out.println("Ready");
            

            
            test.getDroneControl().takeOff();
            System.out.println("Should be in the air");

            test.getDroneControl().hover();
            System.out.println("Hovering");
            test.getDroneControl().move(0, 25, 0, 0);
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException ex)
            {
                System.out.println("Interruped exception");
                Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Should be moving foward.");
            test.getDroneControl().move(0, 0, 0, 0);
            System.out.println("Should have stopped moving");
            test.getDroneControl().land();
            
            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Land");
            test.getDroneControl().disconnect();
            System.out.println("Disconnect");
        }
        catch (IOException ex)
        {
            Logger.getLogger(Intellidrone.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Shit went wrong.");
        }
    }
}
