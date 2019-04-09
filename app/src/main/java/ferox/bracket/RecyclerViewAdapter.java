package ferox.bracket;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    ItemTouchHelper helper;
    LinearLayoutManager linearLayoutManager;
    DefaultItemAnimator defaultItemAnimator;

    Context mContext;
    ArrayList<String> seeds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Participant> players;

    AlertDialog.Builder builder;


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

                swapSeed(players.get(movedPosition), players.get(targetPostition));
                Collections.swap(players, movedPosition, targetPostition);

                notifyItemMoved(movedPosition, targetPostition);
                notifyItemChanged(targetPostition);
                notifyItemChanged(movedPosition);

                return true;
            }


            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {


                if (viewHolder.getAdapterPosition() > -1) {
                    Participant player = players.get(viewHolder.getAdapterPosition());
                    //TODO handle time errors and add drag down to refresh list to make sure list is accurate

                    CustomViewHolder cvh = (CustomViewHolder) viewHolder;

                    if (player.getSeed() != (cvh.initialListPos + 1)) {
                        player.setSeed(viewHolder.getAdapterPosition() + 1);
                        ParticipantSettings settings = new ParticipantSettings();
                        settings.setSeed(player.getSeed());
                        ChallongeRequests.sendRequest(response -> {
                        }, ChallongeRequests.participantUpdate(player.getTournamentID(), String.valueOf(player.getId()), settings));
                    }
                }
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Participant player = players.get(position);
//                players.remove(position);
//                notifyItemRemoved(position);

                new AlertDialog.Builder(viewHolder.itemView.getContext()).setMessage("Delete participant?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            ChallongeRequests.sendRequest(response -> {
                                    }
                                    , ChallongeRequests.participantDestroy(String.valueOf(player.getTournamentID()), String.valueOf(player.getId())));

                            players.remove(position);
                            notifyItemRemoved(position);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            defaultItemAnimator.setSupportsChangeAnimations(true);
                            notifyItemChanged(position, null);
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

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_participant_list_item, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {


        holder.participantSeedView.setText(String.valueOf(players.get(position).getSeed()));
        holder.participantNameView.setText(players.get(position).getName());
        holder.participantDragHandle.setOnTouchListener((v, event) -> {

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                //defaultitemanimator randomly breaks when scroll but setting this parameter to false seems to fix the issue
                holder.initialListPos = holder.getAdapterPosition();
                defaultItemAnimator.setSupportsChangeAnimations(false);
                helper.startDrag(holder);
            }

            return false;
        });


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
                    ParticipantSettings settings = new ParticipantSettings();
                    settings.setName(player.getName());
                    ChallongeRequests.sendRequest(response -> {
                    }, ChallongeRequests.participantUpdate(player.getTournamentID(), String.valueOf(player.getId()), settings));
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

    public ArrayList<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(ArrayList<String> seeds) {
        this.seeds = seeds;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<Participant> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Participant> players) {
        this.players = players;
    }
}
