package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Fin de archivo
public class SemanticAction15 extends SemanticAction{

    public SemanticAction15(){ }

    @Override
    public void execute(char character, LexerAnalyzer la) {
        la.setToken(0,"EOF");
		la.addRecognizedTokens("Fin de archivo: " + character);
        State state=la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());
    }
}
