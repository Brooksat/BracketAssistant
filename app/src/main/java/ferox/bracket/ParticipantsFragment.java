package ferox.bracket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;


public class ParticipantsFragment extends Fragment {

    private static final String TAG = "ParticipantsFragment";
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    String url;


    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ItemTouchHelper helper;
    ArrayList<String> playerSeeds = new ArrayList<>();
    ArrayList<Participant> players = new ArrayList<>();
    ChallongeRequests CR = new ChallongeRequests(api_key);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View v = inflater.inflate(R.layout.fragment_participants, container, false);


        recyclerView = v.findViewById(R.id.participant_list);

        helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int movedPosition = viewHolder.getAdapterPosition();
                int targetPostition = target.getAdapterPosition();

                //Collections.swap(playerSeeds, movedPosition, targetPostition);
                Collections.swap(players, movedPosition, targetPostition);

                adapter.notifyItemMoved(movedPosition, targetPostition);
                adapter.notifyItemChanged(targetPostition);
                adapter.notifyItemChanged(movedPosition);

                return false;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                int viewHolderPos = viewHolder.getAdapterPosition();

                if ((viewHolderPos + 1) != players.get(viewHolderPos).getSeed()) {
                    updateParticipantSeed(url, String.valueOf(players.get(viewHolderPos).getId()), viewHolderPos + 1);
                }


            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

        });

        helper.attachToRecyclerView(recyclerView);

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

    public void updateParticipantSeed(String tournamentURL, String participantId, int seed) {
        VolleyLog.DEBUG = true;

        RequestQueue queue = RequestQueueSingleton.getInstance(this.getContext().getApplicationContext()).
                getRequestQueue();


        StringRequest stringRequest = new StringRequest(Request.Method.PUT, CR.participantUpdate(tournamentURL, participantId, seed),
                response -> {
                    Log.d("ParticipantUpdate", response);
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

            player.setId(participantObject.get("id").getAsInt());
            player.setName(participantObject.get("name").getAsString());
            player.setSeed(participantObject.get("seed").getAsInt());
            players.add(player);
            playerSeeds.add(String.valueOf(player.getSeed()));

        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewAdapter(this.getContext(), playerSeeds, players);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
