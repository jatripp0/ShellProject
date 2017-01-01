package edu.frostburg.cosc460.TrippJohnathan;

import java.util.ArrayList;

/**
 * A simple class to handle operations related to commands for the shell interface.
 * Currently, this class contains only one method to interpret an input string and
 * generate a command, specified to be in the format of an ArrayList.
 * @author Johnathan Tripp (╯°□°）╯︵ ┻━┻
 */
public class Command {
    
    /**
     * Generates an ArrayList of Strings by splitting the parameter String. This
     * ArrayList is the recognized format for commands in the shell interface.
     * @param s a String containing a shell command from the user's input
     * @return an ArrayList consisting of the arguments of the user's command,
     * split at whitespaces, to be passed to the shell interface program.
     */
    public static ArrayList<String> interpret(String s){
        ArrayList<String> arrl = new ArrayList<>();
        String[] sarr = s.split("\\s+");
        for(String s1 : sarr){
            arrl.add(s1);
        }
        return arrl;
    }
}
