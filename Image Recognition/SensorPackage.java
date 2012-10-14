/**
 * Represents a sensor package.
 * This class stores color (hue, saturation and value) ranges that are used
 * in identifying this specific sensor package. Color ranges are stored for both
 * the inner and outer identification circles.
 * 
 * @author Michael Staunton
 * @version 0.1
 */
public class SensorPackage
{
    private int hueMin, hueMax;
    private int saturationMin, saturationMax;
    private int valueMin, valueMax;
    private int innerHueMin, innerHueMax;
    private int innerSaturationMin, innerSaturationMax;
    private int innerValueMin, innerValueMax;
    
    /*
     * Default constructor.
     * Constructs a SensorPackage object with hue, saturation and value ranges of 0 to 0.
     */
    public SensorPackage()
    {
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
    }

    /**
     * Optional constructor.
     * Constructs a SensorPackage object with the given hue, saturation and value ranges.
     * 
     * @param hueMinimum The lowest value of the hue range (outer circle).
     * @param hueMaximum The highest value of the hue range (outer circle).
     * @param saturationMinimum The lowest value of the saturation range (outer circle).
     * @param saturationMaximum The highest value of the saturation range (outer circle).
     * @param valueMinimum The lowest value of the value range (outer circle).
     * @param valueMaximum The highest value of the value range (outer circle).
     * @param innerHueMinimum The lowest value of the hue range (inner circle).
     * @param innerHueMaximum The highest value of the hue range (inner circle).
     * @param innerSaturationMinimum The lowest value of the saturation range (inner circle).
     * @param innerSaturationMaximum The highest value of the saturation range (inner circle).
     * @param innervalueMinimum The lowest value of the value range (inner circle).
     * @param innerValueMaximum The highest value of the value range (inner circle).
     * @throws NumberOutOfRangeException If hue (0 - 179), saturation (0 - 255) and/or value (0 - 255) range is invalid.
     */
    public SensorPackage(int hueMinimum, int hueMaximum, int saturationMinimum, int saturationMaximum, int valueMinimum, int valueMaximum,
            int innerHueMinimum, int innerHueMaximum, int innerSaturationMinimum, int innerSaturationMaximum, int innerValueMinimum, int innerValueMaximum) throws NumberOutOfRangeException
    {
        // Validate hue range (outer circle)
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
        
        // Validate saturation range (outer circle)
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
        
        // Validate value range (outer circle)
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
        
        // Validate hue range (inner circle)
        if (innerHueMaximum < innerHueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum hue must be greater than the minimum hue!");
        }
        else if (innerHueMinimum < 0 || innerHueMinimum > 179)
        {
            throw new NumberOutOfRangeException("Minimum hue must be between 0 and 179!");
        }
        else if (innerHueMaximum < 0 || innerHueMaximum > 179)
        {
            throw new NumberOutOfRangeException("Maximum hue must be between 0 and 179!");
        }
        
        // Validate saturation range (inner circle)
        if (innerSaturationMaximum < innerSaturationMinimum)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be greater than the minimum saturation!");
        }
        else if (innerSaturationMinimum < 0 || innerSaturationMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum saturation must be between 0 and 255!");
        }
        else if (innerSaturationMaximum < 0 || innerSaturationMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum saturation must be between 0 and 255!");
        }
        
        // Validate value range (inner circle)
        if (innerValueMaximum < innerValueMinimum)
        {
            throw new NumberOutOfRangeException("Maximum value must be greater than the minimum value!");
        }
        else if (innerValueMinimum < 0 || innerValueMinimum > 255)
        {
            throw new NumberOutOfRangeException("Minimum value must be between 0 and 255!");
        }
        else if (innerValueMaximum < 0 || innerValueMaximum > 255)
        {
            throw new NumberOutOfRangeException("Maximum value must be between 0 and 255!");
        }
        
        hueMin = hueMinimum;
        hueMax = hueMaximum;
        saturationMin = saturationMinimum;
        saturationMax = saturationMaximum;
        valueMin = valueMinimum;
        valueMax = valueMaximum;
        innerHueMin = innerHueMinimum;
        innerHueMax = innerHueMaximum;
        innerSaturationMin = innerSaturationMinimum;
        innerSaturationMax = innerSaturationMaximum;
        innerValueMin = innerValueMinimum;
        innerValueMax = innerValueMaximum;
    }
    
    /**
     * Sets the lowest value and highest value of the hue range for the outer circle.
     * 
     * @param hueMinimum The lowest value of the hue range.
     * @param hueMaximum The highest value of the hue range.
     * @throws NumberOutOfRangeException If hue range is invalid (outside of 0 to 179 or when the minimum is greater than the maximum).
     */
    public void setHueRange(int hueMinimum, int hueMaximum) throws NumberOutOfRangeException
    {
        // Validate hue range
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
    
    /**
     * Sets the lowest value and highest value of the saturation range for the outer circle.
     * 
     * @param saturationMinimum The lowest value of the saturation range.
     * @param saturationMaximum The highest value of the saturation range.
     * @throws NumberOutOfRangeException If saturation range is invalid (outside of 0 to 255 or when the minimum is greater than the maximum).
     */
    public void setSaturationRange(int saturationMinimum, int saturationMaximum) throws NumberOutOfRangeException
    {
        // Validate saturation range
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
    
    /**
     * Sets the lowest value and highest value of the value range for the outer circle.
     * 
     * @param valueMinimum The lowest value of the value range.
     * @param valueMaximum The highest value of the value range.
     * @throws NumberOutOfRangeException If value range is invalid (outside of 0 to 255 or when the minimum is greater than the maximum). 
     */
    public void setValueRange(int valueMinimum, int valueMaximum) throws NumberOutOfRangeException
    {
        // Validate value range
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
    
    /**
     * Sets the lowest value and highest value of the hue range for the inner circle.
     * 
     * @param hueMinimum The lowest value of the hue range.
     * @param hueMaximum The highest value of the hue range.
     * @throws NumberOutOfRangeException If hue range is invalid (outside of 0 to 179 or when the minimum is greater than the maximum).
     */
    public void setInnerHueRange(int hueMinimum, int hueMaximum) throws NumberOutOfRangeException
    {
        // Validate hue range
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
    
    /**
     * Sets the lowest value and highest value of the saturation range for the inner circle.
     * 
     * @param saturationMinimum The lowest value of the saturation range.
     * @param saturationMaximum The highest value of the saturation range.
     * @throws NumberOutOfRangeException If saturation range is invalid (outside of 0 to 255 or when the minimum is greater than the maximum).
     */
    public void setInnerSaturationRange(int saturationMinimum, int saturationMaximum) throws NumberOutOfRangeException
    {
        // Validate saturation range
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
    
    /**
     * Sets the lowest value and highest value of the value range for the inner circle.
     * 
     * @param valueMinimum The lowest value of the value range.
     * @param valueMaximum The highest value of the value range.
     * @throws NumberOutOfRangeException If value range is invalid (outside of 0 to 255 or when the minimum is greater than the maximum). 
     */
    public void setInnerValueRange(int valueMinimum, int valueMaximum) throws NumberOutOfRangeException
    {
        // Validate value range
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
    
    /**
     * Returns the lowest value of the hue range (outer circle).
     * 
     * @return The lowest value of the hue range (outer circle).
     */
    public int getHueMinimum()
    {
        return(hueMin);
    }
    
    /**
     * Returns the highest value of the hue range (outer circle).
     * 
     * @return The highest value of the hue range (outer circle).
     */
    public int getHueMaximum()
    {
        return(hueMax);
    }
    
    /**
     * Returns the lowest value of the saturation range (outer circle).
     * 
     * @return The lowest value of the saturation range (outer circle).
     */
    public int getSaturationMinimum()
    {
        return(saturationMin);
    }
    
    /**
     * Returns the highest value of the saturation range (outer circle).
     * 
     * @return The highest value of the saturation range (outer circle).
     */
    public int getSaturationMaximum()
    {
        return(saturationMax);
    }
    
    /**
     * Returns the lowest value of the value range (outer circle).
     * 
     * @return The lowest value of the value range (outer circle).
     */
    public int getValueMinimum()
    {
        return(valueMin);
    }
    
    /**
     * Returns the highest value of the value range (outer circle).
     * 
     * @return The highest value of the value range (outer circle).
     */
    public int getValueMaximum()
    {
        return(valueMax);
    }
    
    /////////////
    
    /**
     * Returns the lowest value of the hue range (inner circle).
     * 
     * @return The lowest value of the hue range (inner circle).
     */
    public int getInnerHueMinimum()
    {
        return(innerHueMin);
    }
    
    /**
     * Returns the highest value of the hue range (inner circle).
     * 
     * @return The highest value of the hue range (inner circle).
     */
    public int getInnerHueMaximum()
    {
        return(innerHueMax);
    }
    
    /**
     * Returns the lowest value of the saturation range (inner circle).
     * 
     * @return The lowest value of the saturation range (inner circle).
     */
    public int getInnerSaturationMinimum()
    {
        return(innerSaturationMin);
    }
    
    /**
     * Returns the highest value of the saturation range (inner circle).
     * 
     * @return The highest value of the saturation range (inner circle).
     */
    public int getInnerSaturationMaximum()
    {
        return(innerSaturationMax);
    }
    
    /**
     * Returns the lowest value of the value range (inner circle).
     * 
     * @return The lowest value of the value range (inner circle).
     */
    public int getInnerValueMinimum()
    {
        return(innerValueMin);
    }
    
    /**
     * Returns the highest value of the value range (inner circle).
     * 
     * @return The highest value of the value range (inner circle).
     */
    public int getInnerValueMaximum()
    {
        return(innerValueMax);
    }
}
