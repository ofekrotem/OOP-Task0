package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Call_A;
import ex0.simulator.Elevator_A;
import ex0.simulator.ElevetorCallList;

import java.util.*;

public class MyAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1;
    private Building _building;
    private int numOfFloors;
    private ArrayList<Integer> floorOrder;
    private int[] dist;


    public MyAlgo(Building b) {
        _building = b;
        numOfFloors = _building.maxFloor() + Math.abs(_building.minFloor());
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
        for (int i = 0; i<_building.numberOfElevetors();i++){
            dist[i]=Integer.MAX_VALUE;
        }
        if (_building.numberOfElevetors() == 1) {
            return 0;
        }
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getState() == Elevator.LEVEL) {
                dist[i]=Math.abs(_building.getElevetor(i).getPos()) - c.getSrc();
            }
            if (_building.getElevetor(i).getState() == c.getType()) {
                switch (c.getType()) {
                    case UP:
                        if (_building.getElevetor(i).getPos()<=c.getSrc())
                            dist[i]=Math.abs(_building.getElevetor(i).getPos()) - c.getSrc();
                    case DOWN:
                        if (_building.getElevetor(i).getPos()>=c.getSrc())
                            dist[i]=Math.abs(_building.getElevetor(i).getPos()) - c.getSrc();
                }
            }
        }
        return MinValueIndexArr(dist);
    }

    private int MinValueIndexArr(int[] arr) {
        int MinValue = arr[0];
        int index=0;
        for(int i =1; i<arr.length;i++){
            if(arr[i]<MinValue){
                MinValue = arr[i];
                index = i;
            }
        }
        return index;
    }

    @Override
    public void cmdElevator(int elev) {
        Elevator_A curr = (Elevator_A) _building.getElevetor(elev);
        if (curr.get_curr_calls().size()>0) {
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.INIT)
                curr.goTo(curr.get_curr_calls().get(0).getSrc());
            if (curr.get_curr_calls().get(0).getState() == CallForElevator.GOIND2DEST) {
                orderSetter(curr);
                curr.goTo(floorOrder.get(0));
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
        Collections.sort(floorOrder);
    }
}

