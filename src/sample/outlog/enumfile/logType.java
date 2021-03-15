package sample.outlog.enumfile;

/**
 * 日志分类
 * @author 杨佳颖
 */

public enum logType {
    OTHER(0,"员日志"),
    DB(5,"日志");

    private int code;
    private String type;

    logType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

}
