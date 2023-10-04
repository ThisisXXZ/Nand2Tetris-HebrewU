import java.util.*;
import java.io.*;

public class LabelParser {
	
	private Map<String, Integer> symbolMap;
	private int counter;
	
	public LabelParser(String filePath) throws IOException {
		symbolMap = new HashMap<String, Integer>();
		counter = 0;
		symbolMap.put("SP", 0);
		symbolMap.put("LCL", 1);
		symbolMap.put("ARG", 2);
		symbolMap.put("THIS", 3);
		symbolMap.put("THAT", 4);
		symbolMap.put("SCREEN", 16384);
		symbolMap.put("KBD", 24576);
		for (int i = 0; i <= 15; ++i)
			symbolMap.put("R" + Integer.toString(i), i);
		
		
		BufferedReader input = new BufferedReader(new FileReader(filePath));
		String str = "";
		while ((str = input.readLine()) != null) {
			CodeChecker cc = new CodeChecker(str);
			if (cc.isL())
				symbolMap.put(cc.symbol(), counter);
			if (cc.nextLine())
				++counter;
		}
		input.close();
	}
	
	public Map<String, Integer> getMap() {
		return symbolMap;
	}
	
	public static void main(String[] args) throws IOException {
		LabelParser x = new LabelParser("C:\\Users\\XXZ\\Desktop\\document\\2023-2024 sem1\\nand to tetris\\code\\project6\\src\\test.txt");
		System.out.println(x.getMap().get("CHIE"));
		System.out.println(x.getMap().get("YUKIKO"));
		System.out.println(x.getMap().get("RISE"));
	}
}
