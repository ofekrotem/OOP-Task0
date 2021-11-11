package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class represents the simplest algorithm for elevator allocation - it uses a trivial concept of Shabat-Elevator (ignoring all the calls).
 * It simply stops on any floor on the way up and then stops on any floor on the way down.
 */
public class ClassicElevator implements ElevatorAlgo {
    public static final int UP=1, DOWN=-1;
    private int _direction;
    private Building _building;
    private ArrayList<CallForElevator>[] cmdCheck;
    private ArrayList<Integer> cmdCounter;
    private ArrayList<Elevator> _allElevators;
    private Elevator[] speedyElevator;

    public ClassicElevator(Building b) {
        _building = b;
        _direction = UP;
        _allElevators = new ArrayList<Elevator>();
        cmdCheck= new ArrayList[this._building.numberOfElevetors()];
        cmdCounter=new ArrayList<>();
        speedyElevator = new Elevator[this._building.numberOfElevetors()];
        for (int i=0;i<this._building.numberOfElevetors();i++){
            Elevator e = this._building.getElevetor(i);
            _allElevators.add(e);
            cmdCounter.add(0);
            cmdCheck[i] = new ArrayList<CallForElevator>();
            speedyElevator[i] = e;
        }
        cmdCounter.add(0);
        bubbleSort(speedyElevator);
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "Ex0_OOP_Smart_Elevator";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        if(_building.numberOfElevetors()==1){
            cmdCheck[0].add(c);
            return 0;
        }

        for(int i=0;i<_allElevators.size();i++){
            cmdCounter.add(cmdCheck[i].size());
            cmdCounter.remove(i);
        }
        cmdCounter.remove(0);
        cmdCounter.add(sum(cmdCounter));

        ArrayList<Integer> freeElevators = new ArrayList<>();
        for(int i=0;i<this._building.numberOfElevetors();i++){
            if(cmdCheck[i].size()==0){
                freeElevators.add(i);
            }
        }
        if (freeElevators.size()==1){
            cmdCheck[freeElevators.get(0)].add(c);
            return freeElevators.get(0);
        }
        else if(freeElevators.size()>1){
            int ans = closestElev(c.getSrc(),freeElevators);
            cmdCheck[ans].add(c);
            return ans;
        }
        else{
            if((Math.abs(c.getDest()-c.getSrc()))/(_building.maxFloor()-_building.minFloor())>0.75){
                int ans = speedyElevator[speedyElevator.length-1].getID();
                cmdCheck[ans].add(c);
                return ans;
            }
            int ans = commonAndNotFull();
            if(ans==-1){
                ans=lessOcupiedElev();}
            cmdCheck[ans].add(c);
            return ans;
        }
    }

    private int commonAndNotFull() {
        for (int i =0;i<_allElevators.size();i++){
            for(int j=0;j<cmdCheck[i].size();j++){
                if(cmdCheck[i].get(0).getType()==cmdCheck[i].get(j).getType()&&cmdCheck[i].get(0).getType()==1){
                    if(cmdCheck[i].get(0).getSrc()<=cmdCheck[i].get(j).getSrc()&&cmdCheck[i].get(j).getDest()<=cmdCheck[i].get(0).getDest()){
                        if((cmdCounter.get(cmdCounter.size()-1))/_allElevators.size()>cmdCheck[i].size()){
                            return i;
                        }
                    }
                }
                if(cmdCheck[i].get(0).getType()==cmdCheck[i].get(j).getType()&&cmdCheck[i].get(0).getType()==-1){
                    if(cmdCheck[i].get(0).getSrc()==cmdCheck[i].get(j).getSrc()&&cmdCheck[i].get(j).getDest()>cmdCheck[i].get(0).getDest()){
                        if((cmdCounter.get(cmdCounter.size()-1))/_allElevators.size()>cmdCheck[i].size()){
                            return i;
                        }
                    }
                    if(cmdCheck[i].get(0).getDest()==cmdCheck[i].get(j).getDest()&&cmdCheck[i].get(j).getSrc()<cmdCheck[i].get(0).getSrc()){
                        if((cmdCounter.get(cmdCounter.size()-1))/_allElevators.size()>cmdCheck[i].size()){
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private int sum(ArrayList<Integer> cmdCounter) {
        int sum=0;
        for (int i=0;i<cmdCounter.size();i++){
            sum+=cmdCounter.get(i);
        }
        return sum;
    }

    private int lessOcupiedElev() {
        int x=-1;
        int capacity = Integer.MAX_VALUE;
        for (int i=0;i<_allElevators.size();i++){
            if(cmdCheck[i].size()<capacity){
                capacity=cmdCheck[i].size();
                x=i;
            }
        }
        return x;
    }

    private int lessOcupiedElev(ArrayList<Integer> arr) {
        int x=Integer.MAX_VALUE;
        int ans = -1;
        for (int i=0;i<arr.size();i++){
            if (cmdCheck[arr.get(i)].size()<x){
                x=cmdCheck[arr.get(i)].size();
                ans =arr.get(i);
            }
        }
        return ans;
    }
    private int closestElev(int src, ArrayList<Integer> freeElev) {
        int ans = 0;
        int x=Integer.MAX_VALUE;
        for(int i=0;i<freeElev.size();i++){
            if(Math.abs(_allElevators.get(freeElev.get(i)).getPos()-src)<x){
                x=_allElevators.get(freeElev.get(i)).getPos()-src;
                ans=freeElev.get(i);
            }
        }
        return ans;
    }
    @Override
    /**
     * Simply stops on any floor on the way up and then stops on any floor on the way down.
     */
    public void cmdElevator(int elev) {
        if (cmdCheck[elev].size() == 0){
//            System.out.println("no calls"+_allElevators.get(elev).getID());
            return;
        }
        else {
            if(_allElevators.get(elev).getState()==0&&cmdCheck[elev].get(0).getState()==2){
//                System.out.println("Made it to SRC "+_allElevators.get(elev).getID());
                _allElevators.get(elev).goTo(cmdCheck[elev].get(0).getDest());
                return;
            }
            if(_allElevators.get(elev).getState()==0&&cmdCheck[elev].get(0).getState()==3){
//                System.out.println("Made it to DEST "+_allElevators.get(elev).getID());
                cmdCheck[elev].remove(0);
                cmdElevator(elev);
                return;
            }
            if(_allElevators.get(elev).getState()==0&&cmdCheck[elev].get(0).getState()!=2){
//                System.out.println("goTo command to "+_allElevators.get(elev).getID());
                _allElevators.get(elev).goTo(cmdCheck[elev].get(0).getSrc());
                for(int i=1;i<cmdCheck[elev].size();i++){
                    if(cmdCheck[elev].get(i).getType()==cmdCheck[elev].get(0).getType()&&cmdCheck[elev].get(0).getType()==1){
                        if(_allElevators.get(elev).getPos() == cmdCheck[elev].get(i).getSrc()&&cmdCheck[elev].get(i).getDest()<cmdCheck[elev].get(0).getDest()){
                            _allElevators.get(elev).stop(cmdCheck[elev].get(i).getDest());
                            break;
                        }
                    }
                    if(cmdCheck[elev].get(i).getType()==cmdCheck[elev].get(0).getType()&&cmdCheck[elev].get(0).getType()==-1){
                        if(cmdCheck[elev].get(i).getSrc()==cmdCheck[elev].get(0).getSrc()&&cmdCheck[elev].get(i).getDest()>cmdCheck[elev].get(0).getDest()){
                            _allElevators.get(elev).stop(cmdCheck[elev].get(i).getDest());
                            cmdCheck[elev].remove(i);
                            break;
                        }
                        else if(cmdCheck[elev].get(i).getDest()==cmdCheck[elev].get(0).getDest()&&cmdCheck[elev].get(i).getSrc()<cmdCheck[elev].get(0).getSrc()){
                            _allElevators.get(elev).stop(cmdCheck[elev].get(i).getSrc());
                            cmdCheck[elev].remove(i);
                            break;
                        }
                    }

                }
                return;
            }
        }
    }
    public int getDirection() {return this._direction;}

    private void bubbleSort(Elevator[] arr) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++){
                if (arr[j].getSpeed() > arr[j+1].getSpeed()) {
                    // swap arr[j+1] and arr[j]
                    Elevator temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
    }
}