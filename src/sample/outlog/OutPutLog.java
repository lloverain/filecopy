package sample.outlog;

public interface OutPutLog {
    String sucess(String text);
    String error(String message, Exception e);
}
