package vn.java.dto.response;

public class FailureResponse extends DataResponse<Object> {

    public FailureResponse(int status, String message, Object data) {
        super(status, message, data);
    }

    public FailureResponse(int status, String message) {
        super(status, message);
    }
}
