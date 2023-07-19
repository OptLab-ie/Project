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
	public static double[][] d;
	
	public static void main(String[] args) {
		
		Path path = Paths.get("C:\\Users\\최적화_연구실_PC1\\OneDrive - Chonnam National University\\바탕 화면\\OptLab-Project\\Project\\2023Project\\"
				+ "/south-data.txt");
		
		
		try {
			List<String> allLines = Files.readAllLines(path);
			n= 8;
			K= 3;
			d = new double [n][n];
			
			for(int i=0; i<n; i++) {
				String[] tmpdata = allLines.get(i).split(",");
				for(int j=0; j<n; j++) {
					d[i][j] = Double.parseDouble(tmpdata[j]);
				}
			}
			
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
