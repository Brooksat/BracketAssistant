package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class ParticipantsFragment extends Fragment {

    private static final String TAG = "ParticipantsFragment";
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    String url;

    RecyclerView recyclerView;
    ArrayList<String> playerSeeds = new ArrayList<>();
    ArrayList<String> playerNames = new ArrayList<>();
    ChallongeRequests CR = new ChallongeRequests(api_key);
    ArrayList<Participant> playerList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_participants, container, false);


        recyclerView = v.findViewById(R.id.participant_list);

        url = intent.getStringExtra("tournamentURL");
        showTournament();
        return v;
    }


    public void showTournament() {
        VolleyLog.DEBUG = true;

        RequestQueue queue = RequestQueueSingleton.getInstance(this.getContext().getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, CR.tournamentShow(url),
                response -> {
                    Log.d("Response", response);
                    initPlayerList(response);
                    Log.d("Request", " Request Received");
                }, error -> Log.d("Response", String.valueOf(error)));

        RequestQueueSingleton.getInstance(this.getContext()).addToRequestQueue(stringRequest);

    }

    public void initPlayerList(String jsonString) {

        JsonParser jsonParser = new JsonParser();
        JsonElement tournament = jsonParser.parse(jsonString);
        JsonArray participants = tournament.getAsJsonObject().get("tournament").getAsJsonObject().get("participants").getAsJsonArray();

        for (JsonElement participant : participants) {
            Participant player = new Participant();
            JsonObject participantObject = participant.getAsJsonObject().get("participant").getAsJsonObject();

            player.setId(participantObject.get("tournament_id").getAsInt());
            player.setName(participantObject.get("name").getAsString());
            player.setSeed(participantObject.get("seed").getAsInt());
            playerList.add(player);
            playerNames.add(player.getName());
            playerSeeds.add(String.valueOf(player.getSeed()));

        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this.getContext(), playerSeeds, playerNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
