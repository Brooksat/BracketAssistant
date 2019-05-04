package ferox.bracket;

import java.util.ArrayList;

public interface VolleyCallback {

    void onSuccess(String response);

    void onErrorResponse(ArrayList errorList);
}

