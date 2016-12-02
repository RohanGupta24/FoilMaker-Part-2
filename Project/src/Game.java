
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Game implements Runnable{
    Socket socket;

    public static ArrayList<String> gameTokenList = new ArrayList<String>();     //ArrayList to store all of the game keys
    public static ArrayList<String> userTokenList = new ArrayList<String>();    //ArrayList to store all the user tokens
    public static HashMap<String, String> userMap = new HashMap<String, String>();  //Map that holds username as the key and userToken as the value so one can look up a player through userToken
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

    public String getResponse(String input){
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

<<<<<<< HEAD
=======
        String[] register = input.split("--");
        String output = "RESPONSE--CREATENEWUSER--";





        if(register.length < 2){
            output += "INVALIDMESSAGEFORMAT--";
            return output;
        }else if(register.length == 2){
            output += "INVALIDPASSWORD--";
            return output;
        }


        if(register[1].length() < 10 && register[1].length() > 1){
            String match = register[1];
            if((!isAlphanumericUserName(match))){
                output += "INVALIDUSERNAME--";
                return output;
            }

        }else{
            output += "INVALIDUSERNAME--";
            return output;
        }



        if(register[2].length() < 10 && register[2].length() > 1){
            String match = register[2];
            if((!isAlphanumericPassword(match))){
                output += "INVALIDPASSWORD--";
                return output;
            }

        }else{
            output += "INVALIDPASSWORD--";
            return output;
        }




        output += "End";
        return output;
>>>>>>> origin/master
    }

    //INCOMPLETE: Not completely done -- need to work on the file input
    public String userLogin(String input){
        String status = "";
        String output = "RESPONSE--LOGIN--";
        String[] loginData = input.split("--");
        if(loginData.length != 3) {
            status = "INVALIDMESSAGEFORMAT--";
        }
        else {
            String username = loginData[1];
            String password = loginData[2];
            BufferedReader in = new BufferedReader(new FileReader(new File("UserDatabase")));
            String line;
            int counter = 0;
            while((line = in.readLine()) != null) { //NOT FUNCTIONAL: if login is successful, generate a unique user token of length 10
                if(line.contains(username)) {
                    counter++;
                    if(line.contains(password)) {
                        String userToken = generateUserToken();
                        status = "SUCCESS--";
                        output += status;
                        Player player = new Player(username, password);
                        return output;
                    }
                    else {
                        continue;
                    }
                }
                else {
                    line = in.readLine();
                }
            }
            if(counter == 0) {
                status = "UNKNOWNUSER--";
                output += status;
                return output;
            }
            else if(counter == 1) {
                status = "INVALIDUSERPASSWORD--";
                output += status;
                return output;
            }
            else if(userMap.containsKey(username)) { //How should we check whether the player is already logged in or not? ANSWER: Use HashMap that has a list of players for every gameToken?
                status = "USERALREADYLOGGEDIN--";
                output += status;
                return output;
            }


        }
    }

    public String newGame(String input){
        String status = "";
        String output = "RESPONSE--STARTNEWGAME--";
        String[] newGameData = input.split("--");
        String userToken = newGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        if(checkUserTokenValidity == true) {
            if(playing == false) { //INCOMPLETE: check if user is already playing (boolean value, true or false)
                status = "SUCCESS--";
                output += status;
                return output;
            }
            else {
                status = "FAILURE--";
                output += status;
                return output;
            }
        }
        else {
            status = "USERNOTLOGGEDIN--";
            output += status;
            return output;
        }

    }

    public String joinGame(String input){
        String status = "";
        String output = "RESPONSE--JOINGAME--";
        String[] joinGameData = input.split("--");
        String userToken = joinGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        String gameToken = joinGameData[2];
        boolean checkGameTokenValidity = isGameTokenValid(gameToken);
        if(checkUserTokenValidity == false) {
            status = "USERNOTLOGGEDIN--" + gameToken;
            output += status;
            return output;
        }
        else if(checkGameTokenValidity == false) {
            status = "GAMEKEYNOTFOUND--" + gameToken;
            output += status;
            return output;
        }
        else if(playing == true) {
            status = "FAILURE--" + gameToken;
            output += status;
            return output;
        }
        else { //INCOMPLETE: Have some kind of map that connects username to userToken?
            String addParticipantMessage = "NEWPARTICIPANT--";

        }


    }

    public String launchGame(String input){
        String status = "";
        String output = "RESPONSE--ALLPARTICIPANTSHAVEJOINED--";
        String[] launchGameData = input.split("--");
        String userToken = launchGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        String gameToken = launchGameData[2];
        boolean checkGameTokenValidity = isGameTokenValid(gameToken);
        if(checkUserTokenValidity == false) {
            status = "USERNOTLOGGEDIN--";
            output += status;
            return output;
        }
        else if(checkGameTokenValidity == false) {
            status = "INVALIDGAMETOKEN--";
            output += status;
            return output;
        }
        else if(playing == true) {
            status = "USERNOTGAMELEADER--";
            output += status;
            return output;
        }
        else {
            //Finish this off; if(SUCCESS)...
        }
    }
    public String sendWord(String input){

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

    public String generateUserToken() {
        String userToken = RandomStringUtils.randomAlphanumeric(10);
        return userToken;
    }

    public boolean isUserTokenValid(String userToken) {
        for(int i = 0; i < userTokenList.size(); i++) {
            if(userToken.equals(userTokenList.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameTokenValid(String gameToken) {
        for(int i = 0; i < gameTokenList.size(); i++) {
            if(gameToken.equals(gameTokenList.get(i))) {
                return true;
            }
        }
        return false;

    }




}
