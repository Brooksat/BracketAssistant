package ferox.bracket.CustomClass;

import com.google.gson.JsonArray;

/*
Convenience class for ordering matches_by_round fields in a useful way
 */
public class StringJsonArrayPair {
    String name;
    JsonArray jsonArr;

    public StringJsonArrayPair(String name, JsonArray jsonArr) {
        this.name = name;
        this.jsonArr = jsonArr;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonArray getJsonArr() {
        return jsonArr;
    }

    public void setJsonArr(JsonArray jsonArr) {
        this.jsonArr = jsonArr;
    }
}


