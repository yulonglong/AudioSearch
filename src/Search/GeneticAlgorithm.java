package Search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import Evaluation.Precision;

class Gene {
	double[] w = new double[7];
	double precision;

	Gene() {
		precision = 0.0;
		w[0] = w[2] = w[3] = w[4] = w[5] = w[6] = 1.0;
	}

	Gene(double _w1, double _w2, double _w3, double _w4, double _w5, double _w6, double _w7) {
		w[0] = _w1;
		w[1] = _w2;
		w[2] = _w3;
		w[3] = _w4;
		w[4] = _w5;
		w[5] = _w6;
		w[6] = _w7;
	}
}

class GeneComparator implements Comparator<Gene> {
	@Override
	public int compare(Gene o1, Gene o2) {
		if (o1.precision == o2.precision)
			return 0;
		else if (o1.precision < o2.precision)
			return 1;
		else
			return -1;
	}
}

public class GeneticAlgorithm {
	SearchDemo m_searchDemo;

	// For GA
	private int bestNGenes = 5;
	private double rangeMin = 0;
	private double rangeMax = 1000;
	private int maxGenerations = 1000000;
	private double mutationChance = 0.25;

	public GeneticAlgorithm(SearchDemo searchDemo) {
		m_searchDemo = searchDemo;
	}

	private void generateRandomGenes(ArrayList<Gene> geneList) {
		Random random = new Random();
		for (int j = 0; j < bestNGenes; j++) {
			double[] value = new double[7];
			for (int i = 0; i < 7; i++) {
				value[i] = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
			}
			Gene gene = new Gene(value[0], value[1], value[2], value[3], value[4], value[5], value[6]);
			geneList.add(gene);
		}
	}

	private Gene generateOffspring(Gene gene1, Gene gene2) {
		Random randomCrossover = new Random();
		Random randomMutation = new Random();
		Random randomPositiveMutation = new Random();
		Random randomValue = new Random();
		double[] value = new double[7];
		for (int i = 0; i < 7; i++) {

			// Crossover
			if (randomCrossover.nextDouble() < 0.5) {
				value[i] = gene1.w[i];
			} else {
				value[i] = gene2.w[i];
			}

			// Mutation
			double randMutation = randomMutation.nextDouble();
			double randPositive = randomPositiveMutation.nextDouble();
			double randValue = randomValue.nextDouble();
			if (randMutation < mutationChance / 4.0) {
				if (randPositive < 0.5) {
					value[i] = value[i] + (randValue * 100) + 1;
				} else {
					value[i] = value[i] - (randValue * 100) + 1;
				}
			} else if (randMutation < mutationChance / 2.0) {
				if (randPositive < 0.5) {
					value[i] = value[i] + (randValue * 10) + 1;
				} else {
					value[i] = value[i] - (randValue * 10) + 1;
				}
			} else if (randMutation < mutationChance) {
				if (randPositive < 0.5) {
					value[i] = value[i] + (randValue * 2);
				} else {
					value[i] = value[i] - (randValue * 2);
				}
			}
			// Make sure attributes have proper signs
			if (((i < 6) || (i == 11)) && value[i] < 0) {
				value[i] = -1 * value[i];
			} else if (((i >= 6) && (i <= 10)) && value[i] > 0) {
				value[i] = -1 * value[i];
			}
		}
		Gene offspring = new Gene(value[0], value[1], value[2], value[3], value[4], value[5], value[6]);
		return offspring;
	}

	public void generateNewGenes(ArrayList<Gene> geneList) {
		ArrayList<Gene> tempGeneList = new ArrayList<Gene>();
		for (Gene gene : geneList) {
			tempGeneList.add(gene);
		}
		geneList.clear();

		for (int i = 0; i < bestNGenes - 1; i++) {
			for (int j = i + 1; j < bestNGenes; j++) {
				Gene offSpring = generateOffspring(tempGeneList.get(i), tempGeneList.get(j));
				geneList.add(offSpring);
			}
		}
		for (int i = 0; i < bestNGenes; i++) {
			Gene fittestGene = tempGeneList.get(i);
			geneList.add(fittestGene);
		}
		tempGeneList.clear();
	}
	
	private double runTestGA(Gene currGene) {
		m_searchDemo.setWeight(currGene.w[0], currGene.w[1], currGene.w[2], currGene.w[3]);
		m_searchDemo.setSimilarityWeight(currGene.w[4], currGene.w[5], currGene.w[6]);
		return Precision.evaluate(m_searchDemo);
	}

	public void runGA() {
		ArrayList<Gene> geneList = new ArrayList<Gene>();

		generateRandomGenes(geneList);
		for (int i = 0; i < maxGenerations; i++) {
			System.out.println("--------- Generation " + i + " ----------");
			System.err.println("--------- Generation " + i + " ----------");
			generateNewGenes(geneList);
			generateRandomGenes(geneList);
			for (Gene currGene : geneList) {
				currGene.precision = runTestGA(currGene);
				System.out.println(currGene.w[0] + "--" + currGene.w[1] + "--" + currGene.w[2] + "--" + currGene.w[3] + " ||| " + currGene.w[4] + "--" + currGene.w[5] + "--" + currGene.w[6]);
				System.out.println("Mean Precision : " + currGene.precision);

			}
			geneList.sort(new GeneComparator());
			for (int z = 0; z < bestNGenes; z++) {
				Gene currGene = geneList.get(z);
				System.err.println(currGene.w[0] + "--" + currGene.w[1] + "--" + currGene.w[2] + "--" + currGene.w[3]  + " ||| " + currGene.w[4] + "--" + currGene.w[5] + "--" + currGene.w[6]);
				System.err.println("Mean Precision : " + currGene.precision);
			}
		}
	}
}