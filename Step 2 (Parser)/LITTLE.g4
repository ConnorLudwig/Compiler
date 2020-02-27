grammar LITTLE;

fragment DIGIT0     : '0'..'9' ;
fragment DIGIT1     : '1'..'9' ;
fragment NL         : '\r'? '\n'
                    | '\r' ;

//KEYWORDS
PROGRAM             : 'PROGRAM';
BEGIN               : 'BEGIN';
END                 : 'END';
FUNCTION            : 'FUNCTION';
READ                : 'READ';
WRITE               : 'WRITE';
IF                  : 'IF';
ELSE                : 'ELSE';
FOR                 : 'FOR';
RETURN              : 'RETURN';
INT                 : 'INT';
VOID                : 'VOID';
STRING              : 'STRING';
FLOAT               : 'FLOAT';
WHILE               : 'WHILE';
ENDIF               : 'ENDIF';
ENDWHILE            : 'ENDWHILE';

//OPERATORS
ASSIGNOP            : ':=';
COMMA               : ',';
OPAR             : '(';
CPAR            : ')';
SEMICOLON           : ';';
ADDOP               : '+' | '-';
MULOP               : '*' | '/';
COMPOP              : '<' | '>' | '=' | '!=' | '<=' | '>=';

//TOKENS
IDENTIFIER          : ([a-z] | [A-Z]) ([a-z]+ | [A-Z]+ | DIGIT0+)* ;
INTLITERAL          : DIGIT1* DIGIT0+ ;
FLOATLITERAL        : INTLITERAL '.' DIGIT0*
                    | '.' DIGIT0+
                    ;
STRINGLITERAL       : '"' ~('"')* '"' ;
COMMENT             : '--' ~[\r\n]* NL -> skip
                    ;

OPERATOR            : ASSIGNOP
                    | ADDOP
                    | MULOP
                    | COMPOP
                    | OPAR
                    | CPAR
                    | SEMICOLON
                    | COMMA
                    ;
WHITESPACE          : [ \n\t\r]+ -> skip;



/* Program */
program :     PROGRAM id BEGIN pgm_bdy END;
id : IDENTIFIER;
pgm_bdy : decl func_declarations;
decl : string_decl decl | var_decl decl | empty;
empty : ;

/* Global String Decleration */
string_decl : STRING id ASSIGNOP str SEMICOLON;
str : STRINGLITERAL;

/* Variable Declaration */
var_decl : var_type id_list;
var_type : FLOAT | INT;
any_type : VOID| FLOAT| INT;
id_list : id id_tail;
id_tail : COMMA id id_tail | SEMICOLON | empty;

/* Function Parameter List */
param_decl_list : param_decl param_decl_tail | empty;
param_decl : var_type id;
param_decl_tail : COMMA param_decl param_decl_tail | empty;

/* Function Declarations */
func_declarations : func_decl func_declarations | empty;
func_decl : FUNCTION any_type id OPAR param_decl_list CPAR BEGIN func_body END;
func_body : decl stmt_list;

/* Statement List */
stmt_list : stmt stmt_list | empty;
stmt : base_stmt | if_stmt | while_stmt;
base_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements*/
assign_stmt : assign_expr SEMICOLON;
assign_expr : id ASSIGNOP expr;
read_stmt : READ OPAR id_list CPAR SEMICOLON;
write_stmt : WRITE OPAR id_list CPAR SEMICOLON;
return_stmt : RETURN expr SEMICOLON;

/* Expressions */
expr : expr_prefix factor;
expr_prefix : expr_prefix factor ADDOP | empty;
factor : factor_prefix postfix_expr;
factor_prefix : factor_prefix postfix_expr MULOP | empty;
postfix_expr : primary | call_expr;
call_expr : id OPAR expr_list CPAR;
expr_list : expr expr_list_tail | empty;
expr_list_tail : COMMA expr expr_list_tail | empty;
primary : OPAR expr CPAR | id | INTLITERAL | FLOATLITERAL;

/* Complex Statements and Condition */
if_stmt : IF OPAR cond CPAR decl stmt_list else_part ENDIF;
else_part : ELSE decl stmt_list | empty;
cond : expr COMPOP expr;


/* While Statements */
while_stmt : WHILE OPAR cond CPAR decl stmt_list ENDWHILE;
