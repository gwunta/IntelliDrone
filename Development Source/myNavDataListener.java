package Intellidrone;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.NavDataListener;
import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.*;

public class myNavDataListener implements NavDataListener 
{

	// Stores the yaw value in degrees
	private float yaw;

	//listener method for NavDataListener, accepts navdata and stores it in yaw
	public void navDataReceived(NavData nd)
        {
		yaw = nd.getYaw();
	}

	//allows for accessing yaw value
	public float getYaw() { return yaw; }
}
