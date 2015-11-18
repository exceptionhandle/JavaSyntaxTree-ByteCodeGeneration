

Syntax Tree Object diagram and Byte Code
========================================
The code generates correct bytecode for integer or float variables and creates the object diagram using jive given in the tested cases in the testcase folder

OUTPUT Bytecode Structure:

1. Generates ByteCode with iconst, bipush, or sipush depending upon the numeric value of the literal:
 For small constants, in the range 0..5, the constant is implicit in the name of the instruction: iconst_0 ... iconst_5
 In generating code for integers in the range 6..127, the actual value comes immediately after the opcode bipush We are not dealing with negative numbers in TinyPL, but Java encodes numbers from -128 to +127 using 8 bits (one byte). Therefore, Java leaves one byte after the instruction for bipush.
 For short integers greater than 127, the generated opcode is sipush. Now we need two bytes to encode the value and hence Java leaves two bytes after the instruction for sipush.
2. The initialization, test, and increment components of a for-loop are all optional, and the simplest loop is of the form for ( ; ; )

The Grammer used for the java implementation in format of the programming language TinyPL:
program -> decls stmts end

decls -> int idlist ';'

idlist -> id [',' idlist ]

stmts -> stmt [ stmts ]

stmt -> assign ';'| cmpd | cond | loop

assign -> id '=' expr

cmpd -> '{' stmts '}'

cond -> if '(' rexp ')' stmt [ else stmt ]

loop -> for '(' [assign] ';' [rexp] ';' [assign] ')' stmt

rexp -> expr ('<' | '>' | '==' | '!= ') expr

expr -> term [ ('+' | '-') expr ]

term -> factor [ ('*' | '/') term ]

factor -> int_lit | id | '(' expr ')'
