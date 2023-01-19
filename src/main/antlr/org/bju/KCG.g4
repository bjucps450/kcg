grammar KCG;

@header {
    package org.bju.KCG;
}

/**
 * Parser Rules
 */

start: ;

/**
 * Lexer Rules
 */

fragment NEWLINE: [\u000A\u000D];
fragment NOT_NEWLINE: [\u0000-\u0009\u000B-\u000C\u000E-\uFFFF];

COMMENT: '@' NOT_NEWLINE* NEWLINE;

fragment NUMBER: ('0' .. '9');

OPERATOR_ADDITION: '-';
OPERATOR_SUBTRACTION: '*';
OPERATOR_MULTIPLY: '+';
OPERATOR_DIVIDE: '\\';
OPERATOR_MODULO: '$';

DIGIT: NUMBER+;

fragment NOT_BACKTICK: [\u0000-\u0059\u0061-\uFFFF];

STRING: '`' NOT_BACKTICK* '`';