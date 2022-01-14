package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

public class SyntacticTreeASIG extends SyntacticTree{
    public SyntacticTreeASIG(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeASIG(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeASIG(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {

        String assembler = "";
        Attribute attribute = null;
        String register = "";

        if (checkChildrenUse()) {
            register = resgisterContainer.getRegister();
            assembler += "MOV " + register + ", _" + this.getRight().getAttribute().getScope() + '\n';
            assembler += "MOV _" + this.getLeft().getAttribute().getScope() + ", " + register + '\n';
            resgisterContainer.setAverableRegister(register);
        }else {
            if (this.getLeft().getAttribute().getUse().equals(Use.registro) &&
                this.getRight().getAttribute().getUse().equals(Use.registro)) {
                assembler += "MOV " + this.getLeft().getAttribute().getLexeme() + ", " + this.getRight().getAttribute().getLexeme() + '\n';
                resgisterContainer.setAverableRegister(this.getRight().getAttribute().getLexeme());
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
            } else if (this.getLeft().getAttribute().getUse().equals(Use.registro)) {
                assembler += "MOV " + this.getLeft().getAttribute().getLexeme() + ", _" + this.getRight().getAttribute().getScope() + '\n';
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
            } else {
                if (this.getRight().getAttribute().getUse().equals(Use.registro)) {
                    assembler += "MOV _" + this.getLeft().getAttribute().getScope() + ", " + this.getRight().getAttribute().getLexeme() + '\n';
                    resgisterContainer.setAverableRegister(this.getRight().getAttribute().getLexeme());
                }
            }
        }

        this.deleteChildren(this);
        //this.replaceRoot(this, attribute);
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";

        String scope = this.getRight().getAttribute().getScope().substring(0, 1);
        if(!scope.equals("_@"))
            assembler += "FLD _" + this.getRight().getAttribute().getScope()+ '\n';

        assembler += "FSTP _" + this.getLeft().getAttribute().getScope() + '\n';
        this.deleteChildren(this);
        return assembler;
    }
}
