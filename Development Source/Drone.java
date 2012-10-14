package Intellidrone;

import java.io.IOException;

public interface Drone
{	
	/**
	 * Move should move the drone
	 * @param roll the left-right tilt. -1.0 being full left? and +1.0 being full right
	 * @param pitch the front-back tilt. -1.0 being full ? and +1.0 being full ?
	 * @param thrust the lift force. ?
	 * @param yaw the rotational translation. -1.0 being full anti-clockwise, +1.0 being full clockwise
	 */
	public void move(double roll, double pitch, double thrust, double yaw) throws IOException;
	
	/**
	 * Method should give the drone an automated takeoff command.
	 */
	public void takeOff();
	
	/**
	 * Method should give the drone an automated takeoff command.
	 */
	public void land();
	
	/**
	 * Method should return the latest coordinates of the drone.
	 */
	public Coord getCoord();
	
	
	/**
	 * Method should return the compass bearing of the drone, in degrees from north.
	 */
	public double getHeading();
	
	/**
	 * Method should tell the drone to pick up a package.
	 */
	public void pickUp();
	
	/**
	 * Method should tell the drone to put down a package.
	 */
	public void putDown();
        

}
