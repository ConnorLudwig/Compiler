package compiler;

import java.util.*;

public class Listener extends LITTLEBaseListener {

    Stack allElements; //holds the order of all the symbol tables
    //current scope makes well use of a stack becasue we know the current scope will always be on top.
    Stack currentScope; //holds current scope of tables to know if a name has been used twice in the same scope
    int biterator = 0; //iterator for the number of blocks in the code
    IR list;
    int regCount;
    boolean normal = false;
    ArrayList<String> splitList = new ArrayList<>();
    ArrayList<String> temp = new ArrayList<>();

    public Listener() {
        allElements = new Stack<>();//create blank stack for allElements
        currentScope = new Stack<>();//create blank stack for allElements
        list = new IR();
        regCount = 1;
        String initialName = "GLOBAL";
        SymbolTable initial = new SymbolTable(initialName);//initilaize global table because that scope is always first.
        allElements.addElement(initial);
        currentScope.addElement(initial);

    }

    @Override
    public void enterFunc_decl(LITTLEParser.Func_declContext ctx) {
        String fd = ctx.getText();//retrieve entire decleration text
        fd = fd.replaceAll("[FLOAT|INT|VOID|FUNCTION]", "");//removes everything but the name and value.
        String delimiter = "\\(";
        String[] title = fd.split(delimiter);
        title[0] = title[0].replaceAll("\\s", "");
        SymbolTable t = new SymbolTable(title[0]);//make new table and gives it a title retrieved from function.
        allElements.addElement(t);
        currentScope.addElement(t);
        ArrayList<String> elements = new ArrayList<>();
        elements.add("LABEL");
        elements.add(title[0]);
        list.newElement(elements);
        
        
    }

    @Override
    public void enterIf_stmt(LITTLEParser.If_stmtContext ctx) {
        biterator++;//add one to the block iterator since we are entering a new block.
        String title = "BLOCK " + biterator;//save block name to a string.
        SymbolTable t = new SymbolTable(title);//table with the updated block name
        allElements.addElement(t);
        currentScope.addElement(t);
    }

    @Override
    public void enterWhile_stmt(LITTLEParser.While_stmtContext ctx) {
        biterator++; //add one to the block iterator since we are entering a new block.
        String title = "BLOCK " + biterator; //save block name to a string.
        SymbolTable t = new SymbolTable(title); //table with the updated block name
        allElements.addElement(t);
        currentScope.addElement(t);
    }

    @Override
    public void enterElse_part(LITTLEParser.Else_partContext ctx) {
        String elseStat = ctx.getText();//retrieves full statment
        //for every "if" statment, our gramamr does not require an else block, so this method checks to see if there is one
        if (elseStat.contains("ELSE")) {
            biterator++;//only gets increased an a new block created and added to the stack if there is an else block
            String title = "BLOCK " + biterator;
            SymbolTable t = new SymbolTable(title);
            allElements.addElement(t);
            currentScope.addElement(t);
        }
    }

    @Override
    public void enterString_decl(LITTLEParser.String_declContext ctx) {
        String sd = ctx.getText();//retrieves full statment from the string decleration
        String text = sd.replaceAll("STRING", "");//removes everything from the statment except the name and values
        String delimiter = ":=";
        String[] delimiterArray = text.split(delimiter);//we use the assignment operator ":=" as the delimeter becasue so we know where everything is located in the statment.
        String title = delimiterArray[0];//retrieves the value just before the split so we know from the grammar that is the name.
        String data = delimiterArray[1].substring(0, delimiterArray[1].length() - 1); //the value is after the split but we know it is before the ";" so we subtract 1.
        SymbolTable alive = (SymbolTable) currentScope.peek();//adds a new decleration to the current table, current table is named "alive"
        alive.newDecleration(title, "STRING", data);
    }

    @Override
    public void enterParam_decl(LITTLEParser.Param_declContext ctx) {
        String paramDecl = ctx.getText();//retrieves full statment from the parameter decleration
        boolean check = paramDecl.contains("FLOAT");//checks to see if it is a float or an int
        paramDecl = paramDecl.replaceAll("[FLOAT|INT]", "");//remove everything from the text except the name and values.

        //peek retrieves the very first table from our symbol table to ensure we stay within the same scope.
        SymbolTable alive = (SymbolTable) currentScope.peek();
        if (check) {
            alive.newDecleration(paramDecl, "FLOAT", null);//if it is a float, we name it such, but if not we know it's an int.
        } else {
            alive.newDecleration(paramDecl, "INT", null);
        }
    }

    @Override
    public void enterVar_decl(LITTLEParser.Var_declContext ctx) {
        String variable = ctx.getText(); //retrieves full statment from the decleration
        boolean check = variable.contains("FLOAT");//checks to see if it is a float or an int
        variable = variable.replaceAll("[FLOAT|INT]", "");//removes everything but the name and value from the statment.
        variable = variable.replaceAll("\\;", "");//removes the semicolon
        String delimiter = "\\,";
        String[] all = variable.split(delimiter); //places all the values in an array to be referenced later.
        SymbolTable alive = (SymbolTable) currentScope.peek();//retrievs the top of the stack so we know we are in the right scope.
        for (String i : all) {
            //iterate through every variable so we know what to name it when it is added to correct symbol table.
            if (check) {
                alive.newDecleration(i, "FLOAT", null);
            } else {
                alive.newDecleration(i, "INT", null);
            }
        }
    }

    public String findVariable(String in) {
        boolean running = true;
        String varType = null;
        SymbolTable alive = (SymbolTable) allElements.peek();
        ArrayList<ArrayList<String>> entries = alive.getParameters();
        while (running == true) {
            for (int i = 0; i < alive.size(); i++) {
                ArrayList<String> variable = alive.get(i);
                if (variable.get(0).equals(in)) {
                    varType = variable.get(1);
                    running = false;
                }
            }
        }
        return varType;

    }

    public String getRegister() {
        return "$T" + regCount;
    }

    @Override
    public void enterRead_stmt(LITTLEParser.Read_stmtContext ctx) {
        String rs = ctx.getText();
        rs = rs.replaceAll("READ", "");
        rs = rs.replaceAll("\\s", "");
        rs = rs.replaceAll("\\(", "");
        rs = rs.replaceAll("\\);", "");
        String[] variables = rs.split("\\,");
        String varType = null;
        ArrayList<String> elements = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            varType = findVariable(variables[i]);
            switch (varType) {
                case "STRING":
                    elements.add("READS");
                    break;
                case "INT":
                    elements.add("READI");
                    break;
                case "FLOAT":
                    elements.add("READF");
                    break;
                default:
                    throw new IllegalArgumentException("Problem retrieving variable type from symbol tables.");
            }
            elements.add(variables[i]);
            list.newElement(elements);
        }
    }

    @Override
    public void enterWrite_stmt(LITTLEParser.Write_stmtContext ctx) {
        String ws = ctx.getText();
        ws = ws.replaceAll("READ", "");
        ws = ws.replaceAll("\\s", "");
        ws = ws.replaceAll("\\(", "");
        ws = ws.replaceAll("\\);", "");
        String[] variables = ws.split("\\,");
        String varType = null;
        ArrayList<String> elements = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            varType = findVariable(variables[i]);
            switch (varType) {
                case "STRING":
                    elements.add("WRITES");
                    break;
                case "INT":
                    elements.add("WRITEI");
                    break;
                case "FLOAT":
                    elements.add("WRITEF");
                    break;
                default:
                    throw new IllegalArgumentException("Problem retrieving variable type from symbol tables.");
            }
            elements.add(variables[i]);
            list.newElement(elements);
        }
    }

    @Override
    public void enterAssign_stmt(LITTLEParser.Assign_stmtContext ctx) {
        String id = ctx.getText();
        String delimiter = ":=";
        String[] split = id.split(delimiter);
        id = split[0].replaceAll("\\s", "");
        String equation = split[1].replaceAll("\\s", "");
        String assignID = findVariable(id);
        int i = 0;
        while (i <= equation.length() - 1) {
            String tempString = null;
            boolean running = true;
            Character current = equation.charAt(i);
            tempString = String.valueOf(current);
            if (Character.isDigit(current)) {
                if (i == equation.length() - 1) {
                    i++;
                }
                while (running && i < equation.length() - 1) {
                    Character next = equation.charAt(i + 1);
                    if (next.equals('.')) {
                        tempString = tempString + next;
                        i++;
                    } else if (Character.isDigit(next)) {
                        tempString = tempString + next;
                        i++;
                    } else {
                        i++;
                        running = false;
                    }

                }
                if (i < equation.length()) {
                    splitList.add(tempString);
                }
            } else {
                splitList.add(tempString);
                i++;
            }
            System.out.println(splitList);
        }

        while (splitList.contains("(")) {
            handleParentheses(assignID);
        }
        normal = true;
        handleNormal(assignID, assignID);

    }


    public void handleParentheses(String s) {
        while (splitList.contains("(")) {
            int start = splitList.indexOf("(");
            int end = splitList.indexOf(")");
            for (int i = start + 1; i < end; i++) {
                temp.add(splitList.get(i));
            }
            while (temp.contains("*") || temp.contains("/")) {
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).equals("*")) {
                        handleMul(i, s);
                        break;
                    } else if (temp.get(i).equals("/")) {
                        handleDiv(i, s);
                        break;
                    }

                }

            }
            while (temp.contains("+") || temp.contains("-")) {
                for (int j = 0; j < temp.size(); j++) {
                    if (temp.get(j).equals("-")) {
                        handleSub(j, s);
                        break;
                    } else if (temp.get(j).equals("+")) {
                        handleAdd(j, s);
                        break;
                    }
                }
            }

            splitList.set(start, getRegister());
            for (int i = start; i < end; i++) {
                splitList.remove((start + 1));
            }

        }
    }

    public void handleNormal(String s, String name) {
        while ((splitList.contains("*") || splitList.contains("/") || splitList.contains("-") || splitList.contains("+"))) {
            if (splitList.size() == 1) {
                break;
            }
            for (int i = 0; i < splitList.size(); i++) {
                if (splitList.get(i).equals("*")) {
                    handleMul(i, s);
                    break;
                } else if (splitList.get(i).equals("/")) {
                    handleDiv(i, s);
                    break;
                } else if (splitList.get(i).equals("-")) {
                    handleSub(i, s);
                    break;
                } else if (splitList.get(i).equals("+")) {
                    handleAdd(i, s);
                    break;
                }
            }
        }
        if (!splitList.get(0).contains("$T")) {
            ArrayList<String> elements = new ArrayList<>();
            elements.add("STORI");
            elements.add(getRegister());
            elements.add(name);
            list.newElement(elements);
        }
        ArrayList<String> elements = new ArrayList<>();
        elements.add("STOEI");
        elements.add(getRegister());
        elements.add(name);
        list.newElement(elements);
        regCount++;

    }

    public void handleMul(int index, String s) {
        String operation = null;
        String storeType = null;
        if (s.equals("INT")) {
            operation = "MULTI";
            storeType = "STOREI";
        } else {
            operation = "MULTF";
            storeType = "STOREF";
        }
        String[] variables = new String[2];
        if (normal) {
            variables[0] = splitList.get(index - 1);
            variables[1] = splitList.get(index + 1);
        } else {
            variables[0] = temp.get(index - 1);
            variables[1] = temp.get(index + 1);
        }
        for (int i = 0; i < variables.length; i++) {
            if (Character.isDigit(variables[i].charAt(0))) {
                ArrayList<String> elements = new ArrayList<>();
                elements.add(storeType);
                elements.add(variables[i]);
                elements.add(getRegister());
                variables[i] = getRegister();
                regCount++;
                list.newElement(elements);
                System.out.println(elements);
            }
        }
        ArrayList<String> elements = new ArrayList<>();
        elements.add(operation);
        elements.add(variables[0]);
        elements.add(variables[1]);
        elements.add(getRegister());
        list.newElement(elements);
        System.out.println(elements);
        if (normal) {
            splitList.set(index, getRegister());
            splitList.remove(index + 1);
            splitList.remove(index - 1);
            if (splitList.size() > 1) {
                regCount++;
            }
        } else {
            temp.set(index, getRegister());
            temp.remove(index + 1);
            temp.remove(index - 1);
            if (temp.size() > 1) {
                regCount++;
            }
        }
    }

    public void handleDiv(int index, String s) {
        String operation = null;
        String storeType = null;
        if (s.equals("INT")) {
            operation = "DIVI";
            storeType = "STOREI";
        } else {
            operation = "DIVF";
            storeType = "STOREF";
        }
        String[] variables = new String[2];
        if (normal) {
            variables[0] = splitList.get(index - 1);
            variables[1] = splitList.get(index + 1);
        } else {
            variables[0] = temp.get(index - 1);
            variables[1] = temp.get(index + 1);
        }
        for (int i = 0; i < variables.length; i++) {
            if (Character.isDigit(variables[i].charAt(0))) {
                ArrayList<String> elements = new ArrayList<>();
                elements.add(storeType);
                elements.add(variables[i]);
                elements.add(getRegister());
                variables[i] = getRegister();
                regCount++;
                list.newElement(elements);
                System.out.println(elements);
            }

        }
        ArrayList<String> elements = new ArrayList<>();
        elements.add(operation);
        elements.add(variables[0]);
        elements.add(variables[1]);
        elements.add(getRegister());
        list.newElement(elements);
        System.out.println(elements);
        if (normal) {
            splitList.set(index, getRegister());
            splitList.remove(index + 1);
            splitList.remove(index - 1);
            if (splitList.size() > 1) {
                regCount++;
            }
        } else {
            temp.set(index, getRegister());
            temp.remove(index + 1);
            temp.remove(index - 1);
            if (temp.size() > 1) {
                regCount++;
            }
        }
    }

    public void handleSub(int index, String s) {
        String operation = null;
        String storeType = null;
        if (s.equals("INT")) {
            operation = "SUBI";
            storeType = "STOREI";
        } else {
            operation = "SUBF";
            storeType = "STOREF";
        }
        String[] variables = new String[2];
        if (normal) {
            variables[0] = splitList.get(index - 1);
            variables[1] = splitList.get(index + 1);
        } else {
            variables[0] = temp.get(index - 1);
            variables[1] = temp.get(index + 1);
        }
        for (int i = 0; i < variables.length; i++) {
            if (Character.isDigit(variables[i].charAt(0))) {
                ArrayList<String> elements = new ArrayList<>();
                elements.add(storeType);
                elements.add(variables[i]);
                elements.add(getRegister());
                variables[i] = getRegister();
                regCount++;
                list.newElement(elements);
                System.out.println(elements);
            }
        }
        ArrayList<String> elements = new ArrayList<>();
        elements.add(operation);
        elements.add(variables[0]);
        elements.add(variables[1]);
        elements.add(getRegister());
        list.newElement(elements);
        System.out.println(elements);
        if (normal) {
            splitList.set(index, getRegister());
            splitList.remove(index + 1);
            splitList.remove(index - 1);
            if (splitList.size() > 1) {
                regCount++;
            }
        } else {
            temp.set(index, getRegister());
            temp.remove(index + 1);
            temp.remove(index - 1);
            if (temp.size() > 1) {
                regCount++;
            }
        }

    }

    public void handleAdd(int index, String s) {
        String operation = null;
        String storeType = null;
        if (s.equals("INT")) {
            operation = "ADDI";
            storeType = "STOREI";
        } else {
            operation = "ADDF";
            storeType = "STOREF";
        }
        String[] variables = new String[2];
        if (normal) {
            variables[0] = splitList.get(index - 1);
            variables[1] = splitList.get(index + 1);
        } else {
            variables[0] = temp.get(index - 1);
            variables[1] = temp.get(index + 1);
        }
        for (int i = 0; i < variables.length; i++) {
            if (Character.isDigit(variables[i].charAt(0))) {
                ArrayList<String> elements = new ArrayList<>();
                elements.add(storeType);
                elements.add(variables[i]);
                elements.add(getRegister());
                variables[i] = getRegister();
                regCount++;
                list.newElement(elements);
                System.out.println(elements);
            }
        }
        ArrayList<String> elements = new ArrayList<>();
        elements.add(operation);
        elements.add(variables[0]);
        elements.add(variables[1]);
        elements.add(getRegister());
        list.newElement(elements);
        System.out.println(elements);
        if (normal) {
            splitList.set(index, getRegister());
            splitList.remove(index + 1);
            splitList.remove(index - 1);
            if (splitList.size() > 1) {
                regCount++;
            }
        } else {
            temp.set(index, getRegister());
            temp.remove(index + 1);
            temp.remove(index - 1);
            if (temp.size() > 1) {
                regCount++;
            }
        }

    }
    
    @Override
    public void exitAssign_stmt(LITTLEParser.Assign_stmtContext ctx) {
        splitList.clear();
        temp.clear();
    }

    public Stack<SymbolTable> getSymbolTables() {//returns the stack that contains all symbol tables for every scope.
        return allElements;
    }
    
    public IR returnIR(){
        return list;
    }

    /**
     * *************************************************************************************************************************
     ***************None of the below methods are used for this step but we
     * will need them for later***************************** **************Code
     * was copied over from the LITTLEListenerBase class so methods could be
     * overridden************************
     * ***************************************************************************************************************************
     */
    @Override
    public void enterTokens(LITTLEParser.TokensContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTokens(LITTLEParser.TokensContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEmpty(LITTLEParser.EmptyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEmpty(LITTLEParser.EmptyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterProgram(LITTLEParser.ProgramContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitProgram(LITTLEParser.ProgramContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterId(LITTLEParser.IdContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitId(LITTLEParser.IdContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPgm_bdy(LITTLEParser.Pgm_bdyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPgm_bdy(LITTLEParser.Pgm_bdyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDecl(LITTLEParser.DeclContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDecl(LITTLEParser.DeclContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitString_decl(LITTLEParser.String_declContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStr(LITTLEParser.StrContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStr(LITTLEParser.StrContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitVar_decl(LITTLEParser.Var_declContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterVar_type(LITTLEParser.Var_typeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitVar_type(LITTLEParser.Var_typeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAny_type(LITTLEParser.Any_typeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAny_type(LITTLEParser.Any_typeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterId_list(LITTLEParser.Id_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitId_list(LITTLEParser.Id_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterId_tail(LITTLEParser.Id_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitId_tail(LITTLEParser.Id_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParam_decl_list(LITTLEParser.Param_decl_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParam_decl_list(LITTLEParser.Param_decl_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParam_decl(LITTLEParser.Param_declContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParam_decl_tail(LITTLEParser.Param_decl_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParam_decl_tail(LITTLEParser.Param_decl_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFunc_declarations(LITTLEParser.Func_declarationsContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunc_declarations(LITTLEParser.Func_declarationsContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunc_decl(LITTLEParser.Func_declContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFunc_body(LITTLEParser.Func_bodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunc_body(LITTLEParser.Func_bodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStmt_list(LITTLEParser.Stmt_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStmt_list(LITTLEParser.Stmt_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStmt(LITTLEParser.StmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStmt(LITTLEParser.StmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBase_stmt(LITTLEParser.Base_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBase_stmt(LITTLEParser.Base_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAssign_expr(LITTLEParser.Assign_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAssign_expr(LITTLEParser.Assign_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitRead_stmt(LITTLEParser.Read_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterWrite_stmt(LITTLEParser.Write_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitWrite_stmt(LITTLEParser.Write_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterReturn_stmt(LITTLEParser.Return_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitReturn_stmt(LITTLEParser.Return_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpr(LITTLEParser.ExprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpr(LITTLEParser.ExprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpr_prefix(LITTLEParser.Expr_prefixContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpr_prefix(LITTLEParser.Expr_prefixContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFactor(LITTLEParser.FactorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFactor(LITTLEParser.FactorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFactor_prefix(LITTLEParser.Factor_prefixContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFactor_prefix(LITTLEParser.Factor_prefixContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPostfix_expr(LITTLEParser.Postfix_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPostfix_expr(LITTLEParser.Postfix_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCall_expr(LITTLEParser.Call_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCall_expr(LITTLEParser.Call_exprContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpr_list(LITTLEParser.Expr_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpr_list(LITTLEParser.Expr_listContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpr_list_tail(LITTLEParser.Expr_list_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpr_list_tail(LITTLEParser.Expr_list_tailContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPrimary(LITTLEParser.PrimaryContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPrimary(LITTLEParser.PrimaryContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitIf_stmt(LITTLEParser.If_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitElse_part(LITTLEParser.Else_partContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCond(LITTLEParser.CondContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCond(LITTLEParser.CondContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitWhile_stmt(LITTLEParser.While_stmtContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
}
