package mapper;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.AbstractRadial;


/**
 *
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class Radial extends AbstractRadial
{                
    private int tickLabelPeriod; // Draw value at every nth tickmark  
    
    // Images used to combine layers for background and foreground
    private java.awt.image.BufferedImage bImage;
    private java.awt.image.BufferedImage fImage;
    
    private java.awt.image.BufferedImage pointerImage;
    private java.awt.image.BufferedImage pointerShadowImage;    
    private java.awt.image.BufferedImage thresholdImage;
    private java.awt.image.BufferedImage minMeasuredImage;
    private java.awt.image.BufferedImage maxMeasuredImage;
    private java.awt.image.BufferedImage disabledImage;          
    private double angle;
    private final java.awt.geom.Point2D CENTER = new java.awt.geom.Point2D.Double();
    private final java.awt.geom.Point2D LCD_POSITION = new java.awt.geom.Point2D.Double();
    private final java.awt.Dimension LCD_DIM = new java.awt.Dimension();
    private boolean section3DEffectVisible;
    private java.awt.RadialGradientPaint section3DEffect;
    private final java.awt.geom.Point2D TRACK_OFFSET = new java.awt.geom.Point2D.Double();
    private final java.awt.font.FontRenderContext RENDER_CONTEXT = new java.awt.font.FontRenderContext(null, true, true);
    private java.awt.font.TextLayout unitLayout;
    private final java.awt.geom.Rectangle2D UNIT_BOUNDARY = new java.awt.geom.Rectangle2D.Double();
    private double unitStringWidth;
    private java.awt.font.TextLayout valueLayout;
    private final java.awt.geom.Rectangle2D VALUE_BOUNDARY = new java.awt.geom.Rectangle2D.Double();    
        
    public Radial()
    {
        super();                  
        setLcdVisible(true);
        switch(getGaugeType())
        {
            case TYPE1:                    
                tickLabelPeriod = 20;
                break;
                
            case TYPE2:                
                tickLabelPeriod = 10; 
                break;
                
            case TYPE3:                    
                tickLabelPeriod = 10; 
                break;
                
            case TYPE4:                
                tickLabelPeriod = 10;
                break;
                
            default:                
                tickLabelPeriod = 10;
                break;
        }                                         
        angle = 0;              
        section3DEffectVisible = false;                
        init(getInnerBounds().width, getInnerBounds().height);
    }

    @Override
    public final AbstractGauge init(final int WIDTH, final int HEIGHT)
    {                         
        if (isDigitalFont())
        {
            setLcdValueFont(LCD_DIGITAL_FONT.deriveFont(0.7f * WIDTH * 0.15f));            
        }
        else
        {
            setLcdValueFont(LCD_STANDARD_FONT.deriveFont(0.625f * WIDTH * 0.15f));       
        }

        if (getUseCustomLcdUnitFont())
        {
            setLcdUnitFont(getCustomLcdUnitFont().deriveFont(0.25f * WIDTH * 0.15f));
        }
        else
        {
            setLcdUnitFont(LCD_STANDARD_FONT.deriveFont(0.25f * WIDTH * 0.15f));
        }
                     
        // Create Background Image
        if (bImage != null)
        {
            bImage.flush();
        }
        bImage = create_Image(WIDTH, WIDTH);
        
        // Create Foreground Image
        if (fImage != null)
        {
            fImage.flush();
        }
        fImage = create_Image(WIDTH, WIDTH);
        
        if (isFrameVisible())
        {
            switch (getFrameType())
            {
                case ROUND:
                    FRAME_FACTORY.createRadialFrame(WIDTH, getFrameDesign(), getCustomFrameDesign(), isFrame3dEffectVisible(), bImage);   
                    break;
                case SQUARE:
                    FRAME_FACTORY.createLinearFrame(WIDTH, WIDTH, getFrameDesign(), getCustomFrameDesign(), isFrame3dEffectVisible(), bImage);
                    break;
                default:
                    FRAME_FACTORY.createRadialFrame(WIDTH, getFrameDesign(), getCustomFrameDesign(), isFrame3dEffectVisible(), bImage);
                    break;
            }
        }        
                
        if (isBackgroundVisible())
        {
            create_BACKGROUND_Image(WIDTH, "", "", bImage);
        }        

        switch(getGaugeType())
        {
            case TYPE1:
                create_POSTS_Image(WIDTH, fImage, eu.hansolo.steelseries.tools.PostPosition.CENTER, eu.hansolo.steelseries.tools.PostPosition.MAX_CENTER_TOP, eu.hansolo.steelseries.tools.PostPosition.MIN_LEFT);                    
                break;

            case TYPE2:
                create_POSTS_Image(WIDTH, fImage, eu.hansolo.steelseries.tools.PostPosition.CENTER, eu.hansolo.steelseries.tools.PostPosition.MIN_LEFT, eu.hansolo.steelseries.tools.PostPosition.MAX_RIGHT);
                break;

            case TYPE3:
                create_POSTS_Image(WIDTH, fImage, eu.hansolo.steelseries.tools.PostPosition.CENTER, eu.hansolo.steelseries.tools.PostPosition.MAX_CENTER_BOTTOM, eu.hansolo.steelseries.tools.PostPosition.MAX_RIGHT);
                break;

            case TYPE4:                

            default:
                create_POSTS_Image(WIDTH, fImage, eu.hansolo.steelseries.tools.PostPosition.CENTER, eu.hansolo.steelseries.tools.PostPosition.MIN_BOTTOM, eu.hansolo.steelseries.tools.PostPosition.MAX_BOTTOM);
                break;
        }         

        TRACK_OFFSET.setLocation(0, 0);
        CENTER.setLocation(getGaugeBounds().getCenterX(), getGaugeBounds().getCenterX());                    
        if (isTrackVisible())
        {
            create_TRACK_Image(WIDTH, getFreeAreaAngle(), getTickmarkOffset(), getMinValue(), getMaxValue(), getAngleStep(), getTrackStart(), getTrackSection(), getTrackStop(), getTrackStartColor(), getTrackSectionColor(), getTrackStopColor(), 0.38f, CENTER, getTickmarkDirection(), TRACK_OFFSET, bImage);
        }        
           
        // Create areas if not empty
        if (!getAreas().isEmpty())
        {
            createAreas(bImage);
        }        
        // Create the sections 3d effect gradient overlay
        if (section3DEffectVisible)
        {
            section3DEffect = create_3D_RADIAL_GRADIENT(WIDTH, 0.38f);
        }
        // Create sections if not empty
        if (!getSections().isEmpty())
        {
            createSections(bImage);
        }        
        
        create_TICKMARKS_Image(WIDTH, getFreeAreaAngle(), getTickmarkOffset(), getMinValue(), getMaxValue(), getAngleStep(), tickLabelPeriod, 0, getScaleDividerPower(), isDrawTicks(), isDrawTickLabels(), getTickmarkSections(), 0.38f, 0.09f, new java.awt.geom.Point2D.Double(WIDTH / 2.0, WIDTH / 2.0), eu.hansolo.steelseries.tools.Direction.CLOCKWISE, null, bImage);
        
        create_TITLE_Image(WIDTH, getTitle(), getUnitString(), bImage);
                
        if (isLcdVisible())
        {
            switch(getGaugeType())
            {
                case TYPE1:
                    create_LCD_Image(new java.awt.geom.Rectangle2D.Double(((getGaugeBounds().width - WIDTH * 0.55) / 2.0), (getGaugeBounds().height * 0.55), (WIDTH * 0.55), (WIDTH * 0.15)), getLcdColor(), getCustomLcdBackground(), bImage);                
                    LCD_POSITION.setLocation(((getGaugeBounds().width - WIDTH * 0.55) / 2.0), (getGaugeBounds().height * 0.55));
                    LCD_DIM.setSize(WIDTH * 0.55, WIDTH * 0.15);
                    break;

                case TYPE2:
                    create_LCD_Image(new java.awt.geom.Rectangle2D.Double(((getGaugeBounds().width - WIDTH * 0.55) / 2.0), (getGaugeBounds().height * 0.55), (WIDTH * 0.55), (WIDTH * 0.15)), getLcdColor(), getCustomLcdBackground(), bImage);                                  
                    LCD_POSITION.setLocation(((getGaugeBounds().width - WIDTH * 0.55) / 2.0), (getGaugeBounds().height * 0.55));
                    LCD_DIM.setSize(WIDTH * 0.55, WIDTH * 0.15);
                    break;

                case TYPE3:
                    create_LCD_Image(new java.awt.geom.Rectangle2D.Double(((getGaugeBounds().width - WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55), (WIDTH * 0.4), (WIDTH * 0.15)), getLcdColor(), getCustomLcdBackground(), bImage);                                
                    LCD_POSITION.setLocation(((getGaugeBounds().width - WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55));
                    LCD_DIM.setSize(WIDTH * 0.4, WIDTH * 0.15);
                    break;

                case TYPE4:                    

                default:
                    create_LCD_Image(new java.awt.geom.Rectangle2D.Double(((getGaugeBounds().width - WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55), (WIDTH * 0.4), (WIDTH * 0.15)), getLcdColor(), getCustomLcdBackground(), bImage);
                    LCD_POSITION.setLocation(((getGaugeBounds().width - WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55));
                    LCD_DIM.setSize(WIDTH * 0.4, WIDTH * 0.15);
                    break;
            }              
        }              
               
        if (pointerImage != null)
        {
            pointerImage.flush();
        }
        pointerImage = create_POINTER_Image(WIDTH, getPointerType());

        if (pointerShadowImage != null)
        {
            pointerShadowImage.flush();
        }
        pointerShadowImage = create_POINTER_SHADOW_Image(WIDTH, getPointerType());
        
        if (thresholdImage != null)
        {
            thresholdImage.flush();
        }
        thresholdImage = create_THRESHOLD_Image(WIDTH);

        if (minMeasuredImage != null)
        {
            minMeasuredImage.flush();
        }
        minMeasuredImage = create_MEASURED_VALUE_Image(WIDTH, new java.awt.Color(0, 23, 252, 255));

        if (maxMeasuredImage != null)
        {
            maxMeasuredImage.flush();
        }
        maxMeasuredImage = create_MEASURED_VALUE_Image(WIDTH, new java.awt.Color(252, 29, 0, 255));

        if (isForegroundVisible())
        {            
            switch(getFrameType())
            {
                case SQUARE:
                    FOREGROUND_FACTORY.createLinearForeground(WIDTH, WIDTH, false, fImage);
                    break;
                    
                case ROUND:
                    
                default:
                    FOREGROUND_FACTORY.createRadialForeground(WIDTH, false, getForegroundType(), fImage);
                    break;
            }
        }

        if (disabledImage != null)
        {
            disabledImage.flush();
        }
        disabledImage = create_DISABLED_Image(WIDTH);                        
                                          
        setCurrentLedImage(getLedImageOff());
        
        return this;
    }
       
    @Override
    protected void paintComponent(java.awt.Graphics g)
    {
        if (!isInitialized())
        {            
            return;
        }
        
        final java.awt.Graphics2D G2 = (java.awt.Graphics2D) g.create();        
        CENTER.setLocation(getGaugeBounds().getCenterX(), getGaugeBounds().getCenterX());

        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_DITHERING, java.awt.RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        //G2.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS, java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        G2.translate(getInnerBounds().x, getInnerBounds().y);        
        final java.awt.geom.AffineTransform OLD_TRANSFORM = G2.getTransform();

        // Draw combined background image
        G2.drawImage(bImage, 0, 0, null);
                                
        // Draw threshold indicator
        if (isThresholdVisible())
        {
            G2.rotate(getRotationOffset() + (getThreshold() - getMinValue()) * getAngleStep(), CENTER.getX(), CENTER.getY());            
            G2.drawImage(thresholdImage, (int) (getGaugeBounds().width * 0.480369999), (int) (getGaugeBounds().height * 0.13), null);
            G2.setTransform(OLD_TRANSFORM);
        }

        // Draw min measured value indicator
        if (isMinMeasuredValueVisible())
        {
            G2.rotate(getRotationOffset() + (getMinMeasuredValue() - getMinValue()) * getAngleStep(), CENTER.getX(), CENTER.getY());
            G2.drawImage(minMeasuredImage, (int) (getGaugeBounds().width * 0.4865), (int) (getGaugeBounds().height * 0.105), null);
            G2.setTransform(OLD_TRANSFORM);
        }

        // Draw max measured value indicator
        if (isMaxMeasuredValueVisible())
        {
            G2.rotate(getRotationOffset() + (getMaxMeasuredValue() - getMinValue()) * getAngleStep(), CENTER.getX(), CENTER.getY());
            G2.drawImage(maxMeasuredImage, (int) (getGaugeBounds().width * 0.4865), (int) (getGaugeBounds().height * 0.105), null);
            G2.setTransform(OLD_TRANSFORM);
        }
                   
        // Draw LED if enabled
        if (isLedVisible())
        {
            G2.drawImage(getCurrentLedImage(), (int) (getGaugeBounds().width * getLedPositionX()), (int) (getGaugeBounds().height * getLedPositionY()), null);
        }

        // Draw LCD display
        if (isLcdVisible())
        {            
            if (getLcdColor() == eu.hansolo.steelseries.tools.LcdColor.CUSTOM)
            {
                G2.setColor(getCustomLcdForeground());
            }
            else
            {
                G2.setColor(getLcdColor().TEXT_COLOR);
            }
            G2.setFont(getLcdUnitFont());                        
            if (isLcdUnitStringVisible())
            {
                unitLayout = new java.awt.font.TextLayout(getLcdUnitString(), G2.getFont(), RENDER_CONTEXT);
                UNIT_BOUNDARY.setFrame(unitLayout.getBounds());
                G2.drawString(getLcdUnitString(), (int) (LCD_POSITION.getX() + (LCD_DIM.width - UNIT_BOUNDARY.getWidth()) - LCD_DIM.width * 0.03), (int) (LCD_POSITION.getY() + LCD_DIM.height * 0.76f));
                unitStringWidth = UNIT_BOUNDARY.getWidth();
            }
            else
            {
                unitStringWidth = 0;
            }
            G2.setFont(getLcdValueFont());
            valueLayout = new java.awt.font.TextLayout(formatLcdValue(getLcdValue()), G2.getFont(), RENDER_CONTEXT);
            VALUE_BOUNDARY.setFrame(valueLayout.getBounds());        
            G2.drawString(formatLcdValue(getLcdValue()), (int) (LCD_POSITION.getX() + (LCD_DIM.width - unitStringWidth - VALUE_BOUNDARY.getWidth()) - LCD_DIM.width * 0.09), (int) (LCD_POSITION.getY() + LCD_DIM.height * 0.76f));
        }
        
        // Draw the pointer
        angle = getRotationOffset() + (getValue() - getMinValue()) * getAngleStep();
        G2.rotate(angle + (Math.cos(Math.toRadians(angle - getRotationOffset() - 91.5))), CENTER.getX(), CENTER.getY());
        G2.drawImage(pointerShadowImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);
        G2.rotate(angle, CENTER.getX(), CENTER.getY());
        G2.drawImage(pointerImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw combined foreground image
        G2.drawImage(fImage, 0, 0, null);
        
        // Draw disabled image if component isEnabled() == false
        if (!isEnabled())
        {
            G2.drawImage(disabledImage, 0, 0, null);
        }

        G2.translate(-getInnerBounds().x, -getInnerBounds().y);

        G2.dispose();
    }

    @Override
    public void setValue(double value)
    {
        super.setValue(value);
                
        if (isValueCoupled())
        {
            setLcdValue(value);            
        }
        repaint(getInnerBounds());                        
    }
            
    public int getTickLabelPeriod()
    {
        return this.tickLabelPeriod;
    }

    public void setTickLabelPeriod(final int TICK_LABEL_PERIOD)
    {
        this.tickLabelPeriod = TICK_LABEL_PERIOD;        
        init(getInnerBounds().width, getInnerBounds().height);
        repaint(getInnerBounds());
    }
        
    /**
     * Returns true if the 3d effect gradient overlay for the sections is visible
     * @return true if the 3d effect gradient overlay for the sections is visible
     */
    public boolean isSection3DEffectVisible()
    {
        return this.section3DEffectVisible;
    }
    
    /**
     * Defines the visibility of the 3d effect gradient overlay for the sections
     * @param SECTION_3D_EFFECT_VISIBLE 
     */
    public void setSection3DEffectVisible(final boolean SECTION_3D_EFFECT_VISIBLE)
    {
        this.section3DEffectVisible = SECTION_3D_EFFECT_VISIBLE;
        init(getInnerBounds().width, getInnerBounds().height);
        repaint(getInnerBounds());
    }
    
    @Override
    public java.awt.geom.Point2D getCenter()
    {
        return new java.awt.geom.Point2D.Double(bImage.getWidth() / 2.0 + getInnerBounds().x, bImage.getHeight() / 2.0 + getInnerBounds().y);
    }

    @Override
    public java.awt.geom.Rectangle2D getBounds2D()
    {
        return new java.awt.geom.Rectangle2D.Double(bImage.getMinX(), bImage.getMinY(), bImage.getWidth(), bImage.getHeight());
    }
                       
    // <editor-fold defaultstate="collapsed" desc="Areas related">    
    private void createAreas(final java.awt.image.BufferedImage IMAGE)
    {
        final double ORIGIN_CORRECTION;
        final double ANGLE_STEP;

        if (bImage != null)
        {
            switch(getGaugeType())
            {
                case TYPE1:
                    ORIGIN_CORRECTION = 180;
                    ANGLE_STEP = 90 / (getMaxValue() - getMinValue());
                    break;

                case TYPE2:
                    ORIGIN_CORRECTION = 180;
                    ANGLE_STEP = 180 / (getMaxValue() - getMinValue());
                    break;

                case TYPE3:
                    ORIGIN_CORRECTION = 270;
                    ANGLE_STEP = 270 / (getMaxValue() - getMinValue());
                    break;

                case TYPE4:
                    ORIGIN_CORRECTION = 240;
                    ANGLE_STEP = 300 / (getMaxValue() - getMinValue());
                    break;

                default:
                    ORIGIN_CORRECTION = 240;
                    ANGLE_STEP = 300 / (getMaxValue() - getMinValue());
                    break;
            }

            if (bImage != null && !getAreas().isEmpty())
            {            
                final double RADIUS = bImage.getWidth() * 0.38f - bImage.getWidth() * 0.04f;
                final double FREE_AREA = bImage.getWidth() / 2.0 - RADIUS;
                final java.awt.geom.Rectangle2D AREA_FRAME = new java.awt.geom.Rectangle2D.Double(bImage.getMinX() + FREE_AREA, bImage.getMinY() + FREE_AREA, 2 * RADIUS, 2 * RADIUS);           
                for (eu.hansolo.steelseries.tools.Section area : getAreas())
                {                
                    area.setFilledArea(new java.awt.geom.Arc2D.Double(AREA_FRAME, ORIGIN_CORRECTION - (area.getStart() * ANGLE_STEP) + (getMinValue() * ANGLE_STEP), -(area.getStop() - area.getStart()) * ANGLE_STEP, java.awt.geom.Arc2D.PIE));                
                }           
            }  
            
            // Draw the areas
            if (isAreasVisible() && IMAGE != null)
            {                
                final java.awt.Graphics2D G2 = IMAGE.createGraphics();
                G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                for (eu.hansolo.steelseries.tools.Section area : getAreas())
                {
                    G2.setColor(area.getColor());
                    G2.fill(area.getFilledArea());
                }
                G2.dispose();
            }                
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Sections related">    
    private void createSections(final java.awt.image.BufferedImage IMAGE)
    {                                            
        final double ORIGIN_CORRECTION;
        final double ANGLE_STEP;
        final double OUTER_RADIUS;
        final double INNER_RADIUS;
        final double FREE_AREA_OUTER_RADIUS;
        final double FREE_AREA_INNER_RADIUS;
        final java.awt.geom.Ellipse2D INNER;
        
        if (bImage != null)
        {
            switch (getGaugeType())
            {
                case TYPE1:
                    ORIGIN_CORRECTION = 180.0;
                    ANGLE_STEP = 90.0 / (getMaxValue() - getMinValue());
                    OUTER_RADIUS = bImage.getWidth() * 0.38f;
                    INNER_RADIUS = bImage.getWidth() * 0.38f - bImage.getWidth() * 0.04f;
                    FREE_AREA_OUTER_RADIUS = bImage.getWidth() / 2.0 - OUTER_RADIUS;
                    FREE_AREA_INNER_RADIUS = bImage.getWidth() / 2.0 - INNER_RADIUS;
                    INNER = new java.awt.geom.Ellipse2D.Double(bImage.getMinX() + FREE_AREA_INNER_RADIUS, bImage.getMinY() + FREE_AREA_INNER_RADIUS, 2 * INNER_RADIUS, 2 * INNER_RADIUS);
                    break;

                case TYPE2:
                    ORIGIN_CORRECTION = 180.0;
                    ANGLE_STEP = 180.0 / (getMaxValue() - getMinValue());
                    OUTER_RADIUS = bImage.getWidth() * 0.38f;
                    INNER_RADIUS = bImage.getWidth() * 0.38f - bImage.getWidth() * 0.04f;
                    FREE_AREA_OUTER_RADIUS = bImage.getWidth() / 2.0 - OUTER_RADIUS;
                    FREE_AREA_INNER_RADIUS = bImage.getWidth() / 2.0 - INNER_RADIUS;
                    INNER = new java.awt.geom.Ellipse2D.Double(bImage.getMinX() + FREE_AREA_INNER_RADIUS, bImage.getMinY() + FREE_AREA_INNER_RADIUS, 2 * INNER_RADIUS, 2 * INNER_RADIUS);
                    break;

                case TYPE3:                
                    ORIGIN_CORRECTION = 270.0;
                    ANGLE_STEP = 270.0 / (getMaxValue() - getMinValue());          
                    OUTER_RADIUS = bImage.getWidth() * 0.38f;
                    INNER_RADIUS = bImage.getWidth() * 0.38f - bImage.getWidth() * 0.04f;
                    FREE_AREA_OUTER_RADIUS = bImage.getWidth() / 2.0 - OUTER_RADIUS;
                    FREE_AREA_INNER_RADIUS = bImage.getWidth() / 2.0 - INNER_RADIUS;
                    INNER = new java.awt.geom.Ellipse2D.Double(bImage.getMinX() + FREE_AREA_INNER_RADIUS, bImage.getMinY() + FREE_AREA_INNER_RADIUS, 2 * INNER_RADIUS, 2 * INNER_RADIUS);
                    break;

                case TYPE4:
                    

                default:
                    ORIGIN_CORRECTION = 240.0;
                    ANGLE_STEP = 300.0 / (getMaxValue() - getMinValue());
                    OUTER_RADIUS = bImage.getWidth() * 0.38f;
                    INNER_RADIUS = bImage.getWidth() * 0.38f - bImage.getWidth() * 0.04f;
                    FREE_AREA_OUTER_RADIUS = bImage.getWidth() / 2.0 - OUTER_RADIUS;
                    FREE_AREA_INNER_RADIUS = bImage.getWidth() / 2.0 - INNER_RADIUS;
                    INNER = new java.awt.geom.Ellipse2D.Double(bImage.getMinX() + FREE_AREA_INNER_RADIUS, bImage.getMinY() + FREE_AREA_INNER_RADIUS, 2 * INNER_RADIUS, 2 * INNER_RADIUS);
                    break;
            }

            for (eu.hansolo.steelseries.tools.Section section : getSections())
            {                            
                final double ANGLE_START = ORIGIN_CORRECTION - (section.getStart() * ANGLE_STEP) + (getMinValue() * ANGLE_STEP);
                final double ANGLE_EXTEND = -(section.getStop() - section.getStart()) * ANGLE_STEP;                

                final java.awt.geom.Arc2D OUTER_ARC = new java.awt.geom.Arc2D.Double(java.awt.geom.Arc2D.PIE);
                OUTER_ARC.setFrame(bImage.getMinX() + FREE_AREA_OUTER_RADIUS, bImage.getMinY() + FREE_AREA_OUTER_RADIUS, 2 * OUTER_RADIUS, 2 * OUTER_RADIUS);
                OUTER_ARC.setAngleStart(ANGLE_START);
                OUTER_ARC.setAngleExtent(ANGLE_EXTEND);
                final java.awt.geom.Area SECTION = new java.awt.geom.Area(OUTER_ARC);

                SECTION.subtract(new java.awt.geom.Area(INNER));

                section.setSectionArea(SECTION);
            } 
            
            // Draw the sections
            if (isSectionsVisible() && IMAGE != null)
            {
                final java.awt.Graphics2D G2 = IMAGE.createGraphics();
                G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                for (eu.hansolo.steelseries.tools.Section section : getSections())
                {
                    G2.setColor(section.getColor());
                    G2.fill(section.getSectionArea());
                    if (section3DEffectVisible)
                    {
                        G2.setPaint(section3DEffect);
                        G2.fill(section.getSectionArea());
                    }
                }
                G2.dispose();
            }
        }
    }
    // </editor-fold>    
    
    @Override
    public String toString()
    {
        return "Radial " + getGaugeType();
    } 
}