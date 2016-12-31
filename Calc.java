/**
 * @author kunpeng
 * 计算器
 */
public class Calc {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String source = " ( 1 + 5 * ( 20 + 1 ) + 1 ) * 10 ";
		Parser parser = new Parser();
		Node root = parser.parse(source);
		int result = root.exec();
		System.out.println(source+"="+result);
	}
	public static class Parser{
		private Scanner fScanner;
		private Token token;
		public Node parse(String source){
			fScanner = new Scanner(source);
			advance();
			Node opNode  = matchOpExpr();
			return opNode;
		}
		private Node matchOpExpr() {
			switch (token.type) {
			case '(':{
					GroupNode gNode = matchGroup();
					advance();
					if(token.type == Scanner.OP){
						return matchOpExpr(gNode);
					}else if(token.type == Scanner.EOF){
						return gNode;
					}
				}
				break;
			case Scanner.NUM:{
					Token num1 = token;
					NumberNode lhs = new NumberNode();
					lhs.value = num1.value;
					advance();
					if(token.type == Scanner.OP){
						return matchOpExpr(lhs);
					}else if(token.type == Scanner.EOF){
						return lhs;
					}
				}
			default:
				System.err.println("error");
				break;
			}
			return null;
		}
		private GroupNode matchGroup() {
			advance();
			GroupNode gNode = new GroupNode();
			gNode.expr = matchOpExpr();
			if(token.type!=')'){
				System.err.println("expected ')' but '"+token.value+"'");
			}
			return gNode;
		}
		public OpNode matchOpExpr(Node lhs) {
			OpNode opNode = new OpNode();
			Token op = token;
			advance();
			Node rhs = null;
			if(token.type == '('){
				rhs = matchGroup();
			}else{
				Token num2 = token;
				NumberNode rhs1 = new NumberNode();
				rhs1.value = num2.value;
				rhs = rhs1;
			}
			advance();
			opNode.left = lhs;
			opNode.right = rhs;
			opNode.op = (char) op.value.charAt(0);
			if(token.type==Scanner.OP){
				if(token.priority > op.priority){
					opNode.right = matchOpExpr(rhs);
				}else{
					opNode = matchOpExpr(opNode);
				}
			}
			return opNode;
		}
		private void advance() {
			token = fScanner.nextToken();
		}
	}
	public static class Scanner{
		public String fSource;
		private int index = 0;
		private int start = 0;
		public static final int NUM = 0;
		public static final int OP = 1;
		public static final int EOF = -1;
		public Scanner(String source){
			fSource = source;
		}
		private void advance(){
			index++;
		}
		private boolean isEOF(){
			return index >= fSource.length();
		}
		public Token nextToken(){
			start = index;
			Token token = new Token();
			if(isEOF()){
				token.type = EOF;
				return token;
			}
			char ch = fSource.charAt(index);
			advance();
			switch (ch) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				while(!isEOF() && Character.isDigit(fSource.charAt(index))){
					advance();
				}
				token.type = NUM;
				token.value = fSource.substring(start, index);
				break;
			case '(':
			case ')':
				token.type = ch;
				token.value = String.valueOf(ch);
				break;
			case '+':
			case '-':
				token.priority = 1;
				token.type = OP;
				token.value = String.valueOf(ch);
				break;
			case '*':
			case '/':
				token.priority = 2;
				token.type = OP;
				token.value = String.valueOf(ch);
				break;
			case ' ':
				token = nextToken();
				break;
			default:
				System.err.println("error code : "+ch);
				break;
			}
			return token;
		}
		
	}
	
	public static class Token{
		public int type;
		public short priority;
		public String value;
		@Override
		public String toString() {
			return "Token [type=" + type + ", value=" + value + "]";
		}
	}
	public static class Node{
		public int exec() {
			return -1;
		}
	}
	public static class NumberNode extends Node{
		String value;
		@Override
		public int exec() {
			return Integer.parseInt(value);
		}
	}
	public static class GroupNode extends Node{
		public Node expr;
		@Override
		public int exec() {
			return expr.exec();
		}
	}
	public static class OpNode extends Node{
		public char op;
		public Node left;
		public Node right;
		public int exec() {
			switch (op) {
			case '+':
				return left.exec() + right.exec();
			case '-':
				return left.exec() - right.exec();
			case '*':
				return left.exec() * right.exec();
			case '/':
				return left.exec() / right.exec();
			default:
				break;
			}
			return super.exec();
		}
	}
}
