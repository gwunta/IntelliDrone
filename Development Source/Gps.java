
package Intellidrone;

import java.util.regex.*;
import java.util.*;

/**
 * The GPS class stores lat/long information. 
 * It loops every second querying the telint for new information
 *
 * @author Audey Isaacs
 * @version 0.6
 * @since 13/10/12
 * 
 * Changelog:
 * 0.1 Created class and some methods
 * 0.2 Introduced telint
 * 0.3 Introduced threaded loop method
 * 0.4 Removed set lat/lon methods
 * 0.5 Improved commenting level of detail
 * 0.6 Added coord method
 * 
 */
public class Gps
 implements Runnable
{

	/** telint stores the telnet interface */
	private Telint telint;
	
	/** 
	 * latitude stores the latitude in the form
	 * of decimal degrees, with negative values indicating
	 * southward and positive values indicating northward.
	 */
	private double latitude;
	
	/** 
	 * longitude stores the longitude in the form
	 * of decimal degrees, with negative values indicating
	 * westward and positive values indicating eastward.
	 */
	private double longitude;
	

	/**
	 * This is the a constructor with no parameters
	 * it creates a Telint object
	 *
	 * @throws InterruptedException
	 */
	public Gps() throws InterruptedException
	{
		try {
			telint = new Telint("192.168.1.1", 23);
		} catch(Exception e) {}
		
		(new Thread(this)).start();
	}

	/**
	 * This is a constructor with parameters.
	 * As it is passed a Telint object, it does not need to create one.
	 * This constructor is used in the case that the client program
	 * has already created a Telint object.
	 *
	 * @param telint a Telint telnet interface object
	 */
	public Gps(Telint newtelint)
	{
		telint = newtelint;
	}
	
	/** A method to get the Telint object being used by the class */
	public Telint getTelint() {return telint;}
	
	/** A method to set the Telint object being used by the class */
	public void setTelint(Telint newtelint) 
	{
		//interrupt run
		
		telint = newtelint;
		
		//restart run
	}
	
	/** A method to get the latest coordinate */
	public synchronized Coord getCoord() {return new Coord(latitude, longitude);}
	
	/** A method to get the latest latitude value */
	public synchronized double getLatitude() {return latitude;}
	
	/** A method to get the latest longitude value */
	public synchronized double getLongitude() {return longitude;}
	
	/** A method to set the latest latitude value */	
	public synchronized void setLatitude(double newlat) {latitude = newlat;}
	
	/** A method to set the latest longitude value */
	public synchronized void setLongitude(double newlon) {longitude = newlon;}

	/**
	 * This method collects data from telint.
	 * It runs in it's own thread, and every second
	 * queries the telnet interface for new data and then
	 * sets that data
	 */
	public void run()
	{
	
		// A pattern to match the GPS data coming back over the telnet interface
		Pattern gprmcPatt = Pattern.compile("\\$GPRMC,(.+),A,(\\d\\d)(\\d+\\.\\d+),(\\S),(\\d\\d\\d)(\\d+\\.\\d+),(\\S),.+");
		
		boolean loop = true;
		while(loop) 
		{
		
			// get a some data from the telnet interface
			String line = telint.recv();		
			
			// run the data through the pattern match
			Matcher matcher = gprmcPatt.matcher(line);
		
			// if the matcher found GPS information
			if(matcher.find()) 
			{
		
				// interperet the information and store it into
				//  latitude and longitude
				
				// The non-decimal degrees
				double lat = Float.valueOf(matcher.group(2));
				double lon = Float.valueOf(matcher.group(5));
				
				// The decimal minutes
				double latmins = Float.valueOf(matcher.group(3));
				double lonmins = Float.valueOf(matcher.group(6));
		
				// convert the minutes to decimal degrees and add
				//  them to the decimal latitude and longitude
				lat += (latmins / 60);
				lon += (lonmins / 60);
		
				// if the latitude is southward
				if(matcher.group(4).equals("S")) 
				{
					// make it negative
					lat = -lat;
				}
				
				// if the longitude is westward
				if(matcher.group(7).equals("W"))
				{
					// make it negative
					lon = -lon;
				}
		
				// set the latest latitude and longitude
				//latitude = lat;
				//longitude = lon;
				setLatitude(lat);
				setLongitude(lon);		
			}
			
			try 
			{
				// sleep for a second
				Thread.sleep(1000);
			} 
			// if we were interrupted
			catch (InterruptedException e)
			{
				// quit method
				return;	
			}
		}
	}
}
