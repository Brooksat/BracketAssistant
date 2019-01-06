package ferox.bracket;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

public class ChallongeRequests {

    static String apiurl = "https://api.challonge.com/v1/tournaments";
    Context mContext;
    String requestResponse;

    /**
     * Main.
     */
//    public static void main(String[] args) throws Exception {
//
//        ChallongeRequests CR = new ChallongeRequests();
//        System.out.println("Testing 1 - Send Http GET request");
//
//
//        System.out.println(CR.getTournament("ka35zhyo"));
//
//    }
    public ChallongeRequests(Context context) {
        mContext = context;
    }


    public String getTournament(String tournamentName) throws Exception {
        //String url = apiurl + '/' + tournamentName + ".json?api_key=hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07&include_matches=1&include_participants=1";
        String url = apiurl + '/' + tournamentName + "/participants" + ".json?api_key=hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

        VolleyLog.DEBUG = true;


        RequestQueue queue = RequestQueueSingleton.getInstance(mContext.getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        requestResponse = response;
                        Log.d("Request", " Request Recieved");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestResponse = "Error";
                Log.d("Response", " Error");
            }
        });


        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(stringRequest);


//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//        //optional default is GET
//        con.setRequestMethod("GET");
//
//        //add request header
//        con.setRequestProperty("Show", "Show");
//
//        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);
//
//        if (responseCode != 200) {
//            System.out.println("Error Stream");
//
//
//            BufferedReader ine = new BufferedReader(
//                    new InputStreamReader(con.getErrorStream()));
//
//            StringBuffer responsee = new StringBuffer();
//            String inputLinee;
//
//            while ((inputLinee = ine.readLine()) != null) {
//                responsee.append(inputLinee);
//
//            }
//            ine.close();
//            FileOutputStream fos = new FileOutputStream("error.txt");
//            byte[] stb = responsee.toString().getBytes();
//            fos.write(stb);
//            System.out.println(responsee.toString());
//        } else {
//
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            return response.toString();
//            //print result
//            //System.out.println(response.toString());
//        }
        return requestResponse;
    }

}
