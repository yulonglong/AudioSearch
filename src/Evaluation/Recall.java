package Evaluation;

import java.io.File;
import java.util.ArrayList;

import Player.SoundEffectDemo;
import Search.SearchDemo;

/**
 * Created by workshop on 10/1/2015.
 */
public class Recall {
	private static final int k = 125;

	public static double evaluate(SearchDemo searchDemo) {
		double totalRecall = 0;

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
			totalRecall += (double) relevantFiles / (double) k;
		}
		double meanRecall = totalRecall / (double) listOfFiles.length;
		return meanRecall;
	}
}
