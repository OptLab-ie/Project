package vrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class InitialSolutionReader {
    private static String str;
    private static int n = 0;
    private static int cnt = 0;
    private static List<List<Integer>> routes;
    private static List<Date> dates = new ArrayList<>();
    private static String dataName = "east-test";
    public static List<Date> readInitialSolution() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader("/Users/phs/Desktop/OptLab/Project/InitialSolution/"
                        + dataName
                        + ".txt")
        );

        while ((str = reader.readLine()) != null) {
            if (str.length() == 1) {
                if(routes !=null){
                    Date date = new Date(n, routes);
                    dates.add(date);
                }
                n = Integer.parseInt(str);
                routes = new ArrayList<>();
                cnt = 0;
            } else {
                routes.add(new ArrayList<>());
                StringTokenizer st = new StringTokenizer(str);
                while (st.hasMoreTokens()) {
                    routes.get(cnt).add(Integer.parseInt(st.nextToken()));
                }
                cnt++;
            }
        }
        Date date = new Date(n, routes);
        dates.add(date);
        for(int i = 0; i< dates.size(); i++){
            System.out.println(dates.get(i).getDate());
            for(int j = 0; j< dates.get(i).getRoutes().size(); j++){
                System.out.println(dates.get(i).getRoutes().get(j));
            }
        }
        return dates;
    }
//    public static void main(String[] args) throws IOException {
//        readInitialSolution();
//    }
}

