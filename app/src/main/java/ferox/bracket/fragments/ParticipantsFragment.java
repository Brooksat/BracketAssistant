package ferox.bracket.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Objects;

import ferox.bracket.CustomClass.ChallongeRequests;
import ferox.bracket.CustomClass.CustomLinearLayoutManager;
import ferox.bracket.CustomClass.RecyclerViewAdapter;
import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Participant;
import ferox.bracket.Tournament.Tournament;


public class ParticipantsFragment extends Fragment {

    private static final String TAG = "ParticipantsFragment";
    String api_key = "hyxStYdr5aFDRNHEHscBgrzKGXCgNFp4GWfErw07";

    String url;

    ImageButton participantsOptions;
    Tournament tournament;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    // ArrayList<String> playerSeeds = new ArrayList<>();
    ArrayList<Participant> players = new ArrayList<>();

    DefaultItemAnimator defaultItemAnimator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        tournament = Objects.requireNonNull(intent.getExtras()).getParcelable("tournament");
        assert tournament != null;
        if (!TextUtils.isEmpty(tournament.getSubdomain())) {
            url = tournament.getSubdomain() + "-" + tournament.getUrl();
        } else {
            url = tournament.getUrl();
        }
        View v = inflater.inflate(R.layout.fragment_participants, container, false);
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        participantsOptions = v.findViewById(R.id.menu);
        participantsOptions.setOnClickListener(v1 -> {

            PopupMenu popupMenu = new PopupMenu(getContext(), participantsOptions);
            popupMenu.getMenuInflater().inflate(R.menu.participants_fragments_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Add": {

                        makeAddParticipantDialog();

                        break;
                    }
                    case "Shuffle": {
                        makeShuffleDialog();


                        break;
                    }
                    case "Refresh": {
                        refresh();
                        break;
                    }
                }
                return true;
            });
            popupMenu.show();
        });


        defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);

        adapter = new RecyclerViewAdapter(getContext(), players, linearLayoutManager, defaultItemAnimator);

        adapter.setTournament(tournament);
        recyclerView = v.findViewById(R.id.participant_list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.getHelper().attachToRecyclerView(recyclerView);


        if (!tournament.isSearched()) {
            sendParticipantIndexRequest();
        } else {
            participantsOptions.setClickable(false);
        }
        return v;
    }


    public void refresh() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                tournament = new Gson().fromJson(new JsonParser().parse(response).getAsJsonObject().get("tournament"), Tournament.class);
                tournament.undoJsonShenanigans();
                adapter.setTournament(tournament);
                sendParticipantIndexRequest();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {

            }
        }, ChallongeRequests.tournamentShow(tournament.getId()));
    }

    private void sendParticipantIndexRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                adapter.initPlayerList(response);
            }

            @Override
            public void onErrorResponse(ArrayList errorResponse) {
                Log.d("RequestError", errorResponse.toString());
            }
        }, ChallongeRequests.participantIndex(url));
    }


//    public void initPlayerList(String jsonString) {
//
//        players.clear();
//
//        JsonParser jsonParser = new JsonParser();
//        JsonArray participants = jsonParser.parse(jsonString).getAsJsonArray();
//
//
//        for (JsonElement participant : participants) {
//
//            JsonObject participantObject = participant.getAsJsonObject().get("participant").getAsJsonObject();
//            Participant player = new Gson().fromJson(participantObject, Participant.class);
//            players.add(player);
//           // playerSeeds.add(String.valueOf(player.getSeed()));
//
//        }
//
//        adapter.notifyDataSetChanged();
//    }

    //TODO disable delete and other function if tournament has ended
    //TODO add invite by challonge username

    private void makeAddParticipantDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Paricipant");
        View dialogueLayout = getLayoutInflater().inflate(R.layout.fragment_participant_add_dialog, null);
        LinearLayout addDialogLayout = dialogueLayout.findViewById(R.id.participant_add_dialog_layout);
        EditText nameText = dialogueLayout.findViewById(R.id.new_participant_name);
        EditText seedText = dialogueLayout.findViewById(R.id.new_participant_seed);
        LinearLayout errorsLayout = dialogueLayout.findViewById(R.id.participant_add_dialog_error_layout);
        builder.setView(dialogueLayout);
        builder.setPositiveButton("Add", (dialog1, which) -> {
        });
        builder.setNegativeButton("Cancel", (dialog1, which) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                Participant player = new Participant();
                player.setName(nameText.getText().toString());
                try {
                    player.setSeed(Integer.parseInt(seedText.getText().toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(ParticipantsFragment.this.getContext(), "Not an Integer", Toast.LENGTH_SHORT).show();
                }

                ChallongeRequests.sendRequest(new VolleyCallback() {
                    @Override
                    public void onSuccess(String response) {
                        dialog.dismiss();
                        ChallongeRequests.sendRequest(new VolleyCallback() {
                            @Override
                            public void onSuccess(String response) {

                                adapter.initPlayerList(response);

                            }

                            @Override
                            public void onErrorResponse(ArrayList errorResponse) {

                            }
                        }, ChallongeRequests.participantIndex(url));
                    }

                    @Override
                    public void onErrorResponse(ArrayList errorList) {
                        errorsLayout.removeAllViews();
                        for (int i = 0; i < errorList.size(); i++) {
                            TextView error = (TextView) getLayoutInflater().inflate(R.layout.menu_spinner_item, null);
                            error.setText(String.valueOf(errorList.get(i)));
                            error.setSelected(true);
                            error.setTextColor(Color.RED);
                            errorsLayout.addView(error);
                        }
                    }
                }, ChallongeRequests.participantCreate(url, player));
            });
        });
        dialog.show();
    }

    private void makeShuffleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Shuffle Participants");
        builder.setPositiveButton("OK", (dialog, which) -> {
            ChallongeRequests.sendRequest(new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    ChallongeRequests.sendRequest(new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            adapter.initPlayerList(response);
                        }

                        @Override
                        public void onErrorResponse(ArrayList errorResponse) {
                            Log.d("RequestError", errorResponse.toString());
                        }
                    }, ChallongeRequests.participantIndex(url));
                }

                @Override
                public void onErrorResponse(ArrayList errorResponse) {

                }
            }, ChallongeRequests.participantRandomize(url));


        })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .create().show();
    }



}
