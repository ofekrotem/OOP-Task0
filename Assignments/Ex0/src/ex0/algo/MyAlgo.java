package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Elevator_A;

import java.util.*;

public class MyAlgo implements ElevatorAlgo {
    private final Building _building;
    private ArrayList<Integer> floorOrder;
    private double[] time2Floor;
    private ArrayList<Elevator> Elevators;
    private ArrayList<CallForElevator>[] Calls;
    private ArrayList<Integer> AvailableElevators;
    private ArrayList<Integer> CommonEndpoints;


    public MyAlgo(Building b) {
        _building = b;
        floorOrder = new ArrayList<Integer>();
        time2Floor = new double[_building.numberOfElevetors()];
        Elevators = new ArrayList<Elevator>();
        Calls = new ArrayList[_building.numberOfElevetors()];
        AvailableElevators = new ArrayList<Integer>();
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            Elevators.add(_building.getElevetor(i));
            Calls[i] = new ArrayList<CallForElevator>();
        }
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "MyAlgo";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        AvailableElevators.clear();
        if (_building.numberOfElevetors() == 1) {
            Calls[0].add(c);
            return 0;
        }
        for (int i = 0; i < this._building.numberOfElevetors(); i++) {
            if (Calls[i].size() == 0) {
                AvailableElevators.add(i);
            }
        }
        switch (AvailableElevators.size()) {
            case 1: {
                Calls[AvailableElevators.get(0)].add(c);
                return AvailableElevators.get(0);
            }
            case 0: {
                CommonEndpointsInit(c, Calls);
                switch (CommonEndpoints.size()) {
                    case 0:
                        Calls[lessOcupiedElev()].add(c);
                        return lessOcupiedElev();
                    default:
                        Calls[MostFreeElev()].add(c);
                        return MostFreeElev();
                }
            }
            default: {
                int forReturn = whoIsClosestt(AvailableElevators, c);
                Calls[forReturn].add(c);
                return forReturn;
            }
        }
        /*Arrays.fill(time2Floor, Double.MAX_VALUE);
        if (_building.numberOfElevetors() == 1) { //base case
            return 0;
        }
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getState() == Elevator.LEVEL) {
                time2Floor[i] = TimeToGetThere(_building.getElevetor(i), c.getSrc());
            }
            if (_building.getElevetor(i).getState() == c.getType()) {
                switch (c.getType()) {
                    case CallForElevator.UP:
                        if (_building.getElevetor(i).getPos() < (c.getSrc() - _building.getElevetor(i).getSpeed() - _building.getElevetor(i).getStopTime()))
                            time2Floor[i] = TimeToGetThere(_building.getElevetor(i), c.getSrc());
                        break;
                    case CallForElevator.DOWN:
                        if (_building.getElevetor(i).getPos() > (c.getSrc() + _building.getElevetor(i).getSpeed() + _building.getElevetor(i).getStopTime()))
                            time2Floor[i] = TimeToGetThere(_building.getElevetor(i), c.getSrc());
                        break;
                }
            }
        }
//        Elevator_A elev = (Elevator_A) _building.getElevetor(MinValueIndexArr(time2Floor));
//        elev.get_curr_calls().add(c);
        return MinValueIndexArr(time2Floor);*/


    }

    private int whoIsClosestt(ArrayList<Integer> availableElevators, CallForElevator c) {
        int index = 0;
        int dist = Math.abs(_building.getElevetor(availableElevators.get(0)).getPos() - c.getSrc());
        for (int i = 1; i < availableElevators.size(); i++) {
            if (Math.abs(_building.getElevetor(availableElevators.get(i)).getPos() - c.getSrc()) < dist) {
                index = i;
                dist = Math.abs(_building.getElevetor(availableElevators.get(i)).getPos() - c.getSrc());
            }
        }
        return index;
    }

    private int lessOcupiedElev() {
        int index = 0;
        int callsnum = Calls[0].size();
        for (int i = 0; i < Elevators.size(); i++) {
            if (Calls[i].size() < callsnum) {
                callsnum = Calls[i].size();
                index = i;
            }
        }
        return index;
    }

    private int MostFreeElev() {
        int index = CommonEndpoints.get(0);
        int callsnum = Calls[CommonEndpoints.get(0)].size();
        for (int i = 1; i < CommonEndpoints.size(); i++) {
            if (Calls[CommonEndpoints.get(i)].size() < callsnum) {
                callsnum = Calls[CommonEndpoints.get(i)].size();
                index = CommonEndpoints.get(i);
            }
        }
        return index;
    }

    private void CommonEndpointsInit(CallForElevator c, ArrayList<CallForElevator>[] calls) {
        CommonEndpoints = new ArrayList<>();
        for (int i = 0; i < calls.length; i++) {
            for (int j = 0; j < calls[i].size(); j++) {
                if (Calls[i].get(j).getType() == c.getType()) {
                    if (Calls[i].get(j).getDest() == c.getDest() || Calls[i].get(j).getSrc() == c.getSrc()) {
                        CommonEndpoints.add(i);
                        break;
                    }
                }
            }
        }
    }

    private double TimeToGetThere(Elevator e, int floor) {
        double time = 0;
        if (e.getState() == Elevator.LEVEL) {
            time += (e.getTimeForClose() + e.getTimeForOpen() + e.getStartTime() + e.getStopTime() + (e.getSpeed() * Math.abs(e.getPos() - floor)));
        } else {
            time += (e.getTimeForOpen() + e.getStopTime() + (e.getSpeed() * Math.abs(e.getPos() - floor)));
        }
        return time;
    }

    private int MinValueIndexArr(double[] arr) {
        double MinValue = arr[0];
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < MinValue) {
                MinValue = arr[i];
                index = i;
            }
        }
        return index;
    }

    @Override
    public void cmdElevator(int elev)
    {
        if (Calls[elev].size() == 0) return;
        if (Elevators.get(elev).getState() == 0)
        {
          switch (Calls[elev].get(0).getState())
          {
              case 2:
                  Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
                  return;
              case 3:
                  Calls[elev].remove(0);
                  return;
              default:

          }

        }

    }




    private void nextStop(Elevator curr) {
        switch (curr.getState()) {
            case Elevator.UP:
                for (int i = 0; i < floorOrder.size(); i++) {
                    if (curr.getPos() < (floorOrder.get(i) - curr.getSpeed() - curr.getStopTime())) {
                        curr.stop(floorOrder.get(i));
                        break;
                    }
                }
                break;
            case Elevator.DOWN:
                for (int i = 0; i < floorOrder.size(); i++) {
                    if (curr.getPos() > (floorOrder.get(i) + curr.getSpeed() + curr.getStopTime())) {
                        curr.stop(floorOrder.get(i));
                        break;
                    }
                }
                break;
        }
    }

    private void orderSetter(Elevator_A curr) {
        floorOrder.clear();
        floorOrder.add(curr.get_curr_calls().get(0).getDest());
        for (int i = 1; i < curr.get_curr_calls().size(); i++) {
            floorOrder.add(curr.get_curr_calls().get(i).getSrc());
            floorOrder.add(curr.get_curr_calls().get(i).getDest());
        }
        Set<Integer> set = new HashSet<>(floorOrder);
        floorOrder.clear();
        floorOrder.addAll(set);
        if (curr.getState() == Elevator.UP)
            Collections.sort(floorOrder);
        else floorOrder.sort(Collections.reverseOrder());

    }
}