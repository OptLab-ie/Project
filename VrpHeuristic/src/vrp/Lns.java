package vrp;

import java.util.*;

public class Lns {

	private final Random rand;
	private VrpProblem problem;

	public Lns(VrpProblem problem, Random rand) {
		this.problem = problem;
		this.rand = rand;
	}

	public void remove(VrpSolution sol, int numToRemove) {
		List<Integer> removedSites = new ArrayList<>();
		List<Integer> assignedSites = new ArrayList<>();
		List<Date> dates = sol.getDates();

		int depot = 0;
		// 해당하는 날짜 객체를 가져옴
		for(Date date : dates){
			for (List<Integer> route : date.getRoutes()) {
				for (Integer siteID : route) {
					if(siteID == depot) continue;
					assignedSites.add(siteID);
				}
			}
		}

		// remove
		while(removedSites.size() != numToRemove){
			// take a random removed node
			// 해당 날짜에 경로에 포함되어 있는 sites 중 제거할 인덱스를 하나 추출해야함
			double random = rand.nextDouble();    // 0 ~ 1 사이 값
			int index = (int) (random * assignedSites.size());
			Integer removeSiteId = assignedSites.get(index);
			assignedSites.remove(removeSiteId);
			removedSites.add(removeSiteId);
		}
		// build the new solution
		buildRoutesWithoutRemovedSites(dates, removedSites);
//		d.updateRoutes(newRoutes);
		// Date 객체의 경로 검사

//		Iterator<List<Integer>> it = d.getRoutes().iterator();
//		while (it.hasNext()){
//
//			it.remove();
//		}
		removeEmptyRoute(dates);
		removeEmptyDate(sol, dates);

		sol.addRemovedSites(removedSites);
	}

	public void removeEmptyRoute(List<Date> dates){
		int emptyRoute = 2;
		for(Date date : dates){
			for (int i = 0; i < problem.getNumVehicles(); i++) {
				for(List<Integer> route : date.getRoutes()){
					if(route.size() == emptyRoute){
						date.getRoutes().remove(route);
						break;
					}
				}
			}
		}
	}

	public void removeEmptyDate(VrpSolution sol, List<Date> dates){
		for(Date date : dates){
			if(date.getRoutes().isEmpty()){
				sol.getDates().remove(date);
				sol.getNoRoutesDates().add(date.getDate());
				sol.getHaveRoutesDates().remove(date.getDate());
				break;
			}
		}
	}

	public void buildRoutesWithoutRemovedSites(List<Date> dates, List<Integer> removedSites) {
		for(Date date : dates){
			for(List<Integer> route : date.getRoutes()){
				route.removeAll(removedSites);
			}
		}
	}

	public void repair(VrpSolution sol){

		List<Integer> removedSites = sol.getRemovedSites();
		List<Integer> noRoutesDates = sol.getNoRoutesDates();

		List<List<Integer>> availableRoutes = new ArrayList<>();

		while(!removedSites.isEmpty()) {
			Integer removedId = removedSites.get(0);    // 삽일할 노드

			availableRoutes.clear();
			findAvailableRoutes(sol, availableRoutes, removedId);

			boolean didYouInsert = false;
			do{
				// 삽입할 수 있는 경로가 없는 경우
				if(availableRoutes.isEmpty()){
					didYouInsert = insertedWithoutMinSite(removedId, sol);
					removedSites.remove(removedId);
					break;
				}

				// find nearest customer from removedCustomer
				Integer minSiteId = findMinSite(problem, removedId, availableRoutes);
				System.out.println("가장 가까운 현장 id : " + minSiteId);

				// insert removed site to route including nearest site
				for(List<Integer> route : availableRoutes) {
					if (route.contains(minSiteId)) {
						didYouInsert = insertedSite(sol, route, minSiteId, removedId);
						if(didYouInsert) {
							removedSites.remove(removedId);
						} else{
							availableRoutes.remove(route);
						}
						break;
					}
				}
			}while(didYouInsert == false);

		}

	}

	private Integer chooseRandomDate(List<Integer> noRoutesDates) {
		double random = rand.nextDouble();
		int index = (int) (random * noRoutesDates.size());
		Integer date = noRoutesDates.get(index);
		return date;
	}

	private void findAvailableRoutes(VrpSolution sol, List<List<Integer>> availableRoutes, Integer removedId) {
		List<Site> sites = problem.getSites();

		// 노드에 저장된 방문가능한 날짜를 불러옴
		Site site = sites.get(removedId);
		int availableDate = site.getAvailableDate();

		for(Date date : sol.getDates()){
			// 1. 가능한 날짜 이후의 경로만 고려
			if(date.getDate() > availableDate){
				for(List<Integer> route : date.getRoutes()) {
					// 2. 삽입할 노드의 서비스 타임 + 경로 총 소요시간 < 설정한 시간 이 조건을 만족하는 경로들만 탐색
					if(sol.calWorkingTime(route) + site.getServiceTime() < problem.getTimeLimit()){
						// 삽입가능한 경로 리스트 추가
						availableRoutes.add(route);
					}
				}
			}
		}
	}

	public boolean insertedWithoutMinSite(Integer removedId, VrpSolution sol){
		// 차량 운행을 1대만 스케줄링하고 있는 날짜가 있는 경우
		for(Date date : sol.getDates()){
			if(date.getRoutes().size() < problem.getNumVehicles()){
				List<Integer> route = createNewRoute(removedId);
				date.addRoute(route);
				return true;
			}
		}

		createNewSchedule(removedId, sol);
		return true;
	}

	private void createNewSchedule(Integer removedId, VrpSolution sol) {
		// 경로가 존재하지 않는 날짜 중 어느 날짜에 삽입할지 랜덤으로 결정
		List<Integer> noRoutesDates = sol.getNoRoutesDates();
		Integer date = chooseRandomDate(noRoutesDates);
		// 새로운 Date 객체 생성
		Date D = new Date(date);
		// 새로운 경로 생성
		List<Integer> route = createNewRoute(removedId);

		// 해당 날짜 경로에 추가
		D.addRoute(route);
		// 솔루션에 날짜 객체 추가
		sol.addDate(D);

		sol.getNoRoutesDates().remove(date);
		sol.getHaveRoutesDates().add(date);
	}

	public List<Integer> createNewRoute(Integer removedId){
		List<Integer> route = new ArrayList<>();
		// 출발지
		route.add(0);
		// 방문할 현장 추가
		route.add(removedId);
		// 도착지
		route.add(0);
		return route;
	}

	public Integer findMinSite(VrpProblem problem, Integer removedId, List<List<Integer>> availableRoutes){
		double min = Double.MAX_VALUE;
		int minSiteId = 0;
		for(List<Integer> route : availableRoutes) {
			for(Integer assignedId : route){
				double dis = problem.getDis(removedId, assignedId);
				if(dis < min) {
					min = dis;
					minSiteId = assignedId;
				}
			}
		}
		return minSiteId;
	}

	public boolean insertedSite(VrpSolution sol, List<Integer> route, Integer minId, Integer insertId) {

		if(minId == 0){
			List<Integer> route_init = copyRoute(route);
			List<Integer> route_end = copyRoute(route);

			route_init.add(1, insertId);
			route_end.add(route.size()-1,insertId);
			// 여기서 체크후에 안되면 return false
			if(overTimeLimit(sol, route_init) && overTimeLimit(sol, route_end))
				return false;

			if(calCost(route_init) < calCost(route_end)){
				// case insert init
				route.add(1, insertId);
			}else{
				// case insert end
				route.add(route.size()-1,insertId);
			}
			return true;
		}

		// 경로상 가장 가까운 현장의 위치를 찾는다
		int index = route.indexOf(minId);

		List<Integer> route_back = copyRoute(route);
		List<Integer> route_front = copyRoute(route);

		route_back.add(index+1, insertId);
		route_front.add(index, insertId);
		if(overTimeLimit(sol, route_back) && overTimeLimit(sol, route_front))
			return false;

		if(calCost(route_back) < calCost(route_front)){
			route.add(index+1, insertId);
		}else{
			route.add(index, insertId);
		}
		return true;
	}

	public boolean overTimeLimit(VrpSolution sol, List<Integer> route){
		return sol.calWorkingTime(route) > problem.getTimeLimit();
	}

	public List<Integer> copyRoute(List<Integer> route){
		List<Integer> copyRoute = new ArrayList<>();
		for(Integer Site : route){
			copyRoute.add(Site);
		}
		return copyRoute;
	}


	public double calCost(List<Integer> route){
		double cost = 0.;
		for(int i = 0; i < route.size()-1; i++) {
			Integer now = route.get(i);
			Integer next = route.get(i + 1);
			cost += problem.getTravelTime(now, next);
		}
		return cost;
	}


//	private int chooseByRankAndRelatedness(HashSet<Integer> remaining, VrpSolution sol, int rank, int cityId,
//			int[] cityVehicles) {
//		// the head is the least element, i.e. the one whose compareTo(any other
//		// element) is less than 0
//		// we want the head of the queue to be the least related customer
//		// thus, we want compareTo to return negative if the argument is more related
//		// than us
//		PriorityQueue<CityRelatedness> heap = new PriorityQueue<CityRelatedness>(rank + 1);
//		for (int remainingCityId : remaining) {
//			double relatedness = relatedness(cityId, remainingCityId, sol, cityVehicles);
//			if (heap.size() < rank + 1 || relatedness > heap.peek().relatedness) {
//				if (heap.size() == rank + 1) {
//					heap.remove();
//				}
//				heap.add(new CityRelatedness(remainingCityId, relatedness));
//			}
//		}
//		return heap.peek().cityId;
//	}
//
//	private double relatedness(int nodeId1, int nodeId2, VrpSolution sol, int[] cityVehicles) {
//		double dist = sol.getProblem().getDistances()[nodeId1][nodeId2];
//		double denom = dist / maxDist;
//		if (cityVehicles[nodeId1] == cityVehicles[nodeId2]) {
//			denom += 1.0;
//		}
//		return 1 / denom;
//	}
//	
//	private class CityRelatedness implements Comparable<CityRelatedness> {
//	    public int cityId;
//	    public double relatedness;
//
//	    public CityRelatedness(int cityId, double relatedness) {
//	      this.cityId = cityId;
//	      this.relatedness = relatedness;
//	    }
//	    
//	    @Override
//	    public int compareTo(CityRelatedness other) {
//	      return (int)Math.signum(this.relatedness - other.relatedness);
//	    }
//	  }

}
