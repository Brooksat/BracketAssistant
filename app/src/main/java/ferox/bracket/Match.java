package ferox.bracket;

import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("id")
    private String id;
    @SerializedName("tournament_id")
    private String tournamentID;
    @SerializedName("identifier")
    private int identifier;

    private int p1Seed;
    private int p2Seed;
    @SerializedName("player1_prereq_identifier")
    private int p1PreviousIdentifier;
    @SerializedName("player2_prereq_identifier")
    private int p2PreviousIdentifier;
    private Participant p1;
    private Participant p2;
    private Match p1PreviousMatch;
    private Match p2PreviousMatch;
    private boolean P1Decided;
    private boolean P2Decided;
    @SerializedName("player1_placeholder_text")
    private String p1PrereqText;
    @SerializedName("player2_placeholder_text")
    private String p2PrereqText;
    @SerializedName("winner_id")
    private String winnerID;
    @SerializedName("loser_id")
    private String loserID;
    private boolean isWinners;


    public Match(String id, int number, int p1Seed, int p2Seed, Participant p1, Participant p2,
                 Match p1PreviousMatch, Match p2PreviousMatch, boolean P1Decided, boolean P2Decided,
                 String p1PrereqText, String p2PrereqText) {
        super();
        this.id = id;
        this.identifier = number;
        this.p1Seed = p1Seed;
        this.p2Seed = p2Seed;
        this.p1 = p1;
        this.p2 = p2;
        this.p1PreviousMatch = p1PreviousMatch;
        this.p2PreviousMatch = p2PreviousMatch;
        this.P1Decided = P1Decided;
        this.P2Decided = P2Decided;
        this.p1PrereqText = p1PrereqText;
        this.p2PrereqText = p2PrereqText;

    }

    public Match() {
        id = "";
        tournamentID = "";
        identifier = 0;
        p1Seed = 0;
        p2Seed = 0;
        p1PreviousIdentifier = 0;
        p2PreviousIdentifier = 0;
        p1 = null;
        p2 = null;
        p1PreviousMatch = null;
        p2PreviousMatch = null;
        P1Decided = false;
        P2Decided = false;
        p1PrereqText = "";
        p2PrereqText = "";
        winnerID = "";
        loserID = "";

    }

    /**
     * When deserializing JSON sets any JsonNull fields to their default values, therefore settings
     * string to null, this methods changes String fields to ""
     */
    public void undoJsonShenanigans() {
        if (id == null) {
            id = "";
        }
        if (tournamentID == null) {
            tournamentID = "";
        }
        if (p1PrereqText == null) {
            p1PrereqText = "";
        }
        if (p2PrereqText == null) {
            p2PrereqText = "";
        }
        if (winnerID == null) {
            winnerID = "";
        }
        if (loserID == null) {
            loserID = "";
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public int getP1PreviousIdentifier() {
        return p1PreviousIdentifier;
    }

    public void setP1PreviousIdentifier(int p1PreviousIdentifier) {
        this.p1PreviousIdentifier = p1PreviousIdentifier;
    }

    public int getP2PreviousIdentifier() {
        return p2PreviousIdentifier;
    }

    public void setP2PreviousIdentifier(int p2PreviousIdentifier) {
        this.p2PreviousIdentifier = p2PreviousIdentifier;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getP1Seed() {
        return p1Seed;
    }

    public void setP1Seed(int p1Seed) {
        this.p1Seed = p1Seed;
    }

    public int getP2Seed() {
        return p2Seed;
    }

    public void setP2Seed(int p2Seed) {
        this.p2Seed = p2Seed;
    }

    public Participant getP1() {
        return p1;
    }

    public void setP1(Participant p1) {
        this.p1 = p1;
    }

    public Participant getP2() {
        return p2;
    }

    public void setP2(Participant p2) {
        this.p2 = p2;
    }

    public Match getP1PreviousMatch() {
        return p1PreviousMatch;
    }

    public void setP1PreviousMatch(Match p1PreviousMatch) {
        this.p1PreviousMatch = p1PreviousMatch;
    }

    public Match getP2PreviousMatch() {
        return p2PreviousMatch;
    }

    public void setP2PreviousMatch(Match p2PreviousMatch) {
        this.p2PreviousMatch = p2PreviousMatch;
    }

    public boolean isP1Decided() {
        return P1Decided;
    }

    public void setP1Decided(boolean p1Decided) {
        P1Decided = p1Decided;
    }

    public boolean isP2Decided() {
        return P2Decided;
    }

    public void setP2Decided(boolean p2Decided) {
        P2Decided = p2Decided;
    }

    public String getP1PrereqText() {
        return p1PrereqText;
    }

    public void setP1PrereqText(String p1PrereqText) {
        this.p1PrereqText = p1PrereqText;
    }

    public String getP2PrereqText() {
        return p2PrereqText;
    }

    public void setP2PrereqText(String p2PrereqText) {
        this.p2PrereqText = p2PrereqText;
    }

    public String getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(String winnerID) {
        this.winnerID = winnerID;
    }

    public String getLoserID() {
        return loserID;
    }

    public void setLoserID(String loserID) {
        this.loserID = loserID;
    }

    public boolean isWinners() {
        return isWinners;
    }

    public void setIsWinners(boolean winners) {
        isWinners = winners;
    }
}
