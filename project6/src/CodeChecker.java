
public class CodeChecker {
	
	private String code;
	private int type;
	private int isComment, isA, isCEqual, isCSemi, isL;
	// 0: empty line or comment line
	// 1: A-command
	// 2: C-command
	// 3: L-command
	
	public CodeChecker(String code) {
		this.code = code;
		isComment = code.indexOf("//");
		isA       = code.indexOf('@');
		isCEqual  = code.indexOf('=');
		isCSemi   = code.indexOf(';');
		isL       = code.indexOf('(');
		
		int chk = Integer.MAX_VALUE;
		if (isComment != -1)	chk = min(chk, isComment);
		if (isA		  != -1)	chk = min(chk, isA);
		if (isCEqual  != -1)	chk = min(chk, isCEqual);
		if (isCSemi   != -1)	chk = min(chk, isCSemi);
		if (isL       != -1)	chk = min(chk, isL);	
		
		if (chk == isComment)					type = 0;
		if (chk == isA      )					type = 1;
		if (chk == isCEqual || chk == isCSemi)	type = 2;
		if (chk == isL		) 					type = 3;		
	}
	
	private int min(int x, int y) { return x < y ? x : y; } 
	
	public int commandType()  {  return type;  }
	
	public boolean nextLine() {  return type == 1 || type == 2;  }
	public boolean isA()	  {  return type == 1;  }
	public boolean isC()	  {  return type == 2;  }
	public boolean isL()	  {  return type == 3;  }
	
	public String symbol() {
		if (type != 1 && type != 3)
			throw new IllegalArgumentException();
		
		String res = "";
		if (type == 1) {
			int i = isA + 1;
			while (i < code.length() && !Character.isWhitespace(code.charAt(i))) {
				res += code.charAt(i);
				++i;
			}
		} else {
			int i = isL + 1;
			while (i < code.length() && code.charAt(i) != ')') {
				res += code.charAt(i);
				++i;
			}
		}
		return res;
	}
	
	public String dest() {
		if (type != 2)	throw new IllegalArgumentException();
		String res = "";
		if (isCEqual == -1)
			return res;
		
		int i = isCEqual - 1;
		while (i >= 0 && !Character.isWhitespace(code.charAt(i))) {
			res = code.charAt(i) + res;
			--i;
		}
		return res;
	}
	
	public String comp() {
		if (type != 2)	throw new IllegalArgumentException();
		String res = "";
		
		if (isCEqual == -1) {
			int i = isCSemi - 1;
			while (i >= 0 && !Character.isWhitespace(code.charAt(i))) {
				res = code.charAt(i) + res;
				--i;
			}
		} else {
			int i = isCEqual + 1;
			while (i < code.length() && !Character.isWhitespace(code.charAt(i)) 
								     && code.charAt(i) != ';') {
				res += code.charAt(i);
				++i;
			}
		}
		return res;
	}
	
	public String jump() {
		if (type != 2)	throw new IllegalArgumentException();
		String res = "";
		if (isCSemi == -1)
			return res;
		
		int i = isCSemi + 1;
		while (i < code.length() && !Character.isWhitespace(code.charAt(i))) {
			res += code.charAt(i);
			++i;
		}
		return res;
	}
	
	static public void main(String[] args) {
		CodeChecker x = new CodeChecker("@YOSUKE");
		if (x.commandType() == 2) {
			System.out.println(x.dest());
			System.out.println(x.comp());
			System.out.println(x.jump());
		} else {
			System.out.println(x.symbol());
		}
	}
}
