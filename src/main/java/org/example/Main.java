package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        InputDevice input=new InputDevice();
        OutputDevice output=new OutputDevice();
        Application app=new Application(input,output);
        app.run();
        System.out.println();


    }

}