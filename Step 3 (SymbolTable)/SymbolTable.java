/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SymbolTable {

    //INSTANCE VARIABLES
    ArrayList<ArrayList<String>> allData = new ArrayList<>(); //List of 3-tuples, which are in their own list
    ArrayList<String> dataEntry; //3-tuples
    String name;

    //name is the name of the Symbol Table
    public SymbolTable(String inName) {
        name = inName;
    }

    //Constructor for SymbolTable
    public void newDecleration(String inName, String inType, String inValue) {
        dataEntry = new ArrayList<>(); //3-tuple of name, type, value in symbol table
        dataEntry.add(inName);
        dataEntry.add(inType);
        dataEntry.add(inValue);
        allData.add(dataEntry); //adds entry to full list of 3-tuples
    }

    //Returns the name
    public String getName() {
        return name;
    }

    //Returns the Data in the Symbol table
    public ArrayList<ArrayList<String>> getData() {
        return allData;
    }

    public static void prettyPrint(Stack<SymbolTable> inTables) { //prints all symbol tables with vars, blocks, etc.
        Stack<SymbolTable> reverse = new Stack<SymbolTable>();
        while (!inTables.empty()) {
            reverse.push((SymbolTable) inTables.pop()); //reverses order of stack so its in order
        }
        while (!reverse.empty()) { //runs while stack is not empty
            SymbolTable current = (SymbolTable) reverse.pop(); //gets current table
            System.out.println("Symbol table " + current.getName()); //prints table name
            ArrayList<ArrayList<String>> tableData = current.getData(); //gets all 3 tuples for current table
            boolean check = errorCheck(tableData);
            if (check) {
                for (int j = 0; j < tableData.size(); j++) { //runs through all 3 tuples
                    ArrayList<String> variable = tableData.get(j); //gets current 3 tuple
                    System.out.print("name " + variable.get(0) + " type " + variable.get(1)); //prints 3 tuple	
                    if (variable.get(2) != null) {
                        System.out.print(" value " + variable.get(2));	//value only if not null
                    }
                    System.out.println(); //spacing
                }
                if (!reverse.empty()) { //no newline at EOF
                    System.out.println();
                }
            }
        }
    }

    public static boolean errorCheck(ArrayList<ArrayList<String>> inData) {
        List usedNames = new LinkedList<>();
        for (int i = 0; i < inData.size(); i++) { //gets current 3 tuple
            ArrayList<String> variable = inData.get(i);
            if (!usedNames.contains(variable.get(0))) { //if the var name is not already defined in the program, add it to the list
                usedNames.add(variable.get(0));
            } else { //the same name is being used twice, invalid, throw error
                System.out.println("DECLARATION ERROR " + variable.get(0));
                return false;
            }
        }
        usedNames.clear();
        return true;
    }
}
