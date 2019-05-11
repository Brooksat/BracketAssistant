package ferox.bracket.Tournament;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Round {
    @SerializedName("title")
    String title;
    @SerializedName("number")
    int number;
    boolean isGrandFinals;
    boolean isWinners;
    ArrayList<Match> matchList;

    public Round(String title, int number, boolean isGrandFinals, ArrayList<Match> matchList) {
        this.title = title;
        this.number = number;
        this.isGrandFinals = isGrandFinals;
        this.matchList = matchList;
    }


    public Round() {
        this.title = "";
        this.number = 0;
        this.isGrandFinals = true;
        matchList = new ArrayList<>();
    }

    /**
     * When deserializing JSON sets any JsonNull fields to their default values, therefore settings
     * string to null, this methods changes String fields to ""
     */
    public void undoJsonShenanigans() {
        if (title == null) {
            title = "";
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isGrandFinals() {
        return isGrandFinals;
    }

    public void setGrandFinals(boolean grandFinals) {
        isGrandFinals = grandFinals;
    }

    public boolean isWinners() {
        return isWinners;
    }

    public void setIsWinners(boolean winners) {
        isWinners = winners;
    }
}
