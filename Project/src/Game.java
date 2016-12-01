
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Game implements Runnable{
    Socket socket;

    public static ArrayList<String> gameTokenList = new ArrayList<String>();     //ArrayList to store all of the game keys
    public static HashMap<String,ArrayList<Player>> gameKeyMap = new HashMap<String,ArrayList<Player>>(); //Map to hold a list of
    // players for each game

    public Game(Socket socket){
        this.socket = socket;
    }


    public static void main(String[] args) throws IOException{

        ServerSocket serverSocket = new ServerSocket(50000);
        System.out.println("Waiting for connection");
        while(true) {
            Socket socket = serverSocket.accept();
            Game game = new Game(socket);
            new Thread(game).start();
        }

    }




    public void run() {
        System.out.println("Connection received from " + socket.getLocalSocketAddress());

        try {

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            Scanner scan = new Scanner(socket.getInputStream());



            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                System.out.println("Recived from client: " + input);
                String output = parse(input);
                System.out.println("Sent to client: " + output);
                printWriter.printf("%s\n", output);
                printWriter.flush();
            }


            printWriter.close();
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parse(String input){
        String output = "";

        if(input.contains("CREATENEWUSER")){
            output = newUser(input);
        }else if(input.contains("LOGIN")){
            output = userLogin(input);
        }else if(input.contains("JOINGAGAME")){
            output = joinGame(input);
        }else if(input.contains("ALLPARTICIPANTSHAVEJOINED")){
            output = launchGame(input);
        }else if(input.contains("STARTNEWGAME")){
            output = newGame(input);
        }else if(input.contains("PLAYERSUGGESTION")){
            output = suggestions(input);
        }else if(input.contains("PLAYERCHOICE")){
            output = playerChoice(input);
        }

        return output;
    }

    public String newUser(String input){

        String[] register = input.split("--");
        String output = "RESPONSE--CREATENEWUSER--";





        if(register.length < 2){
            output += "INVALIDMESSAGEFORMATE";
            return output;
        }else if(register.length == 2){
            output += "INVALIDPASSWORD";
            return output;
        }


        if(register[1].length() < 10 && register[1].length() > 1){
            String match = register[1];
            if((!isAlphanumericUserName(match))){
                output += "INVALIDUSERNAME";
                return output;
            }

        }else{
            output += "INVALIDUSERNAME";
            return output;
        }



        if(register[2].length() < 10 && register[2].length() > 1){
            String match = register[2];
            if((!isAlphanumericPassword(match))){
                output += "INVALIDPASSWORD";
                return output;
            }

        }else{
            output += "INVALIDPASSWORD";
            return output;
        }




        output += "End";
        return output;
    }

    public String userLogin(String input){

        return null;
    }

    public String newGame(String input){

        return null;
    }

    public String joinGame(String input){

        return null;
    }

    public String sendWord(String input){

        return null;
    }

    public String launchGame(String input){

        return null;
    }

    public String suggestions(String input){

        return null;
    }

    public String playerChoice(String input){
        return null;
    }

    
    public boolean isAlphanumericUserName(String check){

        for(int i = 0; i < check.length(); i++){
            if(!(Character.isLetterOrDigit(check.charAt(i)) || check.charAt(i) == '_')){
                return false;
            }
        }
        return true;
    }




    public boolean isAlphanumericPassword(String check){
        int upper = 0;
        int number = 0;



        for(int i = 0; i < check.length(); i++){

            if(Character.isUpperCase(check.charAt(i))){
                upper++;
            }

            if(Character.isDigit(check.charAt(i))){
                number++;
            }
            if(Character.isLetterOrDigit(check.charAt(i)) || check.charAt(i) == '#' || check.charAt(i) == '&' ||
                    check.charAt(i) == '$' || check.charAt(i) == '*'){
               continue;
            }
        }

        if(!(number > 0) || !(upper > 0)){
            return false;
        }

        return true;
    }




}
