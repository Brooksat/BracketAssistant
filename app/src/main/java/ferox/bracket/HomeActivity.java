package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    ChallongeRequests CR = new ChallongeRequests(api_key);
    ArrayList<Tournament> tournamentList;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("gettting tourneys", "here");
        getTournaments();
        tournamentList = new ArrayList<>();
        nameList = new ArrayList<>();

    }

    public void goToCreateTournamentActivity(View view) {
        Intent intent = new Intent(this, NewTournamentActivity.class);
        startActivity(intent);
    }

    private void makeTournamentList(String jsonResponse) {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonResponse);
        JsonArray tournaments = jsonElement.getAsJsonArray();

        Log.d("List size", String.valueOf(tournaments.size()));

        for (int i = 0; i < tournaments.size(); i++) {

            Tournament tournament = gson.fromJson(tournaments.get(i).getAsJsonObject().get("tournament"), Tournament.class);
            tournament.setTournamentType(tournaments.get(i).getAsJsonObject().get("tournament").getAsJsonObject().get("tournament_type").getAsString());
            tournamentList.add(0, tournament);
        }

        for (int i = 0; i < tournamentList.size(); i++) {
            nameList.add(tournamentList.get(i).getName());
        }


        ListView listView = findViewById(R.id.listOfTournaments);


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), Bracket.class);
                intent.putExtra("tournamentName", tournamentList.get(position).getName());
                intent.putExtra("tournamentURL", tournamentList.get(position).getUrl());
                intent.putExtra("tournamentType", tournamentList.get(position).getTournamentType());
                intent.putExtra("tournamentState", tournamentList.get(position).getState());
                startActivity(intent);
                Log.d("Type", tournamentList.get(position).getTournamentType());


            }
        });

        listView.invalidate();
    }

    public void getTournaments() {
        VolleyLog.DEBUG = true;

        RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, CR.tournamentsIndex(null),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        makeTournamentList(response);
                        Log.d("Request", " Request Received");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response", " Error");
            }
        });


        RequestQueueSingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

}
