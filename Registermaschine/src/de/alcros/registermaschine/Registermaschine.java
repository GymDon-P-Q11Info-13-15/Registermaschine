package de.alcros.registermaschine;

import java.io.*;
import java.util.*;

public class Registermaschine {
	
	private int[] registers = new int[16];
	private int a;
	private int bz;
	private long[] code;
	private ArrayList<String> codeDbg = new ArrayList<String>();
	
	private static final int DELAY = 0;
	
	public static void main(String[] args) throws Exception {
		new Registermaschine(new FileInputStream(args[0]));
	}
	
	public Registermaschine(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		List<Long> codeList = new ArrayList<Long>();
		while((line = reader.readLine()) != null) {
			codeList.add(compile(line));
			codeDbg.add(line);
		}
		code = new long[codeList.size()];
		for(int i = 0; i < code.length; i++)
			code[i] = codeList.get(i);
		while (true) {
			execute(code[bz]);
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {}
		}
	}
	
	public static final long NOP = 0;
	public static final long LOAD = 1;
	public static final long DLOAD = 2;
	public static final long STORE = 3;
	public static final long ADD = 4;
	public static final long SUB = 5;
	public static final long MULT = 6;
	public static final long DIV = 7;
	public static final long JUMP = 8;
	public static final long JGE = 9;
	public static final long JGT = 10;
	public static final long JLE = 11;
	public static final long JLT = 12;
	public static final long JEQ = 13;
	public static final long JNE = 14;
	public static final long END = 15;
	
	public long compile(String line) {
		if(line.indexOf('#') >= 0)
			line = line.substring(0, line.indexOf('#'));
		if(line.indexOf(';') >= 0)
			line = line.substring(0, line.indexOf(';'));
		line = line.trim().toUpperCase();
		if (line.isEmpty()) return NOP<<32;
		
		String[] strarray = line.split(" ");
		if (strarray.length == 1 && strarray[0].equals("END")) return END<<32;
		if (strarray.length != 2) return NOP<<32;
		switch(strarray[0].hashCode()) {
		case 77487: // NOP
			return NOP<<32;
		case 2342118: // LOAD
			return LOAD<<32 | parseInt(strarray[1]);
		case 65141546: // DLOAD
			return DLOAD<<32 | parseInt(strarray[1]);
		case 79233217: // STORE
			return STORE<<32 | parseInt(strarray[1]);
		case 64641: // ADD
			return ADD<<32 | parseInt(strarray[1]);
		case 82464: // SUB
			return SUB<<32 | parseInt(strarray[1]);
		case 2378032: // MULT
			return MULT<<32 | parseInt(strarray[1]);
		case 67697: // DIV
			return DIV<<32 | parseInt(strarray[1]);
		case 2288686: // JUMP
			return JUMP<<32 | parseInt(strarray[1]);
		case 73384: // JGE
			return JGE<<32 | parseInt(strarray[1]);
		case 73399: // JGT
			return JGT<<32 | parseInt(strarray[1]);
		case 73539: // JLE
			return JLE<<32 | parseInt(strarray[1]);
		case 73554: // JLT
			return JLT<<32 | parseInt(strarray[1]);
		case 73334: // JEQ
			return JEQ<<32 | parseInt(strarray[1]);
		case 73601: // JNE
			return JNE<<32 | parseInt(strarray[1]);
		}
		return NOP<<32;
	}
	
	private static int parseInt(String str) {
		int i = Integer.parseInt(str);
		return i; // TODO: hex and octal
	}
	
	public void execute(long code) {
		int op = (int) (code >>> 32);
		int arg = (int)code;
		System.out.printf("%5d: A: %5d, %s\t%s\n", bz, a, Arrays.toString(registers), codeDbg.get(bz));
		bz++;
		switch(op) {
		case (int) NOP: default:
			return;
		case (int) LOAD:
			a = registers[arg&15];
			return;
		case (int) DLOAD:
			a = arg;
			return;
		case (int) STORE:
			registers[arg&15] = a;
			return;
		case (int) ADD:
			a += registers[arg&15];
			return;
		case (int) SUB:
			a -= registers[arg&15];
			return;
		case (int) MULT:
			a *= registers[arg&15];
			return;
		case (int) DIV:
			a /= registers[arg&15];
			return;
		case (int) JUMP:
			bz = arg-1;
			return;
		case (int) JGE:
			if(a >= 0) bz = arg-1;
			return;
		case (int) JGT:
			if(a > 0) bz = arg-1;
			return;
		case (int) JLE:
			if(a <= 0) bz = arg-1;
			return;
		case (int) JLT:
			if(a < 0) bz = arg-1;
			return;
		case (int) JEQ:
			if(a == 0) bz = arg-1;
			return;
		case (int) JNE:
			if(a != 0) bz = arg-1;
			return;
		case (int) END:
			System.out.printf("%5d: A: %5d, %s\n", bz, a, Arrays.toString(registers));
			System.exit(0);
			return;
		}
	}
}
