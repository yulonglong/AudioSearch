package Search;

import Feature.Energy;
import Feature.MFCC;
import Feature.MagnitudeSpectrum;
import Feature.ZeroCrossing;
import Player.SoundEffectDemo;
import SignalProcess.Frames;
import SignalProcess.WaveIO;
import Distance.Cosine;
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
	private static final String s_msFeaturePath = "data/feature/magnitudeSpectrum.txt";
	private static final String s_energyFeaturePath = "data/feature/energy.txt";
	private static final String s_zcFeaturePath = "data/feature/zeroCrossing.txt";
	private static final String s_mfccFeaturePath = "data/feature/mfcc.txt";
			
	
	
	private static final double s_msFeatureWeight = 1;
	private static final double s_energyFeatureWeight = 1;
	private static final double s_zcFeatureWeight = 1;
	private static final double s_mfccFeatureWeight = 1;
	
	private double m_msFeatureWeight = 0;
	private double m_energyFeatureWeight = 0;
	private double m_zcFeatureWeight = 0;
	private double m_mfccFeatureWeight= 0;
	
    HashMap<String, double[]> m_msFeature;
    HashMap<String, double[]> m_energyFeature;
    HashMap<String, double[]> m_zcFeature;
    HashMap<String, double[]> m_mfccFeature;
	
	public SearchDemo() {
		m_msFeature = readFeature(s_msFeaturePath);
		m_energyFeature = readFeature(s_energyFeaturePath);
		m_zcFeature = readFeature(s_zcFeaturePath);
		m_mfccFeature = readFeature(s_mfccFeaturePath);
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

    /***
     * Get the distances between features of the selected query audio and ones of the train set;
     * Please modify this function, select or combine the suitable and feasible methods (in the package named 'Distance') to calculate the distance,
     * such as CityBlock, Cosine and Euclidean by yourself.
     * @param query the selected query audio file;
     * @return the top 20 similar audio files;
     */
    public ArrayList<String> resultList(String query){
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
        
        HashMap<String, Double> simList = new HashMap<String, Double>();

        /**
         * Example of calculating the distance via Cosine Similarity, modify it by yourself please.
         */
        Cosine cosine = new Cosine();

        /**
         * Load the offline file of features (the result of function 'trainFeatureList()'), modify it by yourself please;
         */


//        System.out.println(trainFeatureList.size() + "=====");
        for (Map.Entry f: m_msFeature.entrySet()){
            simList.put((String)f.getKey(), m_msFeatureWeight * cosine.getDistance(msFeatureQuery, (double[]) f.getValue()));
        }
        for (Map.Entry f: m_energyFeature.entrySet()){
            simList.put((String)f.getKey(), simList.get((String)f.getKey()) + (m_energyFeatureWeight * cosine.getDistance(energyFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry f: m_zcFeature.entrySet()){
            simList.put((String)f.getKey(), simList.get((String)f.getKey()) + (m_zcFeatureWeight * cosine.getDistance(zcFeatureQuery, (double[]) f.getValue())));
        }
        for (Map.Entry f: m_mfccFeature.entrySet()){
            simList.put((String)f.getKey(), simList.get((String)f.getKey()) + (m_mfccFeatureWeight * cosine.getDistance(mfccFeatureQuery, (double[]) f.getValue())));
        }

        SortHashMapByValue sortHM = new SortHashMapByValue(20);
        ArrayList<String> result = sortHM.sort(simList);

        String out = query + ":";
        for(int j = 0; j < result.size(); j++){
            out += "\t" + result.get(j);
        }

        System.out.println(out);
        return result;
    }

    /**
     * Load the offline file of features (the result of function 'trainFeatureList()');
     * @param featurePath the path of offline file including the features of training set.
     * @return the map of training features, Key is the name of file, Value is the array/vector of features.
     */
    private HashMap<String, double[]> readFeature(String featurePath){
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

                line = br.readLine();
            }
            br.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return fList;
    }
}
