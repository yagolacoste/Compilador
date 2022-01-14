package semantic_Actions;

import Lexer.LexerAnalyzer;
import Lexer.State;
//Ingreso de caracteres que no son identificados por el autómata
public class SemanticAction12 extends SemanticAction{
    private static final String INCORRECTSIMBOL = "Caracter ingresado incorrecto: ";

    public SemanticAction12() {

    }

    @Override
    public void execute(char character, LexerAnalyzer la) {
        String error = "";
        if(la.getActualState() == 0){
            error = "Linea: " + la.getNroLinea() + " Error: " + INCORRECTSIMBOL + character;
            la.setPos(la.getPos() + 1);
        }else{
            error = "Linea: " + la.getNroLinea() + " Error: " + INCORRECTSIMBOL + la.getLexeme();
        }
        la.addError(error);
        la.setLexeme("");
        State state = la.getState(la.getActualState(), la.getColumn(character));
        la.setActualState(state.getNextstate());
    }

}
