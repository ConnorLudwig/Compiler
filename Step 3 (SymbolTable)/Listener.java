

import java.util.*;


public class Listener extends LITTLEBaseListener {
Stack allElements; //holds the order of all the symbol tables
Stack scope; //holds current scope of tables to know if a name has been used twice in the same scope
int biterator; //iterator for the number of blocks in the code
    public Listener() {
		allElements = new Stack<>();//create blank stack for allElements
		scope = new Stack<>();//create blank stack for allElements
		biterator = 0 ;//starts at zero
		SymbolTable initial = new SymbolTable("GLOBAL");//initilaize global table because that scope is always first.
		allElements.addElement(initial);//add table to the stacks
		scope.addElement(initial);
	}

	@Override
	public void enterFunc_decl(LITTLEParser.Func_declContext ctx) {
		String fd = ctx.getText();//retrieve entire decleration text
		fd = fd.replaceAll("[FLOAT|INT|VOID|FUNCTION]", "");//removes everything but the name.
		String[] title = fd.split("\\(");
		title[0] = title[0].replaceAll("\\s", "");
		SymbolTable t = new SymbolTable(title[0]);//make new table and gives it a title retrieved from function.
		allElements.addElement(t);//add to the stacks
		scope.addElement(t);
	}


	@Override
	public void enterWhile_stmt(LITTLEParser.While_stmtContext ctx) {
		biterator++; //add one to the block iterator since we are entering a new block.
		String title = "BLOCK " + biterator; //save block name to a string.
		SymbolTable t = new SymbolTable(title); //table with the updated block name
		allElements.addElement(t); //add to the stacks
		scope.addElement(t);
	}



	@Override
	public void enterIf_stmt(LITTLEParser.If_stmtContext ctx) {
		biterator++;//add one to the block iterator since we are entering a new block.
		String title = "BLOCK " + biterator;//save block name to a string.
		SymbolTable t = new SymbolTable(title);//table with the updated block name
		allElements.addElement(t);//add to the stacks
		scope.addElement(t);
	}


	@Override
	public void enterElse_part(LITTLEParser.Else_partContext ctx) {
		String elseStat = ctx.getText();//retrieves full statment
                //for every "if" statment, our gramamr does not require an else block, so this method checks to see if there is one
		if(elseStat.contains("ELSE")) {
			biterator++;//only gets increased an a new block created and added to the stack if there is an else block
			String title = "BLOCK " + biterator;
			SymbolTable t = new SymbolTable(title);
			allElements.addElement(t);//add to the stacks
			scope.addElement(t);
		}
	}


	@Override
	public void enterParam_decl(LITTLEParser.Param_declContext ctx) {
		String paramDecl = ctx.getText();//retrieves full statment from the parameter decleration
		boolean isFloat = paramDecl.contains("FLOAT");//checks to see if it is a float or an int
		paramDecl = paramDecl.replaceAll("[FLOAT|INT]", "");//remove everything from the text except the name and values.

                //peek retrieves the very first table from our symbol table to ensure we stay within the same scope.
		SymbolTable alive = (SymbolTable)scope.peek();
		if(isFloat) {//if it is a float, we name it such, but if not we know it's an int.
			alive.newDecleration(paramDecl, "FLOAT", null);
		} else {
			alive.newDecleration(paramDecl, "INT", null);
		}
	}


	@Override
	public void enterString_decl(LITTLEParser.String_declContext ctx) {
		String sd = ctx.getText();//retrieves full statment from the string decleration
		String text = sd.replaceAll("STRING", "");//removes everything from the statment except the name and values
		String[] delimiter = text.split(":=");//we use the assignment operator ":=" as the delimeter becasue so we know where everything is located in the statment.
		String title = delimiter[0];//retrieves the value just before the split so we know from the grammar that is the name.
		String data = delimiter[1].substring(0, delimiter[1].length() - 1); //the value is after the split but we know it is before the ";" so we subtract 1.
		SymbolTable alive = (SymbolTable)scope.peek();//adds a new decleration to the current table, current table is named "alive"
		alive.newDecleration(title, "STRING", data);
	}

	@Override
	public void enterVar_decl(LITTLEParser.Var_declContext ctx) {
		String variable = ctx.getText(); //retrieves full statment from the decleration
		boolean isFloat = variable.contains("FLOAT");//checks to see if it is a float or an int
                variable = variable.replaceAll("[FLOAT|INT]", "");//removes everything but the name and value from the statment.
		variable = variable.replaceAll("\\;", "");//removes the semicolon
		String[] all = variable.split("\\,");//places all the values in an array to be referenced later.
		SymbolTable alive = (SymbolTable)scope.peek();//retrievs the top of the stack so we know we are in the right scope.
		for(int i=0; i<all.length; i++) {//iterate through every variable so we know what to name it when it is added to correct symbol table.
			if(isFloat) {
				alive.newDecleration(all[i], "FLOAT", null);
			} else {
				alive.newDecleration(all[i], "INT", null);
			}
		}
	}

	public Stack<SymbolTable> getSymbolTables() {//returns the stack that contains all symbol tables for every scope.
		return allElements;
	}




        /***************************************************************************************************************************
        ***************None of the below methods are used for this step but we will need them for later*****************************
        ****************************************************************************************************************************
        */
        	@Override public void enterTokens(LITTLEParser.TokensContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTokens(LITTLEParser.TokensContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEmpty(LITTLEParser.EmptyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEmpty(LITTLEParser.EmptyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterProgram(LITTLEParser.ProgramContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitProgram(LITTLEParser.ProgramContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterId(LITTLEParser.IdContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitId(LITTLEParser.IdContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPgm_bdy(LITTLEParser.Pgm_bdyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPgm_bdy(LITTLEParser.Pgm_bdyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDecl(LITTLEParser.DeclContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDecl(LITTLEParser.DeclContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */

	@Override public void exitString_decl(LITTLEParser.String_declContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStr(LITTLEParser.StrContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStr(LITTLEParser.StrContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVar_decl(LITTLEParser.Var_declContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVar_type(LITTLEParser.Var_typeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVar_type(LITTLEParser.Var_typeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAny_type(LITTLEParser.Any_typeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAny_type(LITTLEParser.Any_typeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterId_list(LITTLEParser.Id_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitId_list(LITTLEParser.Id_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterId_tail(LITTLEParser.Id_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitId_tail(LITTLEParser.Id_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterParam_decl_list(LITTLEParser.Param_decl_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitParam_decl_list(LITTLEParser.Param_decl_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitParam_decl(LITTLEParser.Param_declContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterParam_decl_tail(LITTLEParser.Param_decl_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitParam_decl_tail(LITTLEParser.Param_decl_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFunc_declarations(LITTLEParser.Func_declarationsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFunc_declarations(LITTLEParser.Func_declarationsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFunc_decl(LITTLEParser.Func_declContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFunc_body(LITTLEParser.Func_bodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFunc_body(LITTLEParser.Func_bodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStmt_list(LITTLEParser.Stmt_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStmt_list(LITTLEParser.Stmt_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStmt(LITTLEParser.StmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStmt(LITTLEParser.StmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBase_stmt(LITTLEParser.Base_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBase_stmt(LITTLEParser.Base_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssign_stmt(LITTLEParser.Assign_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssign_stmt(LITTLEParser.Assign_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssign_expr(LITTLEParser.Assign_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssign_expr(LITTLEParser.Assign_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterRead_stmt(LITTLEParser.Read_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitRead_stmt(LITTLEParser.Read_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWrite_stmt(LITTLEParser.Write_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWrite_stmt(LITTLEParser.Write_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterReturn_stmt(LITTLEParser.Return_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitReturn_stmt(LITTLEParser.Return_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpr(LITTLEParser.ExprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpr(LITTLEParser.ExprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpr_prefix(LITTLEParser.Expr_prefixContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpr_prefix(LITTLEParser.Expr_prefixContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFactor(LITTLEParser.FactorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFactor(LITTLEParser.FactorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFactor_prefix(LITTLEParser.Factor_prefixContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFactor_prefix(LITTLEParser.Factor_prefixContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostfix_expr(LITTLEParser.Postfix_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostfix_expr(LITTLEParser.Postfix_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCall_expr(LITTLEParser.Call_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCall_expr(LITTLEParser.Call_exprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpr_list(LITTLEParser.Expr_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpr_list(LITTLEParser.Expr_listContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpr_list_tail(LITTLEParser.Expr_list_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpr_list_tail(LITTLEParser.Expr_list_tailContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimary(LITTLEParser.PrimaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimary(LITTLEParser.PrimaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitIf_stmt(LITTLEParser.If_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElse_part(LITTLEParser.Else_partContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCond(LITTLEParser.CondContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCond(LITTLEParser.CondContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWhile_stmt(LITTLEParser.While_stmtContext ctx) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
}
