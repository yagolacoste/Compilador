package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Caracteres que pasan directo
public class SemanticAction1 extends SemanticAction{

	public SemanticAction1() {
	}

	@Override
	public void execute(char character, LexerAnalyzer la) {
		la.setPos(la.getPos() + 1);
		String key = Character.toString(character);
		int idNumber = la.getIdReservedWord(key); 
		la.setToken(idNumber,"");
		la.addRecognizedTokens(key);
		State state=la.getState(la.getActualState(), la.getColumn(character));
		la.setActualState(state.getNextstate());
	}

}
