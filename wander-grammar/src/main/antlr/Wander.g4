grammar Wander;
@header {
package dev.ligature.wander.parser;
}
script: (letStat | expression)*;
expression: primative | funcCall | whenExpression; //TODO support method calls and specific types of expressions
letStat: 'let' WANDER_NAME '=' expression;
whenExpression: 'when'; //TODO complete
value: ENTITY | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL;
primative: ENTITY | ATTRIBUTE | value | BOOLEAN;
funcCall: WANDER_NAME '(' argumentList ')';
argumentList: expression; //TODO support multiple comma separated expressions
BOOLEAN: 'true' | 'false';
INTEGER_LITERAL: [0-9]+; //TODO should probably like allow things like 04
FLOAT_LITERAL: [0-9]+ '.' [0-9]+; //TODO should probably not allow things like 000234234.234234
ENTITY: '<' IDENTIFIER '>';
ATTRIBUTE: '@<' IDENTIFIER '>';
fragment IDENTIFIER: [a-zA-Z_][a-zA-Z0-9\-._~:/?#\u005B\u005D@!$&'()*+,;%=]*; //Note: the unicode character in this pattern are for [ and ]
WANDER_NAME: [a-z_][a-zA-Z0-9]*; //TODO not sure if this is what I want
STRING_LITERAL: '"' STRING_CONTENT* '"';
fragment STRING_CONTENT: ~[\u0000-\u001F"\\] | '\\' ["\\/bfnrt] | '\\u' HEX HEX HEX HEX;
fragment HEX: [0-9a-fA-F];
WS: [ \t\r\n]+ -> skip;
//TODO floatExpression
//TODO integerExpression
//TODO booleanExpression
//TODO stringExpression?
//TODO whenExpression
//TODO function call
//TODO function declaration
//TODO traits (declaration + impl)
