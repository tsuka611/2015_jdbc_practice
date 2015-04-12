package jp.co.aw.practice.jdbc.dbcp;

import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbPassword;
import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbUrl;
import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbUser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jp.co.aw.practice.jdbc.ApplicationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtils {

    static final String PREFIX_DRIVER_STRING = "jdbc:apache:commons:dbcp:";
    static final String POSTFIX_DRIVER_STRING = "test";
    static final String DRIVER_STRING = PREFIX_DRIVER_STRING + POSTFIX_DRIVER_STRING;

    static {
        init();
    }

    public static void init() {
        log.debug("Create ConnectionFactory by ConfigUtils value.");
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dbUrl(), dbUser(), dbPassword());

        log.debug("Create PoolableConnectionFactory.");
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

        log.debug("Create GenericObjectPool.");
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);

        log.debug("Set ConnectionPool[{}] to PoolableConnectionFactory[{}].", connectionPool, poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);

        PoolingDriver driver;
        try {
            log.debug("Get Driver from DriverManager by {}.", PREFIX_DRIVER_STRING);
            Class.forName("org.apache.commons.dbcp2.PoolingDriver");
            driver = (PoolingDriver) DriverManager.getDriver(PREFIX_DRIVER_STRING);
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Get Driver from DriverManager failed.", e);
            throw new ApplicationException(e);
        }
        log.debug("Regist pool name for {}", POSTFIX_DRIVER_STRING);
        driver.registerPool(POSTFIX_DRIVER_STRING, connectionPool);
    }

    static Connection checkoutConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("Get Connection from DriverManager failed.");
            throw new ApplicationException(e);
        }
    }

    public static Connection checkoutConnection() {
        return checkoutConnection(DRIVER_STRING);
    }
}
