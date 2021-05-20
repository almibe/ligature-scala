/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
grammar Wander;
@header {
package dev.ligature.wander.parser;
}
script: expression*;
expression: wanderValue | funcCall;
funcCall: '(' atom* ')';
vector: '[' atom* ']';
atom: WANDER_NAME | expression; //TODO
value: ENTITY | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL;
wanderValue: ENTITY | ATTRIBUTE | value | BOOLEAN | vector;
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
//TODO whenExpression
//TODO function call
//TODO function declaration
