package vn.java.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DataResponse<T> implements Serializable {
    private final int status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private transient T data;

    /**
     * Response data for the API to retrieve data successfully. For GET, POST only
     * @param status
     * @param message
     * @param data
     */
    public DataResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Response data when the API executes successfully or getting error. For PUT, PATCH, DELETE
     * @param status
     * @param message
     */
    public DataResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
