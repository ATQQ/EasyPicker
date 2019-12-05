package sugar.enums;

public enum userStatus {
    SUCCESS (1),
    PWDFAULT(0),
    NOTEXIST(-1);

    private int code;

    private userStatus(int ncode){
        this.code=ncode;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "userStatus{" +
                "code=" + code +
                '}';
    }
}
