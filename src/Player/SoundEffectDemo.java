package Player;
import Search.SearchDemo;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import Evaluation.Precision;
import Evaluation.Recall;

/**
 * Created by workshop on 9/18/2015.
 */
public class SoundEffectDemo extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	JPanel contentPane;
    JButton openButton, searchButton, queryButton, runTestButton, trainButton, runGAButton;
    JFileChooser fileChooser;

    File queryAudio = null;
    int resultSize = 20;
    /**
     * If need, please replace the 'querySet' with specific path of test set of audio files in your PC.
     */
    String querySet = "data/input/";
    /**
     * Please Replace the 'basePath' with specific path of train set of audio files in your PC.
     */
    public static final String s_basePath = "D:/GitHub/AudioSearchData/data/input/train/";
    public static final String s_testPath = "D:/GitHub/AudioSearchData/data/input/test/";
    
    JCheckBox m_msCheckBox = new JCheckBox("Magnitude Spectrum");
	JCheckBox m_energyCheckBox = new JCheckBox("Energy");
	JCheckBox m_zcCheckBox = new JCheckBox("Zero Crossing");
	JCheckBox m_mfccCheckBox = new JCheckBox("MFCC");
	
	int m_windowWidth = 1366;
	int m_windowHeight = 900;

    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];
    ArrayList<String> resultFiles = new ArrayList<String>();
    
    SearchDemo m_searchDemo;

    // Constructor
    public SoundEffectDemo() {
    	m_searchDemo = new SearchDemo();
        // Pre-load all the sound files
        queryAudio = null;
        SoundEffect.volume = SoundEffect.Volume.LOW;  // un-mute

        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        String tempName = "";

        queryButton = new JButton("Current Audio:"+tempName);
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        
        runTestButton = new JButton("Run Test");
        runTestButton.addActionListener(this);
        
        trainButton = new JButton("Train");
        trainButton.addActionListener(this);
        
        runGAButton = new JButton("Run GA");
        runGAButton.addActionListener(this);

        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        queryPanel.add(runTestButton);
        queryPanel.add(trainButton);
        queryPanel.add(runGAButton);
        
        queryPanel.add(m_msCheckBox);
        queryPanel.add(m_energyCheckBox);
        queryPanel.add(m_zcCheckBox);
        queryPanel.add(m_mfccCheckBox);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 60, 60));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            resultButton[i] = new JButton(resultLabels[i].getText());

            resultButton[i].addActionListener(this);

            resultButton[i].setVisible(false);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
        }


        resultPanel.setBorder(BorderFactory.createEmptyBorder(30,16,10,16));

        contentPane = (JPanel)this.getContentPane();
        setSize(m_windowWidth,m_windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.add(queryPanel, BorderLayout.PAGE_START);
        contentPane.add(resultPanel, BorderLayout.CENTER);

        contentPane.setVisible(true);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == openButton){
            if (fileChooser == null) {
                fileChooser = new JFileChooser(querySet);

                fileChooser.addChoosableFileFilter(new AudioFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);
            }
            int returnVal = fileChooser.showOpenDialog(SoundEffectDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION){
                queryAudio = fileChooser.getSelectedFile();
            }

            fileChooser.setSelectedFile(null);

            queryButton.setText(queryAudio.getName());

            fileChooser.setSelectedFile(null);

        }else if (e.getSource() == searchButton){
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            m_searchDemo.useDefinedWeight(m_msCheckBox.isSelected(), m_energyCheckBox.isSelected(), m_zcCheckBox.isSelected(), m_mfccCheckBox.isSelected());
            resultFiles = m_searchDemo.resultList(queryAudio.getAbsolutePath());

            for (int i = 0; i < resultFiles.size(); i ++){
                resultLabels[i].setText(resultFiles.get(i));
                resultButton[i].setText(resultFiles.get(i));
                resultButton[i].setVisible(true);
            }
            setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == queryButton){
            new SoundEffect(queryAudio.getAbsolutePath()).play();
        }else if (e.getSource() == runTestButton) {
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	m_searchDemo.useDefinedWeight(m_msCheckBox.isSelected(), m_energyCheckBox.isSelected(), m_zcCheckBox.isSelected(), m_mfccCheckBox.isSelected());
        	System.out.println(Precision.evaluate(s_testPath, m_searchDemo));
        	setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == trainButton) {
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	SearchDemo searchDemo = new SearchDemo();
        	searchDemo.trainFeatureList();
        	setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == runGAButton) {
        	
        }
        else {
            for (int i = 0; i < resultSize; i ++){
                if (e.getSource() == resultButton[i]){
                    String filePath = s_basePath+resultFiles.get(i);
                    new SoundEffect(filePath).play();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        new SoundEffectDemo();
    }
}
