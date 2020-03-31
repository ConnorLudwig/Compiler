
import java.util.ArrayList;
import java.util.Stack;

public class SymbolTable {

    ArrayList<ArrayList<String>> parameters = new ArrayList<>(); //nested array list to hold the 3 values for each decleration.
    ArrayList<String> addValue;
    String tableName;

    //constructor for the symbolTable class, only takes in a name.
    public SymbolTable(String name) {
        tableName = name;
    }

    //used to create each entry for the symbol tables, needs a name, type and value
    public void newDecleration(String name, String type, String value) {
        addValue = new ArrayList<>(); //creates temperary arraylist to hold the 3 values, later to be combined to add to the nested array list
        //adds values to arraylist
        addValue.add(name);
        addValue.add(type);
        addValue.add(value);
        parameters.add(addValue); //groups the 3 values and adds them to the parameters nested list
    }

    //gets name of table
    public String getTableName() {
        return tableName;
    }

    //gets all the values from the full parameters arraylist
    public ArrayList<ArrayList<String>> getParameters() {
        return parameters;
    }

    //method to print all the declerations and blocks, formatted to fit the output provided by the instructor
    public static void prettyPrint(Stack<SymbolTable> input) {
        Stack<SymbolTable> reverse = new Stack<>();
        while (!input.empty()) {
            reverse.push((SymbolTable)input.pop()); //in order to print the output correctly, the stack needs to be reversed by using a push/pop
        }
        while (!reverse.empty()) { //while loop to run code block until all the tables have been gone through
            SymbolTable alive = (SymbolTable)reverse.pop(); //retrieves the top of the stack, which is the current scope
            System.out.println("Symbol table "+alive.getTableName()); //prints table name for the given scope
            ArrayList<ArrayList<String>> values=alive.getParameters(); //creates a nested array list to retrieve the 3 values from each entry
            for (int i = 0; i<values.size(); i++) { //iterates through all three values.
                ArrayList<String> data = values.get(i); //retrieves the three values
                System.out.print("name " + data.get(0) + " type " + data.get(1)); //prints the name and the type
                //there is a chance that the value may be null, so this checks to make sure "null" is not printed to the screen.
                if (data.get(2)!=null) {
                    System.out.print(" value " + data.get(2));
                }
                System.out.println(); //starts new line.
            }
            if (!reverse.empty()) { //if there is more to print, we need move to a new line
                System.out.println();
            }
        }
    }

}
