package oslinker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class LinkerOS {

    /*
        Assumes a 200 word machine for this lab.
        Gets file from standard input at runtime.
        Close to fully functional. Passes tests 1-7.

     */

    private static final int MACHINE_SIZE = 200;
    private static ArrayList<Symbol> symbolTable = null;
    private static ArrayList<String> errorsList = null;
    private static File file = null;
    private static ArrayList<OutputFormat> output = new ArrayList<OutputFormat>();
    private static int numModules; //the number of modules, read in by the file


    public static void main(String [] args){

        if(args.length > 1){
            System.err.println("Too many arguments, require 1(File path)");
        }

        if(args.length < 1){
            System.err.println("No file path specified in arguments");
        }

        file = new File(args[0]);
        errorsList = new ArrayList<String>();
        symbolTable = new ArrayList<Symbol>();


        pass1();

        pass2();

        printer();


    }

    private static void pass1(){

        try{
            Scanner inputFile = new Scanner(file);
            String currentChar = null;
            //currentModSize = 0;
            int offset = 0;


            numModules = inputFile.nextInt();
            //int temp = numModules;


            for(int i = 0; i < numModules; i++){

                currentChar = inputFile.next();
                int num1 = Integer.parseInt(currentChar);
                ArrayList<Symbol> tempArray = new ArrayList<>();

                //each definition is a pair, so take them in pairs
                for(int z = 0; z < num1; z++) {

                    String name = inputFile.next();
                    int definition = inputFile.nextInt();
                    int totalDef = definition + offset;
                    Symbol s = new Symbol(name, totalDef, i);

                    boolean exists = false;

                    for (int j = 0; j < symbolTable.size(); j++) {

                        if (symbolTable.get(j).getName().equals(name)) {
                            errorsList.add("Error: The symbol " + name + " is defined multiple times. The first" +
                                    " value will be used");
                            exists = true;
                        }
                    }
                    if (!exists) {
                        symbolTable.add(s);
                        tempArray.add(s);
                    }
                }

                int num2 = inputFile.nextInt();

                //each use is single, so skip them one by one
                while(num2 --> 0){
                    inputFile.next();
                }

                //references are skipped in pairs
                currentChar = inputFile.next();
                int num3 = Integer.parseInt(currentChar);


                for(int j = 0; j < tempArray.size(); j++){

                    if((tempArray.get(j).getDefinition() - offset) > num3){

                        String name = tempArray.get(j).getName();
                        symbolTable.get(j).setDefinition(offset);
                        errorsList.add("Error: In module " + i + " the def of " + name + " exceeds the module size; zero (relative) used.");
                    }
                }

                offset += num3;
                for(int j = 0; j < num3; j++){
                    inputFile.next();
                    inputFile.next();
                }

            }



        }catch(FileNotFoundException e){
            System.err.println(e);
        }
    }

    private static void pass2(){

        try {

            Scanner inputFile = new Scanner(file);
            String currentChar = null;
            int offset = 0;
            int lineNumber = 0;
            int temp = numModules;
            //System.out.println(temp);
            //Skip the module number again
            inputFile.next();

            for(int i = 0; i < temp; i++){

                currentChar = inputFile.next();
                int num1 = Integer.parseInt(currentChar);

                //we have dealt with definitions, so skip these in pairs
                while(num1 --> 0) {

                    inputFile.next();
                    inputFile.next();
                }

                int num2 = inputFile.nextInt();
                ArrayList<Symbol> uses = new ArrayList<Symbol>();

                //each use is single, they will be placed into a temporary arrayList
                for(int h = 0; h < num2; h++){

                    String symbol = inputFile.next();
                    boolean found = false;

                    for(int j = 0; j < symbolTable.size(); j++){

                        if(symbolTable.get(j).getName().equals(symbol)){
                            uses.add(symbolTable.get(j));
                            found = true;
                        }

                    }

                    if(!found){
                        Symbol noSym = new Symbol(symbol, 0, -1);
                        uses.add(noSym);
                        noSym.setUse(true);
                    }


                }

                currentChar = inputFile.next();
                int instructionCount = Integer.parseInt(currentChar);
                for(int k = 0; k < instructionCount; k++){

                    String instruction = inputFile.next();
                    int address = inputFile.nextInt();

                    if(instruction.equals("I"))
                    {
                        OutputFormat out = new OutputFormat(address, lineNumber);
                        output.add(out);
                        lineNumber++;
                    }

                    else if(instruction.equals("A")){

                        OutputFormat out = new OutputFormat(address, lineNumber);
                        int last3 = address % 1000;
                        int firstDigit = address / 1000;
                        if(last3 >= MACHINE_SIZE){

                            int newAddress = firstDigit * 1000;
                            out.setAddress(newAddress);
                            out.setErrorMessage("Error: Absolute address exceeds machine size; zero used");
                            output.add(out);
                            lineNumber++;
                        }
                        else{

                            output.add(out);
                            lineNumber++;
                        }


                    }

                    else if(instruction.equals("R")){

                        OutputFormat out = new OutputFormat(address, lineNumber);
                        int firstDigit = address / 1000;
                        int last3 = address % 1000;
                        //int newAddress = address + offset;

                        //int last3 = newAddress % 1000;
                        if(last3 >= instructionCount){

                            int newAddress = firstDigit * 1000;
                            out.setAddress(newAddress);
                            out.setErrorMessage("Error: Relative address exceeds module size; zero used");
                            out.setLineNumber(lineNumber);
                            output.add(out);
                            lineNumber++;
                        }
                        else{
                            int newAddress = address + offset;
                            out.setAddress(newAddress);
                            out.setLineNumber(lineNumber);
                            output.add(out);
                            lineNumber++;
                        }




                    }

                    else if(instruction.equals("E")){

                        int reference = address % 1000;
                        int base = address / 1000;
                        base *= 1000;

                        OutputFormat out = new OutputFormat();

                        int index = uses.size();
                        if(reference >= index){
                            out.setErrorMessage("Error: External address exceeds length of use list; treated as immediate");
                            out.setAddress(address);
                            out.setLineNumber(lineNumber);
                            output.add(out);
                            lineNumber++;
                        }
                        else{
                            int ret = uses.get(reference).getDefinition();
                            if(uses.get(reference).getModule() == -1){
                                out.setErrorMessage("Error: " + uses.get(reference).getName() + " is not defined; zero used");
                            }
                            ret += base;
                            out.setAddress(ret);
                            out.setLineNumber(lineNumber);
                            uses.get(reference).setUse(true);
                            output.add(out);
                            lineNumber++;
                        }



                    }

                    else{
                        System.out.println("A provided operation in module " + i + " is not supported.");
                    }

                }

                offset += instructionCount;

            }

            for(int a = 0; a < symbolTable.size(); a++){

                //System.out.println(uses.get(j).getName());
                if(symbolTable.get(a).hasBeenUsed() == false){
                    errorsList.add("Warning: " + symbolTable.get(a).getName() + " was defined in module " + symbolTable.get(a).getModule() + " but never used");
                }
            }


        }catch(FileNotFoundException e){
            System.err.println(e);
        }

    }


    private static void printer(){

        System.out.println("Symbol Table");
        for(int i = 0; i < symbolTable.size(); i++){
            System.out.println(symbolTable.get(i).getName() + " = " + symbolTable.get(i).getDefinition());
        }

        String s = "";

        System.out.println("\nMemory Map");

        for(int i = 0; i < output.size(); i++){

            OutputFormat out = output.get(i);
            s += out.getLineNumber() + ":  ";
            s += out.getAddress() + " ";

            if(out.getErrorMessage() != null) {
                s += out.getErrorMessage();
            }


            s += "\n";
        }

        s += "\n";

        for(int i = 0; i < errorsList.size(); i++){
            s += errorsList.get(i) + "\n";
        }

        System.out.println(s);
    }

}
