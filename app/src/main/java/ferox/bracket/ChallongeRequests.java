package ferox.bracket;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

public class ChallongeRequests {

    static Context applicationContext;
    static final private String API_URL = "https://api.challonge.com/v1/tournaments";
    static final private String JSON_TAG = ".json";
    static final private String API_KEY_SEGMENT = "?api_key=";
    static final private String PARTICIPANTS = "/participants";

    static private String apiKey;


    public ChallongeRequests() {


    }

    static public APIRequest tournamentsIndex(String subDomain) {
        String url = API_URL;
        url += JSON_TAG;
        url += API_KEY_SEGMENT + apiKey;
        if (subDomain != null) {
            url += makeAPIParameter("subdomain", subDomain);
        }
        APIRequest request = new APIRequest(url, Request.Method.GET);
        return request;
    }

    static public String tounamentCreate(String name, String URL, int type, String subdomain, String description, boolean thirdPlaceMatch
    ) {
        String url = API_URL;
        url += JSON_TAG + API_KEY_SEGMENT + apiKey;
        return url;
    }

    static public String tournamentShow(String name) {
        String url = API_URL;
        url += "/" + name;
        url += JSON_TAG + API_KEY_SEGMENT + apiKey;
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
    static public APIRequest jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(String url) {
        String s = "https://challonge.com/";
        s += url + ".json";

        APIRequest request = new APIRequest(s, Request.Method.GET);
        return request;
    }


    static public APIRequest participantIndex(String name) {
        String url = API_URL;
        url += "/" + name;
        url += "/participants";
        url += JSON_TAG + API_KEY_SEGMENT + apiKey;
        APIRequest request = new APIRequest(url, Request.Method.GET);
        return request;
    }

    static public APIRequest participantUpdate(String tournamentUrl, String id, ParticipantSettings settings) {
        String url = API_URL;
        url += "/" + tournamentUrl;
        url += PARTICIPANTS;
        url += "/" + id;
        url += JSON_TAG + API_KEY_SEGMENT + apiKey + "&";
        url += settings.getSettings();

        APIRequest request = new APIRequest(url, Request.Method.PUT);
        return request;
    }

    static public APIRequest participantDestroy(String tournamentURL, String ID) {
        String url = API_URL;
        url += "/" + tournamentURL;
        url += PARTICIPANTS;
        url += "/" + ID;
        url += JSON_TAG + API_KEY_SEGMENT + apiKey + "&";
        APIRequest request = new APIRequest(url, Request.Method.DELETE);
        return request;
    }

    static public APIRequest participantRandomize(String tournamentURL) {
        String url = API_URL;
        url += "/" + tournamentURL;
        url += PARTICIPANTS;
        url += "/randomize";
        url += JSON_TAG + API_KEY_SEGMENT + apiKey;
        APIRequest request = new APIRequest(url, Request.Method.POST);
        return request;
    }


    static public void sendRequest(final VolleyCallback callback, APIRequest request) {


        StringRequest stringRequest = new StringRequest(request.getRequestMethodType(), request.getUrl(),
                response -> callback.onSuccess(response),
                error -> Log.d("Response", String.valueOf(error)));

        RequestQueueSingleton.getInstance(applicationContext).addToRequestQueue(stringRequest);


    }

    static String respond(String response) {
        return response;
    }

    static public String makeAPIParameter(String field, Object value) {
        String s = "&" + field + "=" + String.valueOf(value);
        return s;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context applicationContext) {
        ChallongeRequests.applicationContext = applicationContext;
    }


    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        ChallongeRequests.apiKey = apiKey;
    }
}
