package SymbolTable;

import Lexer.State;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class SymbolTable {
	private Hashtable<String, List<Attribute>> symbolTable;
	private ReservedWord reservedword; 
	
	
	public SymbolTable() {
		super();
		this.symbolTable = new Hashtable<String,List<Attribute>>();
		this.reservedword = new ReservedWord();
	}


	public int getNumberId(String lexeme) {
		String aux = this.symbolTable.get(lexeme).get(0).getId();
		return this.reservedword.getReservedId(aux);
	}

	public void add(String lexeme, Attribute attribute) {
		List<Attribute> attributes = new ArrayList<>();
		if(!this.symbolTable.containsKey(lexeme)) {
			attributes.add(attribute);
			this.symbolTable.put(lexeme, attributes);
		}else{
			this.symbolTable.get(lexeme).add(attribute);
		}
	}

	public boolean checkDouble(String lexeme){
		if(!this.symbolTable.containsKey(lexeme))
			return true;
		return false;
	}

	public Hashtable<String,List<Attribute>> getSymbolTable(){
		Hashtable<String,List<Attribute>> symbolTable = new Hashtable<>(this.symbolTable);
		return symbolTable;
	}

	public String printSymbolTable(){
		String salida="";
		for(String key : this.symbolTable.keySet()){
			salida += "Lexema: " + key + "\n";
			for(Attribute a : this.symbolTable.get(key)) {
				salida += 	" Lexeme ST: " + a.getLexeme() +
							" - Ambito: " + a.getScope() +
							" - Parametros: " + a.printParameters() +
							" - Identificador: " + a.getId() +
							" - Uso: " + a.getUse() + " - Tipo: " + a.getType() +
							" - isDeclared? " + a.isDeclared() + "\n";
			}
		}
		return salida;
	}

	public void deleteSymbolTableEntry(String lexeme){
		List<Attribute> removedAttribute = this.symbolTable.remove(lexeme);
	}

	public void deleteLastElement(String lexeme){
		List<Attribute> removedAttribute = this.symbolTable.get(lexeme);
		removedAttribute.remove(removedAttribute.size()-1);
	}

	public List<Attribute> getAttributes(String lexeme){
		return this.symbolTable.get(lexeme);
	}

	public String getAttributeScope(String lexeme){
		return this.symbolTable.get(lexeme).get(0).getScope();
	}

	public String generateAssemblerCode(){
		String assembler ="";
		String value = "";
		int counter = 0;
		for(String key : this.symbolTable.keySet()) {
			for (Attribute a : this.symbolTable.get(key)) {
				String scope = a.getScope();
				if (a.getFlag() == 1) {
					if (a.getUse().equals(Use.variable) ||
							a.getUse().equals(Use.nombre_parametro))
						value = "?";
					else {
						if (a.getUse().equals(Use.constante)) {
							value = a.getLexeme();
						} else {
							if(a.getUse().equals(Use.cadena)){
								value = "\"" + a.getLexeme() + "\"" + ", 0";
							}
						}
					}

					switch (a.getType().getName()) {
						case "DOUBLE":
							assembler += "_" + scope + " DQ " + value + '\n'; //64 bits
							break;
						case "ULONGINT":
							assembler += "_" + scope + " DD " + value + '\n'; //32 bits
							break;
						case "STRING":
							assembler += "_" + scope + " DB " + value + '\n'; //8 bits
							break;
					}
				}
			}
		}
		return assembler;
	}
}
