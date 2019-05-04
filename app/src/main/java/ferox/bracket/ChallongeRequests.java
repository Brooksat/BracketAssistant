package ferox.bracket;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
        if (!TextUtils.isEmpty(subDomain.trim())) {
            url += makeAPIParameter("subdomain", subDomain);
        }
        return new APIRequest(url, Request.Method.GET);
    }

    static public APIRequest tounamentCreate(Tournament tournament) {
        String url = API_URL;
        url += JSON_TAG + API_KEY_SEGMENT + apiKey + "&";
        url += tournament.getSettings();
        APIRequest request = new APIRequest(url, Request.Method.POST);
        return request;
    }

    static public APIRequest tournamentShow(String tournamentURL) {
        String requestURL = API_URL;
        requestURL += "/" + tournamentURL;
        requestURL += JSON_TAG + API_KEY_SEGMENT + apiKey;
        requestURL += "&include_matches=1&include_participants=1";
        return new APIRequest(requestURL, Request.Method.GET);
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
    static public APIRequest jsonAtTheEndOfTheNormalURLThatGivesYouInfoNotInTheActualAPIMethodsLikeSeriouslyWTFWhyIsThisAThingChallongeGetItTogether(String url, String subdomain) {

        if (!TextUtils.isEmpty(subdomain)) {
            String s = "https://" + subdomain + ".challonge.com/";
            s += url + ".json";
            APIRequest request = new APIRequest(s, Request.Method.GET);
            return request;
        } else {
            String s = "https://challonge.com/";
            s += url + ".json";

            APIRequest request = new APIRequest(s, Request.Method.GET);
            return request;
        }
    }


    static public APIRequest participantIndex(String name) {
        String url = API_URL;
        url += "/" + name;
        url += "/participants";
        url += JSON_TAG + API_KEY_SEGMENT + apiKey;
        APIRequest request = new APIRequest(url, Request.Method.GET);
        return request;
    }

    static public APIRequest participantCreate(String tournamentURL, Participant participant) {
        String url = API_URL;
        url += "/" + tournamentURL;
        url += PARTICIPANTS + JSON_TAG + API_KEY_SEGMENT + apiKey;
        url += participant.getSettings();
        APIRequest request = new APIRequest(url, Request.Method.POST);
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
                error -> onErrorResponse(error, callback));

        RequestQueueSingleton.getInstance(applicationContext).addToRequestQueue(stringRequest);


    }


    public static void onErrorResponse(VolleyError error, VolleyCallback callback) {

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(getApplicationContext(), "Timeout Error", Toast.LENGTH_SHORT).show();
        } else if (error instanceof AuthFailureError) {
            Toast.makeText(getApplicationContext(), "Auth Failure", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();

            String responseBody = null;
            try {
                responseBody = new String(error.networkResponse.data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JsonParser parser = new JsonParser();
            assert responseBody != null;
            JsonArray errorsArray = parser.parse(responseBody).getAsJsonObject().get("errors").getAsJsonArray();
            ArrayList errorsList = new Gson().fromJson(errorsArray, ArrayList.class);

            callback.onErrorResponse(errorsList);


        } else if (error instanceof NetworkError) {
            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
        }
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
