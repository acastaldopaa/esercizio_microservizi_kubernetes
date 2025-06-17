package it.paa.ordini.response;

public class SuccessResponse extends ApiResponse {
    public String status;
    public String message;
    public Object data;

    public SuccessResponse(String status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    /*public SuccessResponse(String status, String message, Object data) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
    }*/
}