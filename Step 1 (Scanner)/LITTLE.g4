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
