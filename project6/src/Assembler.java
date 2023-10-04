import java.util.*;
import java.io.*;

public class Assembler {
	
	private Map<String, Integer> symbolMap;
	private int varAddress;
	
	public Assembler(String filePath) throws IOException {
		LabelParser lp = new LabelParser(filePath);
		symbolMap = new HashMap<String, Integer>(lp.getMap());
		varAddress = 16;
		
		BufferedReader input  = new BufferedReader(new FileReader(filePath));
		BufferedWriter output = new BufferedWriter(new FileWriter(filePath.replace(".asm", ".hack")));
		String str = "";
		while ((str = input.readLine()) != null) {
			
			CodeChecker cc = new CodeChecker(str);
			if (cc.isA()) {
				output.write(translateA(cc.symbol()));
			}
			if (cc.isC()) {
				output.write(translateC(cc));
			}
			if (cc.nextLine()) {
				output.newLine();
			}
			output.flush();
		}
		input.close();
		output.close();
	}
	
	private boolean isNumber(String str) {
		return str.matches("\\d+");
	}
	
	private String translateA(String symbol) {
		String res = "0";
		if (isNumber(symbol)) {
			String bi = Integer.toBinaryString(Integer.parseInt(symbol));
			int padding = 15 - bi.length();
			res = res + "0".repeat(padding) + bi;
		} else {
			if (!symbolMap.containsKey(symbol)) 
				symbolMap.put(symbol, varAddress++);
			String address = Integer.toBinaryString(symbolMap.get(symbol));
			int padding = 15 - address.length();
			res = res + "0".repeat(padding) + address;
		}
		return res;
	}
	
	private String translateC(CodeChecker cc) {
		return "111" + dealWithComp(cc.comp()) + dealWithDest(cc.dest()) + dealWithJump(cc.jump());
	}
	
	private String dealWithComp(String comp) {
		if (comp.equals("0")) {
			return "0101010";
		} else if (comp.equals("1")) {
			return "0111111";
		} else if (comp.equals("-1")) {
			return "0111010";
		} else if (comp.equals("D")) {
			return "0001100";
		} else if (comp.equals("A")) {
			return "0110000";
		} else if (comp.equals("M")) {
			return "1110000";
		} else if (comp.equals("!D")) {
			return "0001101";
		} else if (comp.equals("!A")) {
			return "0110001";
		} else if (comp.equals("!M")) {
			return "1110001";
		} else if (comp.equals("-D")) {
			return "0001111";
		} else if (comp.equals("-A")) {
			return "0110011";
		} else if (comp.equals("-M")) {
			return "1110011";
		} else if (comp.equals("D+1")) {
			return "0011111";
		} else if (comp.equals("A+1")) {
			return "0110111";
		} else if (comp.equals("M+1")) {
			return "1110111";
		} else if (comp.equals("D-1")) {
			return "0001110";
		} else if (comp.equals("A-1")) {
			return "0110010";
		} else if (comp.equals("M-1")) {
			return "1110010";
		} else if (comp.equals("D+A")) {
			return "0000010";
		} else if (comp.equals("D+M")) {
			return "1000010";
		} else if (comp.equals("D-A")) {
			return "0010011";
		} else if (comp.equals("D-M")) {
			return "1010011";
		} else if (comp.equals("A-D")) {
			return "0000111";
		} else if (comp.equals("M-D")) {
			return "1000111";
 		} else if (comp.equals("D&A")) {
 			return "0000000";
 		} else if (comp.equals("D&M")) {
 			return "1000000";
 		} else if (comp.equals("D|A")) {
 			return "0010101";
 		} else {
 			return "1010101";
 		}
	}
	
	private String dealWithDest(String dest) {
		if (dest.length() == 0)
			return "000";
		if (dest.equals("M")) {
			return "001";
		} else if (dest.equals("D")) {
			return "010";
		} else if (dest.equals("MD")) {
			return "011";
		} else if (dest.equals("A")) {
			return "100";
		} else if (dest.equals("AM")) {
			return "101";
		} else if (dest.equals("AD")) {
			return "110";
		} else {
			return "111";
		}
	}
	
	private String dealWithJump(String jump) {
		if (jump.length() == 0)
			return "000";
		if (jump.equals("JGT")) {
			return "001";
		} else if (jump.equals("JEQ")) {
			return "010";
		} else if (jump.equals("JGE")) {
			return "011";
		} else if (jump.equals("JLT")) {
			return "100";
		} else if (jump.equals("JNE")) {
			return "101";
		} else if (jump.equals("JLE")) {
			return "110";
		} else {
			return "111";
		}
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1)
			throw new IllegalArgumentException();
		String path = System.getProperty("user.dir");
		Assembler assembler = new Assembler(path + "\\testfiles\\" + args[0] + ".asm");
	}
}
