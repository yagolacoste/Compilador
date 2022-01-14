package SyntacticTree;

import AssemblerGenerator.RegisterContainer;
import SymbolTable.Attribute;
import SymbolTable.Type;
import SymbolTable.Use;

public class SyntacticTreeMUL extends SyntacticTree{
    public SyntacticTreeMUL(SyntacticTree left, SyntacticTree rigth, Attribute attribute) {
        super(left, rigth, attribute);
    }

    public SyntacticTreeMUL(SyntacticTree left, SyntacticTree rigth) {
        super(left, rigth);
    }

    public SyntacticTreeMUL(SyntacticTree left, Attribute attribute) {
        super(left, attribute);
    }

    @Override
    public String generateAssemblerCodeRegister(RegisterContainer resgisterContainer) {
        String assembler = "";
        String aux = "";
        String register;
        Attribute attribute = null;

        if(this.getRight().getAttribute().getUse().equals(Use.constante)) {
            register = resgisterContainer.forceRegister();
            attribute = new Attribute(register, Use.registro);
            this.getRight().setAttribute(attribute);
            assembler += "MOV " + register + ", _" + this.getRight().getAttribute().getScope() + '\n';
            resgisterContainer.setAverableRegister(register);
        }

        if(!this.getLeft().getAttribute().getLexeme().equals("EAX")) {
            resgisterContainer.setNotAverableRegister(0);
            assembler += "MOV EAX" + ", _" + this.getLeft().getAttribute().getScope() + '\n';
            if(this.getLeft().getAttribute().getUse().equals(Use.registro))
                resgisterContainer.setAverableRegister(this.getLeft().getAttribute().getLexeme());
        }

        assembler += "MOV EDX, 0" + '\n';

        if(this.getRight().getAttribute().getUse().equals(Use.registro))
            assembler += "MUL " + this.getRight().getAttribute().getScope() + '\n';
        else
            assembler += "MUL " + this.getRight().getAttribute().getScope() + '\n';

        resgisterContainer.setAverableRegister("EDX");

        attribute = new Attribute("EAX", Use.registro);
        this.deleteChildren(this);
        this.replaceRoot(this, attribute);
        return assembler;
    }

    @Override
    public String generateAssemblerCodeVariable(RegisterContainer resgisterContainer) {
        String assembler = "";
        assembler += "FLD _" + this.getLeft().getAttribute().getScope()+ '\n';
        assembler += "FLD _" + this.getRight().getAttribute().getScope()+ '\n';
        assembler += "FMUL" + '\n';

        String auxVar = "@aux" + this.counterVar;
        assembler += "FSTP _" + auxVar + '\n';

        this.assemblerData += "_" + auxVar + " DQ ?" + '\n';

        this.deleteChildren(this);
        Attribute attribute = new Attribute(auxVar,auxVar, Use.variable);
        this.replaceRoot(this, attribute);
        this.counterVar++;
        return assembler;
    }
}
