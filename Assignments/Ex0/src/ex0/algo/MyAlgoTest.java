package ex0.algo;

import ex0.*;
import ex0.algo.MyAlgo;
import ex0.simulator.Call_A;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class MyAlgoTest {
    Building b3;
    MyAlgo algoB3;

    public MyAlgoTest() {
        Simulator_A.initData(3, null);
        b3 = Simulator_A.getBuilding();
        algoB3 = new MyAlgo(b3);
    }

    @Test
    void FastestElevTest() {
        int toCompare = 0;
        HashMap<Integer, Double> ElevsAndSpeed = new HashMap<>();
        for (int i = 0; i < algoB3.getBuilding().numberOfElevetors(); i++) {
            ElevsAndSpeed.put(i, algoB3.getBuilding().getElevetor(i).getSpeed());
        }
        double maxValue = Collections.max(ElevsAndSpeed.values());
        for (int i = 0; i < algoB3.getBuilding().numberOfElevetors(); i++) {
            if (ElevsAndSpeed.get(i).doubleValue() == maxValue) {
                toCompare = i;
                break;
            }
        }

        Assertions.assertEquals(toCompare, algoB3.FastestElev());
    }

    @Test
    void MostFreeElevatorTest() {
        ArrayList<CallForElevator>[] Calls = algoB3.getCalls();
        ArrayList<Integer> howMuchCalls = algoB3.getHowMuchCalls();
        CallForElevator c1 = new Call_A(0, 1, 6);
        CallForElevator c2 = new Call_A(0, 1, 6);
        CallForElevator c3 = new Call_A(0, 1, 6);
        howMuchCalls.clear();
        Calls[0].add(c1);
        howMuchCalls.add(1);
        Calls[1].add(c2);
        howMuchCalls.add(1);
        Calls[2].add(c3);
        howMuchCalls.add(1);
        howMuchCalls.add(0);
        howMuchCalls.add(3);
        Assertions.assertEquals(3, algoB3.MostFreeElevator());

    }

    @Test
    void areThereSimillarCallsTest() {
        ArrayList<CallForElevator>[] Calls = algoB3.getCalls();
        ArrayList<Integer> howMuchCalls = algoB3.getHowMuchCalls();
        CallForElevator c0 = new Call_A(0, 0, 10);
        CallForElevator c1 = new Call_A(0, 20, 18);
        CallForElevator c2 = new Call_A(0, 20, 17);
        CallForElevator c3 = new Call_A(0, 19, 17);
        CallForElevator c11 = new Call_A(0, 18, 19);
        CallForElevator c22 = new Call_A(0, 13, 17);
        CallForElevator c33 = new Call_A(0, 14, 15);
        CallForElevator c333 = new Call_A(0, 14, 15);
        CallForElevator cTest = new Call_A(0, 6, 10);
        howMuchCalls.clear();
        for (int i = 0; i < Calls.length; i++) {
            Calls[i].clear();
        }
        Calls[0].add(c0);
        howMuchCalls.add(1);
        Calls[1].add(c1);
        Calls[1].add(c11);
        howMuchCalls.add(2);
        Calls[2].add(c2);
        Calls[2].add(c22);
        howMuchCalls.add(2);
        Calls[3].add(c3);
        Calls[3].add(c33);
        Calls[3].add(c333);
        howMuchCalls.add(3);
        howMuchCalls.add(8);
        Assertions.assertEquals(0, algoB3.areThereSimillarCalls(cTest));
    }

    @Test
    void allocateAnElevatorTest() {
        CallForElevator c1 = new Call_A(0, 1, 6);
        int a = algoB3.allocateAnElevator(c1);
        Assertions.assertEquals(0, a);
        Assertions.assertEquals(1, algoB3.getCalls()[a].size());
    }

    @Test
    void cmdElevatorTest() {
        CallForElevator c1 = new Call_A(0, 1, 6);
        int a = algoB3.allocateAnElevator(c1);
        algoB3.cmdElevator(a);
        if (b3.getElevetor(a).getPos() < c1.getSrc())
            Assertions.assertEquals(Elevator.UP, b3.getElevetor(a).getState());
        else if (b3.getElevetor(a).getPos() > c1.getSrc())
            Assertions.assertEquals(Elevator.DOWN, b3.getElevetor(a).getState());
    }
}