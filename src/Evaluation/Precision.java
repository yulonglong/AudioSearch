package Evaluation;

import java.io.File;
import java.util.ArrayList;

import Player.SoundEffectDemo;
import Search.SearchDemo;

/**
 * Created by workshop on 9/18/2015.
 */
public class Precision {
	private static final int k = 20;

	public static double evaluate(SearchDemo searchDemo) {
		double totalPrecision = 0;

		File folder = new File(SoundEffectDemo.s_testPath);
		File[] listOfFiles = folder.listFiles();

		for (int d = 0; d < listOfFiles.length; d++) {
			int relevantFiles = 0;
			if (listOfFiles[d].isFile()) {
				String queryFilename = listOfFiles[d].getName();
				String queryCategory = queryFilename.replaceAll("[0-9]", "");

				ArrayList<String> resultFiles = new ArrayList<String>();
				File queryAudio = listOfFiles[d];
				
				resultFiles = searchDemo.resultList(queryAudio.getAbsolutePath());

				for (int i = 0; i < resultFiles.size(); i++) {
					String resultCategory = resultFiles.get(i).replaceAll("[0-9]", "");
					if (queryCategory.equalsIgnoreCase(resultCategory)) {
						relevantFiles++;
					}
				}

			}
			// System.out.println("Current Precision is : " + relevantFiles +
			// "/20");
			totalPrecision += (double) relevantFiles / (double) k;
		}
		double meanPrecision = totalPrecision / (double) listOfFiles.length;
		return meanPrecision;
	}
}
