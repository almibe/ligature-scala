grammar Wander;
@header {
package dev.ligature.wander.parser;
}
script: (letStat | expression)*;
expression: primative | funcCall | whenExpression; //TODO support method calls and specific types of expressions
letStat: 'let' IDENTIFIER '=' expression;
whenExpression: 'when'; //TODO complete
entity: '<' IDENTIFIER '>';
attribute: '@<' IDENTIFIER '>';
value: entity | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL;
primative: entity | attribute | value | BOOLEAN;
funcCall: FUNCTION_NAME '(' argumentList ')';
argumentList: expression; //TODO support multiple comma separated expressions
BOOLEAN: 'true' | 'false';
INTEGER_LITERAL: [0-9]+; //TODO should probably like allow things like 04
FLOAT_LITERAL: [0-9]+ '.' [0-9]+; //TODO should probably not allow things like 000234234.234234
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9\-._~:/?#\u005B\u005D@!$&'()*+,;%=]*; //Note: the unicode character in this pattern at for [ and ]
FUNCTION_NAME: [a-z_] [a-zA-Z0-9]*; //TODO not sure if this is what I want
STRING_LITERAL: '"' STRING_CONTENT* '"';
STRING_CONTENT: ~[\u0000-\u001F"\\] | '\\' ["\\/bfnrt] | '\\u' HEX HEX HEX HEX;
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
