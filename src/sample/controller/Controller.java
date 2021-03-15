package sample.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sample.Main;
import sample.tool.DownLoadImage;
import sample.tool.MysqlDBtool;
import sample.tool.SqliteDBtool;
import sample.tool.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * @author 杨佳颖
 * @Data 2020/12/4 15:17
 **/
public class Controller implements Initializable {

    private static Log logger = LogFactory.getLog("mysql");

    private static Log errLog = LogFactory.getLog("programmer");

    private float DownCount = 0f;//总下载量

    private float showNum;//下载完成数量

    public static ExecutorService pool;
    @FXML
    public Button startButton;//开始按钮
    @FXML
    public Button stopButton;//终止按钮

    private final String TABLENAME = "iamge";
    @FXML
    public TextField newpath;//保存本地路径根目录
    @FXML
    private VBox rootLayout;//根节点

    @FXML
    private ComboBox db;//数据库类型

    @FXML
    private TextField url;//数据库地址

    @FXML
    private TextField username;//数据库账号

    @FXML
    private TextField userpassword;//数据库密码

    @FXML
    private TextField port;//端口号

    @FXML
    private TextField name;//数据库名

    @FXML
    private TextArea sql;//sql语句

    @FXML
    private TextArea field;//字段

    @FXML
    private ProgressBar jindutiao;//进度条

    @FXML
    private Label jindutiaowenzi;//进度条文字

    @FXML
    public TextField xianchengshu;//线程数

    @FXML
    public Button FilepathButton;//文件路径按钮

    @FXML
    public TextField ip;//服务器ip

    @FXML
    public TextField tihuanweizhi;//替换位置

    @FXML
    public TextField tihuanneirong;//替换内容

    @FXML
    public TextArea log;//日志控件


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * 启动按钮
     */
    public void Start() {
        logger.info("任务开始");
        showNum = 0f;
        String dbType = db.getSelectionModel().selectedItemProperty().getValue().toString();
        String dburl = url.getText();
        String dbuser = username.getText();
        String dbpass = userpassword.getText();
        String dbport = port.getText();
        String dbname = name.getText();
        String dbsql = sql.getText();
        String dbfield = field.getText();
        String savepath = newpath.getText();
        String xcs = xianchengshu.getText();
        String serverIP = ip.getText();
        String replacementLocation = tihuanweizhi.getText();
        String replaceContent = tihuanneirong.getText();
        int threads = 2;
        int placeNum = 0;
        if (dburl == null || dburl.equals("")) {
            talk("url为空");
            return;
        }
        if (dbuser == null || dbuser.equals("")) {
            talk("用户名为空");
            return;
        }
        if (dbpass == null || dbpass.equals("")) {
            talk("密码为空");
            return;
        }
        if (dbport == null || dbport.equals("")) {
            talk("端口号为空");
            return;
        }
        if (dbname == null || dbname.equals("")) {
            talk("数据库名为空");
            return;
        }
        if (dbsql == null || dbsql.equals("")) {
            talk("sql语句为空");
            return;
        }
        if (dbfield == null || dbfield.equals("")) {
            talk("字段为空");
            return;
        }
        if (savepath == null || savepath.equals("")) {
            talk("保存路径为空");
            return;
        }

        if (serverIP == null || serverIP.equals("")) {
            talk("请输入IP");
            return;
        }
        if (replacementLocation == null || replacementLocation.equals("")) {
            talk("请输入替换位置");
            return;
        } else {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            if (pattern.matcher(replacementLocation).matches()) {
                placeNum = Integer.parseInt(replacementLocation);
            } else {
                talk("请填写整数");
                return;
            }
        }
        if (replaceContent == null || replaceContent.equals("")) {
            talk("请输入替换内容");
            return;
        }

        if (xcs == null || xcs.equals("")) {

        } else {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            if (pattern.matcher(xcs).matches()) {
                threads = Integer.parseInt(xcs);
            } else {
                talk("请填写整数");
                return;
            }
        }
        List<String> fieldList = Utils.getNameList(dbfield);//字段转为list
        Connection mysqlcon = null;//获取mysql连接
        try {
            mysqlcon = MysqlDBtool.getConnection(dbType, dburl, dbuser, dbpass, dbport, dbname);
        } catch (SQLException throwables) {
            errLog.error(throwables.getMessage());
            talk("错误，检查配置文件！");
        } catch (ClassNotFoundException e) {
            errLog.error(e.getMessage());
            talk("数据库驱动加载失败！");
        } catch (IllegalAccessException e) {
            errLog.error(e.getMessage());
            talk("非法访问异常！");
        } catch (InstantiationException e) {
            errLog.error(e.getMessage());
            talk("实例化异常！");
        }
        SqliteDBtool.init(TABLENAME, fieldList);//初始化本地数据库
        Connection sqlitecon = SqliteDBtool.getConnection();//获取sqlite连接
        PreparedStatement preparedStatement = null;//mysql
        ResultSet resultSet = null;//mysql
        Statement statement = null;//sqlite
        try {
            preparedStatement = mysqlcon.prepareStatement(dbsql);
            resultSet = preparedStatement.executeQuery();
            sqlitecon.setAutoCommit(false);//sqlite设置不自动提交
            statement = sqlitecon.createStatement();
            int num = 0;
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    String sql = SqliteDBtool.getSql(TABLENAME, fieldList, resultSet);//获取并生成sql语句
                    System.out.println(sql);
                    statement.addBatch(sql);
                    if (num % 5000 == 0) {
                        statement.executeBatch();
                    }
                    num++;
                }
                statement.executeBatch();
                sqlitecon.commit();
            } else {
                System.out.println("无数据");
            }

        } catch (SQLException throwables) {
            errLog.error(throwables.getMessage());
            talk("错误，请检查错误日志");
            try {
                sqlitecon.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } finally {
            MysqlDBtool.close(mysqlcon, preparedStatement, resultSet);
            SqliteDBtool.close(sqlitecon, statement, null);
        }
        if (pool == null) {
            pool = Executors.newFixedThreadPool(threads);
        } else {
            if (pool.isShutdown()) {
                pool = Executors.newFixedThreadPool(threads);
            }
        }
        log.appendText("开启线程s数量:" + threads + "\n");
        down(fieldList, savepath, serverIP, placeNum, replaceContent);

    }


    /**
     * 新路径
     *
     * @param event
     */
    public void getnewpath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) rootLayout.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            String path = file.getPath();//选择的文件夹路径
            TextField textField = (TextField) Main.scene.lookup("#newpath");
            textField.setText(path);
        }
    }

    /**
     * 弹出框
     *
     * @param info
     */
    private void talk(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("信息");
        alert.headerTextProperty().set(info);
        alert.showAndWait();
    }

    /**
     * 下载
     *
     * @param fieldList 字段数组
     * @param savepath  保存路径
     * @param serverIP  ip
     * @param placeNum  替换位置数
     * @param content   替换内容
     */
    private void down(List<String> fieldList, String savepath, String serverIP, int placeNum, String content) {
        Long starttime = System.currentTimeMillis();
        disable(true);
        jindutiao.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        //读取本地数据库
        Connection sqliteCon = SqliteDBtool.getConnection();
        Statement selectStatement = null;
        ResultSet set = null;
        String count = "select count(1) from " + TABLENAME;
        String selectSql = "select * from " + TABLENAME;
        try {
            selectStatement = sqliteCon.createStatement();//查询本地数据库数据
            set = selectStatement.executeQuery(count);
            DownCount = set.getFloat(1);
            if (DownCount > 0) {
                //如果有数据
                set = selectStatement.executeQuery(selectSql);
                if (set.isBeforeFirst()) {
                    while (set.next()) {
                        for (int i = 0; i < fieldList.size(); i++) {
                            String imageUrl = fieldList.get(i);
                            String url = set.getString(imageUrl);
                            int finalI = i;
                            pool.execute(() -> {
                                try {
                                    if (url != null && !url.equals("") && !url.equals("null")) {
                                        String readUrl = serverIP + url;//读取图片路径
                                        String save = savepath + "\\" + Utils.replaceUrl(url, placeNum, content);//保存路径
                                        String fileName = Utils.getName(url);//获取文件名
                                        DownLoadImage.downImages(save, readUrl, fileName);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                log.appendText("复制:" + readUrl + "成功\n");
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String readUrl = serverIP + url;//读取图片路径
                                                String save = savepath + "\\" + Utils.replaceUrl(url, placeNum, content);//保存路径
                                                String fileName = Utils.getName(url);//获取文件名
                                                DownLoadImage.downImages(save, readUrl, fileName);
                                            } catch (IOException ioException) {
                                                logger.info("失败:" + serverIP + url);
                                                log.appendText("失败:" + serverIP + url + "\n");
                                            }

                                        }
                                    });
                                }
                                if (finalI == (fieldList.size() - 1)) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            float num = (float) (showNum % DownCount);
                                            num += 1;
                                            showNum++;
                                            jindutiao.setProgress(num / DownCount);
                                            jindutiaowenzi.setText("完成" + Math.round(showNum) + ",总计" + Math.round(DownCount));
                                            if (showNum == DownCount) {
                                                logger.info("任务结束");
                                                Long endstart = System.currentTimeMillis();
                                                log.appendText("复制完成,本次耗时" + (endstart - starttime) + "ms\n");
                                                talk("复制完成");
                                                disable(false);
                                            }
                                        }
                                    });
                                }

                            });


                        }
                    }

                } else {
                    System.out.println("无数据");
                }
            }
        } catch (SQLException throwables) {
            errLog.error(throwables.getMessage());
        } finally {
            SqliteDBtool.close(sqliteCon, selectStatement, set);
        }
    }


    /**
     * 停止
     *
     * @param event
     */
    public void Stop(ActionEvent event) {
        if (!pool.isShutdown()) {
            pool.shutdownNow();
            disable(false);
            talk("停止成功");
            logger.info("任务手动结束");
        } else {
            talk("没有什么可终止的...");
        }
    }

    /**
     * 当点击开始之后，所有组件设置不可用
     */
    private void disable(boolean statu) {
        db.setDisable(statu);
        url.setDisable(statu);
        username.setDisable(statu);
        userpassword.setDisable(statu);
        port.setDisable(statu);
        name.setDisable(statu);
        sql.setDisable(statu);
        field.setDisable(statu);
        startButton.setDisable(statu);
        FilepathButton.setDisable(statu);
        xianchengshu.setDisable(statu);
        ip.setDisable(statu);
        tihuanweizhi.setDisable(statu);
        tihuanneirong.setDisable(statu);
    }
}
