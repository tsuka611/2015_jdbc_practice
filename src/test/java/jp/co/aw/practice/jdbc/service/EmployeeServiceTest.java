package jp.co.aw.practice.jdbc.service;

import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.entity.Employee;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Closer;

public class EmployeeServiceTest {

    Connection connection;
    EmployeeService service;

    @Before
    public void setUp() throws Exception {
        connection = ConnectionUtils.checkoutConnection();
        service = new EmployeeService();

        deleteRecords();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        insert("太郎", "taro@mail.com", "001-1234", df.parse("2015/04/01 01:02:03"), false);
        insert("二郎", "jiro@mail.com", "002-1234", df.parse("2015/04/02 01:02:03"), false);
        insert("さぶ", "sabu@mail.com", "003-1234", df.parse("2015/04/03 01:02:03"), true);
        insert("しろう", "siro@mail.com", "004-1234", df.parse("2015/04/04 01:02:03"), false);
    }

    @After
    public void tearDown() throws Exception {
        Closer c = Closer.create();
        if (connection != null)
            c.register(wrap(connection));
        c.close();
    }

    static void assertTimestamp(Timestamp ts, String str) throws ParseException {
        if (Strings.isNullOrEmpty(str)) {
            assertThat(ts, is(nullValue()));
            return;
        }
        assertThat(ts, is(notNullValue()));
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date expected = df.parse(str);
        assertThat(String.format(String.format("Expected [%s] but was [%s]", expected, ts)), ts.getTime(), is(expected.getTime()));
    }

    void deleteRecords() throws Exception {
        Closer c = Closer.create();
        try {
            Statement st = c.register(wrap(connection.createStatement())).getCloseable();
            st.executeUpdate("delete from test");
        } catch (Exception e) {
            throw c.rethrow(e);
        } finally {
            c.close();
        }
    }

    long insert(String name, String mail, String tel, Date updateDate, boolean isDeleted) throws Exception {
        Closer c = Closer.create();
        try {
            PreparedStatement ps = c.register(
                    wrap(connection.prepareStatement("insert into test(name, mail, tel, update_date, is_deleted) values (?, ?, ?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS))).getCloseable();

            int index = 0;
            ps.setString(++index, name);
            ps.setString(++index, mail);
            ps.setString(++index, tel);
            ps.setString(++index, updateDate == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(updateDate));
            ps.setInt(++index, isDeleted ? 1 : 0);
            ps.executeUpdate();

            ResultSet rs = c.register(wrap(ps.getGeneratedKeys())).getCloseable();
            rs.next();
            return rs.getLong(1);
        } catch (Exception e) {
            throw c.rethrow(e);
        } finally {
            c.close();
        }
    }

    @Test
    public void tableName_通常() {
        assertThat(EmployeeService.tableName(), is("test"));
    }

    @Test
    public void cols_通常() {
        assertThat(EmployeeService.cols(), is(Arrays.asList("id", "name", "mail", "tel", "update_date", "is_deleted")));
    }

    @Test
    public void buildObject_通常() throws Exception {
        deleteRecords();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long id = insert("太郎", "taro@mail.com", "001-1234", df.parse("2015/04/01 01:02:03"), false);

        Closer c = Closer.create();
        try {
            Statement st = c.register(wrap(connection.createStatement())).getCloseable();
            ResultSet rs = c.register(
                    wrap(st.executeQuery(String.format("select %s from %s", Joiner.on(",").join(EmployeeService.cols()), EmployeeService.tableName()))))
                    .getCloseable();
            rs.next();
            Employee e = EmployeeService.buildObject(rs);

            assertThat(e.getId(), is(id));
            assertThat(e.getName(), is("太郎"));
            assertThat(e.getMail(), is("taro@mail.com"));
            assertThat(e.getTel(), is("001-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/01 01:02:03");
            assertThat(e.getIsDeleted(), is(false));

        } catch (Exception e) {
            throw c.rethrow(e);
        } finally {
            c.close();
        }
    }

    @Test
    public void buildObject_nullを含む削除オブジェクト() throws Exception {
        deleteRecords();
        long id = insert("太郎", null, null, null, true);

        Closer c = Closer.create();
        try {
            Statement st = c.register(wrap(connection.createStatement())).getCloseable();
            ResultSet rs = c.register(
                    wrap(st.executeQuery(String.format("select %s from %s", Joiner.on(",").join(EmployeeService.cols()), EmployeeService.tableName()))))
                    .getCloseable();
            rs.next();
            Employee e = EmployeeService.buildObject(rs);

            assertThat(e.getId(), is(id));
            assertThat(e.getName(), is("太郎"));
            assertThat(e.getMail(), is(nullValue()));
            assertThat(e.getTel(), is(nullValue()));
            assertTimestamp(e.getUpdateDate(), null);
            assertThat(e.getIsDeleted(), is(true));

        } catch (Exception e) {
            throw c.rethrow(e);
        } finally {
            c.close();
        }
    }

    @Test
    public void findAll_通常() throws Exception {
        List<Employee> ret = service.findAll();
        assertThat(ret.size(), is(3));
        {
            Employee e = ret.get(0);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("太郎"));
            assertThat(e.getMail(), is("taro@mail.com"));
            assertThat(e.getTel(), is("001-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/01 01:02:03");
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(1);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("二郎"));
            assertThat(e.getMail(), is("jiro@mail.com"));
            assertThat(e.getTel(), is("002-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/02 01:02:03");
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(2);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("しろう"));
            assertThat(e.getMail(), is("siro@mail.com"));
            assertThat(e.getTel(), is("004-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/04 01:02:03");
            assertThat(e.getIsDeleted(), is(false));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void findLikeName_引数がnull() throws Exception {
        service.findLikeName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findLikeName_引数が空() throws Exception {
        service.findLikeName("");
    }

    @Test
    public void findLikeName_結果が空() throws Exception {
        List<Employee> ret = service.findLikeName("たろう");
        assertThat(ret.size(), is(0));
    }

    @Test
    public void findLikeName_削除レコードが取得されない() throws Exception {
        List<Employee> ret = service.findLikeName("さぶ");
        assertThat(ret.size(), is(0));
    }

    @Test
    public void findLikeName_通常() throws Exception {
        List<Employee> ret = service.findLikeName("郎");
        assertThat(ret.size(), is(2));
        {
            Employee e = ret.get(0);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("太郎"));
            assertThat(e.getMail(), is("taro@mail.com"));
            assertThat(e.getTel(), is("001-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/01 01:02:03");
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(1);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("二郎"));
            assertThat(e.getMail(), is("jiro@mail.com"));
            assertThat(e.getTel(), is("002-1234"));
            assertTimestamp(e.getUpdateDate(), "2015/04/02 01:02:03");
            assertThat(e.getIsDeleted(), is(false));
        }
    }

    @Test
    public void findLikeName_エスケープ文字で正しく値が取得される() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        insert("%%%", "taro@mail.com", "001-1234", df.parse("2015/04/01 01:02:03"), false);
        insert("___", "jiro@mail.com", "002-1234", df.parse("2015/04/02 01:02:03"), false);
        insert("$$$", "sabu@mail.com", "003-1234", df.parse("2015/04/03 01:02:03"), false);

        {
            List<Employee> ret = service.findLikeName("%%%");
            assertThat(ret.size(), is(1));
            assertThat(ret.get(0).getName(), is("%%%"));
        }
        {
            List<Employee> ret = service.findLikeName("___");
            assertThat(ret.size(), is(1));
            assertThat(ret.get(0).getName(), is("___"));
        }
        {
            List<Employee> ret = service.findLikeName("$$$");
            assertThat(ret.size(), is(1));
            assertThat(ret.get(0).getName(), is("$$$"));
        }
    }
}
