package org.example;
import java.io.InputStream;
import java.util.Scanner;

public class InputDevice{
    private InputStream inputStream;
    private Scanner scanner;
    public InputDevice(InputStream inputStream){
        this.inputStream = inputStream;
        scanner = new Scanner(inputStream);
    }
    public String getString()
    {
        String info = scanner.nextLine();
        return info;
    }
    public Integer getInteger()
    {
        Integer info = scanner.nextInt();
        return info;
    }
    public Boolean getBoolean()
    {
        Boolean info = scanner.nextBoolean();
        return info;
    }
}
