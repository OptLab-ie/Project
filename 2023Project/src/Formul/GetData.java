package Formul;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GetData {

	public static int n = 0;
	public static int K = 0;
	public static int Q = 0;
	public static int[] T;
	public static double[] s;
	public static double[][] d;
	public static double[][][] w;
	
	
	public static void main(String[] args) {
		
		Path path = Paths.get("//Users/hijieung/Desktop/Opt_Project/Project/Data/남구 데이터.txt");
		
		
		try {
			List<String> allLines = Files.readAllLines(path);
			String[] tmpdata;
			n = Integer.parseInt(allLines.get(0));
			K = 2;
			d = new double [n][n];
			T = new int [n];
			w = new double[n][n][K];
			
			for(int i=1; i<n+1; i++) {
				tmpdata = allLines.get(i).split(",");
				for(int j=0; j<n; j++) {
					d[i][j] = Double.parseDouble(tmpdata[j]);
				}
			}
			
			tmpdata = allLines.get(n+2).split(",");
			for(int i=0; i<n; i++) {
					T[i] = Integer.parseInt(tmpdata[i]);
			}
			
			tmpdata = allLines.get(n+3).split(",");
			for(int i=0; i<n; i++) {
					s[i] = Double.parseDouble(tmpdata[i]);
			}
			
			for(int i=0; i<n;i++) {
				for(int j=0; j<n; j++) {
					for(int k=0; k<K; k++) {
						if(i==j) w[i][j][k] = 0;
						else w[i][j][k] = (d[i][j])/1000.;
					}
				}
			}
			//시간 = 거리/속력
			//60km/h = 1000m/m
			
			System.out.println("n: "+n);
			System.out.println("K: "+K);
			System.out.println();
			System.out.println("거리는");
			for(int i=0; i<n; i++) {
				for(int j=0; j<n; j++) {
					System.out.print(d[i][j]+" ");
				}
				System.out.println();
			}
			
		}
		catch(IOException ie) {
			ie.printStackTrace();
		}
		
		
	}

}
