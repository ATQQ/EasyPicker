package sugar.enums;

/**
 * 类型 课程  任务
 */
public enum courseType {
    COURSE(1),
    TASK(0);

    private int code;

    private courseType(int ncode){
        this.code=ncode;
    }

    public int getCode() {
        return code;
    }

}
