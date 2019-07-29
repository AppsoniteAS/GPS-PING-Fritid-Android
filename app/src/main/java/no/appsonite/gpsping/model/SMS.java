package no.appsonite.gpsping.model;

import java.io.Serializable;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 25.01.2016
 */
public class SMS implements Serializable {
    private String number;
    private String message;

    public SMS(String number, String message) {
        this.number = number;
        this.message = message;
    }

    public SMS(){}

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMS sms = (SMS) o;

        if (!number.equals(sms.number)) return false;
        return message.equals(sms.message);

    }

    @Override
    public int hashCode() {
        int result = number.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
