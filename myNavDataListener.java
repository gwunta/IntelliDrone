/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapper;

import com.codeminders.ardrone.NavData;
import com.codeminders.ardrone.NavDataListener;

/**
 *
 * @author gboxall
 */
public class myNavDataListener implements NavDataListener {
	
		private float yaw;
	
		public void navDataReceived(NavData nd) {
			yaw = nd.getYaw();
		}
	
		public float getYaw() { return yaw; }
	}