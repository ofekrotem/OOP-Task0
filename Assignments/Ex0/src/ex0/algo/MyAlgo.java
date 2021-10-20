package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Call_A;
import ex0.simulator.Elevator_A;
import ex0.simulator.ElevetorCallList;

import java.util.*;

public class MyAlgo implements ElevatorAlgo {
    private Building _building;
    private ArrayList<Integer> floorOrder;
    private int[] dist;


    public MyAlgo(Building b) {
        _building = b;
        floorOrder = new ArrayList<Integer>();
        dist = new int[_building.numberOfElevetors()];
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
        for (int i = 0; i < dist.length; i++) { //initialize array
            dist[i] = Integer.MAX_VALUE;
        }
        if (_building.numberOfElevetors() == 1) { //base case
            return 0;
        }
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getState() == Elevator.LEVEL) {
                dist[i] = Math.abs(_building.getElevetor(i).getPos() - c.getSrc());
            }
            if (_building.getElevetor(i).getState() == c.getType()) {
                switch (c.getType()) {
                    case CallForElevator.UP:
                        if (_building.getElevetor(i).getPos() <= c.getSrc())
                            dist[i] = Math.abs(_building.getElevetor(i).getPos() - c.getSrc());
                    case CallForElevator.DOWN:
                        if (_building.getElevetor(i).getPos() >= c.getSrc())
                            dist[i] = Math.abs(_building.getElevetor(i).getPos() - c.getSrc());
                }
            }
        }
        return MinValueIndexArr(dist);
    }

    private int MinValueIndexArr(int[] arr) {
        int MinValue = arr[0];
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
        Elevator_A curr = (Elevator_A) _building.getElevetor(elev);
        if (curr.get_curr_calls().size() == 0) return;
        else if (curr.get_curr_calls().size() == 1) {
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.INIT)
                curr.goTo(curr.get_curr_calls().get(0).getSrc());
            else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(0).getState() == CallForElevator.GOING2SRC)
                curr.goTo(curr.get_curr_calls().get(0).getDest());
            else if (curr.get_curr_calls().get(0).getState() == CallForElevator.DONE) return;
            else curr.goTo(curr.get_curr_calls().get(0).getDest());
        }
        if (curr.get_curr_calls().size() > 1) {
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.INIT)
                curr.goTo(curr.get_curr_calls().get(0).getSrc());
            else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(0).getState() == CallForElevator.GOING2SRC) {
                orderSetter(curr);
                curr.goTo(floorOrder.get(floorOrder.size() - 1));
                switch (curr.getState()) {
                    case Elevator.UP:
                        if (curr.getPos() <= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                    case Elevator.DOWN:
                        if (curr.getPos() >= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                }
            } else if (curr.get_curr_calls().get(0).getState() == CallForElevator.DONE) {
                for (int i = 1; i < curr.get_curr_calls().size(); i++) {
                    if (curr.get_curr_calls().get(i).getState() != CallForElevator.DONE) {
                        if (curr.get_curr_calls().get(i).getState() == CallForElevator.INIT) {
                            curr.goTo(curr.get_curr_calls().get(i).getSrc());
                            break;
                        } else if (curr.getState() == Elevator.LEVEL && curr.get_curr_calls().get(i).getState() == CallForElevator.GOING2SRC) {
                            orderSetter(curr);
                            curr.goTo(floorOrder.get(floorOrder.size() - 1));
                            switch (curr.getState()) {
                                case Elevator.UP:
                                    if (curr.getPos() <= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                                case Elevator.DOWN:
                                    if (curr.getPos() >= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                            }
                        } else {
                            orderSetter(curr);
                            switch (curr.getState()) {
                                case Elevator.UP:
                                    if (curr.getPos() <= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                                case Elevator.DOWN:
                                    if (curr.getPos() >= floorOrder.get(0)) curr.stop(floorOrder.get(0));

                            }
                        }
                    }
                }
            }
                if (curr.get_curr_calls().get(0).getState() == CallForElevator.GOIND2DEST) {
                    orderSetter(curr);
                    switch (curr.getState()) {
                        case Elevator.UP:
                            if (curr.getPos() <= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                        case Elevator.DOWN:
                            if (curr.getPos() >= floorOrder.get(0)) curr.stop(floorOrder.get(0));
                    }

                }

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

