grammar Wander;
script: stat*;
stat: letStat | expression;
expression: primative | funcCall | whenExpression; //TODO support method calls and specific types of expressions
letStat: 'let' IDENTIFIER '=' expression;
whenExpression: 'when'; //TODO complete
entity: '<' IDENTIFIER '>';
attribute: '@<' IDENTIFIER '>';
value: entity | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL;
primative: entity | attribute | value | statement | BOOLEAN;
statement: entity attribute value entity;
funcCall: FUNCTION_NAME '(' argumentList ')';
argumentList: expression; //TODO support multiple comma separated expressions
BOOLEAN: 'true' | 'false';
FUNCTION_NAME: [a-z_] [a-zA-Z0-9]*; //TODO not sure if this is what I want
IDENTIFIER: [a-z]+; //TODO update
STRING_LITERAL: '"' [a-z]+ '"'; //TODO update
FLOAT_LITERAL: [0-9]+ '.' [0-9]; //TODO should probably not allow things like 000234234.234234
INTEGER_LITERAL: [0-9]+; //TODO should probably like allow things like 04
WS: [ \t\r\n]+ -> skip;
//TODO floatExpression
//TODO integerExpression
//TODO stringExpression?
//TODO whenExpression
//TODO function call
//TODO function declaration
//TODO traits (declaration + impl)
