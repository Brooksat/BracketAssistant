package ferox.bracket;

public class match {
    int number;
    int p1Seed;
    int p2Seed;
    participant p1;
    participant p2;
    match previous1;
    match previous2;
    boolean P1Decided;
    boolean P2Decided;


    public match(int number, int p1Seed, int p2Seed, participant p1, participant p2, match previous1, match previous2, boolean P1Decided, boolean P2Decided) {
        super();
        this.number = number;
        this.p1Seed = p1Seed;
        this.p2Seed = p2Seed;
        this.p1 = p1;
        this.p2 = p2;
        this.previous1 = previous1;
        this.previous2 = previous2;
        this.P1Decided = P1Decided;
        this.P2Decided = P2Decided;
    }

    public match() {
        number = 0;
        p1Seed = 0;
        p2Seed = 0;
        p1 = null;
        p2 = null;
        previous1 = null;
        previous2 = null;
        P1Decided = false;
        P2Decided = false;

    }

    public match(int number, participant p1, participant p2, match previous1, match previous2) {
        this.number = number;
        this.p1 = p1;
        this.p2 = p2;
        this.previous1 = previous1;
        this.previous2 = previous2;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getP1Seed() {
        return p1Seed;
    }

    public void setP1Seed(int p1Seed) {
        this.p1Seed = p1Seed;
    }

    public int getP2Seed() {
        return p2Seed;
    }

    public void setP2Seed(int p2Seed) {
        this.p2Seed = p2Seed;
    }

    public participant getP1() {
        return p1;
    }

    public void setP1(participant p1) {
        this.p1 = p1;
    }

    public participant getP2() {
        return p2;
    }

    public void setP2(participant p2) {
        this.p2 = p2;
    }

    public match getPrevious1() {
        return previous1;
    }

    public void setPrevious1(match previous1) {
        this.previous1 = previous1;
    }

    public match getPrevious2() {
        return previous2;
    }

    public void setPrevious2(match previous2) {
        this.previous2 = previous2;
    }

    public boolean isP1Decided() {
        return P1Decided;
    }

    public void setP1Decided(boolean p1Decided) {
        P1Decided = p1Decided;
    }

    public boolean isP2Decided() {
        return P2Decided;
    }

    public void setP2Decided(boolean p2Decided) {
        P2Decided = p2Decided;
    }
}
