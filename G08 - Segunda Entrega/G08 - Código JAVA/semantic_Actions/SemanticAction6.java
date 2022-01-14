package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;

//Chequea que el punto venga solo para los double
public class SemanticAction6 extends SemanticAction{
	private static final double POWERPOSITIVE =  Math.pow(10,308);
	private static final double POWERNEGATIVE =  Math.pow(10,-308);
	private static final double  TOPRANGEPOSITIVE = 1.7976931348623157 * POWERPOSITIVE;
	private static final double LOWRANGEPOSITIVE = 2.2250738585072014 * POWERNEGATIVE;

	public void execute( char character, LexerAnalyzer la) {

		if(la.getLexeme().length() == 1 && la.getLexeme().equals(".")) {
			String error = "Linea: " + la.getNroLinea() + " Error: " + "Ingresó el caracter punto (.) solo";
			la.addError(error);
			la.setLexeme("");
			la.setActualState(0);
		}
		else{
			double num;
			String outOfRange = "";
			String lexeme = la.getLexeme();
			String[] p = lexeme.split("\\.");
			double real = Double.valueOf(lexeme);
			String p0 = p[0];
			if(p.length < 2) {
				p0 = p[0] + ".0";
			}else{
				if(p[0].equals(""))
					p0 =  "0." + p[1];
				else{
					p0 = lexeme;
				}
			}

			if(!String.valueOf(real).equals(p0)){
				num = 1.0;
				outOfRange = "Error";
			}else{
				num = real;
			}
			if((outOfRange.equals("Error") && num == 1.0) || (num < LOWRANGEPOSITIVE || num > TOPRANGEPOSITIVE) && num != 0.0){
				String error = "Linea: " + la.getNroLinea() + " Error: " + "El double se encuentra fuera de rango";
				la.addError(error);
				la.setLexeme("");
				la.setActualState(0);
			}else {
				lexeme = String.valueOf(num);
				if(la.checkDouble(lexeme)) {
					String scope = "D" + SemanticAction.counter;
					SemanticAction.counter++;
					Attribute attribute = new Attribute(lexeme, scope, "NRO_DOUBLE", Type.DOUBLE, Use.constante);
					la.addSymbolTable(lexeme, attribute);
				}
				int idNumber = la.getNumberId(lexeme);
				la.setToken(idNumber, lexeme);
				la.addRecognizedTokens("Valor double: " + lexeme);
				State state = la.getState(la.getActualState(), la.getColumn(character));
				la.setActualState(state.getNextstate());
			}

		}
	}
}