package AssemblerGenerator;

//ULONGINT: registros del procesador (EAX, EBX, ECX Y EDX o AX, BX, CX y DX) y seguimiento de registros.
//DOUBLE: co-procesador 80X87, y variables auxiliares.

import SymbolTable.SymbolTable;
import SymbolTable.Use;
import SyntacticTree.SyntacticTree;

import java.util.List;

public class AssemblerGenerator {

    private SyntacticTree tree;
    private String assemblerHeader = "";
    private String assemblerData = "";
    private String assemblerCode = "";

    public AssemblerGenerator(SyntacticTree tree) {
        this.tree = tree;
    }

    private void concatenateMainHeader() {
        this.assemblerHeader += ".386" + '\n' + ".model flat, stdcall" + '\n' + "option casemap :none" + '\n' +
        "include \\masm32\\include\\windows.inc" + '\n' + "include \\masm32\\include\\kernel32.inc" + '\n' +
        "include \\masm32\\include\\user32.inc" + '\n' + "includelib \\masm32\\lib\\kernel32.lib" + '\n' +
        "includelib \\masm32\\lib\\user32.lib" + '\n' + ".STACK 200h" + '\n';
    }

    private void concatenateDataSection(SymbolTable st) {
        this.assemblerData += ".data" + '\n';
        this.assemblerData += "_errorCero" + " DB " + "\"Error division\", 0" + '\n';
        this.assemblerData += "_errorNegativo" + " DB " + "\"Error resta\" , 0" + '\n';
        this.assemblerData += "_ceroDOUBLE DQ 0.0" + '\n';
        this.assemblerData += "_ceroULONGINT DD 0" + '\n';
        this.assemblerData += st.generateAssemblerCode();
    }
    public void concatenatePROCAssembler(List<SyntacticTree> PROCtrees, RegisterContainer registerContainer) {
        for (SyntacticTree root : PROCtrees) {
            this.assemblerCode += root.getAttribute().getScope() + " proc\n";
            this.getMostLeftTreePROC(root, registerContainer);
            this.assemblerCode += "RET\n";
            this.assemblerCode += root.getAttribute().getScope() + " endp\n";
        }
    }

    private void concatenateCodeSection(List<SyntacticTree> PROCtrees, SyntacticTree root, RegisterContainer registerContainer) {
        this.assemblerCode += ".code" + '\n';
        this.assemblerCode += "Error_Resta_Negativa:"+ '\n';
        this.assemblerCode += "invoke MessageBox, NULL, addr _errorNegativo, addr _errorNegativo, MB_OK"+ '\n';
        this.assemblerCode += "invoke ExitProcess, 0" + '\n';
        this.assemblerCode += "Error_Division_Cero:"+ '\n';
        this.assemblerCode += "invoke MessageBox, NULL, addr _errorCero, addr _errorCero, MB_OK"+ '\n';
        this.assemblerCode += "invoke ExitProcess, 0" + '\n';

        this.concatenatePROCAssembler(PROCtrees, registerContainer);

        this.assemblerCode += "START:" + '\n';
        this.assemblerCode += "FNINIT" + '\n';

        if(root != null)
            this.getMostLeftTree(root, registerContainer);
        this.assemblerCode += "invoke ExitProcess, 0" + '\n' + "END START";
    }


    public void getMostLeftTree(SyntacticTree root, RegisterContainer registerContainer) {
        if (root != null && !root.isLeaf()) {
            if ((root.getRight() != null)) {
                if (root.getLeft().isLeaf() && root.getRight().isLeaf()) {
                    if(root.getLeft().getAttribute().getUse().equals(Use.llamado_procedimiento) &&
                            root.getRight().getAttribute().getUse().equals(Use.llamado_procedimiento)) {
                        this.assemblerCode += root.getLeft().assemblerTechnique(registerContainer);
                        this.assemblerCode += root.getRight().assemblerTechnique(registerContainer);
                    }else
                        this.assemblerCode += root.assemblerTechnique(registerContainer);
                } else {
                    this.getMostLeftTree(root.getLeft(), registerContainer);
                    this.getMostLeftTree(root.getRight(), registerContainer);
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                }
            } else {
                if (root.getLeft().isLeaf())
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                else{
                    this.getMostLeftTree(root.getLeft(), registerContainer);
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                }
            }
        }else{
            if(root.isLeaf() && root.getAttribute().getUse().equals(Use.llamado_procedimiento))
                this.assemblerCode += root.assemblerTechnique(registerContainer);
        }
    }

    public void getMostLeftTreePROC(SyntacticTree root, RegisterContainer registerContainer) {
        if (root != null && !root.isLeaf()) {
            if ((root.getRight() != null) && (root.getLeft() != null)) {
                if (root.getLeft().isLeaf() && root.getRight().isLeaf())
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                else {
                    this.getMostLeftTreePROC(root.getLeft(), registerContainer);
                    this.getMostLeftTreePROC(root.getRight(), registerContainer);
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                }
            } else {
                if (root.getRight() != null) {
                    this.getMostLeftTreePROC(root.getRight(), registerContainer);
                    this.assemblerCode += root.assemblerTechnique(registerContainer);
                }else{
                    if(root.getLeft() != null){
                        this.getMostLeftTreePROC(root.getLeft(), registerContainer);
                        this.assemblerCode += root.assemblerTechnique(registerContainer);
                    }
                }
            }
        }else{
            if(root.isLeaf() && root.getAttribute().getUse().equals(Use.llamado_procedimiento))
                this.assemblerCode += root.assemblerTechnique(registerContainer);
        }
    }

    public String printAssembler(List<SyntacticTree> PROCtrees, SyntacticTree root, RegisterContainer registerContainer, SymbolTable st){
        concatenateMainHeader();
        concatenateCodeSection(PROCtrees, root, registerContainer);
        concatenateDataSection(st);
        if(root != null)
            this.assemblerData += root.getAssemblerData();
        String assembler = this.assemblerHeader + this.assemblerData + this.assemblerCode;
        System.out.println(assembler);
        return assembler;
    }

}
