package SymbolTable;

import java.util.HashMap;
import java.util.Map;

public class ReservedWord {
	
private Map<String, Integer> reserved_table;
	
	public ReservedWord() {
		reserved_table = new HashMap<>();

		reserved_table.put("(",40);
		reserved_table.put(")",41);
		reserved_table.put("*",42);
		reserved_table.put("+",43);
		reserved_table.put(",",44);
		reserved_table.put("-",45);
		reserved_table.put("/",47);
		reserved_table.put("<",60);
		reserved_table.put("=",61);
		reserved_table.put(">",62);
		reserved_table.put(";",59);
		reserved_table.put("{",123);
		reserved_table.put("}",125);
		reserved_table.put("ID",257);
		reserved_table.put("ULONGINT",258);
		reserved_table.put("IF",259);
		reserved_table.put("THEN",260);
		reserved_table.put("ELSE",261);
		reserved_table.put("END_IF",262);
		reserved_table.put("FOR",263);
		reserved_table.put("OUT",264);
		reserved_table.put("PROC",265);
		reserved_table.put("RETURN",266);
		reserved_table.put("DOUBLE",267);
		reserved_table.put("MENOR_IGUAL",268);
		reserved_table.put("MAYOR_IGUAL",269);
		reserved_table.put("IGUAL",270);
		reserved_table.put("DISTINTO",271);
		reserved_table.put("PUNTO_PUNTO",272);
		reserved_table.put("UP",273);
		reserved_table.put("DOWN",274);
		reserved_table.put("CADENA",275);
		reserved_table.put("NA",276);
		reserved_table.put("NRO_DOUBLE",277);
		reserved_table.put("NRO_ULONGINT",278);
	}

	public int getReservedId(String id) {
		return reserved_table.get(id);
	}

}
