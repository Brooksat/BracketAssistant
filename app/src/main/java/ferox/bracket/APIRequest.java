package ferox.bracket;

public class APIRequest {
    private String url;
    private int requestMethodType;

    public APIRequest(String url, int requestMethodType) {
        this.url = url;
        this.requestMethodType = requestMethodType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRequestMethodType() {
        return requestMethodType;
    }

    public void setRequestMethodType(int requestMethodType) {
        this.requestMethodType = requestMethodType;
    }
}
