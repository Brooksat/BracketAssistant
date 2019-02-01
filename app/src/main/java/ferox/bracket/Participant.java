package ferox.bracket;

public class Participant {

    private String name;
    private int seed;

    public Participant() {
        this.name = "Undecided";
        this.seed = 0;
    }

    public Participant(String name, int seed) {
        this.name = name;
        this.seed = seed;
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

}
