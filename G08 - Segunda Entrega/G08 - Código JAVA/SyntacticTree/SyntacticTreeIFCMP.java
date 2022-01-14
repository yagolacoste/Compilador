package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Use;

public class SyntacticTreeIFCMP extends SyntacticTree{

    private static int counter = 0;

    public SyntacticTreeIFCMP(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeIFCMP(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeIFCMP(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String register = "";
        Attribute attribute = null;

        if(this.getLeft().getAttribute().getUse().equals(Use.constante) || this.getLeft().getAttribute().getUse().equals(Use.variable)) {
            register = resgisterContainer.getRegister();
            attribute = new Attribute(register, Use.registro);
            assembler += "MOV " + register + ", _" + this.getLeft().getAttribute().getScope() + '\n';
            this.replaceRoot(this.getLeft(), attribute);
        }

        if((this.getLeft().getAttribute().getUse().equals(Use.registro) &&
            this.getRight().getAttribute().getUse().equals(Use.registro))) {
                assembler += "CMP " + this.getLeft().getAttribute().getLexeme() + ", " + this.getRight().getAttribute().getLexeme() + '\n';
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
                resgisterContainer.setAverableRegister(this.getRight().getAttribute().getLexeme());
        }else {
            if (this.getLeft().getAttribute().getUse().equals(Use.registro)) {
                assembler += "CMP " + this.getLeft().getAttribute().getLexeme() + ", _" + this.getRight().getAttribute().getScope() + '\n';
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
            } else {
                if (this.getRight().getAttribute().getUse().equals(Use.registro)) {
                    assembler += "CMP " + this.getRight().getAttribute().getLexeme() + ", _" + this.getLeft().getAttribute().getScope() + '\n';
                    resgisterContainer.setAverableRegister(this.getRight().getAttribute().getLexeme());
                } else
                    assembler += "CMP _" + this.getLeft().getAttribute().getScope() + ", _" + this.getRight().getAttribute().getScope() + '\n';
            }
        }

        String label = "IF_CMP" + ++counter;
        assembler += getAssemblerConditionULONGINT() + label + '\n';
        SyntacticTreeIF.jLabel.push(label);
        return assembler;
    }



    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";

        assembler += "FLD _" + this.getLeft().getAttribute().getScope() + '\n';
        assembler += "FCOMP _" + this.getRight().getAttribute().getScope() + '\n';

        String auxVar = "@aux" + this.counterVar;

        assembler += "FSTSW _" + auxVar + '\n';
        assembler += "MOV AX, _" + auxVar + '\n';
        assembler += "SAHF" + '\n';

        this.assemblerData += "_" + auxVar + " DW ?" + '\n'; //16 bits

        String label = "IF_CMP" + ++counter;
        assembler += getAssemblerConditionDOUBLE() + label + '\n';
        SyntacticTreeIF.jLabel.push(label);
        this.counterVar++;
        return assembler;
    }


}
