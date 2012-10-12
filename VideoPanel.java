
package mapper;

/**
 *
 * @author gboxall
 */
import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.DroneVideoListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("serial")
public class VideoPanel extends javax.swing.JPanel implements DroneVideoListener {

    private AtomicReference<BufferedImage> image = new AtomicReference<BufferedImage>();
    private AtomicBoolean preserveAspect = new AtomicBoolean(true);
    private BufferedImage noConnection = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);

    /** Creates new form VideoPanel */
    public VideoPanel() 
    {
        Graphics2D g2d = (Graphics2D) noConnection.getGraphics();
        Font f = g2d.getFont().deriveFont(24.0f);
        g2d.setFont(f);
        g2d.drawString("No video connection", 40, 110);
        image.set(noConnection);
    }

    public void setDrone(ARDrone drone) {
        drone.addImageListener(this);
    }

    public void setPreserveAspect(boolean preserve) {
        preserveAspect.set(preserve);
    }

    //@Override
    public void frameReceived(BufferedImage im) {
        image.set(im);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        drawDroneImage(g2d, width, height);
    }

    private void drawDroneImage(Graphics2D g2d, int width, int height) {
        BufferedImage im = image.get();
        if (im == null) {
            return;
        }
        int xPos = 0;
        int yPos = 0;
        if (preserveAspect.get()) {
            g2d.setColor(Color.BLACK);
            g2d.fill3DRect(0, 0, width, height, false);
            float widthUnit = ((float) width / 4.0f);
            float heightAspect = (float) height / widthUnit;
            float heightUnit = ((float) height / 3.0f);
            float widthAspect = (float) width / heightUnit;

            if (widthAspect > 4) {
                xPos = (int) (width - (heightUnit * 4)) / 2;
                width = (int) (heightUnit * 4);
            } else if (heightAspect > 3) {
                yPos = (int) (height - (widthUnit * 3)) / 2;
                height = (int) (widthUnit * 3);
            }
        }
        if (im != null) {
            g2d.drawImage(im, xPos, yPos, width, height, null);
        }
    }

    public void frameReceived(int i, int i1, int i2, int i3, int[] ints, int i4, int i5)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

