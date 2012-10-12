
package mapper;

import com.codeminders.ardrone.ARDrone;

/**
 *
 * @author gboxall
 */
public class DroneController
{
    private ARDrone drone;
    
    public DroneController(ARDrone drone)
    {
        this.drone = drone;
    }
    void spinLeft()
    {
        //drone.move(left_right_tilt, front_back_tilt, vertical_speed, angular_speed);
    }
}
