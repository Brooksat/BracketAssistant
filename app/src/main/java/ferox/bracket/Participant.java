package ferox.bracket;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Participant {

    final static String NAME = "participant[name]=";
    final static String SEED = "participant[seed]=";

    int id;
    private String name;
    private int seed;
    private String tournamentID;

    public Participant() {

    }


    public Participant(String name, int seed) {
        this.name = name;
        this.seed = seed;
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
