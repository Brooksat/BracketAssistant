package ferox.bracket;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Tournament {

    private static final String SINGLE_ELIM = "single elimination";
    private static final String DOUBLE_ELIM = "double elimination";
    private static final String ROUND_ROBIN = "round robin";
    private static final String AWAITING_REVIEW = "awaiting_review";
    private static final String UNDERWAY = "underway";
    private static final String PENDING = "pending";
    private static final String COMPLETE = "complete";
    private static final String MATCH_WINS = "match wins";
    private static final String GAME_WINS = "game wins";
    private static final String POINTS_SCORED = "points scored";
    private static final String POINTS_DIFFERENCE = "points difference";
    private static final String CUSTOM = "custom";
    private static final String DEFAULT_GRANDS = "";
    private static final String SINGLE_MATCH = "single match";
    private static final String SKIP = "skip";

    private static final String TOURNAMENT_NAME = "&tournament[name]=";
    private static final String TOURNAMENT_TYPE = "&tournament[tournament_type]=";
    private static final String TOURNAMENT_URL = "&tournament[url]=";
    private static final String TOURNAMENT_SUBDOMAIN = "&tournament[subdomain]=";
    private static final String TOURNAMENT_DESCRIPTION = "&tournament[description]=";
    private static final String TOURNAMENT_SIGNUP = "&tournament[open_signup]=";
    private static final String TOURNAMENT_THIRD_PLACE = "&tournament[hold_third_place_match]=";
    private static final String TOURNAMENT_SWISS_MATCH_WIN = "&tournament[pts_for_match_win]=";
    private static final String TOURNAMENT_SWISS_MATCH_TIE = "&tournament[pts_for_match_tie]=";
    private static final String TOURNAMENT_SWISS_GAME_WIN = "&tournament[pts_for_game_win]=";
    private static final String TOURNAMENT_SWISS_GAME_TIE = "&tournament[pts_for_game_tie]=";
    private static final String TOURNAMENT_SWISS_PTS_BYE = "&tournament[pts_for_bye]=";
    private static final String TOURNAMENT_SWISS_ROUNDS = "&tournament[swiss_rounds]=";
    private static final String TOURNAMENT_RANKED_BY = "&tournament[ranked_by]=";
    private static final String TOURNAMENT_RR_MATCH_WIN = "&tournament[rr_pts_for_match_win]=";
    private static final String TOURNAMENT_RR_MATCH_TIE = "&tournament[rr_pts_for_match_tie]=";
    private static final String TOURNAMENT_RR_GAME_WIN = "&tournament[rr_pts_for_game_win]=";
    private static final String TOURNAMENT_RR_GAME_TIE = "&tournament[rr_pts_for_game_tie]=";
    private static final String TOURNAMENT_ACCEPT_ATTACHMENTS = "&tournament[accept_attachments]=";
    private static final String TOURNAMENT_SHOW_ROUNDS = "&tournament[show_rounds]=";
    private static final String TOURNAMENT_PRIVATE = "&tournament[private]=";
    private static final String TOURNAMENT_NOTIFY_MATCH_OPEN = "&tournament[notify_users_when_matches_opens]=";
    private static final String TOURNAMENT_NOTIFY_TOURNAMENT_ENDS = "&tournament[notify_users_when_the_tournament_ends]=";
    private static final String TOURNAMENT_SEQUENTIAL_PAIRINGS = "&tournament[sequential_pairings]=";
    private static final String TOURNAMENT_SIGNUP_CAP = "&tournament[signup_cap]=";
    private static final String TOURNAMENT_START_AT = "&tournament[start_at]=";
    private static final String TOURNAMENT_CHECK_IN_DURATION = "&tournament[check_in_duration]=";
    private static final String TOURNAMENT_GRAND_FINALS_MODIFIER = "&tournament[grand_finals_modifier]=";


    @SerializedName("name")
    private String name;
    @SerializedName("tournament_type")
    private String type;
    @SerializedName("url")
    private String url;
    @SerializedName("subdomain")
    private String subdomain;
    @SerializedName("description_source")
    private String description;
    @SerializedName("open_signup")
    private boolean openSignUp;
    @SerializedName("hold_third_place_match")
    private boolean holdThirdPlaceMatch;
    @SerializedName("pts_for_match_win")
    private float swissPtsForMatchWin;
    @SerializedName("pts_for_match_tie")
    private float swissPtsForMatchTie;
    @SerializedName("pts_for_game_win")
    private float swissPtsForGameWin;
    @SerializedName("pts_for_game_tie")
    private float swissPtsForGameTie;
    @SerializedName("pts_for_bye")
    private float swissPtsForBye;
    @SerializedName("swiss_rounds")
    private int swissRounds;
    //Challonge provides a value for number of rounds in swiss bracket
    //it should be left alone but this value's purpose is to tell us if this value is explicitly set
    //TODO remember to set this
    private boolean isSwissRoundSet;
    @SerializedName("ranked_by")
    private String rankedBy;
    @SerializedName("rr_pts_for_match_win")
    private float rrPtsForMatchWin;
    @SerializedName("rr_pts_for_match_tie")
    private float rrPtsForMatchTie;
    @SerializedName("rr_pts_for_game_win")
    private float rrPtsForGameWin;
    @SerializedName("rr_pts_for_game_tie")
    private float rrPtsForGameTie;
    @SerializedName("accept_attachments")
    private boolean acceptAttachments;
    @SerializedName("show_rounds")
    private boolean showRounds;
    @SerializedName("private")
    private boolean isPrivate;
    @SerializedName("notify_users_when_matches_opens")
    private boolean notifyUsersMatchesOpens;
    @SerializedName("notify_users_when_the_tournament_ends")
    private boolean notifyUsersTourneyOver;
    @SerializedName("sequential_parings")
    private boolean sequentialPairings;
    @SerializedName("signup_cao")
    private int signUpCap;
    @SerializedName("start_at")
    private String startAt;
    @SerializedName("check_in_duration")
    private int checkInDuration;
    @SerializedName("grand_finals_modifier")
    private String grandFinalsModifier;
    @SerializedName("state")
    String state;
    @SerializedName("participants_count")
    private int participantCount;

    //TODO UMM GOTTA WORK OUT CONSTRUCTORS

    public Tournament() {
        name = "";
        type = SINGLE_ELIM;
        url = "";
        subdomain = "";
        description = "";
        openSignUp = false;
        holdThirdPlaceMatch = false;
        swissPtsForMatchWin = 1.0f;
        swissPtsForMatchTie = 0.5f;
        swissPtsForGameWin = 0.0f;
        swissPtsForGameTie = 0.0f;
        swissPtsForBye = 1.0f;
        swissRounds = 0;
        isSwissRoundSet = false;
        rankedBy = MATCH_WINS;
        rrPtsForMatchWin = 1.0f;
        rrPtsForMatchTie = 0.5f;
        rrPtsForGameWin = 0.0f;
        rrPtsForGameTie = 0.0f;
        acceptAttachments = false;
        showRounds = true;
        isPrivate = false;
        notifyUsersMatchesOpens = true;
        notifyUsersTourneyOver = true;
        sequentialPairings = false;
        signUpCap = 256;
        startAt = "";
        checkInDuration = 0;
        grandFinalsModifier = DEFAULT_GRANDS;

    }

    public Tournament(String name, String URL, String type, String state, int participants_count) {
        this.name = name;
        this.url = URL;
        this.type = type;
        this.state = state;
        this.participantCount = participants_count;
    }

    public String getSettings() {
        String settings = "";

        try {

            if (getName() != null) {
                settings += TOURNAMENT_NAME + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }
            if (getType() != null) {
                settings += TOURNAMENT_TYPE + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }
            if (getUrl() != null) {
                settings += TOURNAMENT_URL + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }
            if (getSubdomain() != null) {
                settings += TOURNAMENT_SUBDOMAIN + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }
            if (getDescription() != null) {
                settings += TOURNAMENT_DESCRIPTION + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }

            settings += TOURNAMENT_DESCRIPTION + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_THIRD_PLACE + URLEncoder.encode(String.valueOf(isHoldThirdPlaceMatch()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_MATCH_WIN + URLEncoder.encode(String.valueOf(getSwissPtsForMatchWin()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_MATCH_TIE + URLEncoder.encode(String.valueOf(getSwissPtsForMatchTie()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_GAME_WIN + URLEncoder.encode(String.valueOf(getSwissPtsForGameWin()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_GAME_TIE + URLEncoder.encode(String.valueOf(getSwissPtsForGameTie()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_PTS_BYE + URLEncoder.encode(String.valueOf(getSwissPtsForBye()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SWISS_ROUNDS + URLEncoder.encode(String.valueOf(getSwissRounds()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_RANKED_BY + URLEncoder.encode(getRankedBy(), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_RR_MATCH_WIN + URLEncoder.encode(String.valueOf(getRrPtsForMatchWin()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_RR_MATCH_TIE + URLEncoder.encode(String.valueOf(getRrPtsForMatchTie()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_RR_GAME_WIN + URLEncoder.encode(String.valueOf(getRrPtsForGameWin()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_RR_GAME_TIE + URLEncoder.encode(String.valueOf(getRrPtsForGameTie()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_ACCEPT_ATTACHMENTS + URLEncoder.encode(String.valueOf(isAcceptAttachments()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_SHOW_ROUNDS + URLEncoder.encode(String.valueOf(isShowRounds()), StandardCharsets.UTF_8.toString());
            settings += TOURNAMENT_PRIVATE + encode(isPrivate);
            settings += TOURNAMENT_NOTIFY_MATCH_OPEN + encode(isNotifyUsersMatchesOpens());
            settings += TOURNAMENT_NOTIFY_TOURNAMENT_ENDS + encode(isNotifyUsersTourneyOver());
            settings += TOURNAMENT_SEQUENTIAL_PAIRINGS + encode(isSequentialPairings());
            settings += TOURNAMENT_SIGNUP_CAP + encode(getSignUpCap());
            settings += TOURNAMENT_START_AT + encode(getStartAt());
            settings += TOURNAMENT_CHECK_IN_DURATION + encode(getCheckInDuration());
            settings += TOURNAMENT_GRAND_FINALS_MODIFIER + encode(getGrandFinalsModifier());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public String encode(Object object) {
        String encodedString = "";
        try {
            encodedString = URLEncoder.encode(String.valueOf(object), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedString;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", subdomain='" + subdomain + '\'' +
                ", description='" + description + '\'' +
                ", openSignUp=" + openSignUp +
                ", holdThirdPlaceMatch=" + holdThirdPlaceMatch +
                ", swissPtsForMatchWin=" + swissPtsForMatchWin +
                ", swissPtsForMatchTie=" + swissPtsForMatchTie +
                ", swissPtsForGameWin=" + swissPtsForGameWin +
                ", swissPtsForGameTie=" + swissPtsForGameTie +
                ", swissPtsForBye=" + swissPtsForBye +
                ", swissRounds=" + swissRounds +
                ", isSwissRoundSet=" + isSwissRoundSet +
                ", rankedBy='" + rankedBy + '\'' +
                ", rrPtsForMatchWin=" + rrPtsForMatchWin +
                ", rrPtsForMatchTie=" + rrPtsForMatchTie +
                ", rrPtsForGameWin=" + rrPtsForGameWin +
                ", rrPtsForGameTie=" + rrPtsForGameTie +
                ", acceptAttachments=" + acceptAttachments +
                ", showRounds=" + showRounds +
                ", isPrivate=" + isPrivate +
                ", notifyUsersMatchesOpens=" + notifyUsersMatchesOpens +
                ", notifyUsersTourneyOver=" + notifyUsersTourneyOver +
                ", sequentialPairings=" + sequentialPairings +
                ", signUpCap=" + signUpCap +
                ", startAt='" + startAt + '\'' +
                ", checkInDuration=" + checkInDuration +
                ", grandFinalsModifier='" + grandFinalsModifier + '\'' +
                ", state='" + state + '\'' +
                ", participantCount=" + participantCount +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRankedBy() {
        return rankedBy;
    }

    public void setRankedBy(String rankedBy) {
        this.rankedBy = rankedBy;
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

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
