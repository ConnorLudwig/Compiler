package compiler;

import java.util.ArrayList;

public class IR {

    ArrayList<ArrayList<String>> code = new ArrayList<>(); //nested array list to hold the 3 values for each decleration.

    public void newElement(ArrayList list) {
        code.add(list);
    }

    public void printIR() {
        System.out.println(";IR code");
        for (int i = 0; i < code.size(); i++) {
            if (i == 1) {
                System.out.println(";LINK");
            }
            ArrayList<String> alive = code.get(i);
            System.out.print(";");
            for (int j = 0; j < alive.size(); j++) {
                System.out.println(alive.get(j) + " ");
            }
        }
    }

    public void printTiny() {
        ArrayList<ArrayList<String>> tc = new ArrayList<>();
        System.out.println(":tiny code");
        for (int i = 0; i < code.size(); i++) {
            ArrayList<String> alive = code.get(i);
            
            
        }
    }

}
