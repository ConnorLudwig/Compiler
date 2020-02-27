grammar LITTLE;


tokens: .*? EOF;
OPERATOR : ':='|'+'|'-'|'*'|'/'|'='|'!='|'<'|'>'|'('|')'|';'|','|'<='|'>=';
KEYWORD : 'PROGRAM'|'BEGIN'|'END'|'FUNCTION'|'READ'|'WRITE'|'IF'|'ELSE'
         |'ENDIF'|'WHILE'|'ENDWHILE'|'CONTINUE'|'BREAK'|'RETURN'|'INT'|'VOID'|'STRING'|'FLOAT';
COMMENT : '--'(~'\n')* -> skip;
INTLITERAL : [0-9]+;
FLOATLITERAL : [0-9]*?'.'[0-9]+;
STRINGLITERAL : '"'(~'"')*?'"';
IDENTIFIER : [a-zA-Z]+[a-zA-Z0-9]*;
WS : [ \t\r\n]+ -> skip;

/* Program */
program:            PROGRAM id BEGIN pgm_body END
id: IDENTIFIER
pgm_body: decl func_declarations
decl: string_decl decl | var_decl decl | empty

/* Global String Declaration */
string_decl: STRING id := str ;
str: STRINGLITERAL

/* Variable Declaration */
var_decl: var_type id_list ;
var_type: FLOAT | INT
any_type: var_type | VOID
id_list:  id id_tail
id_tail:  COMMA id id_tail | empty

/* Function Paramater List */
param_decl_list: param_decl param_decl_tail | empty
param_decl: var_type id
param_decl_tail: COMMA param_decl param_decl_tail | empty

/* Function Declarations */
func_declarations: func_decl func_declarations | empty
func_decl: FUNCTION any_type id (param_decl_list) BEGIN func_body END
func_body: decl stmt_list

/* Statement List */
stmt_list: stmt stmt_list | empty
stmt: base_stmt | if_stmt | while_stmt
base_stmt: assign_stmt | read_stmt | write_stmt | return_stmt

/* Basic Statements */
assign_stmt: assign_expr ;
assign_expr: id := expr
read_stmt: READ ( id_list );
write_stmt: WRITE ( id_list );
return_stmt: RETURN expr ;

/* Expressions */
expr: expr_prefix factor
expr_prefix: expr_prefix factor addop | empty
factor: factor_prefix postfix_expr
factor_prefix: factor_prefix postfix_expr mulop | empty
postfix_expr: primary | call_expr
call_expr: id ( expr_list )
expr_list: expr expr_list_tail | empty
expr_list_tail: , expr expr_list_tail | empty
primary: ( expr ) | id | INTLITERAL | FLOATLITERAL
addop: + | -
mulop: * | /

/* Complex Statements and Condition */
if_stmt: IF ( cond ) decl stmt_list else_part ENDIF
else_part: ELSE decl stmt_list | empty
cond: expr compop expr
compop: < | > | = | != | <= | >=

/* While statements */
while_stmt: WHILE ( cond ) decl stmt_list ENDWHILE
