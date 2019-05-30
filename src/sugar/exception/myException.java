package sugar.exception;
/*
 *@auther suger
 *2019
 *17:20
 */

public class myException extends Exception{
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public myException(String msg){
        this.msg=msg;
    }
}
