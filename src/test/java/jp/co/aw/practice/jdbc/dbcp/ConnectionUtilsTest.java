package jp.co.aw.practice.jdbc.dbcp;

import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.co.aw.practice.jdbc.ApplicationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Closer;

public class ConnectionUtilsTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkoutConnection_通常初期化にて正しい接続が確立されていること() throws Exception {
        {
            Closer closer = Closer.create();
            try {
                Connection c = closer.register(wrap(ConnectionUtils.checkoutConnection())).getCloseable();
                PreparedStatement ps = closer.register(wrap(c.prepareStatement("select 110 from dual;"))).getCloseable();
                ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
                rs.next();
                assertThat(rs.getInt(1), is(110));
            } catch (Exception e) {
                throw closer.rethrow(e);
            } finally {
                closer.close();
            }
        }
        {
            Closer closer = Closer.create();
            try {
                Connection c = closer.register(wrap(ConnectionUtils.checkoutConnection())).getCloseable();
                PreparedStatement ps = closer.register(wrap(c.prepareStatement("select 220 from dual;"))).getCloseable();
                ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
                rs.next();
                assertThat(rs.getInt(1), is(220));
            } catch (Exception e) {
                throw closer.rethrow(e);
            } finally {
                closer.close();
            }
        }
    }

    @Test(expected = ApplicationException.class)
    public void checkoutConnection_不正なデータを取得しようとする() throws Exception {
        ConnectionUtils.checkoutConnection("xx");
    }
}
