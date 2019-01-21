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
