
package Intellidrone;

/**
 * The Magnet class changes the magnets state.
 *
 * @author Audey Isaacs
 * @version 0.1
 *
 * Changelog:
 * 0.1 Created class and some methods
 *
 */
public class Magnet
{

	/** telint stores the telnet interface */
	private Telint telint;

	/**
	 * This is the a constructor with no parameters
	 * it creates a Telint object
	 *
	 * @throws InterruptedException
	 */
	public Magnet() throws InterruptedException
	{
		try {
			telint = new Telint("192.168.1.1", 23);
		} catch(Exception e) {}
		
	}

	/**
	 * This is a constructor with parameters.
	 * As it is passed a Telint object, it does not need to create one.
	 * This constructor is used in the case that the client program
	 * has already created a Telint object.
	 *
	 * @param telint a Telint telnet interface object
	 */
	public Magnet(Telint newtelint)
	{
		telint = newtelint;
	}
	
	/**
	 * Tells the magnet to pick up an item
	 *
	 * @throws SensorUsedException if the sensor is already holding something
	 * @throws PickUpFailedException if the sensor could not pick up the item
	 */
	public void pickUp() //throws SensorUsedException, PickUpFailedException
	{
		//try locking telint
		
		//pick up
		telint.send("U");
	}
	
	/**
	 * Tells the magnet to put down an item
	 *
	 * @throws NoPackageException if the sensor is not already holding something
	 * @throws PickUpFailedException if the sensor could not put down the item
	 */
	public void putDown() //throws NoPackageException, PutDownFailedException
	{
		//put down
		telint.send("D");
	}
	
}
