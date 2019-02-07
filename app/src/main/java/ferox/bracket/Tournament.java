package ferox.bracket;

public class Tournament {
    private String name;
    private String url;
    private String tournamentType;
    String state;
    private int size;

    public Tournament() {
        this.name = "Default Name";
        this.url = "Defautl URL";
        this.tournamentType = "Default Type";
        this.state = "Default State";
    }

    public Tournament(String name, String URL, String type, String state, int participants_count) {
        this.name = name;
        this.url = URL;
        this.tournamentType = type;
        this.state = state;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
