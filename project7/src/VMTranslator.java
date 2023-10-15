import java.io.*;
import java.util.*;

public class VMTranslator {
	
	int labelCount;
	String fileName;
	
	BufferedReader input;
	BufferedWriter output;
	
	public VMTranslator(String fileName, String filePath) throws IOException {
		
		labelCount = 0;
		this.fileName = fileName.replace(".vm", "");
		
		input   = new BufferedReader(new FileReader(filePath));
		output  = new BufferedWriter(new FileWriter(filePath.replace(".vm", ".asm")));
		
		String str = "";
		while ((str = input.readLine()) != null) {
			ArrayList<String> chunk = parse(str);
			if (chunk.size() == 0)		// empty line
				continue;
			writeln("// " + str);
			if (chunk.size() == 1)		// logical/arithmetic operation 
				calcOperation(chunk);
			if (chunk.size() == 3)		// push/pop operation
				stackOperation(chunk);
			output.newLine();
		}
		
		input.close();
		output.close();
	}
	
	private void writeln(String str) throws IOException {
		output.write(str);
		output.newLine();
		output.flush();
	}
	
	private Boolean empty(String str) {
		if (str == "")	return true;
		int x = str.indexOf("//");
		if (x == -1)
			return false;
		for (int i = 0; i < x; ++i) {
			if (str.charAt(i) != ' ')	return false;
		}
		return true;
	}
	
	private ArrayList<String> parse(String str) {
		ArrayList<String> res = new ArrayList<String>();
		if (empty(str))	return res;
		String word = "";
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == ' ' && !word.isEmpty()) {
				res.add(word);
				word = "";
			}
			if (str.charAt(i) != ' ')
				word += str.charAt(i);
		}
		if (!word.isEmpty())	res.add(word);
		// for (String chunk : res)
		// 		System.out.println(chunk);
		return res;
	}
		
	private void calcOperation(ArrayList<String> chunk) throws IOException {
		String opr = chunk.get(0);
		
		// singular calculation
		if (opr.equals("neg")) {
			singularCalc();
			writeln("M=-M");
		}
		
		if (opr.equals("not")) {
			singularCalc();
			writeln("M=!M");
		}
		
		// binary calculation
		if (opr.equals("add")) {
			binaryCalc();
			writeln("M=M+D");
		}
		
		if (opr.equals("sub")) {
			binaryCalc();
			writeln("M=M-D");
		}
		
		if (opr.equals("and")) {
			binaryCalc();
			writeln("M=M&D");
		}
		
		if (opr.equals("or")) {
			binaryCalc();
			writeln("M=M|D");
		}
		
		// binary comparison
		
		if (opr.equals("eq")) {
			binaryComp("JEQ");
		}
		
		if (opr.equals("gt")) {
			binaryComp("JGT");
		}
		
		if (opr.equals("lt")) {
			binaryComp("JLT");
		}
	}
	
	private void singularCalc() throws IOException {
		writeln("@SP");
		writeln("A=M-1");
	}
	
	private void binaryCalc() throws IOException {
		writeln("@SP");
		writeln("AM=M-1");
		writeln("D=M");
		writeln("A=A-1");
	}
	
	private void binaryComp(String jump) throws IOException {
		binaryCalc();
		writeln("D=M-D");
		writeln("M=-1");
		
		++labelCount;
		writeln("@JUMP_" + String.valueOf(labelCount));
		writeln("D;" + jump);
		writeln("@SP");
		writeln("A=M-1");
		writeln("M=0");
		writeln("(JUMP_" + String.valueOf(labelCount) + ")");
	}
	
	private void stackOperation(ArrayList<String> chunk) throws IOException {
		String opr = chunk.get(0);
		if (opr.equals("push")) {
			push(chunk.get(1), chunk.get(2));
		}
		if (opr.equals("pop")) {
			pop(chunk.get(1), chunk.get(2));
		}
	}
	
	private void push(String seg, String pos) throws IOException {
		if (seg.equals("constant")) {
			writeln("@" + pos);
			writeln("D=A");
			pushD();
		}
		
		if (seg.equals("local")) {
			setD("LCL", pos);
			pushD();
		}
		
		if (seg.equals("argument")) {
			setD("ARG", pos);
			pushD();
		}
		
		if (seg.equals("this")) {
			setD("THIS", pos);
			pushD();
		}
		
		if (seg.equals("that")) {
			setD("THAT", pos);
			pushD();
		}
		
		if (seg.equals("static")) {
			writeln("@" + fileName + "." + pos);
			writeln("D=M");
			pushD();
		}
		
		if (seg.equals("temp")) {
			writeln("@" + pos);
			writeln("D=A");
			writeln("@5");
			writeln("A=A+D");
			writeln("D=M");
			pushD();
		}
		
		if (seg.equals("pointer")) {
			if (pos.equals("0"))
				writeln("@THIS");
			else
				writeln("@THAT");
			writeln("D=M");
			pushD();
		}
	}
	
	private void setD(String head, String pos) throws IOException {
		writeln("@" + head);
		writeln("D=M");
		writeln("@" + pos);
		writeln("A=A+D");
		writeln("D=M");
	}
	
	private void pushD() throws IOException {
		writeln("@SP");
		writeln("A=M");
		writeln("M=D");
		writeln("@SP");
		writeln("M=M+1");
	}
	
	private void pop(String seg, String pos) throws IOException {
		if (seg.equals("local")) {
			popR13();
			setR13("LCL", pos);
		}
		
		if (seg.equals("argument")) {
			popR13();
			setR13("ARG", pos);
		}
		
		if (seg.equals("this")) {
			popR13();
			setR13("THIS", pos);
		}
		
		if (seg.equals("that")) {
			popR13();
			setR13("THAT", pos);
		}
		
		if (seg.equals("static")) {
			popR13();
			writeln("@R13");
			writeln("D=M");
			writeln("@" + fileName + "." + pos);
			writeln("M=D");
		}
		
		if (seg.equals("temp")) {
			popR13();
			writeln("@" + pos);
			writeln("D=A");
			writeln("@5");
			writeln("D=A+D");
			writeln("@R14");
			writeln("M=D");
			writeln("@R13");
			writeln("D=M");
			writeln("@R14");
			writeln("A=M");
			writeln("M=D");
		}
		
		if (seg.equals("pointer")) {
			popR13();
			writeln("@R13");
			writeln("D=M");
			if (pos.equals("0"))
				writeln("@THIS");
			else
				writeln("@THAT");
			writeln("M=D");
		}
		
	}
	
	private void popR13() throws IOException {
		writeln("@SP");
		writeln("AM=M-1");
		writeln("D=M");
		writeln("@R13");
		writeln("M=D");
	}
	
	private void setR13(String head, String pos) throws IOException {
		writeln("@" + head);
		writeln("D=M");
		writeln("@" + pos);
		writeln("D=A+D");
		writeln("@R14");
		writeln("M=D");
		writeln("@R13");
		writeln("D=M");
		writeln("@R14");
		writeln("A=M");
		writeln("M=D");
	}
	
	public static void main(String[] args) throws IOException {
		String fileName = args[0];
		String path = System.getProperty("user.dir");
		VMTranslator translator = new VMTranslator(fileName, path + "\\src\\" + fileName);
	}

}
