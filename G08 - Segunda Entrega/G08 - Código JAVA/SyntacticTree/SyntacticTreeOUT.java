package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;

public class SyntacticTreeOUT extends SyntacticTree{
    public SyntacticTreeOUT(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeOUT(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeOUT(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }


    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        Attribute attribute = this.getLeft().getAttribute();
        this.deleteLeftChildren(this);
        this.replaceRoot(this, attribute);
        assembler += "invoke MessageBox, NULL, addr _" + this.getAttribute().getScope() +
                        ", addr _" + this.getAttribute().getScope() + ", MB_OK \n";
        return assembler;
    }

    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer){
        String assembler = "";
        return assembler;
    }
}
