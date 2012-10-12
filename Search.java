//Search.java

package mapper;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavData;
import java.io.IOException;

/**
 * 
 * @author Grant Boxall
 */
public class Search
{
    private double latitude;
    private double longitude;
    private double bearing;
    private NavData data;
    private Coord coords;
    final int ERROR_RATE=5;   //The margin of error allowed when controlling the drone
   
    public Search(double lat, double lng)
    {
        this.latitude = lat;
        this.longitude = lng;
    }
    
    //Determine which way to rotate, then roatate appropriate way until the drone is in the correct front facing orientation
    public Search(){}
    public void orientate(ARDrone drone, myNavDataListener data, double lat, double longi, GPS gps) throws IOException, InterruptedException
    {
        //get current bearing
        double heading;
        System.out.println("Starting rotation");
        double timer = System.currentTimeMillis();
        do
        {
            //If the current position is to the right of where the drone needs to be then spin the drone left, otherwise spin it right 
            //heading = coords.headingTo(new Coord(lat,longi));
            heading = Math.toDegrees(gps.getCoord().headingTo(new Coord(lat,longi)));
            System.out.println("Heading: " + heading);
            System.out.println("Current Yaw: " + data.getYaw());
            if((data.getYaw() - heading) < 10)
            {
                drone.move(0,0,0,(float)-0.5);
            }
            else
            {
                drone.move(0,0,0,(float)0.5);
            }
            //yaw = data.getYaw();
            Thread.sleep(400);
        }while(!(data.getYaw() < (heading + 10) && (data.getYaw() > heading - 10)));
        //this line may not be needed
        //drone.hover();
        System.out.println("Completed rotation");
    }
    //Move towards target, checking and creating track to target
    
    public void navToTarget(double lat, double lng, ARDrone drone, GPS gps, myNavDataListener data) throws IOException, InterruptedException
    {
        Coord coord1 = new Coord();
        coord1.setLat(gps.getLatitude());
        coord1.setLon(gps.getLongitude());
        Coord coord2 = new Coord();
        coord2.setLat(lat);
        coord2.setLon(lng);
        double distance = coord1.distTo(coord2);
        System.out.println("Distance to target: " +distance);
        Coord[] track;
        track = coord1.getTrack(5,distance,coord2);
        //Check if drone is off track left or right, if so, make correction
        if((coord2.getLat() < lat +0.00015) && (coord2.getLat() > lat - 0.00015))
        {
            if(coord2.getLat() < lat +0.00015)
            {
                do
                {
                    //May need magnetometer here to determine which way we are facing
                }while(coord2.getLat() < lat +0.00015); 
            }
        }
        //Get new track
        for (int i = 0; i < track.length; i++)
        {
            System.out.println("Point " + i + ":" + track[i].getLat() + " , " + track[i].getLon());
        }
    }
    //Move forward until the required latitude and longitude are reached
    /********** NOTE: This might need to be modified to include a 'track'.  The drone's progress ***********
    *********** can then be tracked and modifications to its track can then me made ***********************/
    public void moveToBearing(double lat, double lng, ARDrone drone, GPS gps, myNavDataListener data) throws IOException, InterruptedException
    {
        System.out.println("Starting forward motion");
        double timer = System.currentTimeMillis();
        do
        {
            System.out.println("Coordinates: " + gps.getLatitude() + ", " + gps.getLongitude());
            //if(timer - System.currentTimeMillis() > 10000)
            //{
            //    drone.land();
            //    Thread.sleep(5000);
            //}
            drone.move(0,(float)-0.2,0,0);
            Thread.sleep(200);
            //orientate(drone,data,-32.069724,115.84017,gps);
        }while(!((gps.getLatitude() < lat + 0.00015) && (gps.getLatitude() > lat - 0.00015)
         && (gps.getLongitude() < lng + 0.00015) && (gps.getLongitude() > lng - 0.00015)));
        //drone.hover();
        System.out.println("Forward motion completed");
    }
    //TO DO  - compensate for movements caused by wind - ie getCompBearing() every so often and execute orientate()
    //         and move on again
    
    public boolean imgSearch(ARDrone drone, ImageRec image) throws IOException
    {
        //Drop to 500mm
        float altitude;
        do
        {
            drone.move(0,0,-50,0);
            altitude = data.getAltitude();
        }while(data.getAltitude() > 0.5);
        drone.hover();
        //Begin spiral search
        do
        {
            drone.move(50,-50,0,0);
        }while(!image.isMatch());
        //TO DO - Some condition in here that says whether a matching image was found in the spiral sequence.  
        //If we dont put something here, the search will continue forever.  Options might be time - if we know how 
        //long a spiral sequence search takes then we can limit on that, or GPS coords - if the drone creeeps into new
        //coords then it certainly wont find the object.
        drone.hover();
        return true;
    }
    //Centralise the drone on the object
    public boolean centralise(ARDrone drone, ImageRec image) throws IOException
    {
        int dposx = image.GetObjectPositionX();
        int dposy = image.GetObjectPositionY();
        int square1 = 0;
        int square2 = 0;
        int pickup = 0;
        
        //Do this until the x and y coordinates of the image are central and the drone is at most 1cm from the item
        while((square1 != 1) && (square2 != 1) && (pickup != 1))
        {
            //If the drone is within the specified margin of error on the x axis
            if (dposx <= ERROR_RATE)
            {
                square1 = 1;
            }
            else
            {
                while(dposx <= ERROR_RATE)
                {
                    //If the drone is right of the item then move left
                    if(dposx + ERROR_RATE < 0)
                    {
                        drone.move(-10,0,0,0);
                    }
                    //If the drone is left of the item then move right
                    if(dposx > ERROR_RATE)
                    {
                        drone.move(10,0,0,0);    
                    }
                }
            }
            //If the drone is within the specified margin of error on the y axis
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
                        drone.move(0,0,0,10);
                    }
                    //If the drone is rearward of the item then move forward
                    if(dposy > ERROR_RATE)
                    {
                        drone.move(0,0,0,-10);    
                    }
                }              
            }
         //Lowering to pickup an object placed in here, because as the drone gets lower, the turbulence will probably throw out the drone out of 'square'
        //so we need to constantly need to adjust to ensure the drone is square           
            if (data.getAltitude() <= 0.01)
            {
                pickup = 1;
            }
            else
            {
                while(data.getAltitude() > 0.5)
                {
                        drone.move(0,-10,0,0);
                }              
            }
        }
        //drone.hover() may not be needed
        drone.hover();
        return true;
    }
    //Lowering method to put the drone withn 1cm of the item.  This was created in case it needed to be separated from the above method
    public boolean pickup(ARDrone drone) throws Exception
    {
        while(data.getAltitude() > 0.01)
        {
            drone.move(0,-10,0,0);
        }
        return true;
    }
    
    //Determines the compass bearing to the provided lat and long from the current position
    public double getCompBearing(double lat1, double lon1, double lat2, double lon2)
    { 
        //difference in longitudinal coordinates
         double dLon = Math.toRadians(lon2) - Math.toRadians(lon1);

         //difference in the phi of latitudinal coordinates
         double dPhi = Math.log(Math.tan(Math.toRadians(lat2) / 2 + Math.PI / 4) / Math.tan(Math.toRadians(lat1) / 2 + Math.PI));

         //we need to recalculate dLon if it is greater than pi
         if(Math.abs(dLon) > Math.PI) {
              if(dLon > 0) {
                   dLon = (2 * Math.PI - dLon) * -1;
              }
              else {
                   dLon = 2 * Math.PI + dLon;
              }
         }
         //return the angle, normalized
         return (Math.toDegrees(Math.atan2(dLon, dPhi)) + 360) % 360;
    }
}
