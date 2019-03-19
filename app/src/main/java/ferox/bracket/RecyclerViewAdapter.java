package ferox.bracket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";


    Context mContext;
    ArrayList<String> seeds = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<Participant> players;

    public RecyclerViewAdapter(Context mContext, ArrayList<Participant> players) {
        this.mContext = mContext;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_participant_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.participantSeedView.setText(String.valueOf(players.get(position).getSeed()));
        holder.participantNameView.setText(players.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView participantSeedView;
        TextView participantNameView;
        ImageButton participantEditView;
        ImageButton participantDeleteView;
        ConstraintLayout participantListItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            participantSeedView = itemView.findViewById(R.id.participant_seed);
            participantNameView = itemView.findViewById(R.id.participants_list_name);
            participantEditView = itemView.findViewById(R.id.participant_edit);
            participantDeleteView = itemView.findViewById(R.id.participant_delete);
            participantListItemLayout = itemView.findViewById(R.id.participant_parent_layout);
        }
    }
}
