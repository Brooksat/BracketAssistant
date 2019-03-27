package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    ArrayList<Tournament> tournamentList;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("gettting tourneys", "here");
        indexTournaments();
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
            tournament.setSize(tournaments.get(i).getAsJsonObject().get("tournament").getAsJsonObject().get("participants_count").getAsInt());
            tournamentList.add(0, tournament);
        }

        for (int i = 0; i < tournamentList.size(); i++) {
            nameList.add(tournamentList.get(i).getName());
        }


        ListView listView = findViewById(R.id.tournament_list);


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(this, R.layout.menu_list_item, nameList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(view.getContext(), Bracket.class);
            intent.putExtra("tournamentName", tournamentList.get(position).getName());
            intent.putExtra("tournamentURL", tournamentList.get(position).getUrl());
            intent.putExtra("tournamentType", tournamentList.get(position).getTournamentType());
            intent.putExtra("tournamentState", tournamentList.get(position).getState());
            intent.putExtra("tournamentSize", tournamentList.get(position).getSize());
            startActivity(intent);
            Log.d("TournamentName", String.valueOf(tournamentList.get(position).getName()));


        });

        listView.invalidate();
    }

    public void indexTournaments() {
        VolleyLog.DEBUG = true;

        RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, ChallongeRequests.tournamentsIndex(null),
                response -> {
                    Log.d("Response", response);
                    makeTournamentList(response);
                    Log.d("Request", " Request Received");
                }, error -> Log.d("Response", String.valueOf(error)));

        RequestQueueSingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

}
