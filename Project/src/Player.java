import java.net.Socket;

public class Player {
    private String username;
    private String userToken;
    private String gameToken;
    private String password;
    private int cumulativeScore;
    private int fooled;
    private int fooled_by;
    private String suggestion;
    private String choice;
    private boolean loggedInAndPlaying;
    private int port;
    private String message;
    private int wordOn;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /*public Player(String username, String userToken, String gameToken, String password, int cumulativeScore, int fooled, int fooled_by,
                  String suggestion, String choice, boolean loggedInAndPlaying, int port, String message,int wordOn) {
        this.username = username;
        this.userToken = userToken;
        this.password = password;
        this.cumulativeScore = cumulativeScore;
        this.fooled = fooled;
        this.fooled_by = fooled_by;
        this.suggestion = null;
        this.choice = choice;
        this.loggedInAndPlaying = false;
        this.gameToken = gameToken;
        this.port = port;
        this.message = "Hello";
        this.wordOn = 0;



    }*/

    public Player() {

    }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserToken() {
            return this.userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public String getGameToken() {
            return this.gameToken;
        }

        public void setGameToken(String gameToken) {
            this.gameToken = gameToken;
        }

        public int getCumulativeScore() {
            return this.cumulativeScore;
        }

        public void setCumulativeScore(int cumulativeScore) {
            this.cumulativeScore = cumulativeScore;
        }

        public int getFooled() {
            return this.fooled;
        }

        public void setFooled(int fooled) {
            this.fooled = fooled;
        }

        public int getFooled_by() {
            return this.fooled_by;
        }

        public void setFooled_by(int fooled_by) {
            this.fooled_by = fooled_by;
        }

        public String getSuggestion() {
            return this.suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public String getChoice() {
            return this.choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }

        public boolean getLoggedInAndPlaying() {
            return this.loggedInAndPlaying;
        }

        public void setLoggedInAndPlaying(boolean loggedInAndPlaying) {
            this.loggedInAndPlaying = loggedInAndPlaying;
        }

        public void setPort(int port){this.port = port;}

        public int getPort(){return this.port;}



        public void setMessage(String message){
            this.message = message;
        }

        public String getMessage(){
            return this.message;
        }

        public int getWordOn(){
            return this.wordOn;
        }

        public void addWordOn(){
            this.wordOn++;
        }





}
