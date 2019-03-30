package ferox.bracket;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ParticipantSettings extends Participant {

    final static String NAME = "participant[name]=";
    final static String SEED = "participant[seed]=";

    ParticipantSettings() {
        super();
    }

    public ParticipantSettings(String name, int seed) {
        super(name, seed);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public int getSeed() {
        return super.getSeed();
    }

    @Override
    public void setSeed(int seed) {
        super.setSeed(seed);
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
