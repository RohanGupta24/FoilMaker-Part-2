public class Player {
    private static String username;
    private static String userToken;
    private static String gameToken;
    private static String password;
    private static int cumulativeScore;
    private static int fooled;
    private static int fooled_by;
    private static String suggestion;
    private static String choice;
    private static boolean loggedInAndPlaying;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Player(String username, String userToken, String gameToken, String password, int cumulativeScore, int fooled, int fooled_by,
                  String suggestion, String choice, boolean loggedInAndPlaying) {
        this.username = username;
        this.userToken = userToken;
        this.password = password;
        this.cumulativeScore = cumulativeScore;
        this.fooled = fooled;
        this.fooled_by = fooled_by;
        this.suggestion = suggestion;
        this.choice = choice;
        this.loggedInAndPlaying = loggedInAndPlaying;
        this.gameToken = gameToken;
    }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public String getGameToken() {
            return gameToken;
        }

        public void setGameToken(String gameToken) {
            this.gameToken = gameToken;
        }

        public int getCumulativeScore() {
            return cumulativeScore;
        }

        public void setCumulativeScore(int cumulativeScore) {
            this.cumulativeScore = cumulativeScore;
        }

        public int getFooled() {
            return fooled;
        }

        public void setFooled(int fooled) {
            this.fooled = fooled;
        }

        public int getFooled_by() {
            return fooled_by;
        }

        public void setFooled_by(int fooled_by) {
            this.fooled_by = fooled_by;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }

        public String getChoice() {
            return choice;
        }

        public void setChoice(String choice) {
            this.choice = choice;
        }

        public boolean getLoggedInAndPlaying() {
            return loggedInAndPlaying;
        }

        public void setLoggedInAndPlaying(boolean loggedInAndPlaying) {
            this.loggedInAndPlaying = loggedInAndPlaying;
        }



}
