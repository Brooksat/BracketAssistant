package ferox.bracket;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class ChallongeRequests {

    static Context applicationContext;
    static private String apiurl = "https://api.challonge.com/v1/tournaments";
    static private String jsonTag = ".json";
    static private String apiKeySegment = "?api_key=";
    static private String apiKey;


    public ChallongeRequests() {


    }

    static public String tournamentsIndex(String subDomain) {
        String url = apiurl;
        url += jsonTag;
        url += apiKeySegment + apiKey;
        if (subDomain != null) {
            url += makeAPIParameter("subdomain", subDomain);
        }
        return url;
    }

    static public String tounamentCreate(String name, String URL, int type, String subdomain, String description, boolean thirdPlaceMatch
    ) {
        String url = apiurl;
        url += jsonTag + apiKeySegment + apiKey;
        return url;
    }

    static public String tournamentShow(String name) {
        String url = apiurl;
        url += "/" + name;
        url += jsonTag + apiKeySegment + apiKey;
        url += "&include_matches=1&include_participants=1";
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
    static public String jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(String url) {
        String s = "https://challonge.com/";
        s += url + ".json";
        return s;
    }


    static public String participantIndex(String name) {
        String url = apiurl;
        url += "/" + name;
        url += "/participants";
        url += jsonTag + apiKeySegment + apiKey;
        return url;
    }

    static public String participantUpdate(String url, String ID, int seed) {
        return " ";
    }


    public void sendRequest(StringRequest stringRequest) {


    }


    static public String sendGet(final VolleyCallback callback, String requestURL) {


        RequestQueue queue = RequestQueueSingleton.getInstance(applicationContext).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL,
                response -> callback.onSuccess(response),
                error -> Log.d("Response", String.valueOf(error)));

        RequestQueueSingleton.getInstance(applicationContext).addToRequestQueue(stringRequest);

        return " ";
    }

    static public String makeAPIParameter(String field, Object value) {
        String s = "&" + field + "=" + String.valueOf(value);
        return s;
    }


    public static String getApiurl() {
        return apiurl;
    }

    public static void setApiurl(String apiurl) {
        ChallongeRequests.apiurl = apiurl;
    }

    public static String getJsonTag() {
        return jsonTag;
    }

    public static void setJsonTag(String jsonTag) {
        ChallongeRequests.jsonTag = jsonTag;
    }

    public static String getApiKeySegment() {
        return apiKeySegment;
    }

    public static void setApiKeySegment(String apiKeySegment) {
        ChallongeRequests.apiKeySegment = apiKeySegment;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        ChallongeRequests.apiKey = apiKey;
    }
}
