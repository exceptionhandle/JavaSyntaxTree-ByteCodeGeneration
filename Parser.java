//import java.util.HashMap;
//import java.util.Map;
import java.util.*;

/* 		OBJECT-ORIENTED RECOGNIZER FOR SIMPLE EXPRESSIONS
expr    -> term   (+ | -) expr | term
term    -> factor (* | /) term | factor
factor  -> int_lit | '(' expr ')'     
*/

public class Parser {
	
	public static void main(String[] args) {
		
		System.out.println("Enter a Program, end with end keyword !\n");
		
		Lexer.lex();
		new Program();
		Code.output();
		
	}
}

//program -> decls stmts end

class Program{
	
	Stmts stms;
	Decls d1,d2;

	public Program(){
		d1 = new Decls();
		if(Lexer.nextToken == Token.KEY_INT || Lexer.nextToken == Token.KEY_BOOL)
			d2 = new Decls();
		stms = new Stmts();
		
		if(Token.KEY_END == Lexer.nextToken){
			Code.code[Code.codeptr]= "return";
			Code.codeptr++;
		}
	}
}

// decls   ->  int idlist ';'

class Decls {
	
	// Check for int?
	Idlist idl;
	public Decls(){
		
		
		Lexer.lex();
		idl = new Idlist();
		if(Lexer.nextToken != Token.SEMICOLON)
			Lexer.error("missing semicolon");
		Lexer.lex();
	}
}


// stmts   ->  stmt [ stmts ]
		
class Stmts{
	Stmt st;
	Stmts sts;
	
	public Stmts(){
		st = new Stmt();
		if(Lexer.nextToken!=Token.KEY_END && Lexer.nextToken != Token.RIGHT_BRACE)
		{
			sts = new Stmts();
		}
		
		
		
	}
}

//stmt -> assign ';'| cmpd | cond | loop

class Stmt{
	Assign ass;
	Cmpd cmp;
	Cond con;
	Loop lop;
	
	

	public Stmt(){
	    switch(Lexer.nextToken){
    	case Token.LEFT_BRACE:
	    	cmp = new Cmpd();
    	    break;
	    case Token.KEY_IF:
	        con = new Cond();
    	    break;
	    case Token.KEY_FOR:
    		lop = new Loop();
    		break;
	    case Token.ID: // Assign value to identifier
	    
        	ass = new Assign();
        	Lexer.lex(); //skipping through semicolon
        	break;
        case Token.BOOL_FALSE: // Assign value to identifier
		    
        	ass = new Assign();
        	Lexer.lex(); //skipping through semicolon
        	break;
	    case Token.BOOL_TRUE: // Assign value to identifier
		    
        	ass = new Assign();
        	Lexer.lex(); //skipping through semicolon
        	break;
        
	    default:
	    	break;
    	}
	}
}

// idlist  ->  id [',' idlist ]

class Idlist{
	
	char id; 
	Idlist idl;
	static Map<Character, Integer> variableValue = new HashMap<Character,Integer>();
	static int i = 1;
	
	public Idlist()
	{

	//	System.out.println(Lexer.ident);
		variableValue.put(Lexer.ident, i);
		i=i+1;
		// character
		Lexer.lex();
		if(Lexer.nextToken == Token.COMMA)
		{
			Lexer.lex();
			idl = new Idlist();
		}
	}
}

// loop -> for '(' [assign] ';' [rexp] ';' [assign] ')' stmt

class Loop {
	Assign ass1,ass2;
	Rexp rex;
    Stmt s;
    int codePtr, codePtr1;
    String[] localstore = new String[10];
    Boolean flag = false ;
    
    public Loop(){
    	Lexer.lex();
    	if(Lexer.nextToken != Token.LEFT_PAREN)
    		Lexer.error("missing left parenthesis");
    	Lexer.lex();
    	
    	
    	if(Lexer.nextToken != Token.SEMICOLON)
    	{
    		
    		ass1 = new Assign();
    	}
    	codePtr = Code.codeptr;
    	Lexer.lex();
    	if(Lexer.nextToken != Token.SEMICOLON)
    	{
    		rex = new Rexp();
    	}
    	
		Lexer.lex();
		
		
		
		
    	if(Lexer.nextToken != Token.RIGHT_PAREN)
    	{
    		flag = true;
    		codePtr1 = Code.codeptr;
    		ass2 = new Assign();
    		for(int i= 0 ; i<=3; i++){
    			localstore[i]= Code.code[codePtr1];
    			codePtr1 +=1;
    	}
    	Code.codeptr -=4;
    	}
    	Lexer.lex();
    	
    	s = new Stmt();
    	
    	if(true==flag)
    	{
    		for(int i=0 ; i<=3 ; i++)
    		{
    			Code.code[Code.codeptr]=localstore[i];
    			Code.codeptr++;
    		}
    	}
    	
    	Code.gen(Code.genGoto());
    	Code.code[rex.ptrLocation] += Code.codeptr;
    	
    	Code.code[Code.codeptr-3] += codePtr;
    	
    	}
}

// cond    ->  if '(' rexp ')' stmt [ else stmt ]

class Cond{
	Rexp r;
	Stmt s1,s2;
	int ptrLocation;
	public Cond(){
		if(Lexer.nextToken == Token.KEY_IF)///codeptr = 10
		{
			Lexer.lex();
			if(Lexer.nextToken == Token.LEFT_PAREN)
			{
				Lexer.lex();
				r = new Rexp();
				if(Lexer.nextToken != Token.RIGHT_PAREN)
				{
					Lexer.error("right enclosing Parenthesis missing");
				}
				Lexer.lex();
			}
			s1 = new Stmt();
			if(Lexer.nextToken == Token.SEMICOLON)
				Lexer.lex();
			
			if(Lexer.nextToken == Token.KEY_ELSE)
			{
				ptrLocation = Code.codeptr;
				Code.gen(Code.genGoto());
				Code.code[r.ptrLocation] += Code.codeptr;

				Lexer.lex();
				s2 = new Stmt();
				Code.code[ptrLocation] += Code.codeptr;
			}//code == 20
			else
			{
				Code.code[r.ptrLocation] += Code.codeptr;
				System.out.println(r.ptrLocation);
				}
			
			
		}
		
	}
}


//    assign  ->  id '=' expr

class Assign   {
	char id;
	Expr e;
	static boolean bflag = false;
	static boolean notFlag = false;
	static Stack<Integer> stck;
	int ptr=0,ptr1 = 0;
	
	public Assign()  {
		stck = new Stack<Integer>();
		id= Lexer.ident;
		Lexer.lex(); // skipping through identifier
		Lexer.lex();// skipping through assignment operator
		e = new Expr(); // solving expression for Assignment
		

		if(bflag == true || (Code.codeptr>=3 && Code.checkIfCond(Code.code[Code.codeptr-3])))
		{
			ptr = 3;
			if(Code.codeptr>=3&&Code.checkIfCond(Code.code[Code.codeptr-3]))
			{
				 
				Code.code[Code.codeptr - ptr] += (Code.codeptr + 7 - ptr);
				Code.gen(Code.intcode(1));
				Code.gen(Code.genGoto());
				Code.code[Code.codeptr - ptr] += (Code.codeptr + 4 - ptr);
				Code.gen(Code.intcode(0));

		    }
			else
			{
                ptr1 = 5;
                Code.code[Code.codeptr - ptr1] += (Code.codeptr + 7);
				Code.gen(Code.opcode(Token.BOOL_AND));
                Code.code[Code.codeptr - ptr] += (Code.codeptr + 7 - ptr);
    			Code.gen(Code.intcode(1));
  			    Code.gen(Code.genGoto());
  			    Code.code[Code.codeptr - ptr] += (Code.codeptr + 4 - ptr);
  			    Code.gen(Code.intcode(0));

			}
			bflag = false;
		

		}
		Code.gen(Code.store(Idlist.variableValue.get(id)));

	}
}

// cmpd    ->  '{' stmts '}'

class Cmpd   {
	Stmts s;
	public Cmpd()  {
		if(Lexer.nextToken == Token.LEFT_BRACE)
		{
			Lexer.lex();
			s = new Stmts();
			if(Lexer.nextToken != Token.RIGHT_BRACE)
				Lexer.error("Missing Right Brace");
			Lexer.lex();
		}
	}
}

// expr -> term [ ('+' | '-') expr ]
		
class Expr   { // expr -> term (+ | -) expr | term
	Term t;
	Expr e,e2;
	char op;
	int op1;
	static Stack<Integer> anStck,orStck;

	
	public Expr() {
        anStck = new Stack<Integer>();
        orStck = new Stack<Integer>();
		t = new Term(anStck,orStck);
		if (Lexer.nextToken == Token.ADD_OP || 
				Lexer.nextToken == Token.SUB_OP) 
		{
			op = Lexer.nextChar;
			Lexer.lex();
			e = new Expr(anStck,orStck);
			Code.gen(Code.opcode(op));	 
		}
		if(Lexer.nextToken == Token.BOOL_OR)
		{
			Assign.bflag = true;
			// if any if_compare present before logical OR add that codeptr-2 to stack
			
			if(Code.codeptr>=3&&Code.checkIfCond(Code.code[Code.codeptr-3]))
			    orStck.add(Code.codeptr-3);  
			else
			{
				Code.gen(Code.opcode(op1));
				orStck.add(Code.codeptr);
			}
            while(!anStck.empty())
            {
                Code.code[anStck.pop()] += (Code.codeptr);
		    }
			
			op1 = Lexer.nextToken;
			Lexer.lex();
			e2 = new Expr(anStck,orStck);
		}
		}
		public Expr(Stack<Integer> anStck,Stack<Integer> orStck) {
	        anStck = new Stack<Integer>();
	        orStck = new Stack<Integer>();

			t = new Term(anStck,orStck);
			if (Lexer.nextToken == Token.ADD_OP || 
					Lexer.nextToken == Token.SUB_OP) 
			{
				op = Lexer.nextChar;
				Lexer.lex();
				e = new Expr(anStck,orStck);
				Code.gen(Code.opcode(op));	 
			}
			if(Lexer.nextToken == Token.BOOL_OR)
			{
				Assign.bflag = true;
				// if any if_compare present before logical OR add that codeptr-2 to stack
				
				if(Code.codeptr>=3&&Code.checkIfCond(Code.code[Code.codeptr-3]))
				    orStck.add(Code.codeptr-3);  
				else
				{
					Code.gen(Code.opcode(op1));
					orStck.add(Code.codeptr);
				}
	            while(!anStck.empty())
	            {
	                Code.code[anStck.pop()] += (Code.codeptr);
			    }
				
				op1 = Lexer.nextToken;
				Lexer.lex();
				e2 = new Expr(anStck,orStck);
				
				
			}


	}
}

//rexp -> expr('<'|'>'|'=='|'!=')expr

class Rexp{
	Expr e1,e2;
	int op;
	public int ptrLocation;
	
	public Rexp()
	{
		e1 = new Expr();
		if((Lexer.nextToken == Token.LESSER_OP || 
				Lexer.nextToken == Token.GREATER_OP || 
				Lexer.nextToken == Token.EQ_OP || 
				Lexer.nextToken == Token.NOT_EQ))
		{
			op = Lexer.nextToken;
			Lexer.lex();
			e2 = new Expr();
		
			ptrLocation = Code.codeptr;
			if(Assign.notFlag){
				op = notOp(op);
				Assign.notFlag = false;
			}
			Code.gen(Code.opcode(op));
			
		}
	}
	
	int notOp(int op)
	{
		switch(op){
		case Token.GREATER_OP:
			op = Token.LESSEREQ_OP;
			break;
		case Token.LESSER_OP:
			op = Token.GREATEREQ_OP;
			break;
		case Token.GREATEREQ_OP:
			op = Token.LESSER_OP;
			break;
		case Token.LESSEREQ_OP:
			op = Token.GREATEREQ_OP;
			break;
		case Token.EQ_OP:
			op = Token.LESSER_OP;
			break;
		case Token.NOT_EQ:
			op = Token.GREATER_OP;
			break;
        default:
        	break;
		}
		
		return op;
		
	}

}

// term -> factor [ ('*' | '/') term ]

class Term    { // term -> factor (* | /) term | factor
	Factor f;
	Term t;
	char op;
	int op1;
//	static Stack<Integer> aStck;

	public Term() {
		f = new Factor();
		if (Lexer.nextToken == Token.MULT_OP || 
				Lexer.nextToken == Token.DIV_OP 
				) {
			
			op = Lexer.nextChar;
			Lexer.lex();
			t = new Term();
			Code.gen(Code.opcode(op));
			}
	}
	public Term(Stack<Integer> anStck, Stack<Integer> orStck){
	
		f = new Factor();
		if (Lexer.nextToken == Token.MULT_OP || 
				Lexer.nextToken == Token.DIV_OP 
				) {
			
			op = Lexer.nextChar;
			Lexer.lex();
			t = new Term();
			Code.gen(Code.opcode(op));
			}

		if(Lexer.nextToken == Token.BOOL_AND)
		{
			Assign.bflag = true;

			op1 = Lexer.nextToken;
			
			// if if_compare present before logical AND add that ptr to stack
			
			if(Code.codeptr>3 && Code.checkIfCond(Code.code[Code.codeptr-3]))
			    anStck.add(Code.codeptr-3);
			else
			{
				Code.gen(Code.opcode(op1));
				anStck.add(Code.codeptr);
			}
			
            while(!orStck.empty())
            {
                Code.code[orStck.pop()] += (Code.codeptr);
                
		    }

			
			Lexer.lex();
			t = new Term(anStck,orStck);
			
		}
	}

}

// factor -> int_lit | '(' expr ')' | !(expr) | id

class Factor { 
	Expr e,e1,e2,e3;
	Rexp r1,r2;
	int i;
	String s;
	int op;
	int ptrLocation;
    Factor f;
//	public Stack<Integer> anStck,orStck;
	
	public Factor() {
		switch (Lexer.nextToken) {
		case Token.INT_LIT: // number
			i = Lexer.intValue;
			Code.gen(Code.intcode(i));
			Lexer.lex();
			break;
		case Token.BOOL_TRUE: 
			s = Token.toString(Token.BOOL_TRUE);
			Code.gen(Code.intcode(1));
			Lexer.lex();
			break;
		case Token.BOOL_FALSE: 
			s = Token.toString(Token.BOOL_FALSE);
			Code.gen(Code.intcode(0));
			Lexer.lex();
			break;
		case Token.LEFT_PAREN: // '('
			Lexer.lex();
			r1 = new Rexp();
			Lexer.lex(); // skip over ')'
			break;
		case Token.BOOL_NOT: // '!'
			Lexer.lex(); // skip over '!'
			Assign.notFlag = true;
			f = new Factor();
			
			break;
		case Token.ID:
			Code.gen(Code.load(Idlist.variableValue.get(Lexer.ident)));
			Lexer.lex();
			break;
		default:
			break;
		}
	}

}

class Code {
	static String[] code = new String[1000];
	static int codeptr = 0;
	
	public static void gen(String s) {
		code[codeptr] = s;
		codeptr++;
		if(s.contains("bipush") || s.contains("iload ") || s.contains("istore ")){
			codeptr= codeptr + 1;
		}
		
		if(s.contains("sipush") || checkIfCond(s))	
		{
			codeptr = codeptr + 2;
		}
	}
	
	
	public static boolean checkIfCond(String s)
	{
		if(s == null)
			return false;
		if( s.contains("if_icmple") 
				|| s.contains("if_icmpge") || s.contains("if_icmpne") 
				|| s.contains("if_icmpeq") || s.contains("goto")
			||s.contains("if_icmpgt") || s.contains("if_icmplt") 
			||s.contains("if_eq"))
			return true;
		return false;
	}
	
	public static String genGoto() {
		return "goto ";
	}

	public static String load(Integer integer) {
		if(integer<=3)
		{
			return "iload_"+integer;
		}
		return "iload " + integer;
		
	}

	public static String store(Integer integer) {
		if(integer<=3)
		{
			return "istore_"+integer;
		}
		return "istore " + integer;
	}

	public static String intcode(int i) {
		if (i > 127) return "sipush " + i;
		if (i > 5) return "bipush " + i;
		return "iconst_" + i;
	}
	
	public static String opcode(char op) {
		switch(op) {
		case '+' : return "iadd";
		case '-':  return "isub";
		case '*':  return "imul";
		case '/':  return "idiv";
		default: return "";
		}
	}
	
	public static String opcode(int t){
		switch(t){
		case Token.GREATER_OP : return "if_icmple ";
		case Token.LESSER_OP : return "if_icmpge ";
		case Token.GREATEREQ_OP : return "if_icmplt ";
		case Token.LESSEREQ_OP : return "if_icmpgt ";
		case Token.EQ_OP : return "if_icmpne ";
		case Token.NOT_EQ : return "if_icmpeq ";
		case Token.BOOL_AND : return "if_eq ";
		case Token.BOOL_OR : return "if_eq ";
		default: return "";
		}
	}
	
	public static void output() {
		for (int i=0; i<codeptr; i++)
		{
			if(code[i] == null) continue;
			System.out.println(i + ":" + code[i]);} 
		}
	}
