package org.example;
import java.util.Scanner;

public class InputDevice{
    Scanner in = new Scanner(System.in);
    public String getString()
    {
        String info = in.nextLine();
        return info;
    }
    public Integer getInteger()
    {
        Integer info = in.nextInt();
        return info;
    }
}