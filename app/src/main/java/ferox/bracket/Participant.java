package ferox.bracket;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Participant {

    final static String NAME = "participant[name]=";
    final static String SEED = "participant[seed]=";

    @SerializedName("id")
    int id;
    @SerializedName("seed")
    private int seed;
    @SerializedName("display_name")
    private String name;
    private String tournamentID;

    public Participant() {
        name = "";
        tournamentID = "";
    }


    public Participant(String name, int seed) {
        this.name = name;
        this.seed = seed;
    }


    /**
     * When deserializing JSON sets any JsonNull fields to their default values, therefore settings
     * string to null, this methods changes String fields to ""
     */
    public void undoJsonShenanigans() {
        if (name == null) {
            name = "";
        }
        if (tournamentID == null) {
            tournamentID = "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getSettings() {
        String settings = "";
        try {
            if (getName() != null) {

                settings += "&" + NAME + URLEncoder.encode(getName(), StandardCharsets.UTF_8.toString());
            }
            if (getSeed() > 0) {
                settings += "&" + SEED + URLEncoder.encode(String.valueOf(getSeed()), StandardCharsets.UTF_8.toString());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return settings;
    }
}
