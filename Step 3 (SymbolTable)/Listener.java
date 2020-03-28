
package compiler;
import java.util.*;


public class Listener extends LITTLEBaseListener {
Stack allElements; //keeps track of correct order of symbol tables and nesting
Stack scope; //stack to keep track of current scope for var order etc.
int block; //keeps track of BLOCK for IF, ELSE, WHILE
    public Listener() {
		allElements = new Stack<>(); //initialization
		scope = new Stack<>();
		block = 0 ; //block set to 0 initially
		SymbolTable firstTable = new SymbolTable("GLOBAL"); //1st table is always GLOBAL
		allElements.addElement(firstTable); //GLOBAL is pushed onto both stacks
		scope.addElement(firstTable);
	}
		
	@Override
	public void enterFunc_decl(LITTLEParser.Func_declContext ctx) {
		String funcDecl = ctx.getText(); //Gets entire function declaration text
		funcDecl = funcDecl.replaceAll("[FLOAT|INT|VOID|FUNCTION]", ""); //cleans to only function name
		String[] name = funcDecl.split("\\(");
		name[0] = name[0].replaceAll("\\s", "");
		SymbolTable table = new SymbolTable(name[0]); //creates new table with function name
		allElements.addElement(table); //new table pushed onto stacks
		scope.addElement(table);
	}


	@Override
	public void enterWhile_stmt(LITTLEParser.While_stmtContext ctx) {
		block++; //block incremented for table name
		String tableName = "BLOCK " + block;
		SymbolTable table = new SymbolTable(tableName); //new table created
		allElements.addElement(table); //new table pushed onto stacks
		scope.addElement(table);
	}



	@Override
	public void enterIf_stmt(LITTLEParser.If_stmtContext ctx) {
		//exact same functionality as entering a while statement
		block++;
		String tableName = "BLOCK " + block;
		SymbolTable table = new SymbolTable(tableName);
		allElements.addElement(table);
		scope.addElement(table);
	}


	@Override
	public void enterElse_part(LITTLEParser.Else_partContext ctx) {
		String elseStat = ctx.getText(); //gets text from an else part
		if(elseStat.contains("ELSE")) { //determines if there is really an else or if it was empty	
			block++; //if there was an else create a new block, table, and scoping for it
			String tableName = "BLOCK " + block;
			SymbolTable table = new SymbolTable(tableName);
			allElements.addElement(table); //push new table onto stacks
			scope.addElement(table);
		}
	}

	
	@Override
	public void enterParam_decl(LITTLEParser.Param_declContext ctx) {
		String typeName = ctx.getText(); //gets text from declaration
		boolean isFloat = typeName.contains("FLOAT"); //determines type
		typeName = typeName.replaceAll("[FLOAT|INT]", "");
		SymbolTable current = (SymbolTable)scope.peek(); //gets current symbol table
		if(isFloat) { //adds appropriate type and name to current table
			current.newDecleration(typeName, "FLOAT", null);
		} else {
			current.newDecleration(typeName, "INT", null);
		}
	}


	@Override
	public void enterString_decl(LITTLEParser.String_declContext ctx) {
		String stringDecl = ctx.getText(); //gets text from string declaration
		String nameValue = stringDecl.replaceAll("STRING", ""); //cleans text
		String[] split = nameValue.split(":="); //splits on :=
		String name = split[0]; //the name is before the split
		String value = split[1].substring(0, split[1].length()-1); //the value is after the split minus a ;
		SymbolTable current = (SymbolTable)scope.peek(); //adds entry to current table
		current.newDecleration(name, "STRING", value);
	}

	@Override
	public void enterVar_decl(LITTLEParser.Var_declContext ctx) {
		String variable = ctx.getText(); //gets text from var declarations
		boolean isFloat = variable.contains("FLOAT"); //determines types
                variable = variable.replaceAll("[FLOAT|INT]", "");
		variable = variable.replaceAll("\\;", "");
		String[] allVar = variable.split("\\,"); //gets names of all variables created
		SymbolTable current = (SymbolTable)scope.peek(); //gets current table
		for(int i=0; i<allVar.length; i++) { //runs through each variable
			if(isFloat) { //adds to symbol table with appropriate name and type
				current.newDecleration(allVar[i], "FLOAT", null);
			} else {
				current.newDecleration(allVar[i], "INT", null);
			}	
		}
	}

	public Stack<SymbolTable> getSymbolTables() { //returns the finalized ordered stack of symbol tables
		return allElements; //returns correctly ordered Stack of tables
	}
    
    
}
