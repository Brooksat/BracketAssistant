package ferox.bracket;

import java.util.ArrayList;

public class Round {
    String name;
    int number;
    boolean isInWinners;
    ArrayList<Match> matchList;

    public Round(String name, int number, boolean isInWinners, ArrayList<Match> matchList) {
        this.name = name;
        this.number = number;
        this.isInWinners = isInWinners;
        this.matchList = matchList;
    }


    public Round() {
        this.name = "";
        this.number = 0;
        this.isInWinners = true;
        matchList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(ArrayList<Match> matchList) {
        this.matchList = matchList;
    }
}
