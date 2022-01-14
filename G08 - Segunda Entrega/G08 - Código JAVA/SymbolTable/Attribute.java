package SymbolTable;

import SyntacticTree.*;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
    private String lexeme;
    private String scope;
    private Type type;
    private Use use;
    private String id;
    private SyntacticTree tree;
    private int amount;
    private int flag;
    private boolean declared;
    private List<Parameter> parameters = new ArrayList<Parameter>();

    public Attribute(String lexeme, String scope, Type type, Use use, String id, SyntacticTree tree) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.type = type;
        this.use = use;
        this.id = id;
        this.tree = tree;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, String scope, String id) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.id = id;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, String scope, String id, Type type) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.type = type;
        this.id = id;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, String scope, String id, Type type, Use use) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.type = type;
        this.id = id;
        this.use = use;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme) {
        this.lexeme = lexeme;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, Type type) {
        this.lexeme = lexeme;
        this.type = type;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, Use use) {
        this.lexeme = lexeme;
        this.use = use;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public Attribute(String lexeme, String scope, Use use) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.use = use;
        this.amount = 1;
        this.flag = 0;
        this.declared = false;
    }

    public String getLexeme() { return this.lexeme; }

    public void setLexeme(String lexeme) { this.lexeme = lexeme; }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        if(scope.equals(""))
            this.scope = this.scope + scope;
        else
            this.scope = this.scope + scope;
    }

    public void setScopePROC(String scope){
        this.scope = scope;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public Use getUse() {
        return use;
    }

    public void setUse(Use use) {
        this.use = use;
    }

    public String getId() {
        return id;
    }

    public void increaseAmount(){
        this.amount++;
    }

    public void decreaseAmount(){
        this.amount--;
    }

    public int getAmount(){
        return this.amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SyntacticTree getTree() {
        return tree;
    }

    public void setTree(SyntacticTree tree) {
        this.tree = tree;
    }

    public void setDeclared(){
        this.declared = true;
    }

    public void setDeclaredFalse(){
        this.declared = false;
    }

    public boolean isDeclared(){
        return this.declared;
    }

    public int getFlag(){
        return this.flag;
    }
    public void setFlag(){
        this.flag = 1;
    }

    public void addTreeParameter(SyntacticTree node){
        if (this.tree.getLeft().getLeft() == null){
            this.tree.getLeft().setLeft(node);
        }
        else{
            if(this.tree.getLeft().getRight() == null){
                this.tree.getLeft().setRigth(node);
            }
            else{
                SyntacticTree auxNode = this.tree.getLeft().getRight();
                Attribute attribute = new Attribute("PARAMETROS");
                this.tree.getLeft().setRigth(new SyntacticTreeLeaf(null, null, attribute));
                this.tree.getLeft().getRight().setLeft(auxNode);
                this.tree.getLeft().getRight().setRigth(node);
            }
        }
    }

    public List<Parameter> getParameters(){
       return new ArrayList<Parameter>(this.parameters);
    }

    public void setParameters(List<Parameter> parameters){
        for (Parameter parameter: parameters) {
            this.parameters.add(parameter);
        }
    }

    public String printParameters(){
        String salida = "";
        for (Parameter parameter: this.parameters) {
            salida += parameter.getLexeme() + "," ;
        }
        return salida;
    }
}
