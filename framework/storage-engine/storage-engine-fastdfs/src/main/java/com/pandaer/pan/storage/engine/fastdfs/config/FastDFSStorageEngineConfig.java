package com.pandaer.pan.storage.engine.fastdfs.config;

import com.github.tobato.fastdfs.conn.ConnectionPoolConfig;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

import java.util.List;

@SpringBootConfiguration
@Data
@ConfigurationProperties(prefix = "pan.storage.engine.fastdfs")
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan(value = {"com.github.tobato.fastdfs.service", "com.github.tobato.fastdfs.domain"})
public class FastDFSStorageEngineConfig {
    /**
     * 连接超时时间

     */
    private Integer connectTimeout = 600;

    /**
     * 跟踪服务器列表
     */
    private List<String> trackerList = Lists.newArrayList();


    /**
     * 上传文件时的默认组
     */
    private String group = "group1";


    @Bean
    public PooledConnectionFactory pooledConnectionFactoryPan() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectTimeout(connectTimeout);
        return pooledConnectionFactory;
    }

    @Bean
    public ConnectionPoolConfig connectionPoolConfigPan() {
        return new ConnectionPoolConfig();
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPoolPan(PooledConnectionFactory pooledConnectionFactory, ConnectionPoolConfig connectionPoolConfig) {
        return new FdfsConnectionPool(pooledConnectionFactory, connectionPoolConfig);

    }

    @Bean
    public TrackerConnectionManager trackerConnectionManagerPan(FdfsConnectionPool fdfsConnectionPool) {
        TrackerConnectionManager trackerConnectionManager = new TrackerConnectionManager(fdfsConnectionPool);
        if (trackerList.isEmpty()) {
            throw new RuntimeException("trackerList is empty");
        }
        trackerConnectionManager.setTrackerList(trackerList);
        return trackerConnectionManager;
    }

}
