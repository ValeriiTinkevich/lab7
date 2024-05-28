package common.interaction;

public class ResponseAuth extends Response{
    Integer userID;

    public ResponseAuth(ResponseResult responseResult, String responseBody, Integer userID) {
        super(responseResult, responseBody);
        this.userID = userID;

    }
    public ResponseAuth(ResponseResult responseResult, String responseBody) {
        super(responseResult, responseBody);
    }

    public Integer getUserID() {
        return userID;
    }
}
