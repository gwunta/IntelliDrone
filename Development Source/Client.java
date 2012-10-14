
package Intellidrone;

import java.util.regex.*;
import java.util.*;

/**
 * The Client class demonstrates use of the GPS class
 *
 * @author Audey Isaacs
 * @version 1.0
 *
 */
public class Client 
{

	/**
	 * This is the main function. It has a GPS object and loops every
	 * second displaying the latitude and longitude
	 *
	 * @param args The command line arguments(not used)
	 * @throws InterruptedException so I don't have to try/catch sleep
	 */
	public static void main(String[] args) throws InterruptedException 
	{
	
		//create a GPS object
		Gps gps = new Gps();
		
		Magnet magnet = new Magnet(gps.getTelint());
		
		//wait 1 second
		Thread.sleep(1000);
		
		
		//loop and print lat/lon
		boolean loop = true;
		while(loop)
		{
			magnet.pickUp();
			System.out.println(gps.getLatitude() + ", " + gps.getLongitude());
			Thread.sleep(1000);
			
			magnet.putDown();
			System.out.println(gps.getLatitude() + ", " + gps.getLongitude());
			Thread.sleep(1000);
		}
	}
}
