package vrp;

import java.util.ArrayList;
import java.util.List;

public class VrpProblem {

	private int numCustomers;
	private int numVehicles;
	private int[] availableDate;
	private double[][] dists;
	private int[] serviceTimes;

	private int timeLimit;
	private List<List<Integer>> availableDays = new ArrayList<>();
	private double[][] travelTimes;

	private List<Site> sites = new ArrayList<>();
//	private double[] distsFromDepot;

	public VrpProblem(int numCustomers, int[] availableDate, double[][] dists, int[] serviceTimes) {
		this.numCustomers = numCustomers;
		setNumVehicles(2);
		this.availableDate = availableDate;
		this.dists = dists;
		this.serviceTimes = serviceTimes;

		setNumVehicles(2);
		setTimeLimit(240);
		createSites();
		buildAvaiableDays();
		buildTravelTimes();
	}

	public void createSites(){
		for(int i = 0; i < availableDate.length; i++){
			Site site = new Site(availableDate[i], serviceTimes[i]);
			this.sites.add(site);
		}
	}

	public void setNumVehicles(int numVehicles) {
		this.numVehicles = numVehicles;
	}

	public int getNumVehicles(){
		return this.numVehicles;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void buildAvaiableDays() {
		// 6월기준 30일까지
		int endDay = 31;
		for (int i = 0; i < numCustomers; i++) {
			availableDays.add(new ArrayList<>());
			for (int day = availableDate[i]; day < endDay; day++) {
				availableDays.get(i).add(day);
//				System.out.println(T.get(i).get(t-1));
			}
		}
	}

	public void buildTravelTimes() {
		travelTimes = new double[numCustomers][numCustomers];
		for (int i = 0; i < numCustomers; i++) {
			for (int j = 0; j < numCustomers; j++) {
				if (i == j)
					travelTimes[i][j] = 0;
				else
					travelTimes[i][j] = (dists[i][j]) / 1000.;
			}
		}
		// 시간 = 거리/속력
		// 60km/h = 1000m/m
	}

	public int getNumCustomers() {
		return this.numCustomers;
	}

	public double[][] getDistances() {
		// TODO Auto-generated method stub
		return this.dists;
	}

	public List<Site> getSites(){
		return this.sites;
	}

	public int getTimeLimit(){
		return this.timeLimit;
	}

	public double getDis(int node1, int node2) {
		return this.dists[node1][node2];
	}

	public double getTravelTime(int node1, int node2) {
		return this.travelTimes[node1][node2];
	}

	public int getServiceTimeOfSite(int siteId){
		return serviceTimes[siteId];
	}

}
