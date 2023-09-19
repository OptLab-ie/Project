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
    private static List<List<Integer>> routeDate;
    private static List<Date> routes = new ArrayList<>();
    private static String dataName = "initialtest";
    public static List<Date> readInitialSolution() throws IOException {
        BufferedReader reader = new BufferedReader(
                new FileReader("/Users/hijieung/Desktop/"
                        + dataName
                        + ".txt")
        );

        while ((str = reader.readLine()) != null) {
            if (str.length() == 1) {
                if(routeDate!=null){
                    Date date = new Date(n, routeDate);
                    routes.add(date);
                }
                n = Integer.parseInt(str);
                routeDate = new ArrayList<>();
                cnt = 0;
            } else {
                routeDate.add(new ArrayList<>());
                StringTokenizer st = new StringTokenizer(str);
                while (st.hasMoreTokens()) {
                    routeDate.get(cnt).add(Integer.parseInt(st.nextToken()));
                }
                cnt++;
            }
        }
        Date date = new Date(n, routeDate);
        routes.add(date);
        for(int i=0; i<routes.size();i++){
            System.out.println(routes.get(i).getDate());
            for(int j=0; j<routes.get(i).getRoutes().size(); j++){
                System.out.println(routes.get(i).getRoutes().get(j));
            }
        }
        return routes;
    }
//    public static void main(String[] args) throws IOException {
//        readInitialSolution();
//    }
}

