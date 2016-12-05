
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Game implements Runnable {
    Socket socket;

    public static ArrayList<String> gameTokenList = new ArrayList<String>();     //ArrayList to store all of the game keys
    public static ArrayList<String> userTokenList = new ArrayList<String>();    //ArrayList to store all the user tokens
    public static ArrayList<Player> playerList = new ArrayList<Player>();
    public static ArrayList<ArrayList<Player>> players = new ArrayList<ArrayList<Player>>();
    public static ArrayList<String> questionList = new ArrayList<String>();
    public static ArrayList<String> answerList = new ArrayList<String>();

    public static ConcurrentHashMap<String, Player> userMap = new ConcurrentHashMap<String, Player>();
    public static ConcurrentHashMap<String,ArrayList<Player>> gameMap = new ConcurrentHashMap<String, ArrayList<Player>>();


    public boolean wait = true;



    public Game(Socket socket) {
        this.socket = socket;
    }

    /**
     *Recieves connection from client
     *and starts new thread for the client
     *
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(50000);
        System.out.println("Waiting for connection");
        while (true) {
            Socket socket = serverSocket.accept();
            Game game = new Game(socket);
            new Thread(game).start();
        }

    }

    /**
     * Receives input from client and sends to
     * getResponse method. Then sends getResponse
     * output to client.
     *
     */
    public void run() {
        System.out.println("Connection received from " + socket.getPort());


        try {

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            Scanner scan = new Scanner(socket.getInputStream());


            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                System.out.println("Received from client: " + input);
                String output = getResponse(input);


                if(!(output.contains("skip"))) {
                    System.out.println("Sent to client: " + output);
                    printWriter.printf("%s\n", output);
                    printWriter.flush();
                }

            }


            printWriter.close();
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Receives client message and sends to
     * other methods to do logic, then returns
     * the server to client message.
     *
     * @param input
     * @return String to send to client
     */
    public String getResponse(String input) throws IOException{
        String output = "FAIL";

        if (input.contains("CREATENEWUSER")) {
            output = newUser(input);
        } else if (input.contains("LOGIN")) {
            output = userLogin(input);
        } else if (input.contains("JOINGAME")) {
            output = joinGame(input);
        } else if (input.contains("ALLPARTICIPANTSHAVEJOINED")) {
            output = launchGame(input);
        } else if (input.contains("STARTNEWGAME")) {
            output = newGame(input);
        } else if (input.contains("PLAYERSUGGESTION")) {
            output = suggestions(input);
        } else if (input.contains("PLAYERCHOICE")) {
            output = choices(input);
        } else if (input.contains("LOGOUT")){
            output = logout(socket.getPort());
            System.out.println("Logout" + output);
        }

        return output;
    }


    /**
     * Receives NEWUSER message from client and
     * determins if the new user is valid. If it
     * is valid it writes player information to UserDatabase
     * and returns correct response message.
     *
     * @param input
     * @return
     */
    public String newUser(String input) throws IOException{
        String[] register = input.split("--");
        String output = "RESPONSE--CREATENEWUSER--";
        if (register.length < 2) {
            output += "INVALIDMESSAGEFORMAT--";
            return output;
        } else if (register.length == 2) {
            output += "INVALIDPASSWORD--";
            return output;
        }


        if (register[1].length() < 10 && register[1].length() > 1) {
            String match = register[1];
            if ((!isAlphanumericUserName(match))) {
                output += "INVALIDUSERNAME--";
                return output;
            }

        } else {
            output += "INVALIDUSERNAME--";
            return output;
        }


        if (register[2].length() < 10 && register[2].length() > 1) {
            String match = register[2];
            if ((!isAlphanumericPassword(match))) {
                output += "INVALIDPASSWORD--";
                return output;
            }

        } else {
            output += "INVALIDPASSWORD--";
            return output;
        }


        String write = "\n" + register[1] + ":" + register[2] + ":0:0:0";
        BufferedWriter in = new BufferedWriter(new FileWriter(new File("UserDatabase"), true));
        in.write(write);
        in.close();


        output += "SUCCESS";
        return output;

    }


    /**
     * Checks to see if the user provided is in the UserDatabase
     * and also checks if the user is already logged in. Upon
     * success a Player object is created and added to player list.
     *
     *
     * @param input
     * @return returns login message for client
     */
    public synchronized String userLogin(String input) throws IOException{

        for(int i = 0; i < playerList.size(); i++) {
            System.out.println(playerList.get(i).getUsername());
        }

        System.out.printf("ANDREWS PRIONTF 3: %s\n", playerList.stream().map(Player::getUsername).collect(Collectors.toList()));


        String output = "RESPONSE--LOGIN--";
        String[] loginData = input.split("--");
        if (loginData.length != 3) {
            output += "INVALIDMESSAGEFORMAT--";
        } else {
            String username = loginData[1];
            String password = loginData[2];

            BufferedReader in = new BufferedReader(new FileReader(new File("UserDatabase")));
            String line = "";
            int counter = 0;

            while ((line = in.readLine()) != null) {

                String[] databaseLine = line.split(":");


                if(databaseLine[0].equals(username)){
                    counter++;

                    if(databaseLine[1].equals(password)){

                        synchronized (playerList) {
                            for (Player player : playerList) {
                                if (player.getUsername().equals(username)) {
                                    output += "USERALREADYLOGGEDIN";
                                    return output;
                                }
                            }
                        }

                        String userToken = generateUserToken();
                        output += "SUCCESS--"  + userToken;

                        System.out.printf("ANDREWS PRIONTF 4 BEFORE PLY: %s\n", playerList.stream().map(Player::getUsername).collect(Collectors.toList()));

                        Player player = new Player(username, password);
                        /*playerList.add(new Player(username, password));
                        players.add(playerList);
                        for(List<Player>playerList: players) {
                            for (Player p : playerList) {
                                System.out.print(p.getUsername() + " ");
                            }
                            System.out.println();
                        }*/
                        //Player player = new Player();
                        player.setUsername(username);
                        player.setUserToken(userToken);
                        player.setLoggedInAndPlaying(false);
                        player.setPort(socket.getPort());

                        System.out.printf("ANDREWS PRIONTF 1: %s\n", playerList.stream().map(Player::getUsername).collect(Collectors.toList()));
                        playerList.add(player);
                        System.out.printf("ANDREWS PRIONTF 2: %s\n", playerList.stream().map(Player::getUsername).collect(Collectors.toList()));

                        userTokenList.add(userToken);
                        userMap.put(userToken,player);

                        return output;
                    }
                }
            }

            if (counter == 0) {
                output += "UNKNOWNUSER--";
                return output;
            }


            if (counter >= 1) {
                output += "INVALIDUSERPASSWORD--";
                return output;
            }
        }

        return output;
    }

    /**
     * Receives NewGame request from client and
     * checks to see if user is logged in. Upon success
     * game token is created and a new game is added to
     * the gameMap.
     *
     *
     * @param input
     * @return NewGame message for client
     */
    public String newGame(String input) throws IOException{
        String status = "";
        String output = "RESPONSE--STARTNEWGAME--";
        String[] newGameData = input.split("--");
        String userToken = newGameData[1];

        System.out.printf("THIS IS A PLAYER TOKEN OF THE HOST: %s\n", userToken);

        boolean checkUserTokenValidity = isUserTokenValid(userToken);

        if (checkUserTokenValidity == true) {

            Player playerMe = userMap.get(userToken);

            if (playerMe.getLoggedInAndPlaying() == false) {

                String gameToken = generateGameToken();
                status = "SUCCESS--" + gameToken;
                gameTokenList.add(gameToken);
                output += status;
                playerMe.setGameToken(gameToken);
                ArrayList<Player> temp = new ArrayList<Player>();
                Player asdf = userMap.get(userToken);
                temp.add(asdf);
                gameMap.put(gameToken, temp);


                //gameMap.get(gamekey).put(new User());



                return output;
            } else {
                status = "FAILURE--";
                output += status;
                return output;
            }
        } else {
            status = "USERNOTLOGGEDIN--";
            output += status;
            return output;
        }

    }


    /**
     * Checks to see if Game Token provided is valid
     * add adds user to game is true
     *
     *
     *
     * @param input
     * @return Message for client
     */
    public String joinGame(String input) throws IOException {


        System.out.println("In Join Game");
        String output = "RESPONSE--JOINGAME--";
        String[] joinGameData = input.split("--");
        String userToken = joinGameData[1];
        String gameToken = joinGameData[2];
        Player currentPlayer = userMap.get(userToken);


        System.out.printf("THIS IS A PLAYER TOKEN OF THE REGULAR GUY: %s\n", userToken);




        if (!(isUserTokenValid(userToken))) {
            output += "USERNOTLOGGEDIN--";
            return output;
        } else if (!(isGameTokenValid(gameToken))) {
            output += "GAMEKEYNOTFOUND--";
            return output;
        } else if (userMap.get(userToken).getLoggedInAndPlaying()) {
            output += "FAILURE--";
            return output;
        } else {
            ArrayList<Player> gamePlayers = gameMap.get(gameToken);


            System.out.println(gameMap.toString());

            synchronized (playerList) {
                for (int i = 0; i < playerList.size(); i++) {
                    System.out.println(playerList.get(i).getUsername());
                }
            }

            output += "SUCCESS--" + gameToken;
        }



        return output;
    }


    /**
     * Checks to make sure that user is logged in
     * and that the game token is valid. Then sends
     * NEWWORD to client.
     *
     * @param input
     * @return New Word for client to display
     */
    public String launchGame(String input) throws IOException{


        String output = "RESPONSE--ALLPARTICIPANTSHAVEJOINED--";
        String[] launchGameData = input.split("--");
        String userToken = launchGameData[1];
        String gameToken = launchGameData[2];


        userMap.get(userToken).setMessage("ALLPARTICIPANTSHAVEJOINED");
        if(!(userTokenList.contains(userToken))){
            output += "USERNOTLOGGEDIN--";
            return output;
        }else if(!(gameTokenList.contains(gameToken))){
            output += "INVALIDGAMETOKEN--";
            return output;
        }else{
            synchronized (playerList) {
                Player player = playerList.get(0);
                output = sendWord(player);
            }
        }





        wait = true;
        return output;
    }


    /**
     * Reads in Questions and Answers and
     * sends to client
     *
     *
     * @param player
     * @return new word message for client
     */
    public String sendWord(Player player) throws IOException {
        String output = "NEWGAMEWORD--";
        BufferedReader in = new BufferedReader(new FileReader(new File("WordleDeck")));
        String line;
        int count = 0;

        while ((line = in.readLine()) != null) {
            if(count == player.getWordOn()){
                String[] temp = line.split(":");

                output += temp[0].substring(0,temp[0].length() - 1);
                output += "--";
                output += temp[1].substring(1,temp[1].length());
                return output;
            }else{
                count++;
            }
        }
        return output;
    }


    /**
     * Takes in client word suggestion and checks to
     * see if valid. If it is valid send suggestion to sendRoundOptions
     *
     *
     * @param input
     * @return error message
     */
    public String suggestions(String input) {
        String output = "RESPONSE--PLAYERSUGGESTION--";
        String[] playerSuggestionData = input.split("--");
        String userToken = playerSuggestionData[1];

        String gameToken = playerSuggestionData[2];
        String suggestion = playerSuggestionData[3];
        int userEquals = 0;
        int gameEquals = 0;

        if(!(userTokenList.contains(userToken))) {
            output += "USERNOTLOGGEDIN--";
            return output;
        } else if(!(gameTokenList.contains(gameToken))) {
            output += "INVALIDGAMETOKEN--";
            return output;
        } else if(playerSuggestionData.length != 4) {
            output += "INVALIDMESSAGEFORMAT--";
            return output;
        } else if(!(playerSuggestionData[0].equals("PLAYERSUGGESTION"))){
            output += "UNEXPECTEDMESSAGETYPE--";
            return output;
        }





        return output;

    }


    /**
     * Takes in all suggestions and send the
     * shuffled suggestions to client
     *
     *
     * @return Round Option message for client
     */
    public String sendRoundOptions() {
        String output = "ROUNDOPTIONS--";
        synchronized (playerList) {
            for (Player p : playerList) {
                output += p.getSuggestion() + "--";
            }
        }
        output += answerList.get(0);
        return output;
    }


    /**
     * Takes in user choices and does Game Logic.
     *
     * @param input
     * @return Send Result message to client
     */
    public String choices(String input) {
        String output = "RESPONSE--PLAYERCHOICE--";
        String[] playerChoiceData = input.split("--");
        String userToken = playerChoiceData[1];
        String gameToken = playerChoiceData[2];
        String choice = playerChoiceData[3];
        int userEquals = 0;
        int gameEquals = 0;
        for(String uToken: userTokenList) {
            if(userToken.equals(uToken)) {
                userEquals++;
                break;
            }
            else {
                continue;
            }
        }
        for(String gToken: gameTokenList) {
            if(gameToken.equals(gToken)) {
                gameEquals++;
                break;
            }
            else {
                continue;
            }
        }
        if(userEquals == 0) {
            output += "USERNOTLOGGEDIN--";
            return output;
        }
        else if(gameEquals == 0) {
            output += "INVALIDGAMETOKEN--";
            return output;
        }
        else if(userEquals == 1 && gameEquals == 1) {
            synchronized (playerList) {
                for (Player p : playerList) {
                    if (p.getUserToken().equals(userToken) && p.getGameToken().equals(gameToken)) {
                        p.setChoice(choice);
                    }
                }
            }
        }
        else if(gameToken.length() != 3 && userToken.length() != 10) {
            output += "INVALIDMESSAGEFORMAT--";
            return output;
        }
        else {
            output += "UNEXPECTEDMESSAGETYPE--";
            return output;
        }
        return output;
    }

    public String sendResults(ArrayList<Player> players, String correctChoice) {
        String messageX = "";
        String messageY = "";
        String suggestionX = "";
        String name = "";
        for(Player p: players) {
            suggestionX = p.getSuggestion();
            for(Player r: players) {
                int counter = 0;
                if(r.getChoice().equals(correctChoice)) {
                    messageX = "You got it right!";
                    p.setCumulativeScore(p.getCumulativeScore() + 10);
                }
                if(r.equals(suggestionX)) {
                    counter = 0;
                }
                if(suggestionX.equals(r.getChoice())) {
                    counter++;
                    if(counter != 0) {
                        p.setCumulativeScore(p.getCumulativeScore() + 5);
                        p.setFooled(p.getFooled() + 1);
                        p.setFooled_by(p.getFooled_by() + 1);

                        messageX += " You fooled " + r.getUsername();
                        messageY += "You were fooled by " + p.getUsername();

                    }

                }

            }
        }
        return messageX + " " + messageY;
    }






    /**
     *Checks to see if recieved string is
     * a possible Username that a client can have.
     *
     *
     * @param check
     * @return boolean is received  string is true or false
     */
    public boolean isAlphanumericUserName(String check) {

        for (int i = 0; i < check.length(); i++) {
            if (!(Character.isLetterOrDigit(check.charAt(i)) || check.charAt(i) == '_')) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks to see if the received string is
     * a possible Password that a user can use.
     *
     *
     * @param check
     * @return boolean is received string true or false
     */
    public boolean isAlphanumericPassword(String check) {
        int upper = 0;
        int number = 0;


        for (int i = 0; i < check.length(); i++) {

            if (Character.isUpperCase(check.charAt(i))) {
                upper++;
            }

            if (Character.isDigit(check.charAt(i))) {
                number++;
            }
            if (Character.isLetterOrDigit(check.charAt(i)) || check.charAt(i) == '#' || check.charAt(i) == '&' ||
                    check.charAt(i) == '$' || check.charAt(i) == '*') {
                continue;
            }
        }

        if (!(number > 0) || !(upper > 0)) {
            return false;
        }

        return true;
    }


    /**
     * Generates a random UserToken that is 10
     * characters long and is only alphanumeric
     *
     *
     * @return random UserToken
     */
    public String generateUserToken() {
        String userToken = UUID.randomUUID().toString();
        String output = "";
        int count = 0;

        for(int i = 0; count < 10; i++){
            if(Character.isLetterOrDigit(userToken.charAt(i))){
                output =  output + userToken.charAt(i);
                count++;
            }
        }


        return output;
    }


    /**
     * Generates a random 3 character game token
     *
     *
     * @return String of game Token
     */
    public String generateGameToken() {
        String possible = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = "";

        for(int i = 0; i < 3; i++){
            output += possible.charAt((int)(Math.random() * 52));
        }


        return output;
    }


    /**
     * Checks to see if the provided userToken exists
     * and is valid to play
     *
     * @param userToken
     * @return boolean if the UserToken is Valid
     */
    public boolean isUserTokenValid(String userToken) {
        for (int i = 0; i < userTokenList.size(); i++) {
            if (userTokenList.get(i).equals(userToken)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks to see if the provided GameToken exists
     * and the game is being played
     *
     * @param gameToken
     * @return boolean if GameToken is Valid
     */
    public boolean isGameTokenValid(String gameToken) {
        for (int i = 0; i < gameTokenList.size(); i++) {
            if (gameToken.equals(gameTokenList.get(i))) {
                return true;
            }
        }
        return false;

    }

    /**
     * Loops through all of the players and finds the player
     * object
     *
     *
     * @param port
     * @return Sends Logout message to client
     */
    public String logout(int port){


        synchronized (playerList) {
            for (int i = 0; i < playerList.size(); i++) {
                if (playerList.get(i).getPort() == port) {
                    playerList.get(i).setLoggedInAndPlaying(false);
                    playerList.get(i).setCumulativeScore(0);
                    playerList.get(i).setGameToken(null);
                    playerList.get(i).setPort(0);
                    playerList.set(i, null);
                    playerList.remove(i);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return "Logged out" + port + "skip";
    }


}
