package oslinker;

public class Symbol {

    private String name;
    private boolean hasBeenUsed;
    private int definition;
    private int module;

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public Symbol(String name, int definition, int module){

        this.name = name;
        this.definition = definition;
        this.hasBeenUsed = false;
        this.module = module;
    }

    public boolean hasBeenUsed(){
        return this.hasBeenUsed;
    }

    public void setUse(boolean hasBeenUsed){
        this.hasBeenUsed = hasBeenUsed;
    }

    public String getName(){
        return this.name;
    }

    public void setName(){
        this.name = name;
    }

    public int getDefinition(){
        return this.definition;
    }

    public void setDefinition(int definition){
        this.definition = definition;
    }
}
