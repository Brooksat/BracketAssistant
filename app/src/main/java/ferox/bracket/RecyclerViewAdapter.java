package ferox.bracket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    ItemTouchHelper helper;
    LinearLayoutManager linearLayoutManager;




    Context mContext;
    ArrayList<String> seeds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Participant> players;


    public RecyclerViewAdapter(Context mContext, ArrayList<Participant> players, LinearLayoutManager linearLayoutManager) {
        this.mContext = mContext;
        this.players = players;
        this.linearLayoutManager = linearLayoutManager;

        this.helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

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
            public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {

                return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);

            }

            //            @Override
//            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                super.clearView(recyclerView, viewHolder);
//
//                int viewHolderPos = viewHolder.getAdapterPosition();
//
//                if ((viewHolderPos + 1) != players.get(viewHolderPos).getSeed()) {
//                    updateParticipantSeed(url, String.valueOf(players.get(viewHolderPos).getId()), viewHolderPos + 1);
//                }
//
//
//            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
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
                helper.startDrag(holder);
            }
//            switch (event.getActionMasked()) {
//
//
//                case (MotionEvent.ACTION_DOWN): {
//                    holder.participantListItemLayout.requestDisallowInterceptTouchEvent(true);
//                    helper.startDrag(holder);
//                    break;
//                }
//                case (MotionEvent.ACTION_UP): {
//                    holder.participantListItemLayout.requestDisallowInterceptTouchEvent(false);
//                }
//            }
            return false;
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
        ImageButton participantDeleteView;
        ConstraintLayout participantListItemLayout;

        public CustomViewHolder(View itemView) {
            super(itemView);
            participantDragHandle = itemView.findViewById(R.id.participant_drag_handle);
            participantSeedView = itemView.findViewById(R.id.participant_seed);
            participantNameView = itemView.findViewById(R.id.participants_list_name);
            participantEditView = itemView.findViewById(R.id.participant_edit);
            participantDeleteView = itemView.findViewById(R.id.participant_delete);
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
