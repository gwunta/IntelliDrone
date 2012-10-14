/**
 * Example implementation of a built-in sensor package class.
 *
 * @author michael
 * @version 0.1
 */
public class MySensorPackage extends SensorPackage
{
    /**
     * Example call to super class optional constructor
     * 
     * @throws NumberOutOfRangeException
     */
    MySensorPackage() throws NumberOutOfRangeException
    {
        // Call the super class with the desired built-in sensor package values
        super(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }
}
