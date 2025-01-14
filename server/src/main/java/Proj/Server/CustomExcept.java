package Proj.Server;
public class CustomExcept extends Exception {
    public CustomExcept() {
        super("Unknown exception occured. Please try again. If the issue repeats please report it so it gets fixed.");
    }
    public CustomExcept(String message)
    {
        super(message);
    }
    public static CustomExcept forUnsupportedStackSize(Integer maxCount,String type) {
        return new CustomExcept("Tried to add a stacksize of "+maxCount+" to "+type);
    }
}
