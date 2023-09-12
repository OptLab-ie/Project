package vrp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;


public class VrpReader {

	private static int n = 0;
	private static int[] startDays;
	private static int[] serviceTimes;
	private static double[][] dists;
	private static String dataName = "gwangsan-data-n15";


	private static Path path = Paths.get("/Users/phs/Desktop/OptLab/Project/Data/"
			+ dataName
			+ ".txt");

	public static VrpProblem readDataInstance() throws IOException {
		List<String> allLines = Files.readAllLines(path);
		String[] tmpdata;
		n = Integer.parseInt(allLines.get(0));
//		n = 20;
		n = 19;
		dists = new double[n][n];
		serviceTimes = new int[n];
		startDays = new int[n];

		Random rand = new Random();
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(i == j) {
					dists[i][j] = 0.;
				}
				dists[i][j] = rand.nextDouble() * 10000;
			}
		}
		for (int i = 0; i < n; i++) {
			startDays[i] = rand.nextInt(30);
		}
//		for(int i=1; i<n+1; i++) {
//			tmpdata = allLines.get(i).split(",");
//			for(int j=0; j<n; j++) {
//				dists[i-1][j] = Double.parseDouble(tmpdata[j]);
//			}
//		}
//		
//		tmpdata = allLines.get(n+2).split(",");
////		tmpdata = allLines.get(91).split(",");
//		for(int i=0; i<n; i++) {
//				startDays[i] = Integer.parseInt(tmpdata[i]);
//				System.out.println(tmpdata[i]);
//		}
//		
//		tmpdata = allLines.get(n+2).split(",");
////		tmpdata = allLines.get(92).split(",");
//		for(int i=0; i<n; i++) {
//			serviceTimes[i] = Integer.parseInt(tmpdata[i]);
//		}

		System.out.println("n: "+n);
		System.out.println();
		System.out.println("거리는");
		for(int i=0; i<n; i++) {
			for(int j=0; j<n; j++) {
				System.out.print(dists[i][j]+" ");
			}
			System.out.println();
		}

		return new VrpProblem(n, startDays, dists, serviceTimes);
	}

}