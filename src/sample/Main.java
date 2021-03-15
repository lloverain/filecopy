package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.outlog.ProgrammerLog;
import sample.outlog.UserLog;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private static List<String> logs = new ArrayList<>(5);

    public static UserLog userLog = new UserLog();//用户日志
    public static ProgrammerLog programmerLog = new ProgrammerLog();//程序员日志

    public static Scene scene;
    public static String AppName = "科欣智能复制";

    @Override
    public void start(Stage primaryStage) throws Exception {
        //添加加载的日志
        logs.add("user_log.log");
        Image image = new Image(this.getClass().getResource("/copymove.png").openStream());
        primaryStage.getIcons().add(image);
        //保证窗口关闭后，Stage对象仍然存活
        Platform.setImplicitExit(false);
        //构建系统托盘图标
        BufferedImage bufferedImage = ImageIO.read(this.getClass().getResource("/copymove.png").openStream());
        TrayIcon trayIcon = new TrayIcon(bufferedImage, AppName);
        trayIcon.setImageAutoSize(true);
        //获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        //添加鼠标点击事件
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击左键
                if (e.getButton() == MouseEvent.BUTTON1 || e.getClickCount() == 2) {
                    Platform.runLater(() -> {
                        if (primaryStage.isIconified()) {
                            primaryStage.setIconified(false);
                        }
                        if (!primaryStage.isShowing()) {
                            primaryStage.show();
                        }
                        primaryStage.toFront();
                    });
                }
                //鼠标右键,关闭应用
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    Platform.runLater(() -> {
                        if (!primaryStage.isShowing()) {
                            primaryStage.show();
                        }
                        //        按钮部分可以使用预设的也可以像这样自己 new 一个
                        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, "你真的要退出吗？", new ButtonType("取消", ButtonBar.ButtonData.NO),
                                new ButtonType("确定", ButtonBar.ButtonData.YES));
                        //设置窗口的标题
                        _alert.setTitle(AppName);
                        _alert.setHeaderText("确定退出");
                        //设置对话框的 icon 图标，参数是主窗口的 stage
                        _alert.initOwner(primaryStage);
                        Optional<ButtonType> _buttonType = _alert.showAndWait();
                        //根据点击结果返回
                        if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                            Platform.setImplicitExit(true);
                            tray.remove(trayIcon);
                            System.exit(0);
                            Platform.runLater(primaryStage::close);
                        }
                    });

                }
            }
        });
        //添加托盘图标
        tray.add(trayIcon);
        primaryStage.setTitle(AppName);

        //子窗口随父窗口进行关闭
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //        按钮部分可以使用预设的也可以像这样自己 new 一个
                Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, "你真的要退出吗？", new ButtonType("最小化到系统托盘", ButtonBar.ButtonData.NO),
                        new ButtonType("退出系统", ButtonBar.ButtonData.YES));
                //设置窗口的标题
                _alert.setTitle(AppName);
                _alert.setHeaderText("确定退出");
                //设置对话框的 icon 图标，参数是主窗口的 stage
                _alert.initOwner(primaryStage);
                Optional<ButtonType> _buttonType = _alert.showAndWait();
                //根据点击结果返回
                if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    Platform.setImplicitExit(true);
                    tray.remove(trayIcon);
                    System.exit(0);
                    Platform.runLater(primaryStage::close);
                } else {
                    if (primaryStage.isIconified()) {
                        primaryStage.setIconified(false);
                    }
                    primaryStage.toFront();
                }
            }
        });


        Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));//修改了
        scene = new Scene(root,1208,832);//修改了
//        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);//设置不能窗口改变大小
        primaryStage.setTitle(AppName);//设置标题
        primaryStage.show();

        ComboBox comboBox = (ComboBox) scene.lookup("#db");
        comboBox.getItems().addAll(
                "Mysql",
                "SQLServer"
        );
        comboBox.getSelectionModel().selectFirst();

    }

//    public static void updateUIUser(String text, boolean status) {
//        Platform.runLater(() -> {
//            try {
//                if (status) {
//                    realTimeDBText.appendText(userLog.sucess(text));
//                } else {
//                    realTimeDBText.appendText(userLog.error(text, null));
//
//                }
//
//            } catch (Exception e) {
//
//            }
//        });
//
//
//    }


    public static void main(String[] args) {
        launch(args);
    }
}
