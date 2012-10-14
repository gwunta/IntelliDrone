
package Intellidrone;

/**
 * Coord class<br>
 * Filename: Coord.java<br>
 * Date: Thu 20 Sep 2012<br>
 * Purpose: The Coord class stores a latitude and a longitude, 
 * representing a point on the earth. The Coord class also supplies
 * methods to get the distance from one Coord to another and the
 * instantaneous heading from one Coord to another.<br>
 *
 * @author Audey Isaacs
 * @version 1.0
 */
public class Coord
{
	private double latitude;	/** stores latitude in decimal degrees */
	private double longitude;	/** stores longitude in decimal degrees*/
   
   	/** Default Constructor */
	public Coord()
	{
		latitude = 0;
		longitude = 0;	   
	}
   
   	/** Constructor with parameters
   	 * @param newlat the new latitude
   	 * @param newlon the new longitude
   	 */
	public Coord(double newlat, double newlon)
	{
		latitude = newlat;
		longitude = newlon;
	}
   
   	/** Get method for latitude */
	public double getLat()
	{
		return latitude;
	}
   
   	/** Get method for longitude */
	public double getLon()
	{
		return longitude;
	}
        
        /** Set method for coordinates */
        public void setCoord(double newlat, double newlon)
        {
                latitude = newlat;
		longitude = newlon;
        }
        
   	/** Set method for latitude */
	public void setLat(double newlat)
	{
		latitude = newlat;
	}
   
   	/** Set method for longitude */
	public void setLon(double newlon)
	{
		longitude = newlon;
	}
   
   	/** Method to return the distance from
   	 * one Coord to another Coord
   	 * @retval distance in meters
   	 */
	public double distTo(Coord that)
	{
		int earthrad = 6367500; //avg radius of earth in meters(wolfram alpha)
		
		double dlat = Math.toRadians(this.getLat()) - Math.toRadians(that.getLat());
		double dlon = Math.toRadians(this.getLon()) - Math.toRadians(that.getLon());
		
		double a = Math.sin(dlat/2) * Math.sin(dlat/2) + Math.cos(Math.toRadians(this.getLat())) * 
		           Math.cos(Math.toRadians(that.getLat())) * Math.sin(dlon/2) * Math.sin(dlon/2);
		
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		double dist = earthrad * c;
		
		return dist;
	}
	
	/** Method to return the distance from
   	 * one Coord to another Coord
   	 * @retval heading in radians from north
   	 */
	public double headingTo(Coord that)
	{
		// Math based on http://www.movable-type.co.uk/scripts/latlong.html
		double dx = that.getLat() - this.getLat();
		double dy = Math.cos(this.getLat()) * (that.getLon() - this.getLon());
		double angle = Math.atan2(dy, dx);
		
		/*uncomment this if you only want positive angles
		if(that.getLon() < this.getLon())
		{
			angle += Math.toRadians(360);
		}*/
		
		return angle;
	}        
        
        //Creates a series of GPS points x metres from each other track from point A to point B
        public Coord[] getTrack(int gap, double distance, Coord that)
        {
            //determine number of points required
            int points = (int)distance/gap;
            System.out.println(points);
            Coord[] track = new Coord[points];
            //Prime the Coord array
            for(int i=1; i<points; i++)
            {
                    track[i] = new Coord();
            }  
            //Determine the difference in Lat and Long required along the track
            double reslat = Math.abs(this.getLat() - that.getLat()) / points;
            double reslng = Math.abs(this.getLon() - that.getLon()) / points;
            //Determine whether we need to add or subtract reslat and reslng to determine the track 
            System.out.println("Size of array = " + track.length);
            if (this.getLat() > that.getLat())
            {
                //Set the first point on the track
                track[0] = new Coord();
                track[0].setLat(this.getLat() - reslat);
                for(int i=1; i<points; i++)
                {
                    //Set subsequent points on the track
                    track[i].setLat(track[i-1].getLat() - reslat);
                }
            }
            else
                {
                    track[0] = new Coord();
                    track[0].setLat(this.getLat() + reslat);
                    for(int i=1; i<points; i++)
                    {
                        track[i].setLat(track[i-1].getLat() + reslat);
                    }
                }
            if (this.getLon() > that.getLon())
            {
                track[0].setLon(this.getLon() - reslng);
                for(int i=1; i<points; i++)
                {
                    track[i].setLon(track[i-1].getLon() - reslng);
                }
            }
            else
                {
                    track[0].setLon(this.getLon() + reslng);
                    for(int i=1; i<points; i++)
                    {
                        track[i].setLon(track[i-1].getLon() + reslng);
                    }
                }
            /*//Set the first point on the track
            track[0] = new Coord();
            track[0].setLat(this.getLat() + reslat);
            track[0].setLon(this.getLon() + reslng);
            //Set subsequent points on the track
            for(int i=1; i<points; i++)
            {
                track[i] = new Coord();
                track[i].setLat(track[i-1].getLat() + reslat);
                track[i].setLon(track[i-1].getLon() + reslng);
            }
            //Return a list of Coord objects containing the Lat and Long of the track */
            return track;
        }
}		
