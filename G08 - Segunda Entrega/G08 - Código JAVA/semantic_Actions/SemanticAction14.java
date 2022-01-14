package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

public class SemanticAction14 extends SemanticAction {

	 private static final String INCORRECTSIMBOL = "No se permiten salto de linea en las cadenas ";

    public SemanticAction14() {
    }

    @Override
    public void execute(char character, LexerAnalyzer la) {

    	la.setNroLinea(la.getNroLinea()+1);
		la.setPos(la.getPos()+1);
		String error;
		error = "Linea: " + la.getNroLinea() + " Error: " + INCORRECTSIMBOL + character;
		la.addError(error);
        la.setLexeme("");
        State state = la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());
        

    }

}
