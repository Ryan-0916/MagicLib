package com.magicrealms.magiclib.common.store;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc Mysql数据库相关操作
 * 以下操作皆采用短连接
 * @date 2023-10-01
 */
@SuppressWarnings("unused")
@Slf4j
public class MySqlStore {
    private final String HOST;
    private final int PORT;
    private final String DATABASE;
    private final boolean USER_SSL;
    private final String USER;
    private final String PASSWORD;

    public MySqlStore(String host, int port, String database, boolean useSSL, String user, String password) {
        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
        this.USER_SSL = useSSL;
        this.USER = user;
        this.PASSWORD = password;
    }

    public Optional<Connection> getConnection(){
        String DB_URL = MessageFormat.format("jdbc:mysql://{0}:{1}/{2}?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL={3}",
                HOST,
                String.valueOf(PORT),
                DATABASE,
                USER_SSL ? "true" : "false");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return Optional.ofNullable(DriverManager.getConnection(DB_URL, USER, PASSWORD));
        } catch (ClassNotFoundException | SQLException e) {
            log.error("Mysql 连接异常请检查 Mysql 服务", e);
            return Optional.empty();
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public void select(String sql, Object[] obj, Consumer<ResultSet> resultConsumer){
        Optional<Connection> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return;
        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            for (int i = 0; i < obj.length; i++) preparedStatement.setObject(i + 1,obj[i]);
            resultConsumer.accept(preparedStatement.executeQuery());
        } catch (SQLException e) {
            log.error("MYSQL 查询异常请检查 MYSQL 服务", e);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public int update(String sql, Object[] obj){
        Optional<Connection> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return 0;
        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < obj.length; i++) preparedStatement.setObject(i + 1,obj[i]);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("MYSQL 修改/删除异常请检查 MYSQL 服务", e);
            return 0;
        }
    }

    /**
     * 是否存在该表
     * @param tableName 表名
     * @return {@link Boolean}
     */
    public boolean existTable(String tableName) {
        Optional<Connection> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Connection connection = connectionOptional.get();
             ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return resultSet.next();
        } catch (SQLException e) {
            log.error("MYSQL 查询是否存在表时出现未知异常", e);
            return false;
        }
    }

    /**
     * 释放返回结果集
     * @param res 结果集
     */
    public void closeResultSet(ResultSet res){
        if (res == null) return;
        try{ res.close(); } catch (Exception e){
            log.error("MYSQL 释放结果集时出现未知异常", e);
        }
    }
}
