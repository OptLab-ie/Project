package vrp;

import java.util.ArrayList;
import java.util.List;

public class Date {
    private Integer date;   // id == index
    private List<List<Integer>> routes;
    private double totalCost;
    private double workingTime;

    public Date(int date){
        this.date = date;
        this.routes = new ArrayList<>();
    }
    public Date(int date, List<List<Integer>> routes){
        this.date = date;
        this.routes = routes;
    }

    public void setTotalCost(double totalCost){
        this.totalCost = totalCost;
    }
    public double getCost(){
        return this.totalCost;
    }
    public void setWorkingTime(double travelTime){
        this.workingTime = travelTime;
    }
    public double getWorkingTime(){
        return this.workingTime;
    }
    public void addRoute(List<Integer> route){
        this.routes.add(route);
    }

    public List<List<Integer>> getRoutes (){
        return this.routes;
    }
    public void updateRoutes(List<List<Integer>> routes){
        this.routes = routes;
    }
    public void setDate(Integer date){
        this.date = date;
    }
    public Integer getDate(){
        return this.date;
    }


}
