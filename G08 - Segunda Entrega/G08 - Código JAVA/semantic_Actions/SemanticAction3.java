package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;

//Chequea rango double
public class SemanticAction3 extends SemanticAction{

	private static final double POWERPOSITIVE =  Math.pow(10,308);
	private static final double POWERNEGATIVE =  Math.pow(10,-308);
	private static final double  TOPRANGEPOSITIVE = 1.7976931348623157 * POWERPOSITIVE;
	private static final double LOWRANGEPOSITIVE = 2.2250738585072014 * POWERNEGATIVE;

	@Override
	public void execute(char character, LexerAnalyzer la) {

		double num;
		String outOfRange = "";
		String lexeme = la.getLexeme();
		if (lexeme.contains("d")){
			String[] d = lexeme.split("d");
			double real = Double.valueOf(d[0]);
			String d0 = d[0];
			String[] p = d0.split("\\.");
			if(p.length < 2) {
				d0 = d[0] + "0";
			}else{
				if(p[0].equals(""))
					d0 =  "0" + d[0];
				if(p[1].endsWith("0"))
					d0 = d[0].substring(0, d[0].length()-1);
			}
			d[0] = d0;
			if(!String.valueOf(real).equals(d[0])){
				num = 1.0;
				outOfRange = "Error";
			}else{
				int exponencial;
				exponencial = Integer.valueOf(d[1]);
				num = (double) (real * Math.pow(10,exponencial));
			}
		}
		else
			num = Double.valueOf(lexeme);

		if((outOfRange.equals("Error") && num == 1.0) || (num <= LOWRANGEPOSITIVE || num >= TOPRANGEPOSITIVE) && num != 0.0){
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