package sample.outlog.enumfile;

/**
 * 输出级别
 * @author 杨佳颖
 */
public enum outputLevel {
    INFO(1,"信息"),
    DEBUG(2,"bug"),
    ERROR(3,"错误");

    private int code;
    private String info;

    outputLevel(int code, String info) {
        this.code = code;
        this.info = info;
    }


    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

}
