package Player;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import Evaluation.Precision;
import Search.GeneticAlgorithm;
import Search.SearchDemo;

/**
 * Created by workshop on 9/18/2015.
 */
public class SoundEffectDemo extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	JPanel m_contentPane;
    JButton openButton, searchButton, queryButton, runTestButton, trainButton, runGAButton;
    JFileChooser fileChooser;
    public static JProgressBar s_progressBar = new JProgressBar();

    File queryAudio = null;
    int resultSize = 20;
    /**
     * If need, please replace the 'querySet' with specific path of test set of audio files in your PC.
     */
    String querySet = "data/input/";
    /**
     * Please Replace the 'basePath' with specific path of train set of audio files in your PC.
     */

    public static final String s_basePath = "C:\\Users\\Ian\\WorkspaceGeneral\\AudioSearchData\\data\\input\\train\\";
    public static final String s_testPath = "C:\\Users\\Ian\\WorkspaceGeneral\\AudioSearchData\\data\\input\\test\\";
//    public static final String s_basePath = "D:/GitHub/AudioSearchData/data/input/train/";
//    public static final String s_testPath = "D:/GitHub/AudioSearchData/data/input/test/";

// public static final String s_basePath = "C:\\Users\\Ian\\WorkspaceGeneral\\AudioSearchData\\data\\input\\train\\";
// public static final String s_testPath = "C:\\Users\\Ian\\WorkspaceGeneral\\AudioSearchData\\data\\input\\test\\";

    JCheckBox m_msCheckBox = new JCheckBox("Magnitude Spectrum");
	JCheckBox m_energyCheckBox = new JCheckBox("Energy");
	JCheckBox m_zcCheckBox = new JCheckBox("Zero Crossing");
	JCheckBox m_mfccCheckBox = new JCheckBox("MFCC");

	JCheckBox m_cosineCheckBox = new JCheckBox("Cosine");
	JCheckBox m_euclideanCheckBox = new JCheckBox("Euclidean");
	JCheckBox m_cityblockCheckBox = new JCheckBox("City Block");

	int m_windowWidth = 1366;
	int m_windowHeight = 900;

    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];
    ArrayList<String> resultFiles = new ArrayList<String>();

    SearchDemo m_searchDemo;

    // Constructor
    public SoundEffectDemo() {
    	m_contentPane = (JPanel)this.getContentPane();
        setSize(m_windowWidth,m_windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	// Initialize ProgressBar

		s_progressBar.setStringPainted(true);
		m_contentPane.add(s_progressBar);
		m_contentPane.setVisible(true);
		setVisible(true);

    	m_searchDemo = new SearchDemo();

        m_contentPane.remove(s_progressBar);
        setVisible(false);
        // Pre-load all the sound files
        queryAudio = null;
        SoundEffect.volume = SoundEffect.Volume.HIGH;  // un-mute

        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        String tempName = "";

        queryButton = new JButton("Current Audio:"+tempName);
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        runTestButton = new JButton("Run Test");
        runTestButton.setEnabled(false);
        runTestButton.addActionListener(this);

        trainButton = new JButton("Train");
        trainButton.setEnabled(false);
        trainButton.addActionListener(this);

        runGAButton = new JButton("Run GA");
        runGAButton.setEnabled(false);
        runGAButton.addActionListener(this);

        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        queryPanel.add(runTestButton);
        queryPanel.add(trainButton);
        queryPanel.add(runGAButton);

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(3,50));
        queryPanel.add(separator, "growx, wrap");

        queryPanel.add(m_msCheckBox);
        queryPanel.add(m_energyCheckBox);
        queryPanel.add(m_zcCheckBox);
        queryPanel.add(m_mfccCheckBox);

        JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
        separator2.setPreferredSize(new Dimension(3,50));
        queryPanel.add(separator2, "growx, wrap");

        queryPanel.add(m_cosineCheckBox);
        queryPanel.add(m_euclideanCheckBox);
        queryPanel.add(m_cityblockCheckBox);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 60, 60));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            // Add Mouse Click event to the JLabels (result)
            final int j = i;
            resultLabels[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						relevanceFeedback(j, true);
						System.out.println("Positive relevance feedback at index " + j);
					}
					if (SwingUtilities.isRightMouseButton(e)) {
						relevanceFeedback(j, false);
						System.out.println("Negative relevance feedback at index " + j);
					}

					// Code below here is for feedback that the image is clicked on.
					resultLabels[j].setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					try {
						Thread.sleep(500);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					} finally {
						resultLabels[j].setCursor(Cursor.getDefaultCursor());
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					resultLabels[j].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				@Override
				public void mouseExited(MouseEvent e) {
					resultLabels[j].setCursor(Cursor.getDefaultCursor());
				}
			});

            resultButton[i] = new JButton(resultLabels[i].getText());
            resultButton[i].addActionListener(this);
            resultButton[i].setVisible(false);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
        }


        resultPanel.setBorder(BorderFactory.createEmptyBorder(30,16,10,16));

        m_contentPane.add(queryPanel, BorderLayout.PAGE_START);
        m_contentPane.add(resultPanel, BorderLayout.CENTER);

        m_contentPane = (JPanel)this.getContentPane();
        setSize(m_windowWidth,m_windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_contentPane.setVisible(true);
		setVisible(true);
    }

    @Override
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
        	m_searchDemo.useDefinedSimilarityWeight(m_cosineCheckBox.isSelected(), m_euclideanCheckBox.isSelected(), m_cityblockCheckBox.isSelected());
            resultFiles = m_searchDemo.resultList(queryAudio.getAbsolutePath());
            updateResultUI();
            setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == queryButton){
            new SoundEffect(queryAudio.getAbsolutePath()).play();
        }else if (e.getSource() == runTestButton) {
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	m_searchDemo.useDefinedWeight(m_msCheckBox.isSelected(), m_energyCheckBox.isSelected(), m_zcCheckBox.isSelected(), m_mfccCheckBox.isSelected());
        	m_searchDemo.useDefinedSimilarityWeight(m_cosineCheckBox.isSelected(), m_euclideanCheckBox.isSelected(), m_cityblockCheckBox.isSelected());
        	System.out.println(Precision.evaluate(m_searchDemo));
        	setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == trainButton) {
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	SearchDemo searchDemo = new SearchDemo();
        	searchDemo.trainFeatureList();
        	setCursor(Cursor.getDefaultCursor());
        }else if (e.getSource() == runGAButton) {
        	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        	GeneticAlgorithm ga = new GeneticAlgorithm(m_searchDemo);
        	ga.runGA();
        	setCursor(Cursor.getDefaultCursor());
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

    private void updateResultUI() {
    	for (int i = 0; i < resultFiles.size(); i ++){
            resultLabels[i].setText(resultFiles.get(i));
            resultButton[i].setText(resultFiles.get(i));
            resultButton[i].setVisible(true);
        }
    }

	private void relevanceFeedback(int index, boolean isPositive) {
		m_searchDemo.useDefinedWeight(m_msCheckBox.isSelected(), m_energyCheckBox.isSelected(), m_zcCheckBox.isSelected(), m_mfccCheckBox.isSelected());
    	m_searchDemo.useDefinedSimilarityWeight(m_cosineCheckBox.isSelected(), m_euclideanCheckBox.isSelected(), m_cityblockCheckBox.isSelected());
        resultFiles = m_searchDemo.resultList(queryAudio.getAbsolutePath(), s_basePath + resultFiles.get(index), isPositive);
        updateResultUI();
	}

    public static void main(String[] args) {
        new SoundEffectDemo();
    }
}
