package sample.tool;

import java.sql.*;
import java.util.List;

/**
 * sqlite工具
 *
 * @author 杨佳颖
 * @Data 2020/12/7 11:24
 **/
public class SqliteDBtool {
    /**
     * 获取sqlite连接
     *
     * @return
     */
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:filecopy.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSql(String TABLENAME,List<String> fieldList,ResultSet resultSet) throws SQLException {
        String sql = "insert into " + TABLENAME;
        if(fieldList.size()==1){

            for (int i = 0; i < fieldList.size(); i++) {
                String ziduan = fieldList.get(i);
                sql += "(" + ziduan + ")";
            }
            sql += " values ";
            for (int i = 0; i < fieldList.size(); i++) {
                String ziduan = fieldList.get(i);
                sql += "('" + resultSet.getString(ziduan) + "')";
            }
        }else {
            for (int i = 0; i < fieldList.size(); i++) {
                String ziduan = fieldList.get(i);
                if (i == 0) {
                    sql += "(" + ziduan + " ,";
                } else if (i == fieldList.size() - 1) {
                    sql += ziduan + ")";
                } else {
                    sql += ziduan + ",";
                }
            }
            sql += " values ";
            for (int i = 0; i < fieldList.size(); i++) {
                String ziduan = fieldList.get(i);
                if (i == 0) {
                    sql += "('" + resultSet.getString(ziduan) + "',";
                } else if (i == fieldList.size() - 1) {
                    sql += "'" + resultSet.getString(ziduan) + "')";
                } else {
                    sql += "'" + resultSet.getString(ziduan) + "',";
                }
            }
        }

        return sql;
    }


    public static boolean init(String tableName, List<String> parameters) {
        boolean is = checkTable(tableName);
        return initDB(is, parameters, tableName);
    }

    /**
     * 检测表是否存在
     *
     * @return
     */
    private static boolean checkTable(String table) {
        boolean result = false;
        Connection connection = getConnection();
        Statement stm = null;
        ResultSet resultSet = null;
        try {
            stm = connection.createStatement();
            String sql = "select count(*) from sqlite_master where type = 'table' and name = '" + table + "'";
            resultSet = stm.executeQuery(sql);
            while (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            close(connection, stm, resultSet);
        }
        return result;
    }

    private static boolean initDB(boolean isExist, List<String> parameters, String tableName) {
        boolean statu = false;
        if (isExist) {
            //如果存在，就删掉表重新建立再导入数据
            //SQLite得到连接
            Connection iteconn = getConnection();
            Statement stm = null;
            try {
                String drpSql = "drop table " + tableName;
                String fields = "";
                if(parameters.size()==1){
                    for (int i = 0; i < parameters.size(); i++) {
                        String ziduan = parameters.get(i);
                        fields += "(" + ziduan + " TEXT)";
                    }
                }else {
                    for (int i = 0; i < parameters.size(); i++) {
                        String ziduan = parameters.get(i);
                        if (i == 0) {
                            fields += "(" + ziduan + " TEXT,";
                        } else if (i == parameters.size() - 1) {
                            fields += ziduan + " TEXT)";
                        } else {
                            fields += ziduan + " TEXT,";
                        }
                    }
                }
                String createSql = "CREATE TABLE  " + tableName + " " + fields;
                //关闭事务自动提交
                iteconn.setAutoCommit(false);
                //初始化数据库
                stm = iteconn.createStatement();
                stm.addBatch(drpSql);
                stm.addBatch(createSql);
                stm.executeBatch();
                iteconn.commit();
                statu = true;
            } catch (Exception e) {
                try {
                    iteconn.rollback();
                } catch (SQLException throwables) {
                }
            } finally {
                close(iteconn, stm, null);
            }
        } else {
            Connection iteconn = getConnection();
            Statement stm = null;
            try {
                String fields = "";
                if(parameters.size()==1){
                    for (int i = 0; i < parameters.size(); i++) {
                        String ziduan = parameters.get(i);
                        fields += "(" + ziduan + " TEXT)";
                    }
                }else {
                    for (int i = 0; i < parameters.size(); i++) {
                        String ziduan = parameters.get(i);
                        if (i == 0) {
                            fields += "(" + ziduan + " TEXT,";
                        } else if (i == parameters.size() - 1) {
                            fields += ziduan + " TEXT)";
                        } else {
                            fields += ziduan + " TEXT,";
                        }
                    }
                }

                String createSql = "CREATE TABLE  " + tableName + " " + fields;
                //关闭事务自动提交
                iteconn.setAutoCommit(false);
                //初始化数据库
                stm = iteconn.createStatement();
                stm.addBatch(createSql);
                stm.executeBatch();
                iteconn.commit();
                statu = true;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    iteconn.rollback();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } finally {
                close(iteconn, stm, null);
            }
        }
        return statu;
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
