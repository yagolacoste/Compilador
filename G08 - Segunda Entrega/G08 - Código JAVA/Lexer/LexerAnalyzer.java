package Lexer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import SymbolTable.ReservedWord;
import SymbolTable.SymbolTable;
import SymbolTable.Attribute;
import semantic_Actions.SemanticAction;


public class LexerAnalyzer {
	private static final int EOF = 9999999;
	private static final int FINAL_STATE = -1;


	//Matriz de transición de estados, de acciones semánticas y mapeo de columnas
	public StateMatrix sm;
	//Estado que contiene la celda de la matriz
	private State state;
	//Tabla de palabras reservadas
	private ReservedWord rw;
	//Tabla de símbolos
	private SymbolTable st;

	//Errores, warnings, comentarios y tokens reconocidos
	private List<String> errors;
	private List<String> warning;
	private List<String> comments;
	private List<String> recognizedTokens;

	//Token compuesto por el par <token,lexema> que se forma recorriendo la entrada
	private Token token;
	//Posición en la que nos encontramos en el recorrido de la entrada
	private int pos;
	//Número de línea que estamos analizando en el recorrido de la entrada
	private int nroLinea;
	//Lexema que formamos en el recorrido de la entrada
	private String lexeme;
	//Estado actual de la matriz de estados en el que nos encontramos
	private int actualState;
	//Entrada
	private String source = "";

	public LexerAnalyzer(String source) {
			this.sm = new StateMatrix();
			this.rw = new ReservedWord();
			this.st = new SymbolTable();

			this.errors = new ArrayList<String>();
			this.warning = new ArrayList<String>();
			this.comments = new ArrayList<String>();
			this.recognizedTokens = new ArrayList<String>();

			this.token = new Token();
			this.pos = 0;
			this.nroLinea = 1;
			this.lexeme = new String();
			this.actualState = 0;
			this.source = source;
	}


	//Devuelve el par <token,lexema> a partir de la entrada
	public Token getNextToken() {
		Token finalToken = null;
		SemanticAction action;

		while (actualState != FINAL_STATE && this.getPos() < source.length()) {
			if (actualState == EOF) {
				actualState = -1;
			}else{
			char character = source.charAt(this.getPos());
			int col = this.getColumn(character);       
			this.state = sm.getState(actualState, col);
			action = this.state.getSemanticaction();    
			action.execute(character, this);
			}
		}
		finalToken = new Token(this.token.getId(), this.token.getLexema());
		this.setToken(-1, "");
		this.setActualState(0);
		this.setLexeme("");
		return finalToken;
	}

	//Devuelve el numero de la columna asociada el caracter
	public int getColumn(char character) {
		return sm.getColumn(character);
	}
	//Devuelve el proximo estado del caracter donde tenemos que ir
	public int getActualState() {
		return this.actualState;
	}

	//Detea el proximo estado del caracter donde tenemos que ir
	public void setActualState(int state) {
		 this.actualState =state;
	}
	//Detea el token que se va a devolver
	public void setToken(int id,String lexeme) {
		this.token.setLexema(lexeme);
		this.token.setId(id);
	}
	
	//Devuelve la posicion actual de la letra de entrada
	public int getPos() {
		return this.pos;
	}

	//Setea la posicion actual de la letra de entrada
	public void setPos(int i) {
		this.pos=i;
	}
	
	//Agrega errores a la lista
	public void addError(String error) {
		this.errors.add(error);
	}

	//Retorna la lista de errores
	public String getErrors(){
		String salida="";
		for(String e:this.errors)
			salida+=e+"\n";
		return salida;
	}

	//Agrega warning a la lista
	public void addWarning(String warning) {
		this.warning.add(warning);
	}

	public String getWarning() {
		String salida="";
		for(String w:this.warning)
			salida+=w+"\n";
		return salida;
	}

	//Agrega tokens a la lista de tokens reconocidos
	public void addRecognizedTokens(String token) {
		this.recognizedTokens.add(token);
	}

	//Devuelve la lista de tokens reconocidos
	public List<String> getRecognizedTokens() {
		List<String> tokens = new ArrayList<String>(this.recognizedTokens);
		return tokens;
	}

	//Agrega un <lexema,ID> nuevo a la tabla de simbolos
	public void addSymbolTable(String token, Attribute attribute) {
		this.st.add(token, attribute);
	}

	public boolean checkDouble(String lexeme){
		return this.st.checkDouble(lexeme);
	}

	//Devuelve tabla de símbolos
	public SymbolTable getSymbolTable(){
		return this.st;
	}

	//Devolvemos el objeto st
	public SymbolTable getSt() {
		return this.st;
	}

	//Devuelve entrada tabla de simbolos
	public List<Attribute> getAttribute(String lexeme){
		return this.st.getSymbolTable().get(lexeme);
	}

	public String printSymbolTable(){
		return this.st.printSymbolTable();
	}

	//Devuelve el id de un lexema dado buscado en la tabla de simbolo
	public int getNumberId(String lexeme) {
		return this.st.getNumberId(lexeme);
	}

	//Devuelve el lexema actual que estamos construyendo
	public String getLexeme() {
		return this.lexeme;
	}

	//Construye el lexema
	public void setLexeme(String string) {
		this.lexeme=string;
	}

	//Obtiene la celda(estado) de la matriz de SM
	public State getState(int row,int col) {
		return this.sm.getState(row, col);
	}
	
	//Obtiene el numero de lineas
	public int getNroLinea() {
		return nroLinea;
	}

	//Setea el numero de lineas
	public void setNroLinea(int nroLinea) {
		this.nroLinea = nroLinea;
	}

	//Obtiene el id de la plabra reservada
	public int getIdReservedWord(String key) {
		return rw.getReservedId(key);
	}

	//Agrega comentarios
	public void addComments(String comment) {
		this.comments.add(comment);
	}


	//Devolver source
	public boolean endSource(){
		return (this.source.length() == this.pos);
	}

	//Devolver source
	public String getSource(){
		return this.source;
	}

	public String getAttributeScope(String lexeme){
		return this.st.getAttributeScope(lexeme);
	}

	public boolean checkNegativeDouble(String lexeme){
		final double POWERPOSITIVE =  Math.pow(10,308);
		final double POWERNEGATIVE =  Math.pow(10,-308);
		final double TOPRANGENEGATIVE = -2.2250738585072014 * POWERNEGATIVE;
		final double LOWRANGENEGATIVE = -1.7976931348623157 * POWERPOSITIVE;

		double num;

		if (lexeme.contains("E")){
			String[] d = lexeme.split("E");
			double real = Double.valueOf(d[0]);
			String d0 = d[0];
			String[] p = d0.split("\\.");
			if(p.length < 2) {
				d0 = d[0] + "0";
			}else{
				if(p[0].equals(""))
					d0 =  "0" + d[0];
			}
			d[0] = d0;
			if(!String.valueOf(real).equals(d[0])){
				num = 4.9*Math.pow(10,-324);
			}else{
				int exponencial;
				exponencial = Integer.valueOf(d[1]);
				num = (double) (real * Math.pow(10,exponencial));
			}
		}
		else
			num = Double.valueOf(lexeme);

		if(num < LOWRANGENEGATIVE || num > TOPRANGENEGATIVE && num != 0)
			return true;
		else
			return false;
	}
}
