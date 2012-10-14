package Intellidrone;

import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageRecognition
{
    // Constants
    public static final int BLUR_NO_SCALE = 1;
    public static final int BLUR = 2;
    public static final int BLUR_GAUSSIAN = 3;
    public static final int BLUR_MEDIAN = 4;
    public static final int BLUR_BILATERAL = 5;
    
    // TODO Debugging remove later
    public static final boolean DEBUG_OUT_ENABLED = false;
    
    private IplImage sceneImage;
    private Map<String, Object> m_windowNameMap;
    
    private int hueMin, hueMax;
    private int saturationMin, saturationMax;
    private int valueMin, valueMax;
    private int innerHueMin, innerHueMax;
    private int innerSaturationMin, innerSaturationMax;
    private int innerValueMin, innerValueMax;
    
    private int smoothType, innerSmoothType;
    private int posX, posY;
    private boolean morphEnabled;
    private int smoothIntensity, innerSmoothIntensity;
    
    private double circleDiameterLow, circleDiameterHigh;
    private int morphIntensity;
    
    public ImageRecognition()
    {
        // Create an empty window map
        m_windowNameMap = new HashMap<String, Object>();
        
        // Setup default values
        hueMin = 0;
        hueMax = 0;
        saturationMin = 0;
        saturationMax = 0;
        valueMin = 0;
        valueMax = 0;
        innerHueMin = 0;
        innerHueMax = 0;
        innerSaturationMin = 0;
        innerSaturationMax = 0;
        innerValueMin = 0;
        innerValueMax = 0;
        smoothType = BLUR_GAUSSIAN;
        posX = 0;
        posY = 0;
        morphEnabled = true;
        smoothIntensity = 11;
        circleDiameterLow = 0;
        circleDiameterHigh = 255;
        morphIntensity = 11;
    }
    
    public static boolean WriteImageToFile(String filePath, BufferedImage image)
    {
        if (image == null)
        {
            // TODO call exception here
            return(false);
        }
        
        // Load the image
        IplImage imageOut = IplImage.createFrom(image);
        
        if (imageOut == null)
        {
            return(false);
        }
        
        // Write the image to file
        // TODO: Only works for 8bit single channel and 3 channel BGR! Use cvCvtScale and cvCvtColor!
        // TODO: Check return value here (it's int) could fail...
        cvSaveImage(filePath, imageOut);
        
        return(true);
    }
    
    public IplImage GetImage()
    {
        return(sceneImage);
    }
    
    public BufferedImage GetBufferedImage()
    {
        // If the image is null
        if (sceneImage == null)
        {
            return(null);
        }
        
        // Otherwise convert the image to BufferedImage and return it
        return(sceneImage.getBufferedImage());
    }
    
    public void SetImage(IplImage image)
    {
        sceneImage = image;
    }
    
    public boolean SetImage(BufferedImage image)
    {
        if (sceneImage != null)
        {
            sceneImage.release();
        }
        
        sceneImage = IplImage.createFrom(image);
        
        if (sceneImage == null)
        {
            return(false);
        }
        
        return(true);
    }

    public boolean SetImageFromFile(String filePath)
    {
        // If the image file path is null
        if (filePath == null)
        {
            // Return unsuccessful
            return(false);
        }
            
        // Attempt to open the image file
        sceneImage = cvLoadImage(filePath);
        
        // If opening the image failed
        if (sceneImage == null)
        {
            // Return unsuccessful
            return(false);
        }
        
        // Return successful
        return(true);
    }

    public void DisplayImage(String frameTitle, IplImage image) throws InvalidImageException
    {
        if (image == null)
        {
            throw new InvalidImageException("Cannot display invalid image!");
        }
        
        // Check if a frame already exists with this name
        CanvasFrame canvas = (CanvasFrame) m_windowNameMap.get(frameTitle);
        
        // If a frame exists with this name then update it
        if (canvas != null)
        {
            // Update the image being displayed
            canvas.showImage(image.getBufferedImage()); // TODO change later
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
    
    public void SetHueRange(int hueMinimum, int hueMaximum) throws NumberOutOfRangeException
    {
        if (hueMaximum < hueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum hue must be greater than the minimum hue!");
        }
        else if (hueMinimum < 0 || hueMinimum > 179)
        {
            throw new NumberOutOfRangeException("Minimum hue must be between 0 and 179!");
        }
        else if (hueMaximum < 0 || hueMaximum > 179)
        {
            throw new NumberOutOfRangeException("Maximum hue must be between 0 and 179!");
        }
        
        hueMin = hueMinimum;
        hueMax = hueMaximum;
    }
    
    public void SetSaturationRange(int saturationMinimum, int saturationMaximum) throws NumberOutOfRangeException
    {
        if (saturationMaximum < saturationMinimum)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be greater than the minimum saturation!");
        }
        else if (saturationMinimum < 0 || saturationMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum saturation must be between 0 and 255!");
        }
        else if (saturationMaximum < 0 || saturationMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be between 0 and 255!");
        }
        
        saturationMin = saturationMinimum;
        saturationMax = saturationMaximum;
    }
    
    public void SetValueRange(int valueMinimum, int valueMaximum) throws NumberOutOfRangeException
    {
        if (valueMaximum < valueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum value must be greater than the minimum value!");
        }
        else if (valueMinimum < 0 || valueMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum value must be between 0 and 255!");
        }
        else if (valueMaximum < 0 || valueMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum value must be between 0 and 255!");
        }
        
        valueMin = valueMinimum;
        valueMax = valueMaximum;
    }
    
    public void SetInnerHueRange(int hueMinimum, int hueMaximum) throws NumberOutOfRangeException
    {
        if (hueMaximum < hueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum hue must be greater than the minimum hue!");
        }
        else if (hueMinimum < 0 || hueMinimum > 179)
        {
            throw new NumberOutOfRangeException("Minimum hue must be between 0 and 179!");
        }
        else if (hueMaximum < 0 || hueMaximum > 179)
        {
            throw new NumberOutOfRangeException("Maximum hue must be between 0 and 179!");
        }
        
        innerHueMin = hueMinimum;
        innerHueMax = hueMaximum;
    }
    
    public void SetInnerSaturationRange(int saturationMinimum, int saturationMaximum) throws NumberOutOfRangeException
    {
        if (saturationMaximum < saturationMinimum)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be greater than the minimum saturation!");
        }
        else if (saturationMinimum < 0 || saturationMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum saturation must be between 0 and 255!");
        }
        else if (saturationMaximum < 0 || saturationMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be between 0 and 255!");
        }
        
        innerSaturationMin = saturationMinimum;
        innerSaturationMax = saturationMaximum;
    }
    
    public void SetInnerValueRange(int valueMinimum, int valueMaximum) throws NumberOutOfRangeException
    {
        if (valueMaximum < valueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum value must be greater than the minimum value!");
        }
        else if (valueMinimum < 0 || valueMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum value must be between 0 and 255!");
        }
        else if (valueMaximum < 0 || valueMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum value must be between 0 and 255!");
        }
        
        innerValueMin = valueMinimum;
        innerValueMax = valueMaximum;
    }
    
    public boolean SetSmoothType(int smoothingType)
    {
        if (smoothingType < 1 || smoothingType > 5)
        {
            return(false);
        }
        
        smoothType = smoothingType;
        return(true);
    }
    
    public boolean SetInnerSmoothType(int smoothingType)
    {
        if (smoothingType < 1 || smoothingType > 5)
        {
            return(false);
        }
        
        innerSmoothType = smoothingType;
        return(true);
    }
    
    public void SetSmoothIntensity(int smoothingIntensity) throws NumberOutOfRangeException
    {
        if (smoothingIntensity < 0)
        {
            throw new NumberOutOfRangeException("Smooth intensity must be greater than 0!");
        }
        
        smoothIntensity = smoothingIntensity;
    }
    
    public void SetInnerSmoothIntensity(int smoothingIntensity) throws NumberOutOfRangeException
    {
        if (smoothingIntensity < 0)
        {
            throw new NumberOutOfRangeException("Smooth intensity must be greater than 0!");
        }
        
        innerSmoothIntensity = smoothingIntensity;
    }
    
    public void SetMorphCloseEnabled(boolean morphologyClosingEnabled)
    {
        morphEnabled = morphologyClosingEnabled;
    }
    
    public void SetMorphIntensity(int intensity)
    {
        morphIntensity = intensity;
    }
    
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
    
    public int GetObjectPositionX()
    {
        return(posX);
    }
    
    public int GetObjectPositionY()
    {
        return(posY);
    }
    
    // TODO remove this throws later on
    public boolean Detect() throws InvalidImageException
    {
        if (sceneImage == null)
        {
            System.out.println("NULL Image!");
        }
        
        IplImage imageHSV = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 3);
        IplImage imageThreshed = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 1);

        // Convert the image to HSV colour space
        cvCvtColor(sceneImage, imageHSV, CV_BGR2HSV);
        
        // Produce a binary image based on the given HSV values
        cvInRangeS(imageHSV, cvScalar(hueMin, saturationMin, valueMin, 0), cvScalar(hueMax, saturationMax, valueMax, 0), imageThreshed);
        
        // Blur the thresheld image to avoid false positives
        cvSmooth(imageThreshed, imageThreshed, smoothType, smoothIntensity);
        
        // If morphological closing is enabled
        if (morphEnabled)
        {
            // Apply morphological closing
            cvMorphologyEx(imageThreshed, imageThreshed, null, null, CV_MOP_CLOSE, morphIntensity);
        }
        
        // TODO: Debugging remove this later
        // IplImage imageCanny = IplImage.create(sceneImage.width(), sceneImage.height(), IPL_DEPTH_8U, 1);
        // cvCanny(imageThreshed, imageCanny, 200, 100, 3);
        // this.DisplayImage("Edge Detection", imageCanny);
        
        // Display the input image
        if (DEBUG_OUT_ENABLED)
        {
            this.DisplayImage("Threshed/Blurred/Morphed Original", imageThreshed);
        }
        
        // Detect circles using hough gradient transform
        CvMemStorage storage = cvCreateMemStorage(0);
        CvSeq seq = cvHoughCircles(imageThreshed, storage, CV_HOUGH_GRADIENT, 5, 25, 200, 100, 0, 0);
        
        // If no circles were detected
        if (seq.total() == 0)
        {
            if (DEBUG_OUT_ENABLED)
            {
                System.out.println("Failed to detect any outer circles!");
            }
            
            // No object's were detected
            return(false);
        }
        
        // For each detected circle
        for(int i = 0; i < seq.total(); i++)
        {    
            // TODO Debugging
            if (DEBUG_OUT_ENABLED)
            {
                System.out.println("Detected an outer circle!");
            }
            
            // Calculate the center coordinates, radius and diameter of the circle
            CvPoint3D32f OuterCircle = new CvPoint3D32f(cvGetSeqElem(seq, i));
            CvPoint outerCenter = cvPointFrom32f(new CvPoint2D32f(OuterCircle.x(), OuterCircle.y()));
            int outerRadius = Math.round(OuterCircle.z());
            int outerDiameter = outerRadius * 2;
            
            // TODO Debugging: Show the detected outer circle
            IplImage imageTemp1 = sceneImage;
            cvCircle(imageTemp1, outerCenter, outerRadius, CvScalar.CYAN, 6, CV_AA, 0);
            cvRectangle(imageTemp1, outerCenter, outerCenter, CvScalar.CYAN, 2, 8, 0);
            
            if (DEBUG_OUT_ENABLED)
            {
                this.DisplayImage("Outer Circle Detected", imageTemp1);
                System.out.println("Outer Circle Center: X: " + outerCenter.x() + ", Y: " + outerCenter.y() + "\n");
            }

            // Set a region of interest around the circle (make it slightly larger to avoid imperfect circles)
            IplImage imageROI = IplImage.create(outerDiameter + 6, outerDiameter + 6, IPL_DEPTH_8U, 3);
            IplImage imageROIThreshed = IplImage.create(outerDiameter + 6, outerDiameter + 6, IPL_DEPTH_8U, 1);
            cvSetImageROI(imageHSV, cvRect((outerCenter.x() - outerRadius) - 3, (outerCenter.y() - outerRadius) - 3, outerDiameter + 6, outerDiameter + 6));

            // TODO: An error occurs here fix it sometime... It's an exception of size
            if (imageHSV.roi().width() != imageROI.width())
            {
                System.out.println("Width Fucked Up!");
                System.out.println("HSV Width: " + imageHSV);
                System.out.println("ROI Width: " + imageROI.width());
                return(false);
            }
            else if (imageHSV.roi().height() != imageROI.height())
            {
                System.out.println("Height Fucked Up!");
                System.out.println("HSV Height: " + imageHSV.height());
                System.out.println("ROI Height: " + imageROI.height());
                return(false);
            }
            else
            {
                cvCopy(imageHSV.roi(imageHSV.roi()), imageROI);
            }
            
            // Release the region of interest
            cvResetImageROI(imageHSV);
            
            /* -------------------- Analyze Inner Circle -------------------- */
            
            // Produce a binary image of inner circle color
            cvInRangeS(imageROI, cvScalar(innerHueMin, innerSaturationMin, innerValueMin, 0), cvScalar(innerHueMax, innerSaturationMax, innerValueMax, 0), imageROIThreshed);
            
            // Blur the thresheld image to avoid false positives
            cvSmooth(imageROIThreshed, imageROIThreshed, smoothType, innerSmoothIntensity);
            
            // TODO Debugging: Show the blurred/threshed image
            if (DEBUG_OUT_ENABLED)
            {
                this.DisplayImage("Threshed/Blurred ROI", imageROIThreshed);
            }
            
            // Detect inner circles using hough gradient transform
            // CvSeq innerSeq = cvHoughCircles(imageROIThreshed, storage, CV_HOUGH_GRADIENT, 5, 25, 20, 15, 0, 0);
            CvSeq innerSeq = cvHoughCircles(
                    imageROIThreshed,
                    storage,
                    CV_HOUGH_GRADIENT,
                    2,      // Ratio of accumulator resolution (1 = full image resolution, 2 = half image resolution etc)
                    25,     // Minimum distance between detected circles
                    24,     // Canny lower accumulator threshold. Lower values will detect more circles (easier) (double of lower). 
                    12,     // Canny higher accumulator threshold. Lower values will detect more circles (easier) (half of higher). 
                    0,      // Minimum circle radius
                    0);     // Maximum circle radius
            
            // If no inner circles were detected
            if (innerSeq.total() == 0)
            {
                if (DEBUG_OUT_ENABLED)
                {
                    System.out.println("Failed to detect inner circle!");
                }

                // No object's were detected
                return(false);
            }
            
            // Calculate the center coordinates of the inner circle
            CvPoint3D32f innerCircle = new CvPoint3D32f(cvGetSeqElem(innerSeq, i));
            CvPoint innerCenter = cvPointFrom32f(new CvPoint2D32f(innerCircle.x(), innerCircle.y()));
            int innerRadius = Math.round(innerCircle.z());
            double innerDiameter = innerRadius * 2;
            
            int ROIStartPosX = (outerCenter.x() - outerRadius) - 3;
            int ROIStartPosY = (outerCenter.y() - outerRadius) - 3;
            int innerCircleX = ROIStartPosX + innerCenter.x();
            int innerCircleY = ROIStartPosY + innerCenter.y();
            
            // TODO Debugging: Show the detected inner circle
            if (DEBUG_OUT_ENABLED)
            {
                System.out.println("Detected an inner circle!");
                System.out.println("Inner Circle Center: X: " + innerCenter.x() + ", Y: " + innerCenter.y() + " (original ROI)");
                System.out.println("Inner Circle Center: X: " + innerCircleX + ", Y: " + innerCircleY + " (calculated)\n");
                IplImage imageTemp2 = imageROI;
                cvCircle(imageTemp2, innerCenter, innerRadius, CvScalar.GREEN, 1, CV_AA, 0);
                cvRectangle(imageTemp2, innerCenter, innerCenter, CvScalar.GREEN, 2, 8, 0);
                this.DisplayImage("Detected Inner Circle", imageTemp2);
            }
            
            // TODO: Debugging remove this later
            IplImage imageCanny = IplImage.create(imageROIThreshed.width(), imageROIThreshed.height(), IPL_DEPTH_8U, 1);
            cvCanny(imageROIThreshed, imageCanny, 20, 15, 3);
            
            if (DEBUG_OUT_ENABLED)
            {
                this.DisplayImage("Edge Detection", imageCanny);
            }
            
            // TODO Debugging Display both circle centers to see difference
            CvPoint centerCalced = cvPointFrom32f(new CvPoint2D32f(innerCircleX, innerCircleY));
            cvRectangle(imageTemp1, centerCalced, centerCalced, CvScalar.GREEN, 2, 8, 0);
            
            if (DEBUG_OUT_ENABLED)
            {
                this.DisplayImage("Circle Centers", imageTemp1);
            }
            
            // If the inner circle is located correctly inside the outer circle (both centers match with up to 5 pixels difference)
            if (innerCircleX > (outerCenter.x() - 8) && innerCircleX < (outerCenter.x() + 8) && innerCircleY > (outerCenter.y() - 8) && innerCircleY < (outerCenter.y() + 8))
            {
                if (DEBUG_OUT_ENABLED)
                {
                    System.out.println("The inner circle is correctly positioned inside the outer circle!");
                }
            }
            else
            {
                if (DEBUG_OUT_ENABLED)
                {
                    System.out.println("The inner circle is not correctly positioned!");
                }
                
                return(false);
            }
            
            // Check that the inner and outer circle diameter ratio is roughly the expected value
            double diameterRatio = outerDiameter / innerDiameter;
            if (diameterRatio < circleDiameterLow || diameterRatio > circleDiameterHigh)
            {
                if (DEBUG_OUT_ENABLED)
                {
                    System.out.println("The circle diameter ratio is out of range!");
                    System.out.println("Diameter Ratio: " + diameterRatio);
                }
                return(false);
            }
            
            if (DEBUG_OUT_ENABLED)
            {
                System.out.println("The circle diameter ratio is in range!");
            }
            
            // Calculate and set the object's relative position
            posX = outerCenter.x() - (sceneImage.width() / 2);
            posY = outerCenter.y() - (sceneImage.height() / 2);
            
            // Return successful
            return(true);
        }
        
        // Return successful
        return(true);
    }
    
    // Driver program
    public static void main(String args[]) throws InvalidImageException
    {
        ImageRecognition colorDetector = new ImageRecognition();
        
        // Load an images from file
        if (!colorDetector.SetImageFromFile("/home/michael/img/image-396.jpg"))
        {
            System.out.println("Failed to load image from file!");
            return;
        }

        try
        {
            // Set outer circle color values
            colorDetector.SetHueRange(150, 160); // TODO: Will likely need to open this range up a bit
            colorDetector.SetSaturationRange(0, 255);
            colorDetector.SetValueRange(0, 255);
            
            // Inner between 50 and 100
            // Set inner circle color values
            colorDetector.SetInnerHueRange(80, 100);
            colorDetector.SetInnerSaturationRange(0, 255);
            colorDetector.SetInnerValueRange(0, 255);
            
            // Set other values
            colorDetector.SetSmoothType(ImageRecognition.BLUR_GAUSSIAN);
            colorDetector.SetMorphCloseEnabled(true);
            colorDetector.SetMorphIntensity(14);
            colorDetector.SetSmoothIntensity(11);
            colorDetector.SetInnerSmoothIntensity(5);
            colorDetector.SetCircleDiameterRange(1, 3);
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set circle color values!");
        }
        /*
         colorDetector.SetHueRange(5, 30);
         colorDetector.SetSaturationRange(70, 135);
         colorDetector.SetValueRange(70, 135);
         */
        // colorDetector.SetHueRange(150, 170);
        // colorDetector.SetSaturationRange(184, 204); // THESE ARE FOR TESTIMG.JPG
        // colorDetector.SetValueRange(179, 199);
        
        // Set inner circle color values
        // colorDetector.SetInnerHueRange(20, 60);
        // colorDetector.SetInnerSaturationRange(240, 256); // THESE ARE FOR TESTIMG.JPG
        // colorDetector.SetInnerValueRange(240, 256);
        /*
        colorDetector.SetInnerHueRange(0, 10);
        colorDetector.SetInnerSaturationRange(150, 255);
        colorDetector.SetInnerValueRange(150, 255);
         * 
         */
        
        // If the object is detected
        if (colorDetector.Detect())
        {
            // Output the object's location
            System.out.println("Detected! X: " + colorDetector.GetObjectPositionX() + ", Y: " + colorDetector.GetObjectPositionY());
        }
        // Else no object was detected
        else
        {
            System.out.println("Failed to detect object in frame!");
        }
    }
}