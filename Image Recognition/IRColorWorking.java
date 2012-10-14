import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.CanvasFrame;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class attempts to find a given sensor package in a given image and
 * calculates the pixel distance away from the image source on both the
 * X (width) and Y (height) planes.
 * 
 * @author Michael Staunton
 * @version 0.3
 */
public class IRColorWorking
{
    // Constants
    public static final int BLUR_NO_SCALE = 1;
    public static final int BLUR = 2;
    public static final int BLUR_GAUSSIAN = 3;
    public static final int BLUR_MEDIAN = 4;
    public static final int BLUR_BILATERAL = 5;

    // Scene image, sensor package information
    private IplImage sceneImage;
    private SensorPackage sensorPkg;
    
    // Smoothing settings
    private int smoothType, innerSmoothType;
    private int smoothIntensity, innerSmoothIntensity;
    
    // Morphing settings
    private boolean morphEnabled;
    private int morphIntensity;
    
    private int posX, posY;
    private double circleDiameterLow, circleDiameterHigh;
    private Map<String, Object> m_windowNameMap;
    private boolean debugEnabled;
    
    public IRColorWorking()
    {
        // Create an empty window map
        m_windowNameMap = new HashMap<String, Object>();
        
        // Create a default sensor package
        sensorPkg = new SensorPackage();
        
        // Disable debugging output
        debugEnabled = false;
        
        // Setup smoothing methods and intensities
        smoothType = BLUR_GAUSSIAN;
        innerSmoothType = BLUR_GAUSSIAN;
        smoothIntensity = 11;
        innerSmoothIntensity = 5;
        
        // Enable morphological closing and set intensity
        morphEnabled = true;
        morphIntensity = 11;
        
        // Allow circle diameter ratio difference of up to 255x
        circleDiameterLow = 0;
        circleDiameterHigh = 255;
        
        posX = 0;
        posY = 0;
    }
    
    /**
     * Writes the given image to the given file location. The image output format
     * depends on the extension given to the file location (e.g. .jpg will produce a jpg).
     * Note that only 8-bit single channel or 3-channel BGR images can be saved using this method.
     * 
     * @param filePath The full file path for the desired writing location.
     * @param image The image to be written to the specified file.
     * @throws NullPointerException If a null filePath or image is passed as a parameter.
     * @throws InvalidImageException If the image passed failed to convert from BufferedImage.
     */
    public static void WriteImageToFile(String filePath, BufferedImage image) throws NullPointerException, InvalidImageException
    {
        // If the given image is null
        if (image == null)
        {
            throw new NullPointerException("Cannot pass a null image!");
        }
        // Else if the file path is null
        else if (filePath == null)
        {
            throw new NullPointerException("Cannot pass a null file path!");
        }
        
        // Load the image from the BufferedImage
        IplImage imageOut = IplImage.createFrom(image);
        
        // If failure to load image
        if (imageOut == null)
        {
            throw new InvalidImageException("Failed to convert given image from BufferedImage to IplImage!");
        }

        // TODO: Check return value here (its int) could fail...
        // Write the image to file
        cvSaveImage(filePath, imageOut);
    }
    
    /**
     * Returns the currently set image as a IplImage object.
     * 
     * @return The currently set image.
     */
    public IplImage GetImage()
    {
        return(sceneImage);
    }
    
    /**
     * Returns the currently set image as a BufferedImage object.
     * 
     * @return The currently set image.
     */
    public BufferedImage GetBufferedImage()
    {
        if (sceneImage == null)
        {
            return(null);
        }
        
        // Otherwise convert the image to BufferedImage and return it
        return(sceneImage.getBufferedImage());
    }
    
    /**
     * Sets the image to be analyzed for a sensor package.
     * 
     * @param image The image to analyze.
     * @throws NullPointerException If a null image is passed as a parameter.
     */
    public void SetImage(IplImage image) throws NullPointerException
    {
        if (image == null)
        {
            throw new NullPointerException("Cannot pass a null image!");
        }
        
        sceneImage = image;
    }
    
    /**
     * Sets the image to be analyzed for a sensor package.
     * 
     * @param image The image to analyze.
     * @throws NullPointerException If a null image is passed as a parameter.
     * @throws InvalidImageException If the image passed failed to convert from BufferedImage.
     */
    public boolean SetImage(BufferedImage image) throws NullPointerException, InvalidImageException
    {
        // If the given image is null
        if (image == null)
        {
            throw new NullPointerException("Cannot pass a null image!");
        }
        
        // If the current image is not null then release the memory allocation
        if (sceneImage != null)
        {
            sceneImage.release();
        }
        
        // Load the image from the BufferedImage
        sceneImage = IplImage.createFrom(image);
        
        // If failure to load image
        if (sceneImage == null)
        {
            throw new InvalidImageException("Failed to convert given image from BufferedImage to IplImage!");
        }
        
        return(true);
    }
    
    /**
     * Sets the image to be analyzed for a sensor package from an image file.
     * 
     * @param filePath The file path of the image to load in.
     * @throws NullPointerException If a null file path is passed as a parameter.
     * @throws IOException If the image failed to load from the given file path.
     */
    public void SetImageFromFile(String filePath) throws NullPointerException, IOException
    {
        // If the image file path is null
        if (filePath == null)
        {
            throw new NullPointerException("Cannot pass a null file path!");
        }
            
        // Attempt to open the image file
        sceneImage = cvLoadImage(filePath);
        
        // If opening the image failed
        if (sceneImage == null)
        {
            throw new IOException("Failed to load image from given file path!");
        }
    }

    /**
     * Displays the given image in a window frame with the given frame title.
     * Note that subsequent calls to display an image in a window with the same
     * name will update the window with the new image.
     * 
     * @param frameTitle The title of the window frame.
     * @param image The image to display in the window frame.
     * @throws NullPointerException If a null image is passed as a parameter.
     */
    public void DisplayImage(String frameTitle, IplImage image) throws NullPointerException
    {
        if (image == null)
        {
            throw new NullPointerException("Cannot pass a null image!");
        }
        
        // Check if a frame already exists with this name
        CanvasFrame canvas = (CanvasFrame) m_windowNameMap.get(frameTitle);
        
        // If a frame exists with this name then update it
        if (canvas != null)
        {
            // Update the image being displayed
            canvas.showImage(image.getBufferedImage()); // TODO change later (this is inefficient way around alpha correction)
        }
        // Else create a new frame
        else
        {
            // Display the image in a new window
            canvas = new CanvasFrame(frameTitle);
            canvas.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            canvas.showImage(image);
            
            // Register the frame title with the canvas object
            m_windowNameMap.put(frameTitle, canvas);
        }
    }
    
    /**
     * Sets the sensor package details to look for in the given image.
     * 
     * @param sensorPackage The sensor package information to search for.
     * @throws NullPointerException If a null sensor package is passed as a parameter.
     */
    public void setSensorPackage(SensorPackage sensorPackage) throws NullPointerException
    {
        if (sensorPackage == null)
        {
            throw new NullPointerException("Cannot pass a null sensor package!");
        }
        
        sensorPkg = sensorPackage;
    }
    
    /**
     * Returns the currently set sensor package information.
     * 
     * @return The currently set sensor package information.
     */
    public SensorPackage getSensorPackage()
    {
        return(sensorPkg);
    }
    
    /**
     * Enables debugging output (image displays and console output).
     * This method is useful if something is not working correctly.
     */
    public void EnableDebugOutput()
    {
        debugEnabled = true;
    }
    
    /**
     * Disables debugging output (image displays and console output).
     * This method completely disables all debugging output.
     */
    public void DisableDebugOutput()
    {
        debugEnabled = false;
    }
    
    /**
     * Sets the algorithm used for outer circle smoothing/blur.
     * 
     * @param smoothingType The smoothing algorithm to use (see class constants for possible values).
     * @return False if an invalid smoothing algorithm is specified otherwise returns true.
     */
    public boolean SetSmoothType(int smoothingType)
    {
        if (smoothingType < 1 || smoothingType > 5)
        {
            return(false);
        }
        
        smoothType = smoothingType;
        return(true);
    }
    
    /**
     * Sets the algorithm used for inner circle smoothing/blur.
     * 
     * @param smoothingType The smoothing algorithm to use (see class constants for possible values).
     * @return False if an invalid smoothing algorithm is specified otherwise returns true.
     */
    public boolean SetInnerSmoothType(int smoothingType)
    {
        if (smoothingType < 1 || smoothingType > 5)
        {
            return(false);
        }
        
        innerSmoothType = smoothingType;
        return(true);
    }
    
    /**
     * Sets the smoothing intensity for outer circle detection.
     * 
     * @param smoothingIntensity The intensity of smoothing (higher values will result in more intense smoothing).
     * @throws NumberOutOfRangeException If smoothing intensity is less than 0.
     */
    public void SetSmoothIntensity(int smoothingIntensity) throws NumberOutOfRangeException
    {
        if (smoothingIntensity < 0)
        {
            throw new NumberOutOfRangeException("Smooth intensity must be greater than 0!");
        }
        
        smoothIntensity = smoothingIntensity;
    }
    
    /**
     * Sets the smoothing intensity for inner circle detection.
     * 
     * @param smoothingIntensity The intensity of smoothing (higher values will result in more intense smoothing).
     * @throws NumberOutOfRangeException If smoothing intensity is less than 0.
     */
    public void SetInnerSmoothIntensity(int smoothingIntensity) throws NumberOutOfRangeException
    {
        if (smoothingIntensity < 0)
        {
            throw new NumberOutOfRangeException("Smooth intensity must be greater than 0!");
        }
        
        innerSmoothIntensity = smoothingIntensity;
    }
    
    /**
     * Enables or disables morphological closing for outer circle detection.
     * Note that closing should never be disabled unless a non-standard package
     * identification system is in use.
     * 
     * @param morphologyClosingEnabled True to enable closing otherwise false.
     */
    public void SetMorphCloseEnabled(boolean morphologyClosingEnabled)
    {
        morphEnabled = morphologyClosingEnabled;
    }
    
    /**
     * Sets the intensity for morphological closing.
     * 
     * @param intensity The intensity of morphological closing (higher values will result in more intense morphing).
     */
    public void SetMorphIntensity(int intensity)
    {
        morphIntensity = intensity;
    }
    
    /**
     * Sets the allowable range for circle diameter ratio difference
     * between the outer and inner circles.
     * 
     * @param diameterLow The lowest value of the diameter ratio range.
     * @param diameterHigh The highest value of the diameter ratio range.
     * @throws NumberOutOfRangeException If either diameter ratio is below 0.01 or if the lowest diameter ratio is
     * higher than the highest diameter ratio.
     */
    public void SetCircleDiameterRange(double diameterLow, double diameterHigh) throws NumberOutOfRangeException
    {
        if (diameterHigh < diameterLow)
        {
            throw new NumberOutOfRangeException("Maximum diameter ratio must be greater than the minimum diameter ratio!");
        }
        else if (diameterLow < 0.01 || diameterHigh < 0.01)
        {
            throw new NumberOutOfRangeException("Diameter ratio must be greater than 0.01!");
        }
        
        circleDiameterLow = diameterLow;
        circleDiameterHigh = diameterHigh;
    }
    
    /**
     * Returns the relative X (width) pixel distance of the last successful detection via the Detect() method.
     * A positive value means that the object is to the right while a negative value means it is to the left.
     * A value of zero indicates that the object is on the same width coordinate.
     * 
     * @return The relative X (width) pixel distance of the last successful detection via the Detect() method.
     */
    public int GetObjectPositionX()
    {
        return(posX);
    }
    
    /**
     * Returns the relative Y (height) pixel distance of the last successful detection via the Detect() method.
     * A positive value means that the object is down while a negative value means it is up.
     * A value of zero indicates that the object is on the same height coordinate.
     * 
     * @return The relative Y (height) pixel distance of the last successful detection via the Detect() method.
     */
    public int GetObjectPositionY()
    {
        return(posY);
    }
    
    /**
     * Attempts to detect the given sensor package (using the given details) in the given image.
     * Sensor package position values can be retrieved by using the GetObjectPositionX() and 
     * GetObjectPositionY() methods after a successful detection. This method looks for outer circles
     * matching the description for the sensor package and then looks within all found outer circles
     * for a single inner circle that also matches the sensor package description. Assuming that
     * the diameter ratio is within an acceptable range and that the outer and inner circle's center points
     * do not differ too much then a successful match is returned and the object's position is calculated and stored.
     * 
     * @return True if detection is successful otherwise returns false.
     * @throws NullPointerException If a null image or sensor package are set. 
     */
    public boolean Detect() throws NullPointerException
    {
        // If the scene image is null
        if (sceneImage == null)
        {
            throw new NullPointerException("Image cannot be null!");
        }
        // Else if the sensor package is null
        else if (sensorPkg == null)
        {
            throw new NullPointerException("Sensor package cannot be null!");
        }
        
        // Allocate memory for the HSV image and the threshed image
        IplImage imageHSV = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 3);
        IplImage imageThreshed = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 1);
        IplImage imageOuterCircle = null;

        // Convert the scene image to the HSV colour space
        cvCvtColor(sceneImage, imageHSV, CV_BGR2HSV);
        
        // Produce a binary image of the sensor package outer circle color
        cvInRangeS(imageHSV, cvScalar(sensorPkg.getHueMinimum(), sensorPkg.getSaturationMinimum(), sensorPkg.getValueMinimum(), 0),
                cvScalar(sensorPkg.getHueMaximum(), sensorPkg.getSaturationMaximum(), sensorPkg.getValueMaximum(), 0), imageThreshed);
        
        // Blur the binary image to filter noise and to round circles
        cvSmooth(imageThreshed, imageThreshed, smoothType, smoothIntensity);
        
        // If morphological closing is enabled
        if (morphEnabled)
        {
            // Apply morphological closing to close any potential holes in detections
            cvMorphologyEx(imageThreshed, imageThreshed, null, null, CV_MOP_CLOSE, morphIntensity);
        }
        
        if (debugEnabled)
        {
            // Display the threshed, blurred and morphed scene image
            this.DisplayImage("Threshed/Blurred/Morphed Scene Image (1)", imageThreshed);
            
            // Display the results of the below canny edge detection
            IplImage imageCanny = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 1);
            cvCanny(imageThreshed, imageCanny, 200, 100, 3);
            this.DisplayImage("Canny Edge Detection Outer Circle(2)", imageCanny);
        }
        
        // Perform circle detection for outer circles using hough gradient transform
        CvMemStorage storage = cvCreateMemStorage(0);
        CvSeq outerSeq = cvHoughCircles(imageThreshed, storage, CV_HOUGH_GRADIENT, 4, 25, 200, 100, 0, 0);
        
        // If no outer circles were detected
        if (outerSeq.total() == 0)
        {
            if (debugEnabled)
            {
                System.err.println("DEBUG: Failed to detect any outer circles!");
            }
            
            // Nothing was detected
            return(false);
        }
        
        if (debugEnabled)
        {
            System.out.println("DEBUG: Detected " + outerSeq.total() + " outer circles!");
        }
        
        // For each outer circle detected
        for(int i = 0; i < outerSeq.total(); i++)
        {               
            // Calculate the center coordinates, radius and diameter of the detected outer circle
            CvPoint3D32f OuterCircle = new CvPoint3D32f(cvGetSeqElem(outerSeq, i));
            CvPoint outerCenter = cvPointFrom32f(new CvPoint2D32f(OuterCircle.x(), OuterCircle.y()));
            int outerRadius = Math.round(OuterCircle.z());
            int outerDiameter = outerRadius * 2;

            if (debugEnabled)
            {
                // Display the detected outer circle
                imageOuterCircle = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 3);
                cvCopy(sceneImage, imageOuterCircle);
                cvCircle(imageOuterCircle, outerCenter, outerRadius, CvScalar.RED, 2, CV_AA, 0);
                cvRectangle(imageOuterCircle, outerCenter, outerCenter, CvScalar.RED, 2, 8, 0);
                this.DisplayImage("Outer Circle Detected (" + i + ") (3)", imageOuterCircle);
                
                // Display the outer circle's center pixel coordinates
                System.out.println("DEBUG: Outer Circle Center (" + i + "): X = " + outerCenter.x() + ", Y = " + outerCenter.y() + "\n");
            }

            // Set a region of interest around the outer circle (made slightly larger to avoid imperfect circles)
            IplImage imageROI = IplImage.create(outerDiameter + 6, outerDiameter + 6, IPL_DEPTH_8U, 3);
            IplImage imageROIThreshed = IplImage.create(outerDiameter + 6, outerDiameter + 6, IPL_DEPTH_8U, 1);
            cvSetImageROI(imageHSV, cvRect((outerCenter.x() - outerRadius) - 3, (outerCenter.y() - outerRadius) - 3, outerDiameter + 6, outerDiameter + 6));

            // TODO: An error occurs here sometimes due to ROI size
            if (imageHSV.roi().width() != imageROI.width())
            {
                System.out.println("Width Bugged Out!");
                System.out.println("HSV Width: " + imageHSV);
                System.out.println("ROI Width: " + imageROI.width());
                return(false);
            }
            else if (imageHSV.roi().height() != imageROI.height())
            {
                System.out.println("Height Bugged Out!");
                System.out.println("HSV Height: " + imageHSV.height());
                System.out.println("ROI Height: " + imageROI.height());
                return(false);
            }
            else
            {
                // Copy the region of interest to another image
                cvCopy(imageHSV.roi(imageHSV.roi()), imageROI);
            }
            
            // Release the region of interest
            cvResetImageROI(imageHSV);

            // Produce a binary image of the sensor package inner circle color
            cvInRangeS(imageROI, cvScalar(sensorPkg.getInnerHueMinimum(), sensorPkg.getInnerSaturationMinimum(), sensorPkg.getInnerValueMinimum(), 0),
                    cvScalar(sensorPkg.getInnerHueMaximum(), sensorPkg.getInnerSaturationMaximum(), sensorPkg.getInnerValueMaximum(), 0), imageROIThreshed);
            
            // Blur the binary image to filter noise and to round circles
            cvSmooth(imageROIThreshed, imageROIThreshed, innerSmoothType, innerSmoothIntensity);
            
            if (debugEnabled)
            {
                // Display the threshed and blurred region of interest
                this.DisplayImage("Threshed/Blurred Region Of Interest (4)", imageROIThreshed);
            }
            
            // Perform circle detection the inner circle using hough gradient transform
            CvSeq innerSeq = cvHoughCircles(imageROIThreshed, storage, CV_HOUGH_GRADIENT, 3, 100, 24, 12, 0, 0);
            
            // If no inner circles were detected
            if (innerSeq.total() == 0)
            {
                if (debugEnabled)
                {
                    System.err.println("DEBUG: Failed to detect any inner circle!");
                }

                // Nothing was detected in this outer circle
                continue;
            }
            // Else if more than one inner circle was detected
            else if (innerSeq.total() > 1)
            {
                if (debugEnabled)
                {
                    System.err.println("DEBUG: Detected " + innerSeq.total() + " inner circles!");
                }
                
                // Nothing was detected in this outer circle
                continue;
            }
            
            // Calculate the center coordinates, radius and diameter of the detected inner circle
            CvPoint3D32f innerCircle = new CvPoint3D32f(cvGetSeqElem(innerSeq, i));
            CvPoint innerCenter = cvPointFrom32f(new CvPoint2D32f(((outerCenter.x() - outerRadius) - 3) + innerCircle.x(),
                        ((outerCenter.y() - outerRadius) - 3) + innerCircle.y()));
            int innerRadius = Math.round(innerCircle.z());
            double innerDiameter = innerRadius * 2;
            
            if (debugEnabled)
            {
                // Display the inner circle's center pixel coordinates
                System.out.println("DEBUG: Detected the inner circle!");
                System.out.println("DEBUG: Inner Circle Center: X: " + innerCenter.x() + ", Y: " + innerCenter.y() + "\n");
                
                // Display the detected inner circle
                IplImage imageInnerCircle = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 3);
                cvCopy(sceneImage, imageInnerCircle);
                cvCircle(imageInnerCircle, innerCenter, innerRadius, CvScalar.GREEN, 2, CV_AA, 0);
                cvRectangle(imageInnerCircle, innerCenter, innerCenter, CvScalar.GREEN, 2, 8, 0);
                this.DisplayImage("Detected Inner Circle (5)", imageInnerCircle);
                
                // Display the results of the above canny edge detection
                IplImage imageCanny = IplImage.create(imageROIThreshed.width(), imageROIThreshed.height(), IPL_DEPTH_8U, 1);
                cvCanny(imageROIThreshed, imageCanny, 20, 15, 3);
                this.DisplayImage("Canny Edge Detection Inner Circle (6)", imageCanny);
                
                // Display both the outer and inner circle centers on one image
                IplImage imageBothCircle = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 3);
                cvCopy(imageOuterCircle, imageBothCircle);
                cvCircle(imageBothCircle, innerCenter, innerRadius, CvScalar.GREEN, 2, CV_AA, 0);
                cvRectangle(imageBothCircle, innerCenter, innerCenter, CvScalar.GREEN, 2, 8, 0);
                this.DisplayImage("Both Circles (7)", imageBothCircle);
            }
            
            // If the inner circle is located correctly inside the outer circle (both circle centers are within 5 pixels difference)
            if (innerCenter.x() > (outerCenter.x() - 8) && innerCenter.x() < (outerCenter.x() + 8) && innerCenter.y() > (outerCenter.y() - 8) && innerCenter.y() < (outerCenter.y() + 8))
            {
                if (debugEnabled)
                {
                    System.out.println("DEBUG: The inner circle is correctly positioned inside the outer circle!");
                }
            }
            // Else the inner circle is incorrectly positioned inside the outer circle
            else
            {
                if (debugEnabled)
                {
                    System.err.println("DEBUG: The inner circle is not correctly positioned inside the outer circle!");
                }
                
                // Nothing was detected in this outer circle
                continue;
            }
            
            // Check that the inner and outer circle diameter ratio is roughly the expected value
            double diameterRatio = outerDiameter / innerDiameter;
            if (diameterRatio < circleDiameterLow || diameterRatio > circleDiameterHigh)
            {
                if (debugEnabled)
                {
                    System.err.println("DEBUG: The circle diameter ratio (" + diameterRatio + ") is out of range!");
                }
                
                // Nothing was detected in this outer circle
                continue;
            }
            
            if (debugEnabled)
            {
                System.out.println("DEBUG: The circle diameter ratio is in range (" + diameterRatio + ")!");
            }
            
            // Calculate and set the objects relative position
            posX = innerCenter.x() - (sceneImage.width() / 2);
            posY = innerCenter.y() - (sceneImage.height() / 2);
            
            // Return successful
            return(true);
        }
        
        // Return unsuccessful
        return(false);
    }
    
    // Driver program
    public static void main(String args[]) throws InvalidImageException
    {
        IRColorWorking imgRecog = new IRColorWorking();
        
        try
        {
            // Load an images from file
            imgRecog.SetImageFromFile("/home/michael/img/image-415.jpg");
            
            // Setup the sensor package to detect
            SensorPackage mySensorPkg = new SensorPackage(140, 170, 0, 255, 0, 255, 80, 100, 0, 255, 0, 255);
            imgRecog.setSensorPackage(mySensorPkg);
            
            // Set other values
            imgRecog.SetSmoothType(IRColor.BLUR_GAUSSIAN);
            imgRecog.SetMorphCloseEnabled(true);
            imgRecog.SetMorphIntensity(14);
            imgRecog.SetSmoothIntensity(11);
            imgRecog.SetInnerSmoothIntensity(7);
            imgRecog.SetCircleDiameterRange(1.1, 3);
            imgRecog.EnableDebugOutput();
        }
        catch (IOException ex)
        {
            System.out.println("Failed to load image from file!");
            return;
        }
        catch (Exception ex)
        {
            System.out.println("Failed to set circle color values!");
        }
        
        // If the object is detected
        if (imgRecog.Detect())
        {
            // If the drone is close to the sensor package on the X scale
            if (Math.abs(imgRecog.GetObjectPositionX()) < 5)
            {
                System.out.print("Don't Move + ");
            }
            // Else if moving right
            else if (imgRecog.GetObjectPositionX() > 0)
            {
                System.out.print("Move Right (" + Math.abs(imgRecog.GetObjectPositionX()) + ") + ");
            }
            // Else moving left
            else
            {
                System.out.print("Move Left (" + Math.abs(imgRecog.GetObjectPositionX()) + ") + ");
            }

            // If the drone is close to the sensor package on the Y scale
            if (Math.abs(imgRecog.GetObjectPositionY()) < 5)
            {
                System.out.print("Don't Move");
            }
            // Else if moving down
            else if (imgRecog.GetObjectPositionY() > 0)
            {
                System.out.print("Down (" + Math.abs(imgRecog.GetObjectPositionY()) + ") ...\n");
            }
            // Else moving up
            else
            {
                System.out.print("Up (" + Math.abs(imgRecog.GetObjectPositionY()) + ") ...\n");
            }
        }
        // Else no object was detected
        else
        {
            System.out.println("Failed to detect object in frame!");
        }
    }
}