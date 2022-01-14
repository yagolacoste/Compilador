package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;

//Mueve y suma en caso de que entre \n, \s, \t
public class SemanticAction13 extends SemanticAction{

    @Override
    public void execute(char character, LexerAnalyzer la) {

        if(character == '\n') {
            la.setNroLinea(la.getNroLinea()+1);
        }
        la.setLexeme(""); 
        la.setPos(la.getPos()+1);
        State state = la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());

    }

}

