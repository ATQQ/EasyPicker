package sugar.enums;

public enum commonStatus {
    SUCCESS(1),
    FAIL(0),
    ERROR(-1);

    private int code;

    private commonStatus(int ncode){
        this.code=ncode;
    }

    public int getCode(){
        return code;
    }
}
