package Lexer;

import semantic_Actions.SemanticAction;

public class State {

	private int nextstate;
	private SemanticAction semanticaction;
	
	public State(int nextstate,SemanticAction sa) {
		this.nextstate = nextstate;
		this.semanticaction= sa;
	}
	
	public State(int nextstate) {
		this.nextstate = nextstate;
	}
	
	public void setState(int nextstate) {
		this.nextstate = nextstate;
	}
	
	public void setSemanticAction(SemanticAction s) {
		semanticaction = s;
	}

	public int getNextstate() {
		return nextstate;
	}

	public void setNextstate(int nextstate) {
		this.nextstate = nextstate;
	}

	public SemanticAction getSemanticaction() {
		return semanticaction;
	}

	public void setSemanticaction(SemanticAction semanticaction) {
		this.semanticaction = semanticaction;
	}
	

	
}
