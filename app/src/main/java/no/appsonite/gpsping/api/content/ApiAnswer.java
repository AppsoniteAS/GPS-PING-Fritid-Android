package no.appsonite.gpsping.api.content;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 20.01.2016
 */
public class ApiAnswer {
    private String status;
    private String error;

    public boolean isSuccess() {
        return "ok".equals(status);
    }

    public boolean isError() {
        return !isSuccess();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
