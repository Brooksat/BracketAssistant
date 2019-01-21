package ferox.bracket;

public class Tournament {
    private String name;
    private String url;

    public Tournament() {
        this.name = "Default Name";
        this.url = "Defautl URL";
    }

    public Tournament(String name, String URL) {
        this.name = name;
        this.url = URL;
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
}
