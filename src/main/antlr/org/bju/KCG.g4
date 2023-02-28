grammar KCG;

@header {
    package org.bju.KCG;
}

/**
 * Parser Rules
 */

start: (statements+=statement | methods+=method)*;

statement: statement_if # statementif
         | statement_while # statementwhile
         | statement_assignment # statementassignment
         | expr # statementexpr;

statement_if: KEYWORD_IF cond=expr KEYWORD_THEN OPERATOR_CLOSE_CURLY true+=statement* OPERATOR_OPEN_CURLY false=else?;
else: KEYWORD_IF_NOT OPERATOR_CLOSE_CURLY false+=statement* OPERATOR_OPEN_CURLY;

statement_while: KEYWORD_FOREVER KEYWORD_UNLESS cond=expr OPERATOR_CLOSE_CURLY true+=statement* OPERATOR_OPEN_CURLY;

statement_assignment: id=IDENTIFIER OPERATOR_COMMA value=expr;

method: name=IDENTIFIER OPERATOR_L_SQUARE arguments=args OPERATOR_R_SQUARE OPERATOR_CLOSE_CURLY guts=start OPERATOR_OPEN_CURLY;

args: first=arg (OPERATOR_COMMA second+=arg)*;

arg: name=IDENTIFIER OPERATOR_COLON datatype=type;

type: TYPE_INT # inttype
    | TYPE_STR # strtype
    | TYPE_BOOL # booltype;

exprs: first=expr (OPERATOR_CARET second+=expr)*;

expr: STRING # str
    | DIGIT # int
    | IDENTIFIER # id
    | KEYWORD_YES # true
    | KEYWORD_NO # false
    | op=OPERATOR_BANG expr # not
    | first=expr op=(OPERATOR_MULTIPLY | OPERATOR_DIVIDE) second=expr # multdiv
    | first=expr op=(OPERATOR_ADDITION | OPERATOR_SUBTRACT) second=expr # addsub
    | first=expr op=OPERATOR_AMPERSAND second=expr # and
    | first=expr op=OPERATOR_PIPE second=expr # or
    | name=IDENTIFIER OPERATOR_L_SQUARE params=exprs OPERATOR_R_SQUARE # methodcall;

/**
 * Lexer Rules
 */

fragment NEWLINE: [\u000A\u000D];
fragment NOT_NEWLINE: [\u0000-\u0009\u000B-\u000C\u000E-\uFFFF];

COMMENT: '@' NOT_NEWLINE* NEWLINE;

fragment NUMBER: ('0' .. '9');

WHITESPACE: (' ' | '\t' | '\r' | '\n')+ -> skip;

KEYWORD_IF: 'if';
KEYWORD_IF_NOT: 'if-not';
KEYWORD_THEN: 'then';
KEYWORD_FOREVER: 'forever';
KEYWORD_UNLESS: 'unless';
KEYWORD_YES: 'yes';
KEYWORD_NO: 'no';

TYPE_INT: 'int';
TYPE_STR: 'str';
TYPE_BOOL: 'bool';

IDENTIFIER: ('_' | '.')+;

OPERATOR_ADDITION: '-';
OPERATOR_SUBTRACT: '*';
OPERATOR_MULTIPLY: '+';
OPERATOR_DIVIDE: '\\';
OPERATOR_MODULO: '$';
OPERATOR_CLOSE_CURLY: '}';
OPERATOR_OPEN_CURLY: '{';
OPERATOR_COMMA: ',';
OPERATOR_PIPE: '|';
OPERATOR_AMPERSAND: '&';
OPERATOR_BANG: '!';
OPERATOR_L_SQUARE: '[';
OPERATOR_R_SQUARE: ']';
OPERATOR_COLON: ':';
OPERATOR_CARET: '^';

DIGIT: NUMBER+;

fragment NOT_BACKTICK: [\u0000-\u0059\u0061-\uFFFF];

STRING: '`' NOT_BACKTICK* '`';