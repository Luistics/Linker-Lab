package oslinker;

public class OutputFormat {

    private int address;
    private String errorMessage;
    private int lineNumber;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public OutputFormat(int a, String err, int lineNum){

        this.address = a;
        this.errorMessage = err;
        this.lineNumber = lineNum;

    }

    public OutputFormat(int a, int lineNumber){

        this.address = a;
        this.lineNumber = lineNumber;
    }

    public OutputFormat(){
    }

}
