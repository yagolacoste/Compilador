package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
import SymbolTable.Attribute;

//chequea largo de identificador < 20 (descarta lo que sobra)
public class SemanticAction2 extends SemanticAction{

	private static final int LENGTH = 20;

	@Override
	public void execute(char character, LexerAnalyzer la) {
		String lexeme = la.getLexeme();
		if(lexeme.length() > LENGTH){
			String warning = "Linea: " + la.getNroLinea() + " Warning: " + "La longitud del identificador es mayor a 20";
			la.addWarning(warning);
			lexeme=lexeme.substring(0, LENGTH);
		}

		Attribute attribute = new Attribute(lexeme, lexeme + "@main", "ID");
		la.addSymbolTable(lexeme, attribute);
		int idNumber = la.getNumberId(lexeme);
		la.setToken(idNumber, lexeme);
		la.addRecognizedTokens("Identificador: " + lexeme);
		State state = la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());

	}

}
