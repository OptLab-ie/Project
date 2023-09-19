package vrp.test;

import vrp.*;
import vrp.Date;

import java.io.IOException;
import java.util.*;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		VrpProblem problem = VrpReader.readDataInstance();

//		List<Date> dates = buildRoutes();    // build routes
		List<Date> dates = InitialSolutionReader.readInitialSolution();
		VrpSolution sol = new VrpSolution(dates, problem);    // create solution
//		sol.addDatesList(creatDummyRoutes(4));    // input date부터 30일까지(말일) Routes 더미 생성후 추가

		Random rand = new Random();
		Lns lns = new Lns(problem, rand);

		System.out.println("변경 전 총 거리 비용 : " + sol.calTotalCost());

		sol.showRoutes();    // 변경 전 경로 출력

		double beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
		for (int step = 0; step < 2; step++) {
			lns.remove(sol, 15);

			System.out.print("removedId : ");
			for (Integer removedId : sol.getRemovedSites()) {
				System.out.print(removedId + " ");
			}
			System.out.println();

			lns.repair(sol);
		}
		double afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
		double secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
		System.out.println("실행시간(m) : "+secDiffTime);

		System.out.println("복구된 차량 경로");
		Collections.sort(sol.getDates(), new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2) {
				return o1.getDate() - o2.getDate();
			}
		});
		sol.showRoutes();

		System.out.println("변경 후 총 거리 비용 : " + sol.calTotalCost());
	}

	public static List<Date> creatDummyRoutes(int date){
		List<Date> dateList = new ArrayList<>();
		int vehicle = 2;
		for(int i = date; i < 31; i++){
			List<List<Integer>> routes = new ArrayList<>(vehicle);
//			routes.add(new ArrayList<>());
//			routes.add(new ArrayList<>());
//			for(int k = 0; k < vehicle; k++){
//				List<Integer> route = new ArrayList<>();
//				route.add(0);
//				route.add(0);
//				routes.add(route);
//			}
			dateList.add(new Date(i, routes));
		}
		return dateList;
	}

	public static List<Date> buildRoutes(){
		List<Date> routes = new ArrayList<>();    // solution

		List<List<Integer>> routeDate1 = new ArrayList<>();    // route of date1
		routeDate1.add(new ArrayList<>());
		routeDate1.add(new ArrayList<>());
		// vehicle 0
		routeDate1.get(0).add(0);
		routeDate1.get(0).add(1);
		routeDate1.get(0).add(2);
		routeDate1.get(0).add(3);
		routeDate1.get(0).add(0);
		// vehicle 1
		routeDate1.get(1).add(0);
		routeDate1.get(1).add(4);
		routeDate1.get(1).add(5);
		routeDate1.get(1).add(6);
		routeDate1.get(1).add(0);
		Date r1 = new Date(1, routeDate1);
		routes.add(r1);

		List<List<Integer>> routeDate2 = new ArrayList<>();    // route of date1
		routeDate2.add(new ArrayList<>());
		routeDate2.add(new ArrayList<>());
		routeDate2.get(0).add(0);
		routeDate2.get(0).add(7);
		routeDate2.get(0).add(8);
		routeDate2.get(0).add(9);
		routeDate2.get(0).add(0);
		routeDate2.get(1).add(0);
		routeDate2.get(1).add(10);
		routeDate2.get(1).add(11);
		routeDate2.get(1).add(12);
		routeDate2.get(1).add(0);
		Date r2 = new Date(2, routeDate2);
		routes.add(r2);

		List<List<Integer>> routeDate3 = new ArrayList<>();    // route of date1
		routeDate3.add(new ArrayList<>());
		routeDate3.add(new ArrayList<>());
		routeDate3.get(0).add(0);
		routeDate3.get(0).add(13);
		routeDate3.get(0).add(14);
		routeDate3.get(0).add(15);
		routeDate3.get(0).add(0);
		routeDate3.get(1).add(0);
		routeDate3.get(1).add(16);
		routeDate3.get(1).add(17);
		routeDate3.get(1).add(18);
		routeDate3.get(1).add(0);
		Date r3 = new Date(3, routeDate3);
		routes.add(r3);

		return routes;
	}

}
