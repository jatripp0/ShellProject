package edu.frostburg.cosc460.TrippJohnathan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

/**
 * A class that implements a Linux shell interface, to be executed via command line on the Linux terminal
 * @author Johnathan Tripp (╯°□°）╯︵ ┻━┻
 */
public class TrippJohnathanShell {

    /**
     * Main method for the shell program
     * @param args
     * @throws java.io.IOException thrown if there is an un-handled IO error in the shell program
     */
    public static void main(String[] args) throws java.io.IOException{
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        //command contains an ArrayList of the strings that will be passed to a ProcessBuilder for execution
        ArrayList<String> command = null;
        //An ArrayList to hold the list of executed commands in the shell
        ArrayList<ArrayList<String>> history = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder();
        
        //boolean variable to control execution state of the shell
        boolean running = true;
        while(running){
            //command = null;
            Process proc;
            //Print a shell prompt for each command waiting line
            System.out.print("jsh>");
            commandLine = console.readLine();
            if(commandLine.equals("")) //Re-enter the loop if the user enters an empty command
                continue;
            //Exit command for the shell
            else if(commandLine.equalsIgnoreCase("exit")){
                running = false;
                continue;
            }
            /*
            This case handles the cd command. If entered by the user,
            the current working directory of the shell will be changed
            to the directory entered by the user in the form "/dir1/dir2/..."
            If there is no directory path entered, the cd command alone will
            change the directory to the user's home directory.
            */
            else if(commandLine.trim().startsWith("cd")){
                String[] arr = commandLine.trim().split("\\s+");
                switch(arr.length){
                    //if "cd" is the only argument, change to the home directory
                    case 1:
                        File home = new File(System.getProperty("user.home"));
                        pb.directory(home.getAbsoluteFile());
                        System.setProperty("user.dir", home.getAbsolutePath());
                        continue;
                    //if there is a path argument, check to ensure it is valid and change the directory
                    case 2:
                        if(arr[1].equals("..")){ //If the user enters a command to navigate to the parent directory
                            File parentDir = new File(System.getProperty("user.dir")).getParentFile();
                            pb.directory(parentDir.getAbsoluteFile());
                            System.setProperty("user.dir", parentDir.getAbsolutePath());
                            continue;
                        } else { //If the user enters a command to change to a directory other than the parent or home
                            if(isValidPath(arr[1])){
                                File newDir = new File(System.getProperty("user.dir") + arr[1]);
                                //Prints an error message if the directory given does not exist.
                                if(!newDir.exists()){
                                    System.out.println("No such file or directory. " + 
                                        newDir.getAbsolutePath() + 
                                        "\ndoes not exist. Please try again.");
                                    continue;
                                }
                                pb.directory(newDir.getAbsoluteFile());
                                System.setProperty("user.dir", newDir.getAbsolutePath());
                                continue;
                            } else { //The directory path is invalid and an error message is printed
                                System.out.println("Invalid directory path. Must be formatted as /dir");
                                continue;
                            }
                        }
                    default: //Invalid command, either by formatting or mismatched arguments
                        System.out.println("Invalid command. Please try again.");
                        continue;
                }
                
            }
            /*
            Cases for handling history-related commands.
            If the user enters the command "history", the shell
            will return the indexed list of previously entered
            commands. If there are no entries, an appropriate
            message will be displayed by the printHist method.
            */
            else if(commandLine.equals("history")){System.out.print(printHist(history));}
            else if(commandLine.startsWith("!")){
                String next = commandLine.substring(1);
                //If the !! command is entered, gets the last command used
                if(next.trim().equals("!"))
                    command = history.get(history.size()-1);
                else{
                    try{
                        //attempt to get the index of the history entry
                        int i = Integer.parseInt(next);
                        if(i >= 0 && i < history.size()){
                            command = history.get(i);
                        } else {
                            System.out.println("Invalid entry. Index out of range.");
                            continue;
                        }
                    }
                    //Prints an error message if the entry is in an incorrect format.
                    catch(NumberFormatException e){
                        System.out.println("Invalid entry. Incorrect command format, please use the format !(index).");
                        continue;
                    }
                }
            }
            else { //Convert the user's input to a list of Strings that can be passed as a process command
                command = Command.interpret(commandLine);
            }
            //Add the command to the list of command entries for the user's shell session
            history.add(command);
            pb.command(command).inheritIO(); //Allows the project class to inherit the process' IO streams
            proc = pb.start();

            System.out.print(getProcessOut(proc)); //Prints the output of the process
        }
    }

    /**
     * Gets the output of a process, passed by parameter, and returns it as a 
     * String. The process the output belongs to must have its IO inherited by
     * this class in order for the method to function.
     * @param proc a process used to obtain output from
     * @return a String containing the output of the process
     * @throws IOException if there are issues with IO concerning the process
     */
    private static String getProcessOut(Process proc) throws IOException {
        //Obtain the process' output as an input stream
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        //Write each line of output to the String
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }
    
    /**
     * Constructs a string containing indexed history entries for shell commands.
     * @param cmds an ArrayList of commands, which are ArrayLists of Strings
     * @return a formatted String for printing use
     */
    private static String printHist(ArrayList<ArrayList<String>> cmds){
        if(cmds.isEmpty())
            return "No entries found.";
        String output = "";
        //index counter to avoid issues with retrieving duplicate entries from the ArrayList
        int index = 0;
        //Iterates through each command in the list
        for(ArrayList<String> ar : cmds){
            output += "\n" + index + " ";
            //Iterates through each paramter of each command
            for(String s : ar){
                output += s + " ";
            }
            index++;
        }
        return output;
    }
    
    /**
     * Determines if a given directory path string is valid for use with the 
     * "cd" command. Currently only set to ensure the path begins with a 
     * forward slash.
     * @param path the directory path to be evaluated
     * @return true if the path is valid for the conditions specified
     */
    private static boolean isValidPath(String path){
        return path.startsWith("/");
    }
}
