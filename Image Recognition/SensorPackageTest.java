
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unit test for SensorPackage.
 * 
 * @author michael
 * @version 0.1
 */
public class SensorPackageTest
{
    // Tests default constructor
    // Expected output is "Success"
    public static void Test1()
    {
        System.out.print("Test 1... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        System.out.println("Success");
    }
    
    // Tests optional constructor with 0 values
    // Expected output is "Success"
    public static void Test2()
    {
        System.out.print("Test 2... ");
        
        try
        {
            SensorPackage sensorPkg = new SensorPackage(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            Logger.getLogger(SensorPackageTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed");
        }
    }
    
    // Tests setHueRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test3()
    {
        System.out.print("Test 3... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(0, 179);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setHueRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test4()
    {
        System.out.print("Test 4... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(100, 135);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setHueRange method with out of range (too low) minimum hue
    // Expected output is "Success"
    public static void Test5()
    {
        System.out.print("Test 5... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setHueRange method with out of range (too high) minimum hue
    // Expected output is "Success"
    public static void Test6()
    {
        System.out.print("Test 6... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(180, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setHueRange method with out of range (too low) maximum hue
    // Expected output is "Success"
    public static void Test7()
    {
        System.out.print("Test 7... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setHueRange method with out of range (too high) maximum hue
    // Expected output is "Success"
    public static void Test8()
    {
        System.out.print("Test 8... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(100, 180);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setHueRange method a greater minimum hue than maximum
    // Expected output is "Success"
    public static void Test9()
    {
        System.out.print("Test 9... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(170, 70);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }

    // Tests setSaturationRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test10()
    {
        System.out.print("Test 10... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(0, 255);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setSaturationRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test11()
    {
        System.out.print("Test 11... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(80, 210);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setSaturationRange method with out of range (too low) minimum saturation
    // Expected output is "Success"
    public static void Test12()
    {
        System.out.print("Test 12... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setSaturationRange method with out of range (too high) minimum saturation
    // Expected output is "Success"
    public static void Test13()
    {
        System.out.print("Test 13... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(256, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setSaturationRange method with out of range (too low) maximum saturation
    // Expected output is "Success"
    public static void Test14()
    {
        System.out.print("Test 14... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setSaturationRange method with out of range (too high) maximum saturation
    // Expected output is "Success"
    public static void Test15()
    {
        System.out.print("Test 15... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(100, 256);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setSaturationRange method a greater minimum saturation than maximum
    // Expected output is "Success"
    public static void Test16()
    {
        System.out.print("Test 16... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(160, 30);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }

    // Tests setValueRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test17()
    {
        System.out.print("Test 17... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(0, 255);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setValueRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test18()
    {
        System.out.print("Test 18... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(80, 210);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setValueRange method with out of range (too low) minimum value
    // Expected output is "Success"
    public static void Test19()
    {
        System.out.print("Test 19... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setValueRange method with out of range (too high) minimum value
    // Expected output is "Success"
    public static void Test20()
    {
        System.out.print("Test 20... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(256, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setValueRange method with out of range (too low) maximum value
    // Expected output is "Success"
    public static void Test21()
    {
        System.out.print("Test 21... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setValueRange method with out of range (too high) maximum value
    // Expected output is "Success"
    public static void Test22()
    {
        System.out.print("Test 22... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(100, 256);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setValueRange method a greater minimum value than maximum
    // Expected output is "Success"
    public static void Test23()
    {
        System.out.print("Test 23... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(160, 30);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerHueRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test24()
    {
        System.out.print("Test 24... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(0, 179);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerHueRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test25()
    {
        System.out.print("Test 25... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(100, 135);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerHueRange method with out of range (too low) minimum hue
    // Expected output is "Success"
    public static void Test26()
    {
        System.out.print("Test 26... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerHueRange method with out of range (too high) minimum hue
    // Expected output is "Success"
    public static void Test27()
    {
        System.out.print("Test 27... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(180, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerHueRange method with out of range (too low) maximum hue
    // Expected output is "Success"
    public static void Test28()
    {
        System.out.print("Test 28... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerHueRange method with out of range (too high) maximum hue
    // Expected output is "Success"
    public static void Test29()
    {
        System.out.print("Test 29... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(100, 180);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerHueRange method a greater minimum hue than maximum
    // Expected output is "Success"
    public static void Test30()
    {
        System.out.print("Test 30... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(170, 70);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }

    // Tests setInnerSaturationRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test31()
    {
        System.out.print("Test 31... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(0, 255);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerSaturationRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test32()
    {
        System.out.print("Test 32... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(80, 210);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerSaturationRange method with out of range (too low) minimum saturation
    // Expected output is "Success"
    public static void Test33()
    {
        System.out.print("Test 33... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerSaturationRange method with out of range (too high) minimum saturation
    // Expected output is "Success"
    public static void Test34()
    {
        System.out.print("Test 34... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(256, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerSaturationRange method with out of range (too low) maximum saturation
    // Expected output is "Success"
    public static void Test35()
    {
        System.out.print("Test 35... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerSaturationRange method with out of range (too high) maximum saturation
    // Expected output is "Success"
    public static void Test36()
    {
        System.out.print("Test 36... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(100, 256);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerSaturationRange method a greater minimum saturation than maximum
    // Expected output is "Success"
    public static void Test37()
    {
        System.out.print("Test 37... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(160, 30);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }

    // Tests setInnerValueRange method with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test38()
    {
        System.out.print("Test 38... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(0, 255);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerValueRange method with mid-range allowable values
    // Expected output is "Success"
    public static void Test39()
    {
        System.out.print("Test 39... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(80, 210);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests setInnerValueRange method with out of range (too low) minimum value
    // Expected output is "Success"
    public static void Test40()
    {
        System.out.print("Test 40... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(-1, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerValueRange method with out of range (too high) minimum value
    // Expected output is "Success"
    public static void Test41()
    {
        System.out.print("Test 41... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(256, 100);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerValueRange method with out of range (too low) maximum value
    // Expected output is "Success"
    public static void Test42()
    {
        System.out.print("Test 42... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(100, -1);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerValueRange method with out of range (too high) maximum value
    // Expected output is "Success"
    public static void Test43()
    {
        System.out.print("Test 43... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(100, 256);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests setInnerValueRange method a greater minimum value than maximum
    // Expected output is "Success"
    public static void Test44()
    {
        System.out.print("Test 44... ");
        
        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(160, 30);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    // Tests optional constructor with the minimum and maximum allowable values
    // Expected output is "Success"
    public static void Test45()
    {
        System.out.print("Test 45... ");
        
        try
        {
            SensorPackage sensorPkg = new SensorPackage(0, 179, 0, 255, 0, 255, 0, 179, 0, 255, 0, 255);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests optional constructor with mid-range allowable values
    // Expected output is "Success"
    public static void Test46()
    {
        System.out.print("Test 46... ");
        
        try
        {
            SensorPackage sensorPkg = new SensorPackage(50, 100, 150, 220, 120, 126, 112, 168, 15, 35, 88, 110);
            System.out.println("Success");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests getHueMinimum returns the correct value and that setHueRange correctly sets the value
    // Expected output is "Success"
    public static void Test47()
    {
        System.out.print("Test 47... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(10, 20);
            
            if (sensorPkg.getHueMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set hue range!");
        }
    }
    
    // Tests getHueMaximum returns the correct value and that setHueRange correctly sets the value
    // Expected output is "Success"
    public static void Test48()
    {
        System.out.print("Test 48... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setHueRange(10, 20);
            
            if (sensorPkg.getHueMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set hue range!");
        }
    }
    
    // Tests getSaturationMinimum returns the correct value and that setSaturationRange correctly sets the value
    // Expected output is "Success"
    public static void Test49()
    {
        System.out.print("Test 49... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(10, 20);
            
            if (sensorPkg.getSaturationMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set saturation range!");
        }
    }
    
    // Tests getSaturationMaximum returns the correct value and that setSaturationRange correctly sets the value
    // Expected output is "Success"
    public static void Test50()
    {
        System.out.print("Test 50... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setSaturationRange(10, 20);
            
            if (sensorPkg.getSaturationMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set saturation range!");
        }
    }
    
    // Tests getValueMinimum returns the correct value and that setValueRange correctly sets the value
    // Expected output is "Success"
    public static void Test51()
    {
        System.out.print("Test 51... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(10, 20);
            
            if (sensorPkg.getValueMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set value range!");
        }
    }
    
    // Tests getValueMaximum returns the correct value and that setValueRange correctly sets the value
    // Expected output is "Success"
    public static void Test52()
    {
        System.out.print("Test 52... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setValueRange(10, 20);
            
            if (sensorPkg.getValueMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set value range!");
        }
    }
    
    // Tests getInnerHueMinimum returns the correct value and that setInnerHueRange correctly sets the value
    // Expected output is "Success"
    public static void Test53()
    {
        System.out.print("Test 53... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(10, 20);
            
            if (sensorPkg.getInnerHueMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set hue range!");
        }
    }
    
    // Tests getInnerHueMaximum returns the correct value and that setInnerHueRange correctly sets the value
    // Expected output is "Success"
    public static void Test54()
    {
        System.out.print("Test 54... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerHueRange(10, 20);
            
            if (sensorPkg.getInnerHueMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set hue range!");
        }
    }
    
    // Tests getInnerSaturationMinimum returns the correct value and that setInnerSaturationRange correctly sets the value
    // Expected output is "Success"
    public static void Test55()
    {
        System.out.print("Test 55... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(10, 20);
            
            if (sensorPkg.getInnerSaturationMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set saturation range!");
        }
    }
    
    // Tests getInnerSaturationMaximum returns the correct value and that setInnerSaturationRange correctly sets the value
    // Expected output is "Success"
    public static void Test56()
    {
        System.out.print("Test 56... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerSaturationRange(10, 20);
            
            if (sensorPkg.getInnerSaturationMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set saturation range!");
        }
    }
    
    // Tests getInnerValueMinimum returns the correct value and that setInnerValueRange correctly sets the value
    // Expected output is "Success"
    public static void Test57()
    {
        System.out.print("Test 57... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(10, 20);
            
            if (sensorPkg.getInnerValueMinimum() == 10)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set value range!");
        }
    }
    
    // Tests getInnerValueMaximum returns the correct value and that setInnerValueRange correctly sets the value
    // Expected output is "Success"
    public static void Test58()
    {
        System.out.print("Test 58... ");

        SensorPackage sensorPkg = new SensorPackage();
        
        try
        {
            sensorPkg.setInnerValueRange(10, 20);
            
            if (sensorPkg.getInnerValueMaximum() == 20)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed");
            }
            
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed to set value range!");
        }
    }
    
    // Tests optional constructor with allowable values and that the values are set correctly
    // Expected output is "Success"
    public static void Test59()
    {
        System.out.print("Test 59... ");
        
        try
        {
            SensorPackage sensorPkg = new SensorPackage(50, 100, 150, 220, 120, 126, 112, 168, 15, 35, 88, 110);
            
            if (sensorPkg.getHueMinimum() == 50 &&
                    sensorPkg.getHueMaximum() == 100 &&
                    sensorPkg.getSaturationMinimum() == 150 &&
                    sensorPkg.getSaturationMaximum() == 220 &&
                    sensorPkg.getValueMinimum() == 120 &&
                    sensorPkg.getValueMaximum() == 126 &&
                    sensorPkg.getInnerHueMinimum() == 112 &&
                    sensorPkg.getInnerHueMaximum() == 168 &&
                    sensorPkg.getInnerSaturationMinimum() == 15 &&
                    sensorPkg.getInnerSaturationMaximum() == 35 &&
                    sensorPkg.getInnerValueMinimum() == 88 &&
                    sensorPkg.getInnerValueMaximum() == 110)
            {
                System.out.println("Success");
            }
            else
            {
                System.out.println("Failed to set range values!");
            }
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Failed");
        }
    }
    
    // Tests optional constructor with invalid values
    // Expected output is "Success"
    public static void Test60()
    {
        System.out.print("Test 60... ");
        
        try
        {
            SensorPackage sensorPkg = new SensorPackage(-1, 100, 100, 5, 19, 256, 199, 100, 11, -50, 12, 115);
            System.out.println("Failed");
        }
        catch (NumberOutOfRangeException ex)
        {
            System.out.println("Success");
        }
    }
    
    public static void main(String args[])
    {
        Test1();
        Test2();
        Test3();
        Test4();
        Test5();
        Test6();
        Test7();
        Test8();
        Test9();
        Test10();
        Test11();
        Test12();
        Test13();
        Test14();
        Test15();
        Test16();
        Test17();
        Test18();
        Test19();
        Test20();
        Test21();
        Test22();
        Test23();
        Test24();
        Test25();
        Test26();
        Test27();
        Test28();
        Test29();
        Test30();
        Test31();
        Test32();
        Test33();
        Test34();
        Test35();
        Test36();
        Test37();
        Test38();
        Test39();
        Test40();
        Test41();
        Test42();
        Test43();
        Test44();
        Test45();
        Test46();
        Test47();
        Test48();
        Test49();
        Test50();
        Test51();
        Test52();
        Test53();
        Test54();
        Test55();
        Test56();
        Test57();
        Test58();
        Test59();
        Test60();
    }
}
