package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        InputDevice input=new InputDevice(System.in);
        FileOutputStream fileout= null;
        try {
            fileout = new FileOutputStream("Untitled1/src/main/java/org.example/Output.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        OutputDevice output=new OutputDevice(fileout);
        Application app=new Application(input,output);
        app.run();
        System.out.println();


    }

}