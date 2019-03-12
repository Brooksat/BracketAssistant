package ferox.bracket;

public class Tournament {

    final static String SINGLE_ELIM = "single elimination";
    final static String DOUBLE_ELIM = "double elimination";
    final static String ROUND_ROBIN = "round robin";
    final static String AWAITING_REVIEW = "awaiting_review";
    final static String UNDERWAY = "underway";
    final static String PENDING = "pending";
    final static String COMPLETE = "complete";
    final static String MATCH_WINS = "match wins";
    final static String GAME_WINS = "game wins";
    final static String POINTS_SCORED = "points scored";
    final static String POINTS_DIFFERENCE = "points difference";
    final static String CUSTOM = "custom";
    final static String DEFAULT_GRANDS = "";
    final static String SINGLE_MATCH = "single match";
    final static String SKIP = "skip";






    private String name;
    private String tournamentType;
    private String url;
    private String subdomain;
    private String discription;
    private boolean openSignUp;
    private boolean holdThirdPlaceMatch;
    private float swissPtsForMatchWin;
    private float swissPtsForMatchTie;
    private float swissPtsForGameWin;
    private float swissPtsForGameTie;
    private float swissPtsForBye;
    private int swissRounds;
    private String swissRankedBy;
    private float rrPtsForMatchWin;
    private float rrPtsForMatchTie;
    private float rrPtsForGameWin;
    private float rrPtsForGameTie;
    private boolean acceptAttachments;
    private boolean hideForum;
    private boolean showRounds;
    private boolean isPrivate;
    private boolean notifyUsersMatchesOpens;
    private boolean notifyUsersTourneyOver;
    private boolean sequentialPairings;
    private int signUpCap;
    private String startAt;
    private int checkInDuration;
    private String grandFinalsModifier;



    String state;
    private int size;


    public Tournament() {
        this.name = "Default Name";
        this.url = "Defautl URL";
        this.tournamentType = "Default Type";
        this.state = "Default State";
    }

    public Tournament(String name, String URL, String type, String state, int participants_count) {
        this.name = name;
        this.url = URL;
        this.tournamentType = type;
        this.state = state;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public boolean isOpenSignUp() {
        return openSignUp;
    }

    public void setOpenSignUp(boolean openSignUp) {
        this.openSignUp = openSignUp;
    }

    public boolean isHoldThirdPlaceMatch() {
        return holdThirdPlaceMatch;
    }

    public void setHoldThirdPlaceMatch(boolean holdThirdPlaceMatch) {
        this.holdThirdPlaceMatch = holdThirdPlaceMatch;
    }

    public float getSwissPtsForMatchWin() {
        return swissPtsForMatchWin;
    }

    public void setSwissPtsForMatchWin(float swissPtsForMatchWin) {
        this.swissPtsForMatchWin = swissPtsForMatchWin;
    }

    public float getSwissPtsForMatchTie() {
        return swissPtsForMatchTie;
    }

    public void setSwissPtsForMatchTie(float swissPtsForMatchTie) {
        this.swissPtsForMatchTie = swissPtsForMatchTie;
    }

    public float getSwissPtsForGameWin() {
        return swissPtsForGameWin;
    }

    public void setSwissPtsForGameWin(float swissPtsForGameWin) {
        this.swissPtsForGameWin = swissPtsForGameWin;
    }

    public float getSwissPtsForGameTie() {
        return swissPtsForGameTie;
    }

    public void setSwissPtsForGameTie(float swissPtsForGameTie) {
        this.swissPtsForGameTie = swissPtsForGameTie;
    }

    public float getSwissPtsForBye() {
        return swissPtsForBye;
    }

    public void setSwissPtsForBye(float swissPtsForBye) {
        this.swissPtsForBye = swissPtsForBye;
    }

    public int getSwissRounds() {
        return swissRounds;
    }

    public void setSwissRounds(int swissRounds) {
        this.swissRounds = swissRounds;
    }

    public String getSwissRankedBy() {
        return swissRankedBy;
    }

    public void setSwissRankedBy(String swissRankedBy) {
        this.swissRankedBy = swissRankedBy;
    }

    public float getRrPtsForMatchWin() {
        return rrPtsForMatchWin;
    }

    public void setRrPtsForMatchWin(float rrPtsForMatchWin) {
        this.rrPtsForMatchWin = rrPtsForMatchWin;
    }

    public float getRrPtsForMatchTie() {
        return rrPtsForMatchTie;
    }

    public void setRrPtsForMatchTie(float rrPtsForMatchTie) {
        this.rrPtsForMatchTie = rrPtsForMatchTie;
    }

    public float getRrPtsForGameWin() {
        return rrPtsForGameWin;
    }

    public void setRrPtsForGameWin(float rrPtsForGameWin) {
        this.rrPtsForGameWin = rrPtsForGameWin;
    }

    public float getRrPtsForGameTie() {
        return rrPtsForGameTie;
    }

    public void setRrPtsForGameTie(float rrPtsForGameTie) {
        this.rrPtsForGameTie = rrPtsForGameTie;
    }

    public boolean isAcceptAttachments() {
        return acceptAttachments;
    }

    public void setAcceptAttachments(boolean acceptAttachments) {
        this.acceptAttachments = acceptAttachments;
    }

    public boolean isHideForum() {
        return hideForum;
    }

    public void setHideForum(boolean hideForum) {
        this.hideForum = hideForum;
    }

    public boolean isShowRounds() {
        return showRounds;
    }

    public void setShowRounds(boolean showRounds) {
        this.showRounds = showRounds;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isNotifyUsersMatchesOpens() {
        return notifyUsersMatchesOpens;
    }

    public void setNotifyUsersMatchesOpens(boolean notifyUsersMatchesOpens) {
        this.notifyUsersMatchesOpens = notifyUsersMatchesOpens;
    }

    public boolean isNotifyUsersTourneyOver() {
        return notifyUsersTourneyOver;
    }

    public void setNotifyUsersTourneyOver(boolean notifyUsersTourneyOver) {
        this.notifyUsersTourneyOver = notifyUsersTourneyOver;
    }

    public boolean isSequentialPairings() {
        return sequentialPairings;
    }

    public void setSequentialPairings(boolean sequentialPairings) {
        this.sequentialPairings = sequentialPairings;
    }

    public int getSignUpCap() {
        return signUpCap;
    }

    public void setSignUpCap(int signUpCap) {
        this.signUpCap = signUpCap;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public int getCheckInDuration() {
        return checkInDuration;
    }

    public void setCheckInDuration(int checkInDuration) {
        this.checkInDuration = checkInDuration;
    }

    public String getGrandFinalsModifier() {
        return grandFinalsModifier;
    }

    public void setGrandFinalsModifier(String grandFinalsModifier) {
        this.grandFinalsModifier = grandFinalsModifier;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
