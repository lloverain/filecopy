package sample.tool;

import java.sql.*;

/**
 * mysql工具
 *
 * @author 杨佳颖
 * @Data 2020/12/7 10:29
 **/
public class MysqlDBtool {

    /**
     * 获取数据库连接
     *
     * @param type
     * @param url
     * @param user
     * @param pass
     * @param port
     * @param dbName
     * @return
     */
    public static Connection getConnection(String type, String url, String user, String pass, String port, String dbName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Connection conn = null;
        String jdbcurl = connection_url(type, url, port, dbName);
        Driver driver = null;
        if (type.equals("Mysql")) {
            driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } else if (type.equals("SQLServer")) {
            driver = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        }
        DriverManager.registerDriver(driver);
        conn = DriverManager.getConnection(jdbcurl, user, pass);
        return conn;
    }


    //连接url判断
    private static String connection_url(String dbtype, String ip, String port, String dbname) {
        String jdbcurl = "";
        if ("MYSQL".equals(dbtype.toUpperCase())) {
            jdbcurl = "jdbc:mysql://" + ip + ":"
                    + port + "/" + dbname
                    + "?characterEncoding=utf-8&connectTimeout=3000&autoReconnect=false&serverTimezone=UTC";
        } else if ("SQLSERVER".equals(dbtype.toUpperCase())) {
            jdbcurl = "jdbc:sqlserver://" + ip + ":"
                    + port + ";DatabaseName=" + dbname;
        } else {
            return null;
        }
        return jdbcurl;
    }

    /**
     * 关闭
     *
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {

            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {

            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {

            }
        }
    }

}
