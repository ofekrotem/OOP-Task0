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


    public MyAlgo(Building b) {
        _building = b;
        floorOrder = new ArrayList<Integer>();
        time2Floor = new double[_building.numberOfElevetors()];
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
        Arrays.fill(time2Floor, Double.MAX_VALUE);
        if (_building.numberOfElevetors() == 1) { //base case
            return 0;
        }
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getState() == Elevator.LEVEL) {
                time2Floor[i] = TimeToGetThere(_building.getElevetor(i),c.getSrc() ) ;
            }
            if (_building.getElevetor(i).getState() == c.getType()) {
                switch (c.getType()) {
                    case CallForElevator.UP:
                        if (_building.getElevetor(i).getPos() <= c.getSrc())
                            time2Floor[i] = TimeToGetThere(_building.getElevetor(i),c.getSrc() ) ;
                    case CallForElevator.DOWN:
                        if (_building.getElevetor(i).getPos() >= c.getSrc())
                            time2Floor[i] = TimeToGetThere(_building.getElevetor(i),c.getSrc() ) ;
                }
            }
        }
        return MinValueIndexArr(time2Floor);
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
    public void cmdElevator(int elev) {
        Elevator_A curr = (Elevator_A) _building.getElevetor(elev); //The "elev" elevator in the building;
        if (curr.get_curr_calls().size() == 0) return; //No calls = Do nothing
        else if (curr.get_curr_calls().size() == 1) { //Only one call => Pick up from Src then go to Dest
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.INIT) { // Elevator hasn't  done anything yet with only call
                curr.goTo(curr.get_curr_calls().get(0).getSrc());//Pick up from Src
            } else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(0).getState() == CallForElevator.GOING2SRC) {
                curr.goTo(curr.get_curr_calls().get(0).getDest());
            } else if (curr.get_curr_calls().get(0).getState() == CallForElevator.DONE) return;
            else {
                curr.goTo(curr.get_curr_calls().get(0).getDest());
            }
        } else {
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.INIT) {
                curr.goTo(curr.get_curr_calls().get(0).getSrc());
            } else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(0).getState() == CallForElevator.GOING2SRC) {
                orderSetter(curr);
                curr.goTo(floorOrder.get(floorOrder.size() - 1));
                nextStop(curr);
            } else if (curr.get_curr_calls().get(0).getState() == CallForElevator.DONE) {
                for (int i = 1; i < curr.get_curr_calls().size(); i++) {
                    if (curr.get_curr_calls().get(i).getState() != CallForElevator.DONE) {
                        if (curr.get_curr_calls().get(i).getState() == CallForElevator.INIT) {
                            curr.goTo(curr.get_curr_calls().get(i).getSrc());
                            break;
                        } else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(i).getState() == CallForElevator.GOING2SRC) {
                            orderSetter(curr);
                            curr.goTo(floorOrder.get(floorOrder.size() - 1));
                            nextStop(curr);
                        } else {
                            orderSetter(curr);
                            nextStop(curr);
                        }
                    }
                }
            }
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.GOIND2DEST) {
                orderSetter(curr);
                nextStop(curr);
            }
        }
    }

    private void nextStop(Elevator curr) {
        switch (curr.getState()) {
            case Elevator.UP:
                if (curr.getPos() <= floorOrder.get(0)) curr.stop(floorOrder.get(0));
            case Elevator.DOWN:
                if (curr.getPos() >= floorOrder.get(0)) curr.stop(floorOrder.get(0));
        }
    }

    private void orderSetter(Elevator_A curr) {
        floorOrder.clear();
        floorOrder.add(curr.get_curr_calls().get(0).getDest());
        for (int i = 1; i < curr.get_curr_calls().size(); i++) {
            floorOrder.add(curr.get_curr_calls().get(i).getSrc());
            floorOrder.add(curr.get_curr_calls().get(i).getDest());
        }
        if (curr.getState() == Elevator.UP)
            Collections.sort(floorOrder);
        else Collections.sort(floorOrder, Collections.reverseOrder());
    }
}

