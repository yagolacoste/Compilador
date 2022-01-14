package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//chequea las palabras reservadas
public class SemanticAction4 extends SemanticAction{

	@Override
	public void execute(char character, LexerAnalyzer la) {

		String lexeme = la.getLexeme();
		if( ! ((lexeme.equals("IF") || lexeme.equals("THEN") || lexeme.equals("ELSE")
				|| lexeme.equals("END_IF") || lexeme.equals("OUT") || lexeme.equals("PROC") || lexeme.equals("RETURN")
				|| lexeme.equals("ULONGINT")|| lexeme.equals("DOUBLE") || lexeme.equals("FOR") || lexeme.equals("UP")
				|| lexeme.equals("DOWN") || lexeme.equals("NA") ))){
			la.setLexeme("");
			la.setActualState(0);
			String error = "Linea: " + la.getNroLinea() + " Error: " + "Palabra reservada invalida";
			la.addError(error);
		}
		else {
			int idNumber=la.getIdReservedWord(lexeme);
			la.setToken(idNumber,lexeme);
			la.addRecognizedTokens("Palabra reservada: " + lexeme);
			State state=la.getState(la.getActualState(), la.getColumn(character));
			la.setActualState(state.getNextstate());
		}
	}
	

}
