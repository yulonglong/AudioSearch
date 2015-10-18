package Search;

import Feature.Energy;
import Feature.MFCC;
import Feature.MagnitudeSpectrum;
import Feature.ZeroCrossing;
import Player.SoundEffectDemo;
import SignalProcess.Frames;
import SignalProcess.WaveIO;
import Distance.CityBlock;
import Distance.Cosine;
import Distance.Euclidean;
import Tool.SortHashMapByValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by workshop on 9/18/2015.
 */
public class SearchDemo {
	public static final boolean s_enableLogging = true;
	
	public static final int s_numTrainingData = 1250;
	
	private static final String s_msFeaturePath = "data/feature/magnitudeSpectrum.txt";
	private static final String s_energyFeaturePath = "data/feature/energy.txt";
	private static final String s_zcFeaturePath = "data/feature/zeroCrossing.txt";
	private static final String s_mfccFeaturePath = "data/feature/mfcc.txt";
	
	private static final double s_msFeatureWeight = 462.6082065715304;
	private static final double s_energyFeatureWeight = 545.3160022031567;
	private static final double s_zcFeatureWeight = 786.84652254;
	private static final double s_mfccFeatureWeight = 362.7497443018521;
	
	private static final double s_cosineWeight = 994.8863794410453;
	private static final double s_euclideanWeight = 288.43219834158884;
	private static final double s_cityblockWeight = -148.18298405621834;
	
	private double m_msFeatureWeight = 0;
	private double m_energyFeatureWeight = 0;
	private double m_zcFeatureWeight = 0;
	private double m_mfccFeatureWeight= 0;
	
	private double m_cosineWeight = 0;
	private double m_euclideanWeight = 0;
	private double m_cityblockWeight = 0;
	
    HashMap<String, double[]> m_msFeature;
    HashMap<String, double[]> m_energyFeature;
    HashMap<String, double[]> m_zcFeature;
    HashMap<String, double[]> m_mfccFeature;
    
    HashMap<String, Double> m_simList;
	
	public SearchDemo() {
		SoundEffectDemo.s_progressBar.setMinimum(0);
		SoundEffectDemo.s_progressBar.setMaximum(s_numTrainingData*4);
		m_msFeature = readFeature(s_msFeaturePath, s_numTrainingData*0);
		m_energyFeature = readFeature(s_energyFeaturePath, s_numTrainingData*1);
		m_zcFeature = readFeature(s_zcFeaturePath, s_numTrainingData*2);
		m_mfccFeature = readFeature(s_mfccFeaturePath, s_numTrainingData*3);
	}
	
	public void useDefinedWeight(boolean useMsFeature, boolean useEnergyFeature, boolean useZcFeature, boolean useMfccFeature) {
		m_msFeatureWeight = 0;
		m_energyFeatureWeight = 0;
		m_zcFeatureWeight = 0;
		m_mfccFeatureWeight= 0;
		if (useMsFeature) m_msFeatureWeight = s_msFeatureWeight;
		if (useEnergyFeature) m_energyFeatureWeight = s_energyFeatureWeight;
		if (useZcFeature) m_zcFeatureWeight = s_zcFeatureWeight;
		if (useMfccFeature) m_mfccFeatureWeight = s_mfccFeatureWeight;
	}
	
	public void useDefinedSimilarityWeight(boolean useCosine, boolean useEuclidean, boolean useCityBlock) {
		m_cosineWeight = 0;
		m_euclideanWeight = 0;
		m_cityblockWeight = 0;
		if (useCosine) m_cosineWeight = s_cosineWeight;
		if (useEuclidean) m_euclideanWeight = s_euclideanWeight;
		if (useCityBlock) m_cityblockWeight = s_cityblockWeight;
	}
	
	public void setWeight(double msWeight, double energyWeight, double zcWeight, double mfccWeight) {
		m_msFeatureWeight = msWeight;
		m_energyFeatureWeight = energyWeight;
		m_zcFeatureWeight = zcWeight;
		m_mfccFeatureWeight = mfccWeight;
	}
	
	public void setSimilarityWeight(double cosineWeight, double euclideanWeight, double cityblockWeight) {
		m_cosineWeight = cosineWeight;
		m_euclideanWeight = euclideanWeight;
		m_cityblockWeight = cityblockWeight;
	}
	
    /***
     * Get the feature of train set via the specific feature extraction method, and write it into offline file for efficiency;
     * Please modify this function, select or combine the methods (in the Package named 'Feature') to extract feature, such as Zero-Crossing, Energy, Magnitude-
     * Spectrum and MFCC by yourself.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
	
    public void trainFeatureList(){
        File trainFolder = new File(SoundEffectDemo.s_basePath);
        File[] trainList = trainFolder.listFiles();

        try {

            FileWriter fwMs = new FileWriter(s_msFeaturePath);
            FileWriter fwEnergy = new FileWriter(s_energyFeaturePath);
            FileWriter fwZc = new FileWriter(s_zcFeaturePath);
            FileWriter fwMfcc = new FileWriter(s_mfccFeaturePath);

            for (int i = 0; i < trainList.length; i++) {
                WaveIO waveIO = new WaveIO();
                short[] signal = waveIO.readWave(trainList[i].getAbsolutePath());

                // Extract Magnitude Spectrum
                MagnitudeSpectrum ms = new MagnitudeSpectrum();
                double[] msFeature = ms.getFeature(signal);
                
                String line = trainList[i].getName() + "\t";
                for (double f: msFeature){
                    line += f + "\t";
                }
                fwMs.append(line+"\n");
                
                // Extract Energy
                Energy energy = new Energy();
                double[] energyFeature = energy.getFeature(signal);
                
                String line2 = trainList[i].getName() + "\t";
                for (double f: energyFeature){
                    line2 += f + "\t";
                }
                fwEnergy.append(line2+"\n");
                
                // Extract Zero Crossing
                ZeroCrossing zc = new ZeroCrossing();
                double[] zcFeature = zc.getFeature(signal);
                
                String line3 = trainList[i].getName() + "\t";
                for (double f: zcFeature){
                    line3 += f + "\t";
                }
                fwZc.append(line3+"\n");
                
                // Extract mfcc
                MFCC mfcc = new MFCC(Frames.frameLength);
                mfcc.process(signal);
                double[] mfccFeature = mfcc.getMeanFeature();
                
                String line4 = trainList[i].getName() + "\t";
                for (double f: mfccFeature){
                    line4 += f + "\t";
                }
                fwMfcc.append(line4+"\n");
                
                System.out.println("@=========@" + i);
            }
            fwMs.close();
            fwEnergy.close();
            fwZc.close();
            fwMfcc.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private void normalizeValue(HashMap<String,Double> map) {
    	double maxValue = 1;
    	for (Map.Entry<String, Double> f: map.entrySet()){
        	if (f.getValue() > maxValue) {
        		maxValue = f.getValue();
        	}
        }
    	for (Map.Entry<String, Double> f: map.entrySet()){
        	map.put(f.getKey(), f.getValue()/maxValue);
        }
    }
    
    public ArrayList<String> resultList(String query){ 
    	m_simList = new HashMap<String, Double>();
    	m_simList = getHashMapScore(query);
    	
        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(m_simList);
        
        if (s_enableLogging) {
	        String out = query + ":";
	        for(int j = 0; j < result.size(); j++){
	            out += "\t" + result.get(j);
	        }
	        System.out.println(out);
        }
        return result;
    }
    
    public ArrayList<String> resultList (String query, String feedback, boolean isPositive) {
    	final double feedbackConstant = 0.5;
    	// HashMap<String, Double> simList = getHashMapScore(query);
    	HashMap<String, Double> feedbackList = getHashMapScore(feedback);
    	
    	for (Map.Entry<String,Double> f: feedbackList.entrySet()) {
    		Double originalScore = m_simList.get(f.getKey());
    		Double feedbackScore = f.getValue();
    		Double finalScore;
    		if (isPositive) {
    			finalScore = originalScore + (feedbackConstant*feedbackScore);
    		}
    		else {
    			finalScore = originalScore - (feedbackConstant*feedbackScore);
    		}
    		m_simList.put(f.getKey(), finalScore);
    	}
    	
        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(m_simList);
        
        if (s_enableLogging) {
	        String out = query + ":";
	        for(int j = 0; j < result.size(); j++){
	            out += "\t" + result.get(j);
	        }
	
	        System.out.println(out);
        }
        return result;
    }

    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    private HashMap<String, Double> getHashMapScore(String query){
        WaveIO waveIO = new WaveIO();

        short[] inputSignal = waveIO.readWave(query);
        MagnitudeSpectrum ms = new MagnitudeSpectrum();
        double[] msFeatureQuery = ms.getFeature(inputSignal);
        Energy energy = new Energy();
        double[] energyFeatureQuery = energy.getFeature(inputSignal);
        ZeroCrossing zc = new ZeroCrossing();
        double[] zcFeatureQuery = zc.getFeature(inputSignal);
        MFCC mfcc = new MFCC(Frames.frameLength);
        mfcc.process(inputSignal);
        double[] mfccFeatureQuery = mfcc.getMeanFeature();
        
        
        // Get cosine SimList
        HashMap<String, Double> cosineSimList = new HashMap<String, Double>();
        HashMap<String, Double> cosineMsSimList = new HashMap<String, Double>();
        HashMap<String, Double> cosineEnergySimList = new HashMap<String, Double>();
        HashMap<String, Double> cosineZcSimList = new HashMap<String, Double>();
        HashMap<String, Double> cosineMfccSimList = new HashMap<String, Double>();
        
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	cosineMsSimList.put(f.getKey(), (Cosine.getDistance(msFeatureQuery, (double[]) f.getValue())));
        	// System.out.println(cosineMsSimList.get(f.getKey()));
        }
        for (Map.Entry<String,double[]> f: m_energyFeature.entrySet()){
        	cosineEnergySimList.put(f.getKey(), (Cosine.getDistance(energyFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_zcFeature.entrySet()){
        	cosineZcSimList.put(f.getKey(), (Cosine.getDistance(zcFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_mfccFeature.entrySet()){
        	cosineMfccSimList.put(f.getKey(), (Cosine.getDistance(mfccFeatureQuery, (double[]) f.getValue())));
        }
        normalizeValue(cosineMsSimList);
        normalizeValue(cosineEnergySimList);
        normalizeValue(cosineZcSimList);
        normalizeValue(cosineMfccSimList);
        // Combine 4 features
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	cosineSimList.put(f.getKey(), (m_msFeatureWeight * cosineMsSimList.get(f.getKey())) + (m_energyFeatureWeight * cosineEnergySimList.get(f.getKey())) + (m_zcFeatureWeight * cosineZcSimList.get(f.getKey())) + (m_mfccFeatureWeight * cosineMfccSimList.get(f.getKey())));
        }
        
        // Get Euclidean SimList
        HashMap<String, Double> euclideanSimList = new HashMap<String, Double>();
        HashMap<String, Double> euclideanMsSimList = new HashMap<String, Double>();
        HashMap<String, Double> euclideanEnergySimList = new HashMap<String, Double>();
        HashMap<String, Double> euclideanZcSimList = new HashMap<String, Double>();
        HashMap<String, Double> euclideanMfccSimList = new HashMap<String, Double>();
        
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	euclideanMsSimList.put(f.getKey(), (Euclidean.getDistance(msFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_energyFeature.entrySet()){
        	euclideanEnergySimList.put(f.getKey(), (Euclidean.getDistance(energyFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_zcFeature.entrySet()){
        	euclideanZcSimList.put(f.getKey(), (Euclidean.getDistance(zcFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_mfccFeature.entrySet()){
        	euclideanMfccSimList.put(f.getKey(), (Euclidean.getDistance(mfccFeatureQuery, (double[]) f.getValue())));
        }
        normalizeValue(euclideanMsSimList);
        normalizeValue(euclideanEnergySimList);
        normalizeValue(euclideanZcSimList);
        normalizeValue(euclideanMfccSimList);
        // Combine 4 features
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	euclideanSimList.put(f.getKey(), (m_msFeatureWeight * euclideanMsSimList.get(f.getKey())) + (m_energyFeatureWeight * euclideanEnergySimList.get(f.getKey())) + (m_zcFeatureWeight * euclideanZcSimList.get(f.getKey())) + (m_mfccFeatureWeight * euclideanMfccSimList.get(f.getKey())));
        }
        
        // Get CityBlock SimList
        HashMap<String, Double> cityblockSimList = new HashMap<String, Double>();
        HashMap<String, Double> cityblockMsSimList = new HashMap<String, Double>();
        HashMap<String, Double> cityblockEnergySimList = new HashMap<String, Double>();
        HashMap<String, Double> cityblockZcSimList = new HashMap<String, Double>();
        HashMap<String, Double> cityblockMfccSimList = new HashMap<String, Double>();
        
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	cityblockMsSimList.put(f.getKey(), (Euclidean.getDistance(msFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_energyFeature.entrySet()){
        	cityblockEnergySimList.put(f.getKey(), (Euclidean.getDistance(energyFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_zcFeature.entrySet()){
        	cityblockZcSimList.put(f.getKey(), (Euclidean.getDistance(zcFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry<String,double[]> f: m_mfccFeature.entrySet()){
        	cityblockMfccSimList.put(f.getKey(), (Euclidean.getDistance(mfccFeatureQuery, (double[]) f.getValue())));
        }
        normalizeValue(cityblockMsSimList);
        normalizeValue(cityblockEnergySimList);
        normalizeValue(cityblockZcSimList);
        normalizeValue(cityblockMfccSimList);
        // Combine 4 features
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	cityblockSimList.put(f.getKey(), (m_msFeatureWeight * cityblockMsSimList.get(f.getKey())) + (m_energyFeatureWeight * cityblockEnergySimList.get(f.getKey())) + (m_zcFeatureWeight * cityblockZcSimList.get(f.getKey())) + (m_mfccFeatureWeight * cityblockMfccSimList.get(f.getKey())));
        }
        
        // Overall
        HashMap<String, Double> simList = new HashMap<String, Double>();
        for (Map.Entry<String,double[]> f: m_msFeature.entrySet()){
        	simList.put(f.getKey(), (m_cosineWeight * cosineSimList.get(f.getKey())) + (m_euclideanWeight * euclideanSimList.get(f.getKey())) + (m_cityblockWeight * cityblockSimList.get(f.getKey())));
        }
        
        return simList;
    }

    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private HashMap<String, double[]> readFeature(String featurePath, int currCount){
        HashMap<String, double[]> fList = new HashMap<>();
        try{
            FileReader fr = new FileReader(featurePath);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while(line != null){

                String[] split = line.trim().split("\t");
                if (split.length < 2)
                    continue;
                double[] fs = new double[split.length - 1];
                for (int i = 1; i < split.length; i ++){
                    fs[i-1] = Double.valueOf(split[i]);
                }

                fList.put(split[0], fs);
                
                // Update ProgressBar
                SoundEffectDemo.s_progressBar.setValue(currCount);
        		double currPercentage = ((double) currCount / ((double) s_numTrainingData * 4.0)) * 100.0;
        		SoundEffectDemo.s_progressBar.setString(String.format("%.2f", currPercentage) + "%");
        		currCount++;
        		
                line = br.readLine();
            }
            br.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return fList;
    }
}
