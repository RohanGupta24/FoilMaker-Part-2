
import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.UUID;

public class Game implements Runnable {
    Socket socket;

    public static ArrayList<String> gameTokenList = new ArrayList<String>();     //ArrayList to store all of the game keys
    public static ArrayList<String> userTokenList = new ArrayList<String>();    //ArrayList to store all the user tokens
    public static ArrayList<Player> playerList = new ArrayList<Player>();
    public static ArrayList<String> questionList = new ArrayList<String>();
    public static ArrayList<String> answerList = new ArrayList<String>();
    //public static HashMap<String, String> userMap = new HashMap<String, String>();  //Map that holds username as the key and userToken as the value so one can look up a player through userToken
    //public static HashMap<String, ArrayList<Player>> gameKeyMap = new HashMap<String, ArrayList<Player>>(); //Map to hold a list of
    // players for each game with userToken as the reference
    public static HashMap<String, Player> userMap = new HashMap<String, Player>(); //User token is the key and the value is a player object
    public static HashMap<String, ArrayList<Player>> gameMap = new HashMap<>(); //Game map: gameToken is key and
    // arraylist of players in game is value

    PrintWriter printWriter;

    public Game(Socket socket) {
        this.socket = socket;
    }


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(50000);
        System.out.println("Waiting for connection");
        while (true) {
            Socket socket = serverSocket.accept();
            Game game = new Game(socket);
            new Thread(game).start();
        }

    }


    public void run() {
        System.out.println("Connection received from " + socket.getPort());




        try {

            printWriter = new PrintWriter(socket.getOutputStream());
            Scanner scan = new Scanner(socket.getInputStream());


            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                System.out.println("Recived from client: " + input);
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

    //INCOMPLETE: Not completely done -- need to work on the file input
    public String userLogin(String input) throws IOException{

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

                        for(Player player: playerList) {
                            if(player.getUsername().equals(username)) {
                                output += "USERALREADYLOGGEDIN";
                                return output;
                            }
                        }

                        String userToken = generateUserToken();
                        output += "SUCCESS--"  + userToken;

                        Player player = new Player(username, password);
                        playerList.add(player);
                        userTokenList.add(userToken);
                        userMap.put(userToken,player);
                        player.setPort(socket.getPort());
                        userMap.get(userToken).setLoggedInAndPlaying(false);
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

    public String newGame(String input) {
        String status = "";
        String output = "RESPONSE--STARTNEWGAME--";
        String[] newGameData = input.split("--");
        String userToken = newGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        if (checkUserTokenValidity == true) {
            Player player = userMap.get(userToken);
            if (player.getLoggedInAndPlaying() == false) {
                String gameToken = generateGameToken();
                status = "SUCCESS--" + gameToken;
                gameTokenList.add(gameToken);
                output += status;
                //userMap.get(userToken).setLoggedInAndPlaying(true);
                ArrayList<Player> newGame = new ArrayList();
                newGame.add(player);
                gameMap.put(gameToken, newGame);
                player.setMessage("Hello1");

                Thread check = new Thread(){
                    public void run(){

                        System.out.println("Test: " + player.getMessage());

                        boolean keepGoing = true;

                        while(keepGoing) {

                            if(player.getMessage().contains("ALL")){

                                ArrayList e = gameMap.get(gameToken);
                                System.out.println(gameMap.get(gameToken).toString());

                                for(int i = 0; i < e.size(); i++){

                                    System.out.println(e.get(i).toString());

                                }
                                return;
                            }

                            if(player.getMessage().contains("Start")){
                                printWriter.printf("%s\n",player.getMessage().substring(5));
                                printWriter.flush();
                                player.setMessage("Nope");
                            }

                            try{
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }


                    }

                };

                check.start();








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

    public String joinGame(String input) throws IOException {

        String output = "RESPONSE--JOINGAME--";
        String[] joinGameData = input.split("--");
        String userToken = joinGameData[1];
        String gameToken = joinGameData[2];
        Player currentPlayer = userMap.get(userToken);

        if (!(isUserTokenValid(userToken))) {
            output += "USERNOTLOGGEDIN--";
            return output;
        } else if (!(isGameTokenValid(gameToken))) {
            output += "GAMEKEYNOTFOUND--";
            return output;
        } else if (userMap.get(userToken).getLoggedInAndPlaying()) {

            for(int i = 0 ; i < playerList.size(); i++){
                System.out.println(playerList.get(i).getLoggedInAndPlaying());
            }
            output += "FAILURE--";
            return output;
        } else {
            ArrayList gamePlayers = gameMap.get(gameToken);

            Player leader = (Player)gamePlayers.get(0);
            leader.setMessage("StartNEWPARTICIPANT--" + currentPlayer.getUsername() + "--" + currentPlayer
                    .getCumulativeScore());
            gamePlayers.add(currentPlayer);
            gameMap.replace(gameToken, gameMap.get(gameToken), gamePlayers);
            output += "skip";



        }

        return output;
    }

    public String launchGame(String input) {


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
        }


        /*
        Player player = userMap.get(userToken);
        if (checkUserTokenValidity == false) {
            status = "USERNOTLOGGEDIN--";
            output += status;
            return output;
        } else if (checkGameTokenValidity == false) {
            status = "INVALIDGAMETOKEN--";
            output += status;
            return output;
        } else if (player.getLoggedInAndPlaying() == true) {
            status = "USERNOTGAMELEADER--";
            output += status;
            return output;
        } else {
            //Finish this off; if(SUCCESS)...
            //START GAME - how do we do this?
        }
        */
        return output;
    }

    public String sendWord() throws IOException {
        String output = "NEWGAMEWORD--";
        BufferedReader in = new BufferedReader(new FileReader(new File("WordleDeck")));
        String line;
        while ((line = in.readLine()) != null) {
            String[] questionAndAnswer = line.split(":");
            String question = questionAndAnswer[0];
            questionList.add(question);
            String answer = questionAndAnswer[1];
            answerList.add(answer);
            output += question + "--" + answer;
        }
        return output;
    }


    public String suggestions(String input) {
        String output = "RESPONSE--PLAYERSUGGESTION--";
        String[] playerSuggestionData = input.split("--");
        String userToken = playerSuggestionData[1];
        String gameToken = playerSuggestionData[2];
        String suggestion = playerSuggestionData[3];
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
            for(Player p: playerList) {
                if(p.getUserToken().equals(userToken) && p.getGameToken().equals(gameToken)) {
                    p.setSuggestion(suggestion);
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

    public String sendRoundOptions() {
        String output = "ROUNDOPTIONS--";
        for(Player p: playerList) {
            output += p.getSuggestion() + "--";
        }
        output += answerList.get(0);
        return output;
    }

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
            for(Player p: playerList) {
                if(p.getUserToken().equals(userToken) && p.getGameToken().equals(gameToken)) {
                    p.setChoice(choice);
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


    public boolean isAlphanumericUserName(String check) {

        for (int i = 0; i < check.length(); i++) {
            if (!(Character.isLetterOrDigit(check.charAt(i)) || check.charAt(i) == '_')) {
                return false;
            }
        }
        return true;
    }


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

    public String generateGameToken() {
        String possible = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String output = "";

        for(int i = 0; i < 3; i++){
            output += possible.charAt((int)(Math.random() * 52));
        }


        return output;
    }

    public boolean isUserTokenValid(String userToken) {
        for (int i = 0; i < userTokenList.size(); i++) {
            if (userTokenList.get(i).equals(userToken)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameTokenValid(String gameToken) {
        for (int i = 0; i < gameTokenList.size(); i++) {
            if (gameToken.equals(gameTokenList.get(i))) {
                return true;
            }
        }
        return false;

    }


    public String logout(int port){



        for(int i = 0; i < playerList.size(); i ++){
            if(playerList.get(i).getPort() == port){
                playerList.get(i).setLoggedInAndPlaying(false);
                playerList.get(i).setCumulativeScore(0);
                playerList.get(i).setGameToken(null);
                playerList.get(i).setPort(0);
                playerList.set(i, null);
                playerList.remove(i);
                try {
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }

        return "Logged out" + port + "skip";
    }


    public void sendMessage(Player player, String message) throws IOException{

        System.out.println("in1");
        int port = player.getPort();
        System.out.println("In2");
        Socket socket = new Socket("localhost", port);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        System.out.println("Sent to client: " + message);
        printWriter.printf("%s\n", message);
        printWriter.flush();



    }
}
