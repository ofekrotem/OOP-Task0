package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;

public class MyAlgo implements ElevatorAlgo {
    private final Building _building;
    private ArrayList<Integer> floorOrder;
    private ArrayList<Elevator> Elevators;
    private ArrayList<CallForElevator>[] Calls;
    private ArrayList<Integer> AvailableElevators;
    private ArrayList<Integer> CommonEndpoints;
    private Elevator[] ElevatorsSortedBySpeed;
    private ArrayList<Integer> howMuchCalls;


    public MyAlgo(Building b) {
        _building = b;
        floorOrder = new ArrayList<Integer>();
        Elevators = new ArrayList<Elevator>();
        Calls = new ArrayList[_building.numberOfElevetors()];
        AvailableElevators = new ArrayList<Integer>();
        CommonEndpoints = new ArrayList<>();
        howMuchCalls = new ArrayList<Integer>();
        ElevatorsSortedBySpeed = new Elevator[_building.numberOfElevetors()];
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            Elevators.add(_building.getElevetor(i));
            ElevatorsSortedBySpeed[i] = _building.getElevetor(i);
            Calls[i] = new ArrayList<CallForElevator>();
        }
        SortElevatorsBySpeed();
    }
    private int FastestElev(){
        double tmpSpeed= Elevators.get(0).getSpeed();
        int index=0;
        for (int i=1;i<_building.numberOfElevetors();i++){
            if(Elevators.get(i).getSpeed()>tmpSpeed){
                tmpSpeed=Elevators.get(i).getSpeed();
                index=i;
            }
        }
        return index;
    }
    /**
     * Code for sort + swap is taken from Data Structure course from last semester with changes to sort by speed.
     */
    private void SortElevatorsBySpeed() {
        for (int i = 0; i < ElevatorsSortedBySpeed.length; i++) {
            for (int j = 0; j < ElevatorsSortedBySpeed.length - 1 - i; j++)
                if (ElevatorsSortedBySpeed[j].getSpeed() > ElevatorsSortedBySpeed[j + 1].getSpeed())
                    swap(ElevatorsSortedBySpeed, j, j + 1);
        }
    }

    private static void swap(Elevator[] a, int i, int j) {
        Elevator temp = a[i];
        a[i] = a[j];
        a[j] = temp;
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
                double prop = (Math.abs(c.getDest() - c.getSrc())) / (_building.maxFloor() - _building.minFloor());
                if (prop > 0.75) {
                    Calls[FastestElev()].add(c);
                    return FastestElev();
                } else {
                    howMuchCalls.clear();
                    int sum = 0;
                    for (int i = 0; i < _building.numberOfElevetors(); i++) {
                        howMuchCalls.add(Calls[i].size());
                        sum += Calls[i].size();
                    }
                    howMuchCalls.add(sum);
                    int toReturn = areThereSimillarCalls();
                    if (areThereSimillarCalls() != -1) {
                        Calls[toReturn].add(c);
                        return toReturn;
                    } else {
                        Calls[MostFreeElevator(c)].add(c);
                        return MostFreeElevator(c);
                    }
                }
            default:
                int index=whoIsClosestt(AvailableElevators,c);
                Calls[index].add(c);
                return Elevators.get(index).getID();
        }
    }

    private int areThereSimillarCalls() {
        double AvarageCalls = howMuchCalls.get(howMuchCalls.size() - 1) / _building.numberOfElevetors();
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            switch (Calls[i].get(0).getType()) {
                case CallForElevator.UP:
                    for (int j = 0; j < Calls[i].size(); j++) {
                        if ((Calls[i].get(j).getType() == CallForElevator.UP) && (Calls[i].get(0).getSrc() <= Calls[i].get(j).getSrc()) && (Calls[i].get(j).getDest() <= Calls[i].get(0).getDest()) && (AvarageCalls > Calls[i].size()))
                            return i;
                    }
                case CallForElevator.DOWN:
                    for (int j = 0; j < Calls[i].size(); j++) {
                        if ((Calls[i].get(j).getType() == CallForElevator.DOWN) && (Calls[i].get(0).getSrc() >= Calls[i].get(j).getSrc()) && (Calls[i].get(0).getDest() >= Calls[i].get(j).getDest()) && (AvarageCalls > Calls[i].size()))
                            return i;
                        if ((Calls[i].get(0).getDest() == Calls[i].get(j).getDest()) && (Calls[i].get(j).getSrc() < Calls[i].get(0).getSrc())) {
                            if (AvarageCalls > Calls[i].size())
                                return i;
                        }
                    }
                default:

            }
        }
        return -1;
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
        return Elevators.get(index).getID();
    }

    private int MostFreeElevator(CallForElevator c) {
        //AvailableElevators.clear();
        int callsnum = howMuchCalls.get(0);
        int index=0;
        for (int i = 1; i < Elevators.size(); i++) {
            if (howMuchCalls.get(i) < callsnum) {
                callsnum = Calls[i].size();
                index=i;
            }
            return index;
        }
//        for (int i = 0; i < Elevators.size(); i++) {
//            if (Calls[i].size() <= callsnum + 1) {
//                AvailableElevators.add(i);
//            }
//        }
        return whoIsClosestt(AvailableElevators, c);
    }

    private int MostFreeElev(CallForElevator c) {
        AvailableElevators.clear();
        int index = CommonEndpoints.get(0);
        int callsnum = Calls[CommonEndpoints.get(0)].size();
        for (int i = 1; i < CommonEndpoints.size(); i++) {
            if (Calls[CommonEndpoints.get(i)].size() < callsnum) {
                callsnum = Calls[CommonEndpoints.get(i)].size();
                index = CommonEndpoints.get(i);
            }
        }
        for (int i = 1; i < CommonEndpoints.size(); i++) {
            if (Calls[CommonEndpoints.get(i)].size() <= callsnum + 1) {
                AvailableElevators.add(i);
            }
        }
        return whoIsClosestt(AvailableElevators, c);
    }

    @Override
    public void cmdElevator(int elev) {
        if (Calls[elev].size() == 0){
//            System.out.println("no calls"+_allElevators.get(elev).getID());
            return;
        }
        else {
            if(Elevators.get(elev).getState()==0&&Calls[elev].get(0).getState()==2){
//                System.out.println("Made it to SRC "+_allElevators.get(elev).getID());
                Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
                return;
            }
            if(Elevators.get(elev).getState()==0&&Calls[elev].get(0).getState()==3){
//                System.out.println("Made it to DEST "+_allElevators.get(elev).getID());
                Calls[elev].remove(0);
                cmdElevator(elev);
                return;
            }
            if(Elevators.get(elev).getState()==0&&Calls[elev].get(0).getState()!=2){
//                System.out.println("goTo command to "+_allElevators.get(elev).getID());
                Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
                for(int i=1;i<Calls[elev].size();i++){
                    if(Calls[elev].get(i).getType()==Calls[elev].get(0).getType()&&Calls[elev].get(0).getType()==1){
                        if(Elevators.get(elev).getPos() == Calls[elev].get(i).getSrc()&&Calls[elev].get(i).getDest()<Calls[elev].get(0).getDest()){
                            Elevators.get(elev).stop(Calls[elev].get(i).getDest());
                            break;
                        }
                    }
                    if(Calls[elev].get(i).getType()==Calls[elev].get(0).getType()&&Calls[elev].get(0).getType()==-1){
                        if(Calls[elev].get(i).getSrc()==Calls[elev].get(0).getSrc()&&Calls[elev].get(i).getDest()>Calls[elev].get(0).getDest()){
                            Elevators.get(elev).stop(Calls[elev].get(i).getDest());
                            Calls[elev].remove(i);
                            break;
                        }
                        else if(Calls[elev].get(i).getDest()==Calls[elev].get(0).getDest()&&Calls[elev].get(i).getSrc()<Calls[elev].get(0).getSrc()){
                            Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
                            Calls[elev].remove(i);
                            break;
                        }
                    }

                }
                return;
            }
        }
    }
//    public void cmdElevator(int elev) {
//        if (Calls[elev].size() == 0)
//            return;
//        else {
//            if (Elevators.get(elev).getState() == Elevator.LEVEL) {
//                switch (Calls[elev].get(0).getState()) {
//                    case CallForElevator.DONE:
//                        Calls[elev].remove(0);
//                        break;
//                    case CallForElevator.GOIND2DEST:
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                        break;
//                    default:
//                        System.out.println("goTo command to " + Elevators.get(elev).getID());
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                        for (int i = 1; i < Calls[elev].size(); i++) {
//                            if (Calls[elev].get(i).getType() == Calls[elev].get(0).getType() && Calls[elev].get(0).getType() == 1) {
//                                if (Calls[elev].get(i).getSrc() == Calls[elev].get(0).getSrc() && Calls[elev].get(i).getDest() < Calls[elev].get(0).getDest()) {
//                                    Elevators.get(elev).stop(Calls[elev].get(i).getDest());
//                                    Calls[elev].remove(i);
//                                    break;
//                                } else if (Calls[elev].get(i).getDest() == Calls[elev].get(0).getDest() && Calls[elev].get(i).getSrc() > Calls[elev].get(0).getSrc()) {
//                                    Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
//                                    Calls[elev].remove(i);
//                                    break;
//                                }
//                            }
//
//                            if (Calls[elev].get(i).getType() == Calls[elev].get(0).getType() && Calls[elev].get(0).getType() == -1) {
//                                if (Calls[elev].get(i).getSrc() == Calls[elev].get(0).getSrc() && Calls[elev].get(i).getDest() > Calls[elev].get(0).getDest()) {
//                                    Elevators.get(elev).stop(Calls[elev].get(i).getDest());
//                                    Calls[elev].remove(i);
//                                    break;
//                                } else if (Calls[elev].get(i).getDest() == Calls[elev].get(0).getDest() && Calls[elev].get(i).getSrc() < Calls[elev].get(0).getSrc()) {
//                                    Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
//                                    Calls[elev].remove(i);
//                                    break;
//                                }
//                            }
//
//                        }
//                        break;
//
//                }
//            }
//        }
//    }
//            if (Elevators.get(elev).getState() == 0 && Calls[elev].get(0).getState() == 2) {
//                System.out.println("Made it to SRC " + Elevators.get(elev).getID());
//                Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                return;
//            }
//            if (Elevators.get(elev).getState() == 0 && Calls[elev].get(0).getState() == 3) {
//                System.out.println("Made it to DEST " + Elevators.get(elev).getID());
//                Calls[elev].remove(0);
//                cmdElevator(elev);
//                return;
//            }
//            if (Elevators.get(elev).getState() == 0 && Calls[elev].get(0).getState() != 2) {
//                System.out.println("goTo command to " + Elevators.get(elev).getID());
//                Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                for (int i = 1; i < Calls[elev].size(); i++) {
//                    if (Calls[elev].get(i).getType() == Calls[elev].get(0).getType() && Calls[elev].get(0).getType() == 1) {
//                        if (Calls[elev].get(i).getSrc() == Calls[elev].get(0).getSrc() && Calls[elev].get(i).getDest() < Calls[elev].get(0).getDest()) {
//                            Elevators.get(elev).stop(Calls[elev].get(i).getDest());
//                            Calls[elev].remove(i);
//                            break;
//                        } else if (Calls[elev].get(i).getDest() == Calls[elev].get(0).getDest() && Calls[elev].get(i).getSrc() > Calls[elev].get(0).getSrc()) {
//                            Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
//                            Calls[elev].remove(i);
//                            break;
//                        }
//                    }
//
//                    if (Calls[elev].get(i).getType() == Calls[elev].get(0).getType() && Calls[elev].get(0).getType() == -1) {
//                        if (Calls[elev].get(i).getSrc() == Calls[elev].get(0).getSrc() && Calls[elev].get(i).getDest() > Calls[elev].get(0).getDest()) {
//                            Elevators.get(elev).stop(Calls[elev].get(i).getDest());
//                            Calls[elev].remove(i);
//                            break;
//                        } else if (Calls[elev].get(i).getDest() == Calls[elev].get(0).getDest() && Calls[elev].get(i).getSrc() < Calls[elev].get(0).getSrc()) {
//                            Elevators.get(elev).stop(Calls[elev].get(i).getSrc());
//                            Calls[elev].remove(i);
//                            break;
//                        }
//                    }
//
//                }
//                return;
//            }
//        }
//    }
//    public void cmdElevator(int elev) {
//        for (int i =0; i<Calls[elev].size();i++){
//            if (Calls[elev].get(i).getState() == CallForElevator.DONE){
//                Calls[elev].remove(i);
//            }
//        }
//        if (Calls[elev].size() == 0) return;
//        if (Calls[elev].size() == 1) {
//            if (Calls[elev].get(0).getState() == CallForElevator.GOING2SRC || Calls[elev].get(0).getState() == CallForElevator.INIT) {
//                Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                return;
//            }
//            if (Calls[elev].get(0).getState() == CallForElevator.GOIND2DEST) {
//                Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                return;
//            }
//        } else {
//            BuildRoute(elev);
//            if (Elevators.get(elev).getState() == Elevator.LEVEL) {
//                switch (Calls[elev].get(0).getState()) {
//                    case CallForElevator.GOIND2DEST:
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                        return;
//                    case CallForElevator.DONE:
//                        Calls[elev].remove(0);
//                        return;
//                    default:
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                }


//        for (int i = 0; i < Calls[elev].size(); i++) {
//            if (Calls[elev].get(i).getState() == CallForElevator.DONE) {
//                Calls[elev].remove(i);
//            }
//        }
//        switch (Calls[elev].size()) {
//            case 0:
//                return;
//            case 1:
//                if (Elevators.get(elev).getState() == Elevator.LEVEL) {
//                    switch (Calls[elev].get(0).getState()) {
//                        case CallForElevator.GOIND2DEST:
//                            Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                            return;
//                        case CallForElevator.DONE:
//                            Calls[elev].remove(0);
//                            return;
//                        default:
//                            Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                            return;
//                    }
//                }
//                break;
//            default:
//                BuildRoute(elev);
//                break;
//                }


//        if (Calls[elev].size() == 0) return;
//        if (Calls[elev].size() == 1) {
//            if (Elevators.get(elev).getState() == 0) {
//                switch (Calls[elev].get(0).getState()) {
//                    case CallForElevator.GOIND2DEST:
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getDest());
//                        return;
//                    case CallForElevator.DONE:
//                        Calls[elev].remove(0);
//                        return;
//                    default:
//                        Elevators.get(elev).goTo(Calls[elev].get(0).getSrc());
//                        return;
//                    for (int i = 1; i < Calls[elev].size(); i++) {
//                      if(Calls[elev].get(0).getType() == Calls[elev].get(i).getType()){
//                        floorOrder.add(Calls[elev].get(i).getSrc());
//                        floorOrder.add(Calls[elev].get(i).getDest());
//                        orderSetter(elev);
//                        switch (Elevators.get(elev).getState()){
//                            case Elevator.UP:
//                                if (floorOrder.get(0) > Elevators.get(elev).getPos()+Elevators.get(elev).getSpeed() + Elevators.get(elev).getStopTime()){
//                                    Elevators.get(elev).stop(floorOrder.get(0));
//                                }
//                            case Elevator.DOWN:
//                                if (floorOrder.get(0) < Elevators.get(elev).getPos()+Elevators.get(elev).getSpeed() + Elevators.get(elev).getStopTime()){
//                                    Elevators.get(elev).stop(floorOrder.get(0));
//                                }
//                        }

//                    }
//                }
//
//
//            }
//        } else {
//
//        }


//    private void BuildRoute(int index) {
//        route[index].clear();
//        switch (Elevators.get(index).getState()) {
//            case Elevator.UP:
//                for (int i = 0; i < Calls[index].size(); i++) {
//                    if (Calls[index].get(i).getType() == CallForElevator.UP && Calls[index].get(i).getState() == CallForElevator.GOIND2DEST) {
//                        route[index].add(Calls[index].get(i).getDest());
//                        continue;
//                    }
//                    if (Calls[index].get(i).getType() == CallForElevator.UP && Elevators.get(index).getPos() < (Calls[index].get(i).getSrc() - Elevators.get(index).getStopTime())) {
//                        if (Calls[index].get(i).getState() != CallForElevator.GOIND2DEST)
//                            route[index].add(Calls[index].get(i).getSrc());
//                        route[index].add(Calls[index].get(i).getDest());
//                    }
//                }
//                Set<Integer> upSet = new HashSet<>(route[index]);
//                route[index].clear();
//                route[index].addAll(upSet);
//                Collections.sort(route[index]);
//                if (route[index].size() != 0)
//                    Elevators.get(index).stop(route[index].get(0));
//                break;
//            case Elevator.DOWN:
//                for (int i = 0; i < Calls[index].size(); i++) {
//                    if (Calls[index].get(i).getType() == CallForElevator.DOWN && Calls[index].get(i).getState() == CallForElevator.GOIND2DEST) {
//                        route[index].add(Calls[index].get(i).getDest());
//                        continue;
//                    }
//                    if (Calls[index].get(i).getType() == CallForElevator.DOWN && Elevators.get(index).getPos() > (Calls[index].get(i).getSrc() + Elevators.get(index).getStopTime())) {
//                        if (Calls[index].get(i).getState() != CallForElevator.GOIND2DEST)
//                            route[index].add(Calls[index].get(i).getSrc());
//                        route[index].add(Calls[index].get(i).getDest());
//                    }
//                }
//                Set<Integer> downSet = new HashSet<>(route[index]);
//                route[index].clear();
//                route[index].addAll(downSet);
//                Collections.sort(route[index], Collections.reverseOrder());
//                if (route[index].size() != 0)
//                    Elevators.get(index).stop(route[index].get(0));
//                break;
//            case Elevator.LEVEL:
//                if (SentToFirstSrc[index] = true && Calls[index].get(0).getState() != CallForElevator.GOIND2DEST)
//                    return;
//                if (SentToFirstSrc[index] = true && Calls[index].get(0).getSrc() == Elevators.get(index).getPos()) {
//                    SentToFirstSrc[index] = false;
//                }
//                int direction = 0;
//                for (int i = 0; i < Calls[index].size(); i++) {
//                    if (Calls[index].get(i).getState() != CallForElevator.INIT) {
//                        direction = Calls[index].get(i).getType();
//                        break;
//                    }
//                }
//                switch (direction) {
//                    case Elevator.UP:
//                        for (int i = 0; i < Calls[index].size(); i++) {
//                            if (Calls[index].get(i).getType() == CallForElevator.UP && Calls[index].get(i).getState() == CallForElevator.GOIND2DEST) {
//                                route[index].add(Calls[index].get(i).getDest());
//                                continue;
//                            }
//                            if (Calls[index].get(i).getType() == CallForElevator.UP && Elevators.get(index).getPos() <= Calls[index].get(i).getSrc()) {
//                                if (Calls[index].get(i).getState() != CallForElevator.GOIND2DEST) {
//                                    route[index].add(Calls[index].get(i).getSrc());
//                                }
//                                route[index].add(Calls[index].get(i).getDest());
//                            }
//                        }
//                        Set<Integer> upSet1 = new HashSet<>(route[index]);
//                        route[index].clear();
//                        route[index].addAll(upSet1);
//                        Collections.sort(route[index]);
//                        if (route[index].size() != 0)
//                            Elevators.get(index).goTo(route[index].get(0));
//                        break;
//                    case Elevator.DOWN:
//                        for (int i = 0; i < Calls[index].size(); i++) {
//                            if (Calls[index].get(i).getType() == CallForElevator.DOWN && Calls[index].get(i).getState() == CallForElevator.GOIND2DEST) {
//                                route[index].add(Calls[index].get(i).getDest());
//                                continue;
//                            }
//                            if (Calls[index].get(i).getType() == CallForElevator.DOWN && Elevators.get(index).getPos() > Calls[index].get(i).getSrc()) {
//                                if (Calls[index].get(i).getState() != CallForElevator.GOIND2DEST) {
//                                    route[index].add(Calls[index].get(i).getSrc());
//                                }
//                                route[index].add(Calls[index].get(i).getDest());
//                            }
//                        }
//                        Set<Integer> downSet1 = new HashSet<>(route[index]);
//                        route[index].clear();
//                        route[index].addAll(downSet1);
//                        Collections.sort(route[index], Collections.reverseOrder());
//                        if (route[index].size() != 0)
//                            Elevators.get(index).goTo(route[index].get(0));
//                        break;
//                    default: //Build route from 0.elev state eis level. no direction
//                        Elevators.get(index).goTo(Calls[index].get(0).getSrc());
//                        SentToFirstSrc[index] = true;
//                        break;
//                }
//        }
//
//    }


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

    private void orderSetter(int index) {
        floorOrder.clear();
        for (int i = 0; i < Calls[index].size(); i++) {
            if (Calls[index].get(i).getType() == Elevators.get(index).getState()) {
                floorOrder.add(Calls[index].get(i).getSrc());
                floorOrder.add(Calls[index].get(i).getDest());
            }
        }
        Set<Integer> set = new HashSet<>(floorOrder);
        floorOrder.clear();
        floorOrder.addAll(set);
        if (Elevators.get(index).getState() == Elevator.UP)
            Collections.sort(floorOrder);
        else floorOrder.sort(Collections.reverseOrder());

    }
}