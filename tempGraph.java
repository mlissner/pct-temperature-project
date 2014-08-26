/**
 * Program Name: tempGraph.java
 * Author: Michael Lissner
 * Date: 11/30/07
 * Purpose: To create an applet with two display areas. The first allows a user to
 *          select a date and time range for a selection of hikers. The second displays
 *          a graph of the temperature during that time and date range for the hikers
 *          selected. The graphing area is dynamic to adjust to both the temperature and the 
 *          date range requested.
 */

/**
 * TODO: 1. Make it receive parameters for the dimensions, and adjust dynamically from there.
 *           --> Done. Chart will adjust to dynamic applet settings correctly without receiving parameters.
 *       2. Add a popup menu to allow export to text file, and Show/Hide Gridlines
 *       3. Make the date labels and gridlines dynamic according to how much space there are between the gridlines
 *           --> Done. Labels and gridlines are dynamic according to user selections
 *       4. Update the destroy method
 *           --> Done. destroy() method will kill all remaining variables.
 *       5. Make the background on radio and check boxes white!
 *           --> Done. setBackground() method used on JComponents.
 *       6. Make program operation more object oriented by abstracting lines into objects (v2).
 *       7. Optimize program operation by eliminating array copying as much as possible, 
 *          and eliminating unnecessary variables and redundant code (v2).
 */

//package Unit10;

//Import statements without expansion for optimization
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.net.URL;
import java.io.InputStreamReader;


public class tempGraph extends JApplet implements Serializable, ActionListener {
    //Create all variables up front
    private static final long serialVersionUID = -6798266313901670108L;
    //3,816 is the key number. It represents the total span of hours during which any one of the hikers was on the trail.
    private final int timeSpan = 4296;
    //These values represent the number of hours after April 1st that each hiker began.
    final int aBradleyIndex = 1056;
    final int mChurchIndex = 648;
    final int rFranciscoIndex = 576;
    final int mLissnerIndex = 480; 
    final int mMummertIndex = 774;
    final int jSingewaldIndex = 504;
    //And these represent the number of hours after midnight on 9/26 that each hiker ended.
    final int aBradleyEndIndex = 48;
    final int mChurchEndIndex = 2304;
    final int rFranciscoEndIndex = 0; //Last hiker to leave the trail, hence value of zero.
    final int mLissnerEndIndex = 336;
    final int mMummertEndIndex = 48;
    final int jSingewaldEndIndex = 480;
    private JPanel settingsPanel, northSettingsPanel, centerSettingsPanel, 
        southSettingsPanel, graphPanel, titlePanel;
    private Border blackLine;
    private GridBagConstraints c1, c2, c3;
    private Insets i1, i2, i3;
    private JLabel title, dateRange, startDateLabel, endDateLabel, hourRange, hiker;
    private JTextField startDateField, endDateField;
    private JRadioButton nightButton, dayButton, afternoonButton, morningButton, allDayButton;
    private JCheckBox hikerOne, hikerTwo, hikerThree, hikerFour, hikerFive, hikerSix;
    private ButtonGroup dayLengthsButtonGroup;
    private JButton generateButton;
    private String line = null;
    private String[] userStartDate, userEndDate = null;
    private float[] rangedHikerOneArray = new float[timeSpan];
    private float[] rangedHikerTwoArray = new float[timeSpan];
    private float[] rangedHikerThreeArray = new float[timeSpan];
    private float[] rangedHikerFourArray = new float[timeSpan];
    private float[] rangedHikerFiveArray = new float[timeSpan];
    private float[] rangedHikerSixArray = new float[timeSpan];
    private float[] hikerOneArray = new float[timeSpan];
    private float[] hikerTwoArray= new float[timeSpan];
    private float[] hikerThreeArray= new float[timeSpan];
    private float[] hikerFourArray= new float[timeSpan];
    private float[] hikerFiveArray= new float[timeSpan];
    private float[] hikerSixArray = new float[timeSpan]; 
    private int height, width, userStartMonth, userStartDay, userStartYear, userEndMonth, userEndDay, userEndYear;
    private boolean actionPerformedBoo, hikerOneBoo, hikerTwoBoo, hikerThreeBoo, hikerFourBoo, hikerFiveBoo,
        hikerSixBoo, allDayBoo, afternoonBoo, nightBoo, morningBoo, dayBoo; //year variables included for future versions
    private float mean;
    
    
    /*
     * Methods below this point create and destroy the applet 
     */
    //Get things rolling
    public void init() {
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                    setListeners();
                }
            });
        }
        catch (Exception e) {
            System.err.println("createGUI or setListeners didn't successfully complete");
        }
    }
    
    
    //Remove all components and set variables to null
    public void destroy() {
        //Execute a job on the event-dispatching thread:
        //taking the text field out of this applet.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    remove(settingsPanel);
                    remove(northSettingsPanel);
                    remove(centerSettingsPanel);
                    remove(southSettingsPanel);
                    remove(graphPanel);
                    remove(titlePanel);
                    remove(title);
                    remove(dateRange);
                    remove(startDateLabel);
                    remove(endDateLabel);
                    remove(hourRange);
                    remove(hiker);
                    remove(startDateField);
                    remove(endDateField);
                    remove(nightButton);
                    remove(dayButton);
                    remove(afternoonButton);
                    remove(morningButton);
                    remove(allDayButton);
                    remove(hikerOne);
                    remove(hikerTwo);
                    remove(hikerThree);
                    remove(hikerFour);
                    remove(hikerFive);
                    remove(hikerSix);
                    remove(generateButton);
                }
            });
        } catch (Exception e) {
            System.err.println("cleanUp didn't successfully complete");
        }
        settingsPanel = null;
        northSettingsPanel = null;
        southSettingsPanel = null;
        centerSettingsPanel = null;
        graphPanel = null;
        titlePanel = null;
        blackLine = null;
        c1 = c2 = c3 = null;
        i1 = i2 = i3 = null;
        title = null;
        dateRange = null;
        startDateLabel = null;
        endDateLabel = null;
        hourRange = null;
        hiker = null;
        startDateField = null;
        endDateField = null;
        nightButton = null;
        dayButton = null;
        afternoonButton = null;
        morningButton = null;
        allDayButton = null;
        hikerOne = null;
        hikerTwo = null;
        hikerThree = null;
        hikerFour = null;
        hikerFive = null;
        hikerSix = null;
        dayLengthsButtonGroup = null;
        generateButton = null;
        line = null;
        userStartDate = null;
        userEndDate = null;
    }
    
    
    
    /*
     * Methods below this point create the interfaces and 
     * layout the rest of the applet
     */
    //Get things really rolling
    private void createGUI() {
        //Create the layout
        getContentPane().setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 400));
        
        //Make a useful border pattern
        blackLine = BorderFactory.createLineBorder(Color.DARK_GRAY);
        
        //Make the two panels
        createTitlePanel();
        createSettingsPanel();
        
        //Set up the graphing panel
        graphPanel = new JPanel();
        
        graphPanel.setPreferredSize(new Dimension(500, 375));
        getContentPane().add(graphPanel, BorderLayout.CENTER);
    }
    
   
    //Make the titlePanel and call makeTitle() to make the dynamic title
    public void createTitlePanel() {
        titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(750, 25));
        getContentPane().add(titlePanel, BorderLayout.PAGE_START);
        
        //Make and set the title label default value
        title = new JLabel();
        title.setText("PCT Temperature Graphing Tool");

        titlePanel.add(title);
        titlePanel.setBackground(Color.white);
        titlePanel.setBorder(blackLine);
    }
    
  
    //A helper method to make and set the settingsPanel
    public void createSettingsPanel() {
        layoutSettingsPanels();
        createDateRangeInterface();
        createHourRangeInterface();
        createHikerChoiceInterface();
    }
    
     
    //Create three panels to hold the interface sections, and put them in a fourth panel
    public void layoutSettingsPanels() {
        settingsPanel = new JPanel();
        settingsPanel.setPreferredSize(new Dimension(200, 375));
        getContentPane().add(settingsPanel, BorderLayout.LINE_START);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
        settingsPanel.setBackground(Color.white);
        settingsPanel.setBorder(blackLine);
        
        northSettingsPanel = new JPanel();
        northSettingsPanel.setLayout(new GridBagLayout());
        northSettingsPanel.setBackground(Color.white);
        settingsPanel.add(northSettingsPanel);
        
        centerSettingsPanel = new JPanel();
        centerSettingsPanel.setLayout(new GridBagLayout());
        centerSettingsPanel.setBackground(Color.white);
        settingsPanel.add(centerSettingsPanel);
        
        southSettingsPanel = new JPanel();
        southSettingsPanel.setLayout(new GridBagLayout());
        southSettingsPanel.setBackground(Color.white);
        settingsPanel.add(southSettingsPanel);
    }
    
    
    //Create the date range interface using the gridBagLayout
    public void createDateRangeInterface() {
        c1 = new GridBagConstraints();
        i1 = new Insets(0, 8, 3, 8);
        c1.insets = i1;
        c1.ipady = -2;
        
        dateRange = new JLabel("<html><u><strong>Choose Date Range</strong></u></html>");
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 2;
        c1.fill = GridBagConstraints.HORIZONTAL;
        dateRange.setHorizontalAlignment(JLabel.LEFT);
        northSettingsPanel.add(dateRange, c1);
        
        startDateLabel = new JLabel("Start Date: ");
        startDateLabel.setHorizontalAlignment(JLabel.LEFT);
        c1.gridx = 0;
        c1.gridy = 1;
        c1.gridwidth= 1;
        c1.weightx = 0.1;
        northSettingsPanel.add(startDateLabel, c1);
        
        startDateField = new JTextField(8);
        startDateField.setText("MM/DD/YY");
        c1.gridx = 1;
        c1.gridy = 1;
        c1.weightx = 0.7;
        northSettingsPanel.add(startDateField, c1);
        
        endDateLabel = new JLabel("End Date: ");
        endDateLabel.setHorizontalAlignment(JLabel.LEFT);
        c1.gridx = 0;
        c1.gridy = 2;
        c1.weightx = 0.1;
        northSettingsPanel.add(endDateLabel, c1);
        
        endDateField = new JTextField(8);
        endDateField.setText("MM/DD/YY");
        c1.gridx = 1;
        c1.gridy = 2;
        c1.weightx = 0.7;
        c1.fill = GridBagConstraints.HORIZONTAL;
        northSettingsPanel.add(endDateField, c1);
    }

    
    //Create the hour range interface with a gridbaglayout
    public void createHourRangeInterface() {
        c2 = new GridBagConstraints();
        i2 = new Insets(0, 8, 0, 8);
        c2.insets = i2;
        c2.weightx = 0.7;
        
        hourRange = new JLabel("<html><u>Choose Hour Range</u></html>");
        c2.gridx = 0;
        c2.gridy = 0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        centerSettingsPanel.add(hourRange, c2); 
        
        allDayButton = new JRadioButton("Entire Day");
        allDayButton.setMnemonic(KeyEvent.VK_E);
        allDayButton.setBackground(Color.white);
        allDayButton.setSelected(true);
        allDayButton.setHorizontalAlignment(JRadioButton.LEFT);
        c2.gridx = 0;
        c2.gridy = 1;
        c2.ipady = -7;
        centerSettingsPanel.add(allDayButton, c2);
        
        dayButton = new JRadioButton("Days (6am-8pm)");
        dayButton.setMnemonic(KeyEvent.VK_D);
        dayButton.setBackground(Color.white);
        c2.gridx = 0;
        c2.gridy = 2;
        centerSettingsPanel.add(dayButton, c2);
        
        nightButton = new JRadioButton("Nights (8pm-6am)");
        nightButton.setMnemonic(KeyEvent.VK_N);
        nightButton.setBackground(Color.white);
        c2.gridx = 0;
        c2.gridy = 3;
        centerSettingsPanel.add(nightButton, c2);
        
        afternoonButton = new JRadioButton("Afternoons (1pm-4pm)");
        afternoonButton.setMnemonic(KeyEvent.VK_A);
        afternoonButton.setBackground(Color.white);
        c2.gridx = 0;
        c2.gridy = 4;
        centerSettingsPanel.add(afternoonButton, c2);
        
        morningButton = new JRadioButton("Mornings (8am-11am)");
        morningButton.setMnemonic(KeyEvent.VK_M);
        morningButton.setBackground(Color.white);
        c2.gridx = 0;
        c2.gridy = 5;
        centerSettingsPanel.add(morningButton, c2);
        
        //Group the radio buttons.
        dayLengthsButtonGroup = new ButtonGroup();
        dayLengthsButtonGroup.add(allDayButton);
        dayLengthsButtonGroup.add(dayButton);
        dayLengthsButtonGroup.add(nightButton);
        dayLengthsButtonGroup.add(afternoonButton);
        dayLengthsButtonGroup.add(morningButton);
    }
    
    
    //Create the hiker selection interface using the gridBagLayout
    public void createHikerChoiceInterface() {
        c3 = new GridBagConstraints();
        i3 = new Insets(0, 8, 0, 8);
        c3.insets = i3;
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.weightx = 1.0;
        hiker = new JLabel("<html><u><strong>Choose Hiker(s)</strong></u></html>");
        hiker.setHorizontalAlignment(JLabel.LEFT);
        c3.gridx = 0;
        c3.gridy = 0;
        southSettingsPanel.add(hiker, c3);
        
        c3.ipady = -7;
        
        hikerOne = new JCheckBox("<html><font color=\"black\">Adam Bradley '05</font><html>");
        hikerOne.setBackground(new Color(255,85,99));
        hikerOne.setMnemonic(KeyEvent.VK_D);
        c3.gridx = 0;
        c3.gridy = 1;
        southSettingsPanel.add(hikerOne, c3);
        
        hikerTwo = new JCheckBox("<html><font color=\"black\">Matt Church '05</font><html>");
        hikerTwo.setBackground(new Color(105,159,154));
        hikerTwo.setMnemonic(KeyEvent.VK_C);
        c3.gridx = 0;
        c3.gridy = 2;
        southSettingsPanel.add(hikerTwo, c3);
        
        hikerThree = new JCheckBox("<html><font color=\"black\">Robert Francisco '05</font><html>");
        hikerThree.setBackground(new Color(45,220,0));
        hikerThree.setMnemonic(KeyEvent.VK_R);
        c3.gridx = 0;
        c3.gridy = 3;
        southSettingsPanel.add(hikerThree, c3);
        
        hikerFour = new JCheckBox("<html><font color=\"black\">Mike Lissner '04</font><html>");
        hikerFour.setBackground(new Color(255,0,220));
        hikerFour.setMnemonic(KeyEvent.VK_L);
        c3.gridx = 0;
        c3.gridy = 4;
        southSettingsPanel.add(hikerFour, c3);
        
        hikerFive = new JCheckBox("<html><font color=\"black\">Matt Mummert '05</font><html>");
        hikerFive.setBackground(new Color(0,119,255));
        hikerFive.setMnemonic(KeyEvent.VK_U);
        c3.gridx = 0;
        c3.gridy = 5;
        southSettingsPanel.add(hikerFive, c3);
        
        hikerSix = new JCheckBox("<html><font color=\"black\">Jeff Singewald '05</font><html>");
        hikerSix.setBackground(new Color(255,136,0));
        hikerSix.setMnemonic(KeyEvent.VK_S);
        c3.gridx = 0;
        c3.gridy = 6;
        southSettingsPanel.add(hikerSix, c3);
        
        generateButton = new JButton("Generate Graph!");
        c3.gridx = 0;
        c3.gridy = 7;
        i3.top = 10;
        southSettingsPanel.add(generateButton, c3);
    }
   
    
    
    /*
     * Methods below this point listen for and act upon action events
     */
    //Set listeners for all items
    public void setListeners() {
        generateButton.addActionListener(this);
    }

    
    //Gather variables from the user interface
    public void actionPerformed(ActionEvent e) {
        actionPerformedBoo = true;
        
        //Set up variables for the date range
        userStartDate = startDateField.getText().split("/");
        userEndDate = endDateField.getText().split("/");
        try {
            userStartMonth = Integer.parseInt(userStartDate[0]);
            userStartDay = Integer.parseInt(userStartDate[1]);
            userStartYear = Integer.parseInt(userStartDate[2]);
            userEndMonth = Integer.parseInt(userEndDate[0]);
            userEndDay = Integer.parseInt(userEndDate[1]);
            userEndYear = Integer.parseInt(userEndDate[2]);
            dateRange.setText("<html><u><strong>Choose Date Range</strong></u></html>");

            title.setText("PCT Temperature for " + startDateField.getText() + " to " + endDateField.getText());
        }
        catch (Exception ex) {
            dateRange.setText("<html><u><font color=\"maroon\">Enter Valid Dates!</font></u></html>");
            return;
        }
        
        //Determine the hour range selected
        if(allDayButton.isSelected()) {
            allDayBoo = true;
        } else allDayBoo = false;
        if (dayButton.isSelected()) {
            dayBoo = true;
        } else dayBoo = false;
        if (nightButton.isSelected()) {
            nightBoo = true;
        } else nightBoo = false;
        if (afternoonButton.isSelected()) {
            afternoonBoo = true;
        } else afternoonBoo = false;
        if (morningButton.isSelected()) {
            morningBoo = true;
        } else morningBoo = false;
        
        
        //Set the variables for the hikers chosen, and pass 
        //the information to the date setter method
        if (hikerOne.isSelected() ==  false &&
            hikerTwo.isSelected() == false &&
            hikerThree.isSelected() == false &&
            hikerFour.isSelected() == false &&
            hikerFive.isSelected() == false &&
            hikerSix.isSelected() == false )
        {
            hiker.setText("<html><u><font color=\"maroon\">Choose at Least One Hiker!</font></u></html>");
            return;
        }
        else if (hikerOne.isSelected() ||
                 hikerTwo.isSelected() ||
                 hikerThree.isSelected() ||
                 hikerFour.isSelected() ||
                 hikerFive.isSelected() ||
                 hikerSix.isSelected() ) 
        {
            hiker.setText("<html><u>Choose Hiker(s)</u></html>");
            if (hikerOne.isSelected()) {
                hikerOneBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-AdamBradley-2.csv", aBradleyIndex, aBradleyEndIndex),
                                 0, hikerOneArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerOneArray, aBradleyIndex, aBradleyEndIndex), 
                                 0, rangedHikerOneArray, 0, computeNumValues());
            } else {
                hikerOneBoo = false;
                //rangedHikerOneArray = null; //Necessary to empty the array between computations.
            }

            if (hikerTwo.isSelected()) {
                hikerTwoBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-MattChurch-2.csv", mChurchIndex, mChurchEndIndex),
                                 0, hikerTwoArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerTwoArray, mChurchIndex, mChurchEndIndex), 
                                 0, rangedHikerTwoArray, 0, computeNumValues());
            } else {
                hikerTwoBoo = false;
                //rangedHikerTwoArray = null;
            }

            if (hikerThree.isSelected()) {
                hikerThreeBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-RobertFrancisco-1.csv", rFranciscoIndex, rFranciscoEndIndex),
                                 0, hikerThreeArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerThreeArray, rFranciscoIndex, rFranciscoEndIndex), 
                                 0, rangedHikerThreeArray, 0, computeNumValues());
            } else { 
                hikerThreeBoo = false;
                //rangedHikerThreeArray = null;
            }

            if (hikerFour.isSelected()) {
                hikerFourBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-MichaelLissner-1.csv", mLissnerIndex, mLissnerEndIndex), 
                                 0, hikerFourArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerFourArray, mLissnerIndex, mLissnerEndIndex), 
                                 0, rangedHikerFourArray, 0, computeNumValues());
            } else { 
                hikerFourBoo = false;
                //rangedHikerFourArray = null;
            }

            if (hikerFive.isSelected()) {
                hikerFiveBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-MattMummert-1.csv", mMummertIndex, mMummertEndIndex), 
                                 0, hikerFiveArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerFiveArray, mMummertIndex, mMummertEndIndex), 
                                 0, rangedHikerFiveArray, 0, computeNumValues());
            } else { 
                hikerFiveBoo = false;
                //rangedHikerFiveArray = null;
            }

            if (hikerSix.isSelected()) {
                hikerSixBoo = true;
                System.arraycopy(makeFile("http://www.michaeljaylissner.com/archive/java/iButtonResults-JeffSingewald-1.csv", jSingewaldIndex, jSingewaldEndIndex), 
                                 0, hikerSixArray, 0, timeSpan);
                System.arraycopy(setRanges(hikerSixArray, jSingewaldIndex, jSingewaldEndIndex), 
                                 0, rangedHikerSixArray, 0, computeNumValues());
            } else {
                hikerSixBoo = false;
                //rangedHikerSixArray = null;
            }
        }
        repaint();
    }
   

    
    /*
     * Methods below this point are used to paint
     * in the graph area. 
     */
    //Paint the graph and all else
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        super.paint(g); //I'm not sure why I need this...
        
        //Move the origin to a useful location
        g.translate(200, 25);
        
        //Set useful variables
        height = graphPanel.getBounds().height;//375; //graphPanel.getHeight();
        width = graphPanel.getBounds().width;//550; //graphPanel.getWidth();
        
        //Set the background to the default color
        g.clearRect(0, 0, width, height);
        g.drawLine(width-1, height-1, width-1, 0);
        g.drawLine(width-1, height-1, 0, height-1);
        
        //Set the default font
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        
        //Tell the user that something is happening
        updateFeedback("Applet Loading", g);
        
        //Paint the graph
        printXAxis(g);
        
        //Graph the lines if an action has been performed.
        if (actionPerformedBoo) {
            //Give the user an update
            removeFeedback(g);
            updateFeedback("Loading lines", g);
            
            chartLines(g2, rangedHikerOneArray, rangedHikerTwoArray, 
                       rangedHikerThreeArray, rangedHikerFourArray,
                       rangedHikerFiveArray, rangedHikerSixArray);
        }
        
        //Compute and print the mean temperature for the graph
        updateMean(g2);
        
        //Clear any remaining notices to the user
        removeFeedback(g);
    }

    
    //A method to call whenever feedback to the user needs to be printed
    public void updateFeedback(String output, Graphics g) {
        //Get the current color, in case it isn't red
        Color tempColor = g.getColor();
        
        //Set the color to red and draw the string
        g.setColor(Color.red);
        g.drawString(output, 0, 10);
        
        //Reset the color to what it was before the method began
        g.setColor(tempColor);
    }
    
    
    //A method to remove feedback from the screen
    public void removeFeedback(Graphics g) {
        //Get the current color, so we can set it back momentarily
        Color tempColor = g.getColor();
        
        g.setColor(new Color(178,234,255));
        g.fillRect(0, 0, 400, 11);
        
        //Reset the color to what it was before the method began
        g.setColor(tempColor);
    }


    //A method to print the mean for any graph. To be run after the mean is computed or changed.
    public void updateMean(Graphics2D g2) {
        //Get the current color, so we can set it back momentarily
        Color tempColor = g2.getColor();

        //Format and set the meanString variable     
        DecimalFormat df1 = new DecimalFormat("###.#");
        String meanString = "Mean: " + df1.format(mean);
        
        //Measure the size of the string that's getting printed
        FontMetrics fm = getFontMetrics(g2.getFont());
        int w = fm.stringWidth(meanString);
        
        //Draw it with right justification
        g2.setColor(new Color(178,234,255));
        g2.fillRect(400, 0, width-1-400, 11);
        g2.setColor(Color.black);
        g2.drawString(meanString, width-3-w, 10);
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(0, 11, width-1, 11);

        //Reset the color to what it was before the method began
        g2.setColor(tempColor);
    }
    
    
    //A method to print the y axis values and lines
    public void printYAxis(Graphics2D g2, int max, int min) {
        //Get the current color, so we can set it back momentarily
        Color tempColor = g2.getColor();
        g2.setStroke(new BasicStroke());

        //Format and set the meanString variable     
        DecimalFormat df1 = new DecimalFormat("###.0");
        String yString = "Nothing yet";
        
        for (int i = 1; i <= 10; i++) {
            double yValue = max - (i-1)*((max - min)/9.5);
            yString = df1.format(yValue);
            
            FontMetrics fm = getFontMetrics(g2.getFont());
            int w = fm.stringWidth(yString);
            
            //Set the color to vary from red to blue
            g2.setColor(new Color(25*(10/i),0,255*(i/10)));
            
            g2.drawString(yString, width-3-w, 11 + i * ((height-22)/11));
            
            g2.setColor(new Color(222,222,222));
            g2.drawLine(0, 11+i * ((height-22)/11), width-2, 11 + i * ((height-22)/11));
            
        }

        //Reset the color to what it was before the method began
        g2.setColor(tempColor);
    }
    
    
    //A method to print the x axis values and lines
    public void printXAxis(Graphics g) {
        //Get the current color before the method began
        Color tempColor = g.getColor();
        
        //Fill the back to make it pretty
        g.setColor(new Color(178,234,255));
        g.fillRect(0, height-10, width-1, 9);       
        
        //Reset the color and print the line
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, height-11, width-1, height-11);
        
        //Draw the start and end dates
        drawStartDate(g);
        drawEndDate(g);
        
        //Set the color back to what it was
        g.setColor(tempColor);
    }
        
    
    //A method to draw the starting date
    public void drawStartDate(Graphics g) {
        //Get the current color, so we can set it back momentarily
        Color tempColor = g.getColor();
        
        String dateString = "";
        
        //Set the dateString to hold the start date
        if (userStartMonth == 4) dateString = "Apr. " + userStartDay;
        else if (userStartMonth == 5) dateString = "May " + userStartDay;
        else if (userStartMonth == 6) dateString = "June " + userStartDay;
        else if (userStartMonth == 7) dateString = "July " + userStartDay;
        else if (userStartMonth == 8) dateString = "Aug. " + userStartDay;
        else if (userStartMonth == 9) dateString = "Sep. " + userStartDay;
        
        //Draw the String
        g.setColor(Color.black);
        g.drawString(dateString, 2, height-2);
        
        //Reset the color to what it was before the method began
        g.setColor(tempColor);
    }
    
    
    //A method to draw the ending date
    public void drawEndDate(Graphics g) {
        //Get the current color, so we can set it back momentarily
        Color tempColor = g.getColor();
        
        String dateString = "";
        
        //Set the dateString to hold the end date
        if (userEndMonth == 4) dateString = "Apr. " + userEndDay;
        else if (userEndMonth == 5) dateString = "May " + userEndDay;
        else if (userEndMonth == 6) dateString = "June " + userEndDay;
        else if (userEndMonth == 7) dateString = "July " + userEndDay;
        else if (userEndMonth == 8) dateString = "Aug. " + userEndDay;
        else if (userEndMonth == 9) dateString = "Sep. " + userEndDay;
        
        //Measure the size of the string that's getting printed
        FontMetrics fm = getFontMetrics(g.getFont());
        int w = fm.stringWidth(dateString);
        
        //Draw it with right justification
        g.setColor(Color.black);
        g.drawString(dateString, width-w-3, height-2);
        
        //Reset the color to what it was before the method began
        g.setColor(tempColor);
    }
    
    
    /*  
     *  Create and chart the actual data lines requested.
     *  This method must take all of the arrays as arguments
     *  because it needs to compute their locations in the context of
     *  each other.
     */ 
    public void chartLines(Graphics2D g2, float[] rangedHikerOneArray, float[] rangedHikerTwoArray, 
                           float[] rangedHikerThreeArray, float[] rangedHikerFourArray,
                           float[] rangedHikerFiveArray, float[] rangedHikerSixArray) {
        //Get the current color, so we can set it back at end of method
        Color tempColor = g2.getColor();
        
        //Get the max, min and mean, updating labels as appropriate.
        //This could take a few seconds...give the user an update first
        removeFeedback(g2);
        updateFeedback("Computing...", g2);
        
        int max = computeMaximum(rangedHikerOneArray, rangedHikerTwoArray, 
                rangedHikerThreeArray, rangedHikerFourArray, 
                rangedHikerFiveArray, rangedHikerSixArray);
        int min = computeMinimum(rangedHikerOneArray, rangedHikerTwoArray, 
                rangedHikerThreeArray, rangedHikerFourArray, 
                rangedHikerFiveArray, rangedHikerSixArray);
        printYAxis(g2, max, min);
        mean = computeMean(rangedHikerOneArray, rangedHikerTwoArray, 
                rangedHikerThreeArray, rangedHikerFourArray, 
                rangedHikerFiveArray, rangedHikerSixArray);
        updateMean(g2);
        int numValues = computeNumValues();
        
        
        //chart the vertical x axis lines
        //If the number of lines is more than 30, find a reasonable quantity to plot
        int divisor = 1;        
        while ((numValues/divisor) > 30) {
            divisor++;
        }

        //Determine the width between tick markers, and print them floor to ceiling
        int widthBetweenTicks = width/(numValues/divisor);
        for(int i = 1; i <= (numValues/divisor); i++ ) {
            //Draw the tick marks
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(widthBetweenTicks*i, height-11, widthBetweenTicks*i, height -15);
            
            //Draw the vertical gridlines
            g2.setColor(new Color(222,222,222));
            g2.drawLine(widthBetweenTicks*i, height-15, widthBetweenTicks*i, 11);
        }
        
        
        //establish the x values
        int xPoints[] = new int[numValues];
        
        for (int i = 0; i < numValues; i++) {
            xPoints[i] = Math.round(i * ((float)width/numValues));
        }
        
        //Establish the y values for ech of the hikers that was chosen. Much repetitive code here.
        if (hikerOneBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerOneArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(255,85,99));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
        
        if (hikerTwoBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerTwoArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(105,159,154));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
        
        if (hikerThreeBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerThreeArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(45,220,0));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
        
        if (hikerFourBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerFourArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(255,0,220));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
        
        if (hikerFiveBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerFiveArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(0,119,255));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
        
        if (hikerSixBoo) {
            //establish all of the y points by converting temperatures into dynamic coordinates
            int yPoints[] = new int[numValues];
            for (int i = 0; i < numValues; i++) {
                yPoints[i] = Math.round((11 + ((height-22)/12)) + (max - rangedHikerSixArray[i])/(max-min) * ((10*(height-22))/12));    
            }
            
            //Set the color and stroke
            g2.setColor(new Color(255,136,0));
            BasicStroke wideLine = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                    BasicStroke.JOIN_ROUND);
            g2.setStroke(wideLine);
            
            GeneralPath polyline = 
                new GeneralPath(GeneralPath.WIND_EVEN_ODD, xPoints.length);
    
            polyline.moveTo (xPoints[0], yPoints[0]);
    
            for (int i = 0; i < xPoints.length; i++) {
                 polyline.lineTo(xPoints[i], yPoints[i]);
            }
    
            g2.draw(polyline);
        }
                    
        
        //Reset the color and stroke to what it was before the method began
        g2.setColor(tempColor);
        g2.setStroke(new BasicStroke());
        removeFeedback(g2);
    }
    
    
    //Determine the points to plot
    public float[] setRanges(float[] hikerArray, int hikerStartIndex, int hikerEndIndex) {
        
        //Determine the number of days before and during the request
        int startIndex, numValues;
        startIndex = computeStartIndex();
        numValues = computeNumValues();
        
        //Initialize the arrays
        float[] yValues = new float[numValues];
        
        if (allDayBoo) {
            for (int i = 0; i < numValues; i++) {
                yValues[i] = hikerArray[startIndex + i];
            }
        }
        else if (dayBoo) {
            for (int i = 0; i < (numValues/14); i++) {
                for (int j = 0; j < 14; j++) {
                    yValues[(i*14) + j] = hikerArray[startIndex + 6 + (24*i) + j];
                }
            }
        }
        else if (nightBoo) {
            for (int i = 0; i < (numValues/10); i++) {
                for (int j = 0; j < 10; j++) {
                    yValues[(i*10) + j] = hikerArray[startIndex + 20 + (24*i) + j];
                }
            }
        }
        else if (afternoonBoo) {
            for (int i = 0; i < (numValues/3); i++) {
                for (int j = 0; j < 3; j++) {
                    yValues[(i*3) + j] = hikerArray[startIndex + 13 + (24*i) + j];
                }
            }
        }
        else if (morningBoo) {
            for (int i = 0; i < (numValues/3); i++) {
                for (int j = 0; j < 3; j++) {
                    yValues[(i*3) + j] = hikerArray[startIndex + 8 + (24*i) + j];
                }
            }
        }
        
        return yValues;
    }
    
    
    //A method to compute the number of days between two dates selected
    //Returns the number of days in the range chosen.
    public int computeNumDays() {
        /*
         * Month lengths:
         *   aprilLength = 30;
         *   mayLength = 31;
         *   juneLength = 30;
         *   julyLength = 31;
         *   augustLength = 31;
         *   septemberLength = 30;
         */
        
        int numDays;
        
        //Basic formula
        numDays = ((userEndMonth - userStartMonth) * 30) + userEndDay - userStartDay;
        
        if (userEndDay == 31) numDays++; //Special exception to algorithem below
        
        //Adjustments for months with 31 days
        //First we add for months at the start of the range
        //This assumes all hikers finished in September
        if (userStartMonth >= 4 && userStartMonth < 6) numDays = numDays + 3;
        else if (userStartMonth >= 6 && userStartMonth < 8) numDays = numDays + 2;
        else if (userStartMonth >= 8 && userStartMonth < 9) numDays = numDays + 1;
        else if (userStartMonth >= 9) numDays = numDays + 0; //This line for clarity
        
        //Then we subtract for any range ends before September
        if (userEndMonth == 5 || userEndMonth == 4) { 
            numDays = numDays - 3;
            return numDays;
        }
        else if (userEndMonth <= 7 && userEndMonth > 5) { 
            numDays = numDays - 2;
            return numDays;
        }
        else if (userEndMonth <= 9 && userEndMonth > 7) { 
            numDays = numDays - 1;
            return numDays;
        }
        else if (userEndMonth <= 10 && userEndMonth > 9) { //This block for clarity
            numDays = numDays - 0; 
            return numDays;
        }
    
        return numDays;
    }
   
    
    //This will adjust for the day length pattern chosen by the user.
    //Returns the number of values in the range chosen.
    public int computeNumValues () {
        int numValues = computeNumDays();
        
        if (allDayBoo) numValues = numValues * 24;
        else if (dayBoo) numValues = numValues * 14;
        else if (nightBoo) numValues = numValues * 10;
        else if (afternoonBoo) numValues = numValues * 3;
        else if (morningBoo) numValues = numValues * 3;
        
        return numValues;
    }
    
    
    //A method to determine the starting index for the data
    //Returns the number of hours after April 1st that the range chosen indicates
    public int computeStartIndex() {
        /*
         * Month lengths:
         *   aprilLength = 30;
         *   mayLength = 31;
         *   juneLength = 30;
         *   julyLength = 31;
         *   augustLength = 31;
         *   septemberLength = 30;
         */
        
        int startIndex;
        
        //Minus four because April is the index month. 
        startIndex = (userStartMonth - 4) * 30 + userStartDay;
        
        if (userStartMonth == 6) startIndex = startIndex + 1;
        if (userStartMonth == 8) startIndex = startIndex + 2;
        if (userStartMonth == 9) startIndex = startIndex + 3;
        
        //Times 24 and minus 24 to adjust to April 1st.
        return startIndex*24 - 24 ; 
    }
    
    
    //Returns the maximum value of the selected arrays
    public int computeMaximum(float[] rangedHikerOneArray, float[] rangedHikerTwoArray, 
                              float[] rangedHikerThreeArray, float[] rangedHikerFourArray,
                              float[] rangedHikerFiveArray, float[] rangedHikerSixArray) {
        
        float maximum = -50; //assume this is the max...for now.
        
        if (hikerOneBoo) {
            for (int i = 0; i < rangedHikerOneArray.length; i++) {
                if (rangedHikerOneArray[i] > maximum) {
                    maximum = rangedHikerOneArray[i];
                }
            }
        }
        
        if (hikerTwoBoo) {
            for (int i = 0; i < rangedHikerTwoArray.length; i++) {
                if (rangedHikerTwoArray[i] > maximum) {
                    maximum = rangedHikerTwoArray[i];
                }
            }
        }
        
        if (hikerThreeBoo) {
            for (int i = 0; i < rangedHikerThreeArray.length; i++) {
                if (rangedHikerThreeArray[i] > maximum) {
                    maximum = rangedHikerThreeArray[i];
                }
            }
        }
        
        if (hikerFourBoo) {
            for (int i = 0; i < rangedHikerFourArray.length; i++) {
                if (rangedHikerFourArray[i] > maximum) {
                    maximum = rangedHikerFourArray[i];
                }
            }
        }
        
        if (hikerFiveBoo) {
            for (int i = 0; i < rangedHikerFiveArray.length; i++) {
                if (rangedHikerFiveArray[i] > maximum) {
                    maximum = rangedHikerFiveArray[i];
                }
            }
        }
        
        if (hikerSixBoo) {
            for (int i = 0; i < rangedHikerSixArray.length; i++) {
                if (rangedHikerSixArray[i] > maximum) {
                    maximum = rangedHikerSixArray[i];
                }
            }
        }
        
        return (int)maximum; //cast the float to an int to decrease accuracy/complexity.
    }
    
    
    //Returns the minimum value of the selected arrays
    public int computeMinimum(float[] rangedHikerOneArray, float[] rangedHikerTwoArray, 
                              float[] rangedHikerThreeArray, float[] rangedHikerFourArray,
                              float[] rangedHikerFiveArray, float[] rangedHikerSixArray) {
        float minimum = 150; //assume this is the minimum...for now.
        
        if (hikerOneBoo) {
            for (int i = 0; i < rangedHikerOneArray.length; i++) {
                if (rangedHikerOneArray[i] < minimum) {
                    minimum = rangedHikerOneArray[i];
                }
            }
        }
        
        if (hikerTwoBoo) {
            for (int i = 0; i < rangedHikerTwoArray.length; i++) {
                if (rangedHikerTwoArray[i] < minimum) {
                    minimum = rangedHikerTwoArray[i];
                }
            }
        }
        
        if (hikerThreeBoo) {
            for (int i = 0; i < rangedHikerThreeArray.length; i++) {
                if (rangedHikerThreeArray[i] < minimum) {
                    minimum = rangedHikerThreeArray[i];
                }
            }
        }
        
        if (hikerFourBoo) {
            for (int i = 0; i < rangedHikerFourArray.length; i++) {
                if (rangedHikerFourArray[i] < minimum) {
                    minimum = rangedHikerFourArray[i];
                }
            }
        }
        
        if (hikerFiveBoo) {
            for (int i = 0; i < rangedHikerFiveArray.length; i++) {
                if (rangedHikerFiveArray[i] < minimum) {
                    minimum = rangedHikerFiveArray[i];
                }
            }
        }
        
        if (hikerSixBoo) {
            for (int i = 0; i < rangedHikerSixArray.length; i++) {
                if (rangedHikerSixArray[i] < minimum) {
                    minimum = rangedHikerSixArray[i];
                }
            }
        }
        
        return (int)minimum; //cast the float to an int to decrease accuracy/complexity.      
    }   
    
    
    //Returns the mean of the arrays chosen.
    public float computeMean(float[] rangedHikerOneArray, float[] rangedHikerTwoArray, 
                             float[] rangedHikerThreeArray, float[] rangedHikerFourArray,
                             float[] rangedHikerFiveArray, float[] rangedHikerSixArray) {
        float total = 0;
        int numValues = 0;
        
        if (hikerOneBoo) {
            for (int i = 0; i < rangedHikerOneArray.length; i++) {
                if (rangedHikerOneArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerOneArray[i];
                }
            }
        }
        
        if (hikerTwoBoo) {
            for (int i = 0; i < rangedHikerTwoArray.length; i++) {
                if (rangedHikerTwoArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerTwoArray[i];
                }
            }
        }
        
        if (hikerThreeBoo) {
            for (int i = 0; i < rangedHikerThreeArray.length; i++) {
                if (rangedHikerThreeArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerThreeArray[i];
                }
            }
        }
        
        if (hikerFourBoo) {
            for (int i = 0; i < rangedHikerFourArray.length; i++) {
                if (rangedHikerFourArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerFourArray[i];
                }
            }
        }
            
        if (hikerFiveBoo) {
            for (int i = 0; i < rangedHikerFiveArray.length; i++) {
                if (rangedHikerFiveArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerFiveArray[i];
                }
            }
        }
        
        if (hikerSixBoo) {
            for (int i = 0; i < rangedHikerSixArray.length; i++) {
                if (rangedHikerSixArray[i] != 0) {
                    numValues++;
                    total = total + rangedHikerSixArray[i];
                }
            }
        }
        
        float mean = total/numValues;
        
        return mean;
    }
    
    
    
    /*
     * Methods below this point are used to load data 
     * from the text files
     */
    //Load the flat file into proper arrays
    public float[] makeFile (String fileName, int hikerStartIndex, int hikerEndIndex) {
        float[] hikerData = new float[timeSpan];
        
        //Open the file
        try {
            URL file = new URL(fileName);
            BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                        file.openStream()));
            LineNumberReader lnr = new LineNumberReader(in);
            return loadData(lnr,hikerStartIndex,hikerEndIndex);
        }
        catch (MalformedURLException mfe) {
            mfe.printStackTrace();
            destroy();
            return hikerData;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            destroy();
            return hikerData;
        }
    }
    
    
    
    /* Load the arrays with their data, filling out of range areas with zeroes.
     * Each array is represents a timeline. If the hiker is not on the trail during
     * the timeline, the value is filled with a zero.*/
    public float[] loadData (LineNumberReader lnr, int hikerStartIndex, int hikerEndIndex) {
        int numLines = 0;
        float [] hikerData = new float[timeSpan];
        
        //Fills up the useful values after the index point
        try {
            while ((line = lnr.readLine()) != null) {
                String[] tempString = line.split(",");
                
                hikerData[numLines + hikerStartIndex] = Float.parseFloat(tempString[4]);
                
                numLines++;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return hikerData;
    }
}