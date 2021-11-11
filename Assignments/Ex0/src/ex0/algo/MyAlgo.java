//ID1: 209132687 , ID2: 314666611
/**
 * ID1: 209132687 , ID2: 314666611
 */
package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;

public class MyAlgo implements ElevatorAlgo {
    private final Building _building;
    private ArrayList<Elevator> Elevators;
    private ArrayList<CallForElevator>[] Calls;
    private ArrayList<Integer> AvailableElevators;
    private ArrayList<Integer> howMuchCalls;

    /**
     * Constructor
     *
     * @param b Building
     */
    public MyAlgo(Building b) {
        _building = b;
        Elevators = new ArrayList<Elevator>();
        Calls = new ArrayList[_building.numberOfElevetors()];
        AvailableElevators = new ArrayList<Integer>();
        howMuchCalls = new ArrayList<Integer>();
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            Elevators.add(_building.getElevetor(i));
            Calls[i] = new ArrayList<CallForElevator>();
        }
    }
    public ArrayList<CallForElevator>[] getCalls() {
        return Calls;
    }
    public ArrayList<Integer> getHowMuchCalls() {
        return howMuchCalls;
    }
    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "MyAlgo";
    }

    /**
     * @return The index of the fastest elevator
     */
    protected int FastestElev() {
        double tmpSpeed = Elevators.get(0).getSpeed();
        int index = 0;
        for (int i = 1; i < _building.numberOfElevetors(); i++) {
            if (Elevators.get(i).getSpeed() > tmpSpeed) {
                tmpSpeed = Elevators.get(i).getSpeed();
                index = i;
            }
        }
        return index;
    }

    /**
     * Checks all Calls for all elevators to see if any elevator has a call that is simillar to this specific call (SRC and DST are relevant) and if that elevator checks if the elevator is 'busy' (has more calls then the avarage calls per elevator)
     *
     * @return
     */
    protected int areThereSimillarCalls(CallForElevator c) {
        double AvarageCalls = howMuchCalls.get(howMuchCalls.size() - 1) /  _building.numberOfElevetors();
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            switch (c.getType()) {
                case CallForElevator.UP:
                    for (int j = 0; j < Calls[i].size(); j++) {
                        if ((Calls[i].get(j).getType() == CallForElevator.UP) && (c.getSrc() <= Calls[i].get(j).getSrc()) && (Calls[i].get(j).getDest() <= c.getDest()) && (AvarageCalls > Calls[i].size()))
                            return i;
                    }
                case CallForElevator.DOWN:
                    for (int j = 0; j < Calls[i].size(); j++) {
                        if ((Calls[i].get(j).getType() == CallForElevator.DOWN) && (c.getSrc() == Calls[i].get(j).getSrc()) && (c.getDest() > Calls[i].get(j).getDest()) && (AvarageCalls > Calls[i].size()))
                            return i;
                        if ((c.getDest() == Calls[i].get(j).getDest()) && (Calls[i].get(j).getSrc() < c.getSrc()) && (AvarageCalls > Calls[i].size())) {
                            return i;
                        }
                    }
                default:
                    continue;
            }
        }
        return (-7);
    }

    /**
     * @param c the specific call recived
     * @return the index of the closest free elevator to SRC of c
     */
    protected int whoIsClosestt(CallForElevator c) {
        int index = 0;
        int dist = Math.abs(_building.getElevetor(AvailableElevators.get(0)).getPos() - c.getSrc());
        for (int i = 1; i < AvailableElevators.size(); i++) {
            if (Math.abs(_building.getElevetor(AvailableElevators.get(i)).getPos() - c.getSrc()) < dist) {
                index = i;
                dist = Math.abs(_building.getElevetor(AvailableElevators.get(i)).getPos() - c.getSrc());
            }
        }
        return AvailableElevators.get(index);
    }

    /**
     * @return the index of the least occupied elevator
     */
    protected int MostFreeElevator() {
        int callsnum = howMuchCalls.get(0);
        int index = 0;
        for (int i = 1; i < Elevators.size(); i++) {
            if (howMuchCalls.get(i) < callsnum) {
                callsnum = Calls[i].size();
                index = i;
            }
        }
        return index;
    }

    /**
     * Assigns call c to elevator
     *
     * @param c the call for elevator (src, dest)
     * @return index of elevator assigned to
     */
    @Override
    public int allocateAnElevator(CallForElevator c) {
        if (_building.numberOfElevetors() == 1) {
            Calls[0].add(c);
            return 0;
        }
        AvailableElevators.clear();
        for (int i = 0; i < this._building.numberOfElevetors(); i++) {
            if (Calls[i].size() == 0) {
                AvailableElevators.add(i);
            }
        }
        switch (AvailableElevators.size()) {
            case 1:
                Calls[AvailableElevators.get(0)].add(c);
                return AvailableElevators.get(0);
            case 0:
                double yahas = (Math.abs(c.getDest() - c.getSrc())) / (_building.maxFloor() - _building.minFloor());
                int a = FastestElev();
                if (yahas > 0.5) {
                    Calls[a].add(c);
                    return a;
                } else {
                    howMuchCalls.clear();
                    int sum = 0;
                    for (int i = 0; i < _building.numberOfElevetors(); i++) {
                        howMuchCalls.add(Calls[i].size());
                        sum += Calls[i].size();
                    }
                    howMuchCalls.add(sum);
                    a = areThereSimillarCalls(c);
                    if (a >= 0) {
                        Calls[a].add(c);
                        return a;
                    } else {
                        a = MostFreeElevator();
                        Calls[a].add(c);
                        return a;
                    }
                }
            default:
                int index = whoIsClosestt(c);
                Calls[index].add(c);
                return index;
        }
    }

    /**
     * Gives commands to the "elev" elevator
     *
     * @param elev the current Elevator index on which the operation is performs.
     */
    public void cmdElevator(int elev) {
        for (int i = 0; i < Calls[elev].size(); i++) {
            if (Calls[elev].get(i).getState() == CallForElevator.DONE)
                Calls[elev].remove(i);
        }
        if (Calls[elev].size() == 0)
            return;
        else {
            if (Elevators.get(elev).getState() == Elevator.LEVEL) {
                switch (Calls[elev].get(0).getState()) {
                    case CallForElevator.GOIND2DEST:
                        Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
                        for (int i = 1; i < Calls[elev].size(); i++) {
                            switch (Calls[elev].get(0).getType()) {
                                case CallForElevator.UP:
                                    switch (Calls[elev].get(i).getType()) {
                                        case CallForElevator.UP:
                                            if (Calls[elev].get(0).getSrc() <= Calls[elev].get(i).getSrc() && Calls[elev].get(0).getDest() == Calls[elev].get(i).getDest()) {
                                                Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
                                                Calls[elev].remove(i);
                                                break;
                                            } else if (Calls[elev].get(0).getSrc() == Calls[elev].get(i).getSrc() && Calls[elev].get(0).getDest() >= Calls[elev].get(i).getDest()) {
                                                Elevators.get(elev).stop(Calls[elev].get(i).getDest());
                                                Calls[elev].remove(i);
                                                break;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case CallForElevator.DOWN:
                                    switch (Calls[elev].get(i).getType()) {
                                        case CallForElevator.DOWN:
                                            if (Calls[elev].get(0).getDest() == Calls[elev].get(i).getDest() && Calls[elev].get(0).getSrc() > Calls[elev].get(i).getSrc()) {
                                                Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
                                                Calls[elev].remove(i);
                                            } else if (Calls[elev].get(0).getSrc() == Calls[elev].get(i).getSrc() && Calls[elev].get(0).getDest() < Calls[elev].get(i).getDest()) {
                                                Elevators.get(elev).stop(Calls[elev].get(i).getDest());
                                                Calls[elev].remove(i);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                            }
                        }
                        break;
                    default:
                        Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
                        break;
                }
            }
        }
    }
}