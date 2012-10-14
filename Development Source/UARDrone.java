package Intellidrone;

// Import JavaDrone
import com.codeminders.ardrone.*;

// Import Exceptions
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

public class UARDrone implements DroneVideoListener
{
    private ARDrone m_drone = null;
    private BufferedImage m_videoFrameData = null;
    private boolean m_videoFrameUpdateLock;
    
    // Constructor
    public UARDrone() throws UnknownHostException, IOException
    {       
        // Create the drone object
        m_drone = new ARDrone();
        
        // Create the video frame data and set the input format
        m_videoFrameData = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
    }
    
    public boolean Connect(long connectionTimeout)
    {
        try
        {
            // Connect to the drone
            m_drone.connect();
            
            // Wait for a successful connection
            m_drone.waitForReady(connectionTimeout);
            
            // Add a video feed listener
            m_drone.addImageListener(this);

            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        }
    }
    
    public boolean Disconnect()
    {
        try
        {
            // Remove the video feed listener
            m_drone.removeImageListener(this);
            
            // Disconnect from the drone
            m_drone.disconnect();
            
            // Allow the drone to disconnect
            Thread.sleep(1000);

            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        }
        catch (InterruptedException ex)
        {
            return(false);
        }
    }
    
    public boolean TakeOff()
    {
        try
        {
            // Make the drone take off
            m_drone.takeOff();
            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        } 
    }
    
    public boolean Land()
    {
        try
        {
            // Make the drone land
            m_drone.land();
            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        } 
    }
    
    public boolean Trim()
    {
        try
        {
            // Make the drone trim
            m_drone.trim();
            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        } 
    }
        
    public boolean Hover()
    {
        try
        {
            // Make the drone hover
            m_drone.hover();
            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        }
    }
    
    public boolean Move(float leftRightTilt, float frontBackTilt, float verticalSpeed, float angularSpeed)
    {
        try
        {
            // Make the drone move
            m_drone.move(leftRightTilt, frontBackTilt, verticalSpeed, angularSpeed);
            return(true);
        }
        catch (IOException ex)
        {
            return(false);
        }
    }
    
    public boolean SetCamera(int camera)
    {
        try
        {
            switch(camera)
            {
                case 1:
                    m_drone.selectVideoChannel(ARDrone.VideoChannel.HORIZONTAL_ONLY);
                    break;
                case 2:
                    m_drone.selectVideoChannel(ARDrone.VideoChannel.VERTICAL_ONLY);
                    break;
                case 3:
                    m_drone.selectVideoChannel(ARDrone.VideoChannel.HORIZONTAL_IN_VERTICAL);
                    break;
                case 4:
                    m_drone.selectVideoChannel(ARDrone.VideoChannel.VERTICAL_IN_HORIZONTAL);
                    break;
                default:
                    return(false);
            }
        }
        catch (IOException ex)
        {
            return(false);
        }
        
        return(true);
    }

    public BufferedImage GetVideoFrame()
    {
        return(m_videoFrameData);
    }
    
    public synchronized void LockVideoFrameUpdate()
    {
        m_videoFrameUpdateLock = true;
    }
    
    public synchronized void UnlockVideoFrameUpdate()
    {
        m_videoFrameUpdateLock = false;
    }
    
    @Override
    public synchronized void frameReceived(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
    {
        // If video frame updating is not locked
        if (!m_videoFrameUpdateLock)
        {
            // Convert the recieved video frame to a BufferedImage
            m_videoFrameData.setRGB(startX, startY, w, h, rgbArray, offset, scansize);
        }
    }
}