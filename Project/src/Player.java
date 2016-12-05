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

        public synchronized String getUsername() {
            return this.username;
        }

        public synchronized void setUsername(String username) {
            this.username = username;
        }

        public synchronized String getPassword() {
            return this.password;
        }

        public synchronized void setPassword(String password) {
            this.password = password;
        }

        public synchronized String getUserToken() {
            return this.userToken;
        }

        public synchronized void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public synchronized String getGameToken() {
            return this.gameToken;
        }

        public synchronized void setGameToken(String gameToken) {
            this.gameToken = gameToken;
        }

        public synchronized int getCumulativeScore() {
            return this.cumulativeScore;
        }

        public synchronized void setCumulativeScore(int cumulativeScore) {
            this.cumulativeScore = cumulativeScore;
        }

        public synchronized int getFooled() {
            return this.fooled;
        }

        public synchronized void setFooled(int fooled) {
            this.fooled = fooled;
        }

        public synchronized int getFooled_by() {
            return this.fooled_by;
        }

        public synchronized void setFooled_by(int fooled_by) {
            this.fooled_by = fooled_by;
        }

        public synchronized String getSuggestion() {
            return this.suggestion;
        }

        public synchronized void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public synchronized String getChoice() {
            return this.choice;
        }

        public synchronized void setChoice(String choice) {
            this.choice = choice;
        }

        public synchronized boolean getLoggedInAndPlaying() {
            return this.loggedInAndPlaying;
        }

        public synchronized void setLoggedInAndPlaying(boolean loggedInAndPlaying) {
            this.loggedInAndPlaying = loggedInAndPlaying;
        }

        public synchronized void setPort(int port){this.port = port;}

        public synchronized int getPort(){return this.port;}



        public synchronized void setMessage(String message){
            this.message = message;
        }

        public synchronized String getMessage(){
            return this.message;
        }

        public synchronized int getWordOn(){
            return this.wordOn;
        }

        public synchronized void addWordOn(){
            this.wordOn++;
        }





}
