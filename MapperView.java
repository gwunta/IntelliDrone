/*
 * MapperView.java
 */

package mapper;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
//import java.awt.*;
//import java.io.IOException;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.*;
import java.util.*;
import javax.swing.JFileChooser;
//import com.codeminders.ardrone.commands.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
//import java.util.TimerTask;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;
import com.codeminders.ardrone.*;
import eu.hansolo.steelseries.extras.AirCompass;
import eu.hansolo.steelseries.tools.GaugeType;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * The application's main frame.
 */
public class MapperView extends FrameView {

    private Planner wizard = new Planner();
    final JLabel hoverLabel = new JLabel("Java");
    String[] actions = { "Pickup", "Dropoff", "Return" };
    ArrayList<Task> taskList = new ArrayList<Task>();
    Set<Waypoint> waypoints = new HashSet<Waypoint>();
    int delay = 1000;//for timing of updates on 'instrument panel' GUI
    int timing = 0;  //for mission timer - start time reference
//************ NavData nd = new NavData(); //********** Can only be created when connected to drone.  Remove this comment for testing and implementation
    int instruments = 500;
    Coord waypt1 = new Coord(-31.67899,123.4567);
    int taskNum;
    Options options = new Options();
    ARDrone drone = null;;
    NavDataListener datal;
    NavData data;
      
    //Set up the mission timer
    ActionListener missionTimer = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          String hours="00";
          String mins="00";
          String seconds="00";
          int timed = timing + 1;
          do
          {
              if (timed >= 3600)
              {
                hours = fixTime(timed/3600);
                timed = timed%3600;
              }
              if ((timed >= 60) && timed < 3600)
                  {
                    mins = fixTime(timed/60);
                    timed = timed%60;
                  }
                  if (timed < 60)
                        {
                            seconds = fixTime(timed);
                        }
          }while (timed > 59);
          jLabel25.setText(hours + ":" + mins + ":" + seconds);
          timing = timing + 1;
          
          Random generator = new Random();
          altimeter1.setValue(generator.nextDouble() * 1000);  
          Random generator1 = new Random();
          Random generator2 = new Random();
          horizon1.setPitch(generator1.nextDouble() * -10);
          horizon1.setRoll(generator2.nextDouble() * 10);
          Random generator3 = new Random();
          radial1.setGaugeType(GaugeType.TYPE4);
          radial1.setLcdUnitString("m/sec");
          radial1.setValue(generator3.nextDouble() * 20);
          
          //horizon1.setPitch(data.getPitch() * -1);
          //horizon1.setRoll(data.getRoll());

          //Uncomment the below line, the lnes above are used to test the altimeter only
          //altimeter1.setValue(data.getAltitude() * 1000);
      }
    };
 
    Timer missionTime = new Timer(delay, missionTimer);
    
    public MapperView(SingleFrameApplication app) {
        super(app);

        initComponents(); 
        hoverLabel.setVisible(false);
        jXMapKit1.getMainMap().add(hoverLabel);
        jXMapKit1.setAddressLocation(new GeoPosition(-31.952222,115.858889));
        jXMapKit1.setZoom(1);
        //jXMapKit1.getMainMap().addMouseListener(mouseMotionListener);
        //jXMapKit1.setZoomSliderVisible(false);
        final JLabel hoverLabel = new JLabel("Java");
        hoverLabel.setVisible(false);
        jXMapKit1.getMainMap().add(hoverLabel);
        
        
        
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        }); 
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(0);
        jProgressBar1.setStringPainted(true);
        jButton6.setEnabled(false);
        jLabel15.setText("Awaiting Coordinates");
        jTextArea1.setEnabled(false);
        options.setVisible(false);
        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField6.setEnabled(false);
        jTextField7.setEnabled(false);
        jTextField8.setEnabled(false);
        jTextField4.setText("Awaiting Coordinates");
        jTextField5.setText("Nothing");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");  
        radial1.setUnitString("m/sec");
        radial1.setTitle("Air Speed");
        try
        {
            drone = new ARDrone();
            
            
            //datal.navDataReceived(data);
            //drone.addNavDataListener(datal);
            //datal.navDataReceived(data);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(MapperView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MapperApp.getApplication().getMainFrame();
            aboutBox = new MapperAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MapperApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jXMapKit1 = new org.jdesktop.swingx.JXMapKit();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel23 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        videoPanel1 = new mapper.VideoPanel();
        airCompass2 = new mapper.AirCompass();
        altimeter1 = new mapper.Altimeter();
        horizon1 = new mapper.Horizon();
        radial1 = new mapper.Radial();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        fileChooser = new javax.swing.JFileChooser();

        mainPanel.setName("mainPanel"); // NOI18N

        jXMapKit1.setDefaultProvider(org.jdesktop.swingx.JXMapKit.DefaultProviders.OpenStreetMaps);
        jXMapKit1.setName("jXMapKit1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mapper.MapperApp.class).getContext().getResourceMap(MapperView.class);
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setLabelFor(jTextField1);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setLabelFor(jComboBox1);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pickup", "Dropoff", "Return" }));
        jComboBox1.setName("jComboBox1"); // NOI18N

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        jLabel5.setBackground(resourceMap.getColor("jLabel5.background")); // NOI18N
        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(mapper.MapperApp.class).getContext().getActionMap(MapperView.class, this);
        jButton2.setAction(actionMap.get("addWaypoint_Click")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel6.setFont(resourceMap.getFont("jLabel6.font")); // NOI18N
        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(resourceMap.getFont("jLabel7.font")); // NOI18N
        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setFont(resourceMap.getFont("jLabel8.font")); // NOI18N
        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setFont(resourceMap.getFont("jLabel9.font")); // NOI18N
        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setFont(resourceMap.getFont("jLabel10.font")); // NOI18N
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setFont(resourceMap.getFont("jLabel13.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel17.setFont(resourceMap.getFont("jLabel17.font")); // NOI18N
        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel18.setFont(resourceMap.getFont("jLabel18.font")); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setFont(resourceMap.getFont("jLabel19.font")); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setFont(resourceMap.getFont("jLabel20.font")); // NOI18N
        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setFont(resourceMap.getFont("jLabel21.font")); // NOI18N
        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setFont(resourceMap.getFont("jLabel22.font")); // NOI18N
        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jButton5.setAction(actionMap.get("startMission")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        jButton6.setAction(actionMap.get("abort_Click")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        jButton7.setAction(actionMap.get("openWizard")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        jProgressBar1.setName("jProgressBar1"); // NOI18N

        jButton4.setAction(actionMap.get("getImage")); // NOI18N
        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        jLabel23.setFont(resourceMap.getFont("jLabel23.font")); // NOI18N
        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jTextField3.setFont(resourceMap.getFont("jTextField3.font")); // NOI18N
        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setName("jTextField3"); // NOI18N

        jLabel24.setFont(resourceMap.getFont("jLabel24.font")); // NOI18N
        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        videoPanel1.setName("videoPanel1"); // NOI18N

        javax.swing.GroupLayout videoPanel1Layout = new javax.swing.GroupLayout(videoPanel1);
        videoPanel1.setLayout(videoPanel1Layout);
        videoPanel1Layout.setHorizontalGroup(
            videoPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
        );
        videoPanel1Layout.setVerticalGroup(
            videoPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
        );

        airCompass2.setName("airCompass2"); // NOI18N

        javax.swing.GroupLayout airCompass2Layout = new javax.swing.GroupLayout(airCompass2);
        airCompass2.setLayout(airCompass2Layout);
        airCompass2Layout.setHorizontalGroup(
            airCompass2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        airCompass2Layout.setVerticalGroup(
            airCompass2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        altimeter1.setName("altimeter1"); // NOI18N

        javax.swing.GroupLayout altimeter1Layout = new javax.swing.GroupLayout(altimeter1);
        altimeter1.setLayout(altimeter1Layout);
        altimeter1Layout.setHorizontalGroup(
            altimeter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        altimeter1Layout.setVerticalGroup(
            altimeter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        horizon1.setName("horizon1"); // NOI18N

        javax.swing.GroupLayout horizon1Layout = new javax.swing.GroupLayout(horizon1);
        horizon1.setLayout(horizon1Layout);
        horizon1Layout.setHorizontalGroup(
            horizon1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        horizon1Layout.setVerticalGroup(
            horizon1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        radial1.setName("radial1"); // NOI18N

        javax.swing.GroupLayout radial1Layout = new javax.swing.GroupLayout(radial1);
        radial1.setLayout(radial1Layout);
        radial1Layout.setHorizontalGroup(
            radial1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        radial1Layout.setVerticalGroup(
            radial1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        jTextField4.setFont(resourceMap.getFont("jTextField4.font")); // NOI18N
        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setName("jTextField4"); // NOI18N

        jTextField5.setFont(resourceMap.getFont("jTextField5.font")); // NOI18N
        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setName("jTextField5"); // NOI18N

        jLabel25.setFont(resourceMap.getFont("jLabel25.font")); // NOI18N
        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jTextField6.setFont(resourceMap.getFont("jTextField6.font")); // NOI18N
        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setName("jTextField6"); // NOI18N

        jTextField7.setFont(resourceMap.getFont("jTextField7.font")); // NOI18N
        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setName("jTextField7"); // NOI18N

        jTextField8.setFont(resourceMap.getFont("jTextField8.font")); // NOI18N
        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setName("jTextField8"); // NOI18N

        jButton1.setAction(actionMap.get("testFly")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton8.setAction(actionMap.get("emergencyStop")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(59, 59, 59)
                                        .addComponent(jButton8))
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField5))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25))))
                    .addComponent(jLabel19)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel20)
                                    .addComponent(jButton2))
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton7))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGap(54, 54, 54)
                                        .addComponent(jLabel21)
                                        .addGap(45, 45, 45)
                                        .addComponent(jLabel22))))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jButton4))))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(88, 88, 88)
                                .addComponent(jLabel8))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(1, 1, 1)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(173, 173, 173)
                                .addComponent(jLabel24))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(143, 143, 143)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .addComponent(videoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(airCompass2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(horizon1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(altimeter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(radial1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(100, 100, 100)
                                        .addComponent(jXMapKit1, javax.swing.GroupLayout.PREFERRED_SIZE, 948, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(126, 126, 126)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addGap(186, 186, 186)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(jLabel16))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(jLabel15))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jLabel17)))
                .addGap(3095, 3095, 3095))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(radial1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(altimeter1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(horizon1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(airCompass2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(20, 20, 20)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel4)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel6)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel7)
                                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel9)
                                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10)
                                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel25))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel23)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton2)
                                    .addComponent(jButton7))
                                .addGap(37, 37, 37)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jXMapKit1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17))))
                    .addComponent(videoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setAction(actionMap.get("showOptions")); // NOI18N
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        jMenuItem2.setAction(actionMap.get("export_Log")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        fileMenu.add(jMenuItem2);

        jMenuItem1.setAction(actionMap.get("showOptions")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 5102, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4932, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        fileChooser.setName("fileChooser"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void goPerth() {
    jXMapKit1.setAddressLocation(new GeoPosition(-31.952222,115.858889));
    }

    @Action
    public void addWaypoint() {
    //create a Set of waypoints
    Set<Waypoint> waypoints = new HashSet<Waypoint>();
    waypoints.add(new Waypoint(-31.952222,115.868900));
    waypoints.add(new Waypoint(-31.952222,115.868600));
    
    //crate a WaypointPainter to draw the points
    WaypointPainter painter = new WaypointPainter();
    painter.setWaypoints(waypoints);
    jXMapKit1.getMainMap().setOverlayPainter(painter);
    }

    @Action
    public void openWizard() {
    wizard.setSize(400, 300);
    //wizard.setEnabled(true);
    wizard.setVisible(true);
    wizard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Action
    public void addWaypoint_Click()
    {
        //ArrayList<Task> taskList = new ArrayList<Task>();
        if((jTextField1.getText().isEmpty()) || (jTextField3.getText().isEmpty()) || (jTextField2.getText().isEmpty()))
        {
            return;
        }
        
        //Set up the onHover listener for the newly supplied coordinate
        hoverEvent(Double.parseDouble(this.jTextField1.getText()),Double.parseDouble(this.jTextField3.getText()));
        
        Task task = new Task(Double.parseDouble(this.jTextField1.getText()), Double.parseDouble(this.jTextField3.getText()), this.jComboBox1.getSelectedItem().toString(), this.jTextField2.getText(),"Queued");
        taskList.add(task);
        if(jTextArea1.getText().toString().equals(""))     
        {
            jTextArea1.setText(task.getLat().toString() + "," + task.getLong().toString() + "\t" + task.getAction() + "\t" + task.getStatus() + "\t" + task.getFilename());
        }
        else
        {
            jTextArea1.setText(jTextArea1.getText() + "\n" + task.getLat().toString() + "," + task.getLong().toString() + "\t" + task.getAction() + "\t" + task.getStatus() + "\t" + task.getFilename());
        }      
        jTextField1.setText(null);
        jTextField2.setText(null);
        jTextField3.setText(null);
        
        Iterator<Task> itr = taskList.iterator();
        //Task tempTask = new Task(0.000,0.0000,"0","0","0");
        while(itr.hasNext())
        {
            System.out.println("Number of items = " + taskList.size());
            Task tempTask = itr.next();
            //region.add(new GeoPosition(tempTask.getLat(),task.getLong()));
            waypoints.add(new Waypoint(tempTask.getLat(),task.getLong()));
        }
            
            WaypointPainter painter = new WaypointPainter();
            painter.setWaypoints(waypoints);
            jXMapKit1.getMainMap().setOverlayPainter(painter);
            
        final List<GeoPosition> region = new ArrayList<GeoPosition>();        
        //region.add(new GeoPosition(task.getLat(),task.getLong()));
        
        region.add(new GeoPosition(38.266,12.4));
        region.add(new GeoPosition(38.283,15.65));
        region.add(new GeoPosition(36.583,15.166));
        region.add(new GeoPosition(37.616,12.25));
        
        Painter<JXMapViewer> polygonOverlay = new Painter<JXMapViewer>() {

            public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
                g = (Graphics2D) g.create();
                //convert from viewport to world bitmap
                Rectangle rect = map.getViewportBounds();
                g.translate(-rect.x, -rect.y);

                //create a polygon
                Polygon poly = new Polygon();
                for(GeoPosition gp : region) {
                    //convert geo to world bitmap pixel
                    Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
                    poly.addPoint((int)pt.getX(),(int)pt.getY());
                }

                //do the drawing
                g.setColor(new Color(255,0,0,100));
                g.fill(poly);
                g.setColor(Color.RED);
                g.draw(poly);

                g.dispose();
            }
        };
        
        /*for(GeoPosition gp : region)
        {
             jXMapKit1.setAddressLocation(gp);
        }
        //jXMapKit1.setAddressLocation(taskList.iterator().next().getLat());
    
        WaypointPainter trackPainter = new WaypointPainter();
        //set the waypoints
        painter.setWaypoints(waypoints);
        
        //WaypointPainter painter = new WaypointPainter();
        //painter.setWaypoints(waypoints);
        jXMapKit1.getMainMap().setOverlayPainter(painter);
        //create a renderer
        painter.setRenderer(new WaypointRenderer() {
            public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
                //WikiWaypoint wwp = (WikiMashupView.WikiWaypoint) wp;

                //draw tab
                g.setPaint(new Color(0,0,255,200));
                Polygon triangle = new Polygon();
                triangle.addPoint(0,0);
                triangle.addPoint(11,11);
                triangle.addPoint(-11,11);
                g.fill(triangle);
                int width = 20;
                g.fillRoundRect(-width/2 -5, 10, width+10, 20, 10, 10);

                //draw text w/ shadow
                g.setPaint(Color.BLACK);
                //g.drawString(wwp.getTitle(), -width/2-1, 26-1); //shadow
                //g.drawString(wwp.getTitle(), -width/2-1, 26-1); //shadow
                g.setPaint(Color.WHITE);
                //g.drawString(wwp.getTitle(), -width/2, 26); //text
                return false;
            }
        }); 
        jXMapKit1.getMainMap().setOverlayPainter(painter);
        jXMapKit1.getMainMap().repaint();  */
    }

    @Action
    public void getImage()
    {
        JFileChooser fc = new JFileChooser();

    //Add a custom file filter and disable the default
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);

    //Add custom icons for file types.
        fc.setFileView(new ImageFileView());
    //Add the preview pane.
        fc.setAccessory(new ImagePreview(fc));
        int returnVal = fc.showDialog(fc,"Select");
        if (returnVal == fc.APPROVE_OPTION) 
        {
            File file = fc.getSelectedFile();
            jTextField2.setText (file.getAbsolutePath());
        }
        fc.setSelectedFile(null);
    }

    @Action
    public void startMission()
    {
        if(taskList.isEmpty())
        {
            JOptionPane.showMessageDialog(mainPanel, "You need to enter some coordinates before the flight can begin", "IntelliDrone Control Station Error",JOptionPane.ERROR_MESSAGE);
            //Dialog msg = new Dialog();
            //showMessageDialog("You need to enter some coordinates before the flight can begin");
            return;
        }
        //Disable the Start Button, otherwise problems can arise
        jButton5.setEnabled(false);
        jButton6.setEnabled(true);
        //Get the current GPS coords of the drone - this will be sourced from the GPS serial data
        waypt1.setLat(-123.4567);
        waypt1.setLon(345.6789);
        
        //Start Telnet communication
        /*try
        {
            Telnetter telnet = new Telnetter("192.168.0.1","username","password");
            telnet.sendCommand("ls ");
            telnet.disconnect();
        } catch (Exception e) 
            {
                e.printStackTrace();
            }
         */
        //Begin running mission timer
        
        //new Timer(delay, missionTimer).start();
        missionTime.start();
        
        //Update status
        jTextField4.setText("Searching for item");
        
        //Update item on board
        //jLabel16.setText("Nothing");
        
        //Update Drone Status
        jLabel18.setText("Flying");
        
  //Heading reader for 'instrument panel' on the GUI
   ActionListener headingTracker = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          Coord b = new Coord(taskList.get(taskNum).getLat(),taskList.get(taskNum).getLong());
          jTextField6.setText(Double.toString(waypt1.headingTo(b)));
          
          Random generator = new Random();
          //double heading = generator.nextDouble() * 360;
          airCompass2.setValueAnimated(generator.nextDouble() * 360);
          /// Comment out the above three lines and use the one below, these are in for testing animation of the compass only
          
          //create navdata listener and provide a stream of data from the drone that we can do something with
          //airCompass2.setValueAnimated(data.getYaw());
          
      }
  };
  new Timer(delay*3, headingTracker).start();
  
  //Speed reader for 'instrument panel' on the GUI
  ActionListener speedTracker = new ActionListener() {
      public void actionPerformed(ActionEvent evt)  {
          Coord b = new Coord(taskList.get(taskNum).getLat(),taskList.get(taskNum).getLong());
          Coord now = new Coord();
          now.setLat(123.45566);
          now.setLon(1234.2434);
          double dist = now.distTo(b);
          /*try
          {
            Thread.sleep(3000); //change this number if wanting to wait longer between distance readings - this may be too short
          }
          catch(InterruptedException ie)
          {
              System.out.println("I caught an execption, its a big one!!!");
          } */
          double dist2 = now.distTo(b);
          jTextField7.setText(Double.toString((dist-dist2)/3) + " m/sec");   //change /3 to match the number at Thread.Sleep
      }
  };
  new Timer((instruments), speedTracker).start();
  ActionListener batteryTracker = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
          jProgressBar1.setString("50%");
          jProgressBar1.setValue(50);
    
      /* Uncomment the lines below for use with the drone. The lines above are for testing
          jProgressBar1.setString(Integer.toString(nd.getBattery()) + "%");
          jProgressBar1.setValue(nd.getBattery());   */      
      }
  };
  new Timer(delay*5, batteryTracker).start();    
  
   ActionListener altTracker = new ActionListener() {
      public void actionPerformed(ActionEvent evt) {        
         
          jTextField8.setText("2m") ;
      //********Uncomment the line below for use with the drone. The lines above are for testing *******
          //jLabel14.setText(nd.getAltitude() + "m");                  
      }
  };
  new Timer(instruments, altTracker).start();      
  
      }
    
    String fixTime(int tim)
    {
        String fixed;
        if(tim < 10)
        {
               fixed = "0" + Integer.toString(tim);
        }
        else
        {
            fixed = Integer.toString(tim);
        }
        return fixed;
    }

    @Action
    public void abort_Click()
    {
        jButton5.setEnabled(true);
        jButton6.setEnabled(false);
        missionTime.stop();
        jLabel18.setText("Returning to launch location");
    }

    @Action
    public void showOptions()
    {
            options.setVisible(true);
            
    }

    @Action
    public void export_Log()
    {
        try{
          // Create file
          FileWriter fstream;
          if(options.getFilename().isEmpty())
          {
              fstream = new FileWriter("C:\\");
          }
          else 
          {
              fstream = new FileWriter(options.getFilename());
          }
          BufferedWriter out = new BufferedWriter(fstream);
          String output = "   COORDINATES   ACTION \t IMAGE ";
          out.write(output);
          out.flush();
          output = "\n --------------------------------------------------------------";
          out.write(output);
          out.flush();
          Iterator<Task> itr = taskList.iterator();
          //Task tempTask = new Task(0.000,0.0000,"0","0","0");
          while(itr.hasNext())
          {
              //System.out.println("Number of items = " + taskList.size());
              output = "";
              Task tempTask = itr.next();
              output = output + (Double.toString(tempTask.getLat()) + "," + (Double.toString(tempTask.getLong())) + "  ");
              output = output + tempTask.getAction() + "\t";
              output = output + tempTask.getFilename() + "\n";
              out.write(output);
              out.flush();
          }
          out.flush();
          output = "\n ---------------------------------------------------------------";
          out.write(output);
          out.flush();
          output = "Mission Duration: " + jLabel25.getText() + "\n";
          out.write(output);
          out.flush();
          output = "Item On Board: " + jTextField5.getText() + "\n";
          out.write(output);
          out.flush();
          output = "Heading: " + jTextField6.getText() + "\n" ;
          out.write(output);
          out.flush();
          //Close the output stream
          out.close();
          }catch (Exception e){//Catch exception if any
          System.err.println("Error: " + e.getMessage());
          }
          JOptionPane.showMessageDialog(mainPanel, "Export file successfully created", "IntelliDrone Control Station",JOptionPane.PLAIN_MESSAGE);
        //File f = new File(options.getFilename());
    }
    
     //This is supposed to add an onHover event to the geomarker to show the contents of a jLabel
     // but it doesnt seem to work at runtime
    public void hoverEvent(final double lat, final double lng)
    {
        hoverLabel.setVisible(false);
            jXMapKit1.getMainMap().add(hoverLabel);
            jXMapKit1.getMainMap().addMouseMotionListener(new MouseMotionListener(){
                public void mouseDragged(MouseEvent e){}
                public void mouseMoved(MouseEvent e){
                    JXMapViewer map = jXMapKit1.getMainMap();
                    GeoPosition gp = new GeoPosition(lat,lng);
                    Point2D gp_pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
                    Rectangle rect = map.getViewportBounds();
                    Point converted_gp_pt = new Point((int)gp_pt.getX()-rect.x, (int)gp_pt.getY()-rect.y);
                    if(gp_pt.distance(e.getPoint()) < 50)
                    {
                        hoverLabel.setLocation((int)gp_pt.getX()-rect.x, (int)gp_pt.getY()-rect.y);
                        hoverLabel.setVisible(true);
                    } 
                    else
                    {
                        hoverLabel.setVisible(false);
                    }   
                }
            });
    }

    @Action
    public void testFly() throws IOException
    {
        FlightTest tester = new FlightTest(drone);
        tester.run();
        //drone.takeOff();
        //drone.hover();
        try
        {
            Thread.sleep(8000);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(MapperView.class.getName()).log(Level.SEVERE, null, ex);
        }
        drone.land();
    }

    @Action
    public void emergencyStop() throws IOException
    {
        drone.sendEmergencySignal();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private mapper.AirCompass airCompass2;
    private mapper.Altimeter altimeter1;
    private javax.swing.JFileChooser fileChooser;
    private mapper.Horizon horizon1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private org.jdesktop.swingx.JXMapKit jXMapKit1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private mapper.Radial radial1;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private mapper.VideoPanel videoPanel1;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    
}
