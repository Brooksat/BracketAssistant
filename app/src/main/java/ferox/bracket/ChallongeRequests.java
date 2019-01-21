package ferox.bracket;

import android.util.Log;

public class ChallongeRequests {

    static String apiurl = "https://api.challonge.com/v1/tournaments";
    String jsonTag = ".json";
    String apiKeySegment = "?api_key=";
    String apiKey;


    public ChallongeRequests(String apiKey) {
        this.apiKey = apiKey;

    }

    public String tournamentsIndex() {
        String url = this.apiurl;
        url += jsonTag;
        url += apiKeySegment + apiKey;
        Log.d("url", url);
        return url;
    }

    public String tounamentCreate() {
        String url = apiurl;

        return url;
    }

    public String tournamentShow() {
        String url = apiurl;
        return url;
    }




}
