package com.tencent.community.actuator;

import com.tencent.community.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    // 自定义endpoint  readoperation  为get请求
    @ReadOperation
    public String checkConnection() {
        try (
                Connection conn = dataSource.getConnection();
        ) {
            return JsonUtils.getJsonString(0, "获取连接成功!");
        } catch (SQLException e) {
            logger.error("获取连接失败:" + e.getMessage());
            return JsonUtils.getJsonString(1, "获取连接失败!");
        }
    }
}
