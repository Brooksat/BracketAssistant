package ferox.bracket;

public class ChallongeRequests {

    static String apiurl = "https://api.challonge.com/v1/tournaments";
    String jsonTag = ".json";
    String apiKeySegment = "?api_key=";
    String apiKey;


    public ChallongeRequests(String apiKey) {
        this.apiKey = apiKey;

    }

    public String tournamentsIndex(String subDomain) {
        String url = this.apiurl;
        url += jsonTag;
        url += apiKeySegment + apiKey;
        if (subDomain != null) {
            url += makeAPIParameter("subdomain", subDomain);
        }
        return url;
    }

    public String tounamentCreate(String name, String URL, int type, String subdomain, String description, boolean thirdPlaceMatch
    ) {
        String url = apiurl;
        url += jsonTag + apiKeySegment + apiKey;
        return url;
    }

    public String tournamentShow(String name) {
        String url = apiurl;
        url += "/" + name;
        url += jsonTag + apiKeySegment + apiKey;
        return url;
    }

    /*
    As it currently stands you can put .json at the end of the normal URL for a tournament on
    challonge.com and you will receive a json that include fields for round and matches_by_round
    that you can not currently get with any of the official API request methods.
    Maybe this is a remnant of the old api or some incomplete version of the new api v2. However as
    far as I know this isn't mentioned anywhere on the official api page. I am glad tho for the
    moment of serendipity that made me privy to this information because this will make developing
    this app much more convenient.
     */
    public String jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(String url) {
        String s = "https://challonge.com/";
        s += url + ".json";
        return s;
    }


    public String participantIndex(String name) {
        String url = apiurl;
        url += "/" + name;
        url += "/participants";
        url += jsonTag + apiKeySegment + apiKey;
        return url;
    }

    public String makeAPIParameter(String field, Object value) {
        String s = "&" + field + "=" + String.valueOf(value);
        return s;
    }


}
