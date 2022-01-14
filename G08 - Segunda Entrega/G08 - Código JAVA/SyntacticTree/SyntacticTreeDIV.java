package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

import java.util.Stack;

public class SyntacticTreeDIV extends SyntacticTree{
    public SyntacticTreeDIV(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeDIV(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeDIV(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {

        String assembler = "";
        String register = "";
        String aux = "";

        if(this.getRight().getAttribute().getUse().equals(Use.constante) ||
                this.getRight().getAttribute().getUse().equals(Use.variable)) {
            register = resgisterContainer.forceRegister();
            assembler += "MOV " + register + ", _" + this.getRight().getAttribute().getScope() + '\n';
            aux = "DIV " + register + '\n';
            resgisterContainer.setAverableRegister(register);
        }else
            aux = "DIV _" + this.getRight().getAttribute().getScope() + '\n';

        if(this.getRight().getAttribute().getUse().equals(Use.registro))
            assembler += "CMP " + this.getRight().getAttribute().getScope() + ", _ceroULONGINT" + '\n';
        else{
            assembler += "CMP " + register + ", _ceroULONGINT" + '\n';
        }

        assembler += "JE Error_Division_Cero" + '\n';

        if(!this.getLeft().getAttribute().getLexeme().equals("EAX")) {
            resgisterContainer.setNotAverableRegister(0);
            assembler += "MOV EAX" + ", _" + this.getLeft().getAttribute().getScope() + '\n';
            if(this.getLeft().getAttribute().getUse().equals(Use.registro))
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
        }

        assembler += "MOV EDX, 0" + '\n';
        resgisterContainer.setNotAverableRegister(3);

        assembler += aux;

        resgisterContainer.setAverableRegister("EDX");

        this.deleteChildren(this);
        Attribute attribute = new Attribute("EAX", "EAX", Use.registro);
        this.replaceRoot(this, attribute);
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {

        String assembler = "";

        //assembler += "FLD _" + this.getRight().getAttribute().getScope() +'\n';
        assembler += "FCOMP _" + this.getRight().getAttribute().getScope() + '\n';
        assembler += "JE Error_Division_Cero" + '\n';

        assembler += "FLD _" + this.getLeft().getAttribute().getScope() +'\n';
        assembler += "FLD _" + this.getRight().getAttribute().getScope() +'\n';

        assembler += "FDIV" + '\n';

        String auxVar = "@aux" + this.counterVar;
        assembler += "FSTP _" + auxVar +'\n';

        this.assemblerData += "_" + auxVar + " DQ ?" + '\n';

        this.deleteChildren(this);
        Attribute attribute = new Attribute(auxVar, auxVar, Use.variable);
        this.replaceRoot(this, attribute);
        this.counterVar++;
        return assembler;
    }
}
