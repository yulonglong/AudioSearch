package Distance;

/**
 * Created by workshop on 9/18/2015.
 */
// City Block is equivalent to Manhattan
public class CityBlock {
	public static double getDistance(double[] query1, double[] query2){
        if (query1.length != query2.length){
            System.err.println("The dimension of the two vectors does not match!");
            System.exit(1);
        }
        
        double total = 0;
        for (int i = 0; i < query1.length; i++){
        	total += Math.abs(query1[i]-query2[i]);
        }
        return total;
    }
}
