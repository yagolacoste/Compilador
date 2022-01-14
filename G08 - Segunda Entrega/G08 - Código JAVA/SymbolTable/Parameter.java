package SymbolTable;

public class Parameter {

    private String scope;
    private Type type;

    public Parameter(String scope, Type type) {
        this.scope = scope;
        this.type = type;
    }

    public String getLexeme() {
        return this.scope;
    }

    public void setLexeme(String scope) {
        this.scope = scope;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
