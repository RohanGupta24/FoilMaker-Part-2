
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
    //public static HashMap<String, String> userMap = new HashMap<String, String>();  //Map that holds username as the key and userToken as the value so one can look up a player through userToken
    //public static HashMap<String, ArrayList<Player>> gameKeyMap = new HashMap<String, ArrayList<Player>>(); //Map to hold a list of
    // players for each game with userToken as the reference
    public static HashMap<String, Player> userMap = new HashMap<String, Player>(); //User token is the key and the value is a player object


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
        System.out.println("Connection received from " + socket.getLocalSocketAddress());

        try {

            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            Scanner scan = new Scanner(socket.getInputStream());


            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                System.out.println("Recived from client: " + input);
                String output = getResponse(input);
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

    public String getResponse(String input) throws IOException{
        String output = "";

        if (input.contains("CREATENEWUSER")) {
            output = newUser(input);
        } else if (input.contains("LOGIN")) {
            output = userLogin(input);
        } else if (input.contains("JOINGAGAME")) {
            output = joinGame(input);
        } else if (input.contains("ALLPARTICIPANTSHAVEJOINED")) {
            output = launchGame(input);
        } else if (input.contains("STARTNEWGAME")) {
            output = newGame(input);
        } else if (input.contains("PLAYERSUGGESTION")) {
            output = suggestions(input);
        } else if (input.contains("PLAYERCHOICE")) {
            output = choices(input);
        }

        return output;
    }

    public String newUser(String input) {
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


        output += "End";
        return output;

    }

    //INCOMPLETE: Not completely done -- need to work on the file input
    public String userLogin(String input) throws IOException{
        String status = "";
        String output = "RESPONSE--LOGIN--";
        String[] loginData = input.split("--");
        if (loginData.length != 3) {
            output += "INVALIDMESSAGEFORMAT--";
        } else {
            String username = loginData[1];
            String password = loginData[2];

            BufferedReader in = new BufferedReader(new FileReader(new File("UserDatabase")));
            String line;
            int counter = 0;
            while ((line = in.readLine()) != null) {
                if (line.contains(username)) {
                    counter++;
                    if (line.contains(password)) {

                        for(Player player: playerList) {
                            if(player.getUsername().equals(username)) {
                                output += "USERALREADYLOGGEDIN";
                                return output;
                            }
                        }

                        String userToken = generateUserToken();
                        status = "SUCCESS--" + userToken;
                        output += status;
                        Player player = new Player(username, password);
                        playerList.add(player);
                        return output;
                    } else {
                        continue;
                    }
                }
            }

            if (counter == 0) {
                output += "UNKNOWNUSER--";
                return output;
            } else if (counter == 1) {
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
            if (player.getLoggedInAndPlaying() == false) { //INCOMPLETE: check if user is already playing (boolean value, true or false)
                String gameToken = generateGameToken();
                status = "SUCCESS--" + gameToken;
                gameTokenList.add(gameToken);
                output += status;
                player.setLoggedInAndPlaying(true);
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

    public String joinGame(String input) {
        String status = "";
        String output = "RESPONSE--JOINGAME--";
        String[] joinGameData = input.split("--");
        String userToken = joinGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        String gameToken = joinGameData[2];
        boolean checkGameTokenValidity = isGameTokenValid(gameToken);
        Player player = userMap.get(userToken);
        if (checkUserTokenValidity == false) {
            status = "USERNOTLOGGEDIN--" + gameToken;
            output += status;
            return output;
        } else if (checkGameTokenValidity == false) {
            status = "GAMEKEYNOTFOUND--" + gameToken;
            output += status;
            return output;
        } else if (player.getLoggedInAndPlaying() == true) {
            status = "FAILURE--" + gameToken;
            output += status;
            return output;
        } else { //INCOMPLETE: Have some kind of map that connects username to userToken?
            String username = player.getUsername();
            int cumulativeScore = player.getCumulativeScore();
            String addParticipantMessage = "NEWPARTICIPANT--" + username + "--" + cumulativeScore;

        }

        return output;
    }

    public String launchGame(String input) {
        String status = "";
        String output = "RESPONSE--ALLPARTICIPANTSHAVEJOINED--";
        String[] launchGameData = input.split("--");
        String userToken = launchGameData[1];
        boolean checkUserTokenValidity = isUserTokenValid(userToken);
        String gameToken = launchGameData[2];
        boolean checkGameTokenValidity = isGameTokenValid(gameToken);
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
        }

        return output;
    }

    public String sendWord(String input) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File("WordleDeck")));
        
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
        if (userToken.length() > 10) {
            userToken.substring(0, 10);
        }
        return userToken;
    }

    public String generateGameToken() {
        String gameToken = UUID.randomUUID().toString();
        if (gameToken.length() > 3) {
            gameToken.substring(0, 4);
        }
        return gameToken;
    }

    public boolean isUserTokenValid(String userToken) {
        for (int i = 0; i < userTokenList.size(); i++) {
            if (userToken.equals(userTokenList.get(i))) {
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
}
