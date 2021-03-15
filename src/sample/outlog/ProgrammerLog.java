package sample.outlog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sample.tool.Utils;
import sample.outlog.enumfile.logType;
import sample.outlog.enumfile.outputLevel;

/**
 * 其他日志
 *
 * @author 杨佳颖
 */
public class ProgrammerLog implements OutPutLog{

    private Log log = LogFactory.getLog("programmer");

    private static String LogType = logType.OTHER.getType();

    @Override
    public String sucess(String message) {
        String msg = Utils.timeConversion()+" "+LogType + "  " + outputLevel.INFO.getInfo() + ":" + message + "</br>";
        log.info(msg);
        return msg.replace("</br>", "\n");
    }


    @Override
    public String error(String message,Exception e) {
        String msg = Utils.timeConversion()+" "+LogType + "  " + outputLevel.ERROR.getInfo() + ":" + message + "</br>";
        log.error(msg,e);
        return msg.replace("</br>", "\n");
    }
}
