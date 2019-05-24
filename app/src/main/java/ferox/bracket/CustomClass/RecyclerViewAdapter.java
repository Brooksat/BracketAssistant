package ferox.bracket.CustomClass;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;

import ferox.bracket.Interface.VolleyCallback;
import ferox.bracket.R;
import ferox.bracket.Tournament.Participant;
import ferox.bracket.Tournament.Tournament;

//TODO need to disable seed switching if invalid api

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ItemTouchHelper helper;
    private LinearLayoutManager linearLayoutManager;
    private DefaultItemAnimator defaultItemAnimator;

    private Context mContext;

    private ArrayList<Participant> players;

    private AlertDialog.Builder builder;
    Tournament tournament;
    RecyclerView recyclerView;


    public RecyclerViewAdapter(Context mContext, ArrayList<Participant> players, LinearLayoutManager linearLayoutManager, DefaultItemAnimator defaultItemAnimator) {

        this.mContext = mContext;
        builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("Edit Participant Name");

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


        this.players = players;
        this.linearLayoutManager = linearLayoutManager;
        this.defaultItemAnimator = defaultItemAnimator;
        this.helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int movedPosition = viewHolder.getAdapterPosition();
                int targetPostition = target.getAdapterPosition();

                ((CustomViewHolder) target).initialListPos = viewHolder.getAdapterPosition();

                swapSeed(players.get(movedPosition), players.get(targetPostition));
                Collections.swap(players, movedPosition, targetPostition);

                notifyItemMoved(movedPosition, targetPostition);
                notifyItemChanged(targetPostition);
                notifyItemChanged(movedPosition);

                return true;
            }


            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {


                //defaultItemAnimator.setSupportsChangeAnimations(true);
                if (viewHolder.getAdapterPosition() > -1) {
                    Participant player = players.get(viewHolder.getAdapterPosition());
                    //TODO add drag down to refresh list to make sure list is accurate

                    CustomViewHolder cvh = (CustomViewHolder) viewHolder;

                    //changes player seed if viewholder is dropped in a different position than before
                    if (player.getSeed() != (cvh.initialListPos + 1)) {
                        //Toast.makeText(getmContext(), player.getSeed() + "-" + (cvh.initialListPos + 1), Toast.LENGTH_SHORT).show();
                        player.setSeed(viewHolder.getAdapterPosition() + 1);
                        cvh.initialListPos = viewHolder.getAdapterPosition();

                        ChallongeRequests.sendRequest(new VolleyCallback() {
                                                          @Override
                                                          public void onSuccess(String response) {

                                                          }

                                                          @Override
                                                          public void onErrorResponse(ArrayList errorResponse) {
                                                              for (int i = 0; i < errorResponse.size(); i++) {
                                                                  Toast.makeText(mContext, String.valueOf(errorResponse.get(i)), Toast.LENGTH_SHORT).show();
                                                              }
                                                          }
                                                      },
                                ChallongeRequests.participantUpdate(player));
                    } else {
                        //Toast.makeText(getmContext(), player.getSeed() + "-" + (cvh.initialListPos + 1), Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                defaultItemAnimator.setSupportsChangeAnimations(true);
                int position = viewHolder.getAdapterPosition();
                Participant player = players.get(position);
                String dialogTitle = "Remove participant?";
                if (tournament.hasStarted()) {
                    dialogTitle = "Disqualify participant?";
                }

                new AlertDialog.Builder(viewHolder.itemView.getContext()).setMessage(dialogTitle)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            sendParticipantDeleteRequest(player, position);

                        })
                        .setNegativeButton("No", (dialog, which) -> {

                            notifyItemChanged(position);
                        })
                        .create().show();

            }


            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .7f;
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return super.getSwipeEscapeVelocity(3f * defaultValue);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });


    }


    private void sendTournamentShowRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                tournament = new Gson().fromJson(new JsonParser().parse(response).getAsJsonObject().get("tournament"), Tournament.class);
                tournament.undoJsonShenanigans();
                notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(ArrayList errorList) {

            }
        }, ChallongeRequests.tournamentShow(tournament.getId()));
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }


    private void sendParticipantDeleteRequest(Participant participant, int position) {
        if (!tournament.getState().equals(Tournament.COMPLETE)) {
            ChallongeRequests.sendRequest(new VolleyCallback() {
                                              @Override
                                              public void onSuccess(String response) {
                                                  //                                              players.remove(position);
                                                  //                                              notifyItemRemoved(position);
                                                  //                                              notifyItemRangeChanged(position, getItemCount());
                                                  //                                              for (int i=position;i<players.size();i++){
                                                  //                                                  players.get(i).setSeed(players.get(i).getSeed()-1);
                                                  //                                              }
                                                  sendParticipantIndexRequest();

                                              }

                                              @Override
                                              public void onErrorResponse(ArrayList errorResponse) {
                                                  notifyItemChanged(position);
                                              }
                                          },
                    ChallongeRequests.participantDestroy(participant));
        } else {
            Toast.makeText(mContext, "Tournament has already ended, cannot remove", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendParticipantIndexRequest() {
        ChallongeRequests.sendRequest(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                initPlayerList(response);
            }

            @Override
            public void onErrorResponse(ArrayList errorResponse) {
                Log.d("RequestError", errorResponse.toString());
            }
        }, ChallongeRequests.participantIndex(tournament.getId()));
    }

    public void initPlayerList(String jsonString) {
        // Toast.makeText(mContext, "initplayerList called", Toast.LENGTH_SHORT).show();
        // Toast.makeText(mContext, tournament.getState() + " - " + tournament.hasStarted(), Toast.LENGTH_SHORT).show();
        players.clear();

        JsonParser jsonParser = new JsonParser();
        JsonArray participants = jsonParser.parse(jsonString).getAsJsonArray();


        for (JsonElement participant : participants) {

            JsonObject participantObject = participant.getAsJsonObject().get("participant").getAsJsonObject();
            Participant player = new Gson().fromJson(participantObject, Participant.class);
            players.add(player);
            // playerSeeds.add(String.valueOf(player.getSeed()));

        }
        defaultItemAnimator.setSupportsChangeAnimations(true);
        notifyDataSetChanged();
        //if (recyclerView!=null){
        recyclerView.requestLayout();
        //}
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_participant_list_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.participantSeedView.setText(String.valueOf(position + 1));
        holder.participantNameView.setText(players.get(position).getName());
        if (!tournament.hasStarted()) {
            holder.participantDragHandle.setImageTintList(holder.participantDragHandle.getResources().getColorStateList(R.color.menu_title));
            holder.participantDragHandle.setOnTouchListener((v, event) -> {


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        defaultItemAnimator.setSupportsChangeAnimations(false);
                        holder.initialListPos = position;
                        //Toast.makeText(getmContext(), String.valueOf(holder.initialListPos+1),Toast.LENGTH_SHORT).show();

                        helper.startDrag(holder);

                        break;
                    case MotionEvent.ACTION_UP:
                        defaultItemAnimator.setSupportsChangeAnimations(true);
                        break;

                }
                return false;
            });

            holder.participantListItemLayout.setOnTouchListener((v, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.initialListPos = position;
                        //Toast.makeText(getmContext(), String.valueOf(holder.initialListPos+1),Toast.LENGTH_SHORT).show();
                        break;
                    case MotionEvent.ACTION_UP:
                        defaultItemAnimator.setSupportsChangeAnimations(true);
                        break;
                }
                return false;
            });
        } else {
            holder.participantDragHandle.setOnTouchListener((v, event) -> {
                return false;
            });
            holder.participantListItemLayout.setOnTouchListener((v, event) -> {
                return false;
            });
            holder.participantDragHandle.setImageTintList(holder.participantDragHandle.getResources().getColorStateList(R.color.menu_background_light));
        }

        holder.participantEditView.setOnClickListener(v -> {


            EditText input = new EditText(mContext);
            input.setText(players.get(position).getName());
            input.setSelection(input.length());
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                Participant player = players.get(holder.getAdapterPosition());

                if (!player.getName().equals(input.getText().toString())) {
                    holder.participantNameView.setText(input.getText().toString());
                    player.setName(input.getText().toString());

                    ChallongeRequests.sendRequest(new VolleyCallback() {
                        @Override
                        public void onSuccess(String response) {

                        }

                        @Override
                        public void onErrorResponse(ArrayList errorResponse) {

                        }
                    }, ChallongeRequests.participantUpdate(player));
                }
            });
            builder.show();


        });


    }


    @Override
    public int getItemCount() {
        return players.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView participantDragHandle;
        TextView participantSeedView;
        TextView participantNameView;
        ImageButton participantEditView;
        ConstraintLayout participantListItemLayout;
        int initialListPos;

        public CustomViewHolder(View itemView) {
            super(itemView);
            participantDragHandle = itemView.findViewById(R.id.participant_drag_handle);
            participantSeedView = itemView.findViewById(R.id.participant_seed);
            participantNameView = itemView.findViewById(R.id.participants_list_name);
            participantEditView = itemView.findViewById(R.id.participant_edit);
            participantListItemLayout = itemView.findViewById(R.id.participant_parent_layout);


        }


    }


    public void swapSeed(Participant p1, Participant p2) {
        int tmp = p1.getSeed();
        p1.setSeed(p2.getSeed());
        p2.setSeed(tmp);
    }

    public static String getTAG() {
        return TAG;
    }

    public ItemTouchHelper getHelper() {
        return helper;
    }

    public void setHelper(ItemTouchHelper helper) {
        this.helper = helper;
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }


    public ArrayList<Participant> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Participant> players) {
        this.players = players;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
