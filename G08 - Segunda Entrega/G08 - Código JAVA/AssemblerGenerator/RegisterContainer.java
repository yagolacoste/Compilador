package AssemblerGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RegisterContainer {

    private HashMap<Integer, String> registers = new HashMap<>();

    List<Boolean> averableRegisters = new ArrayList<>();

    public RegisterContainer() {
        this.registers.put(0,"EAX");
        this.registers.put(1,"EBX");
        this.registers.put(2,"ECX");
        this.registers.put(3,"EDX");
        this.registers.put(4,"AX");
        this.registers.put(5,"BX");
        this.registers.put(6,"CX");
        this.registers.put(7,"DX");

        this.setAverableRegisters();
    }

    public void setAverableRegisters(){
        for (int i = 0; i < 8; i++) {
            this.averableRegisters.add(true);
        }
    }

    public void setAverableRegister(int pos){
        this.averableRegisters.add(pos, true);
    }

    public void setAverableRegister(String register){
        Set<Integer> set = this.registers.keySet();
        for(Integer i : set){
            if(this.registers.get(i).equals(register)){
                setAverableRegister((int)i);
                break;
            }
        }
    }

    public void setNotAverableRegister(int pos){
        this.averableRegisters.add(pos, false);
    }

    public int getAverableRegister(){
        int pos = 0;
        for (Boolean b : this.averableRegisters) {
            if(b.booleanValue() == true)
                return pos;
            pos++;
        }
        return pos;
    }

    public String getRegister(){
        int pos = this.getAverableRegister();
        this.setNotAverableRegister(pos);
        return this.registers.get(pos);
    }

    public String forceRegister(){
        String register = "";
        for(int i = 0; i < this.averableRegisters.size(); i++){
            if(this.averableRegisters.get(i)){
                register = this.registers.get(i);
                if(!register.equals("EAX") && !register.equals("EDX")) {
                    this.setNotAverableRegister(i);
                    return register;
                }
            }
        }
        return register;
    }

}
