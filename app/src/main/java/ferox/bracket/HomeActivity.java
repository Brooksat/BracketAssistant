package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";
    ArrayList<Tournament> tournamentList;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tournamentList = new ArrayList<>();
        nameList = new ArrayList<>();
        //TODO add in subdomain functionality
        ChallongeRequests.sendRequest(response -> makeTournamentList(response), ChallongeRequests.tournamentsIndex(null));

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
            tournament.setType(tournaments.get(i).getAsJsonObject().get("tournament").getAsJsonObject().get("tournament_type").getAsString());
            tournament.setParticipantCount(tournaments.get(i).getAsJsonObject().get("tournament").getAsJsonObject().get("participants_count").getAsInt());
            tournamentList.add(0, tournament);
        }

        for (int i = 0; i < tournamentList.size(); i++) {
            nameList.add(tournamentList.get(i).getName());
        }


        ListView listView = findViewById(R.id.tournament_list);


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(this, R.layout.menu_list_item, nameList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("TAG", tournamentList.get(position).toString());
            Intent intent = new Intent(view.getContext(), Bracket.class);
            //TODO only need url , anything else can be set using that
            intent.putExtra("tournamentName", tournamentList.get(position).getName());
            intent.putExtra("tournamentURL", tournamentList.get(position).getUrl());
            intent.putExtra("tournamentType", tournamentList.get(position).getType());
            intent.putExtra("tournamentState", tournamentList.get(position).getState());
            intent.putExtra("tournamentSize", tournamentList.get(position).getParticipantCount());
            startActivity(intent);
            Log.d("TournamentName", String.valueOf(tournamentList.get(position).getName()));


        });

        listView.invalidate();
    }



}
