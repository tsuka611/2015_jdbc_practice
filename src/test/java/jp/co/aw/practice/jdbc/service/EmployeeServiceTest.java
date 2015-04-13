package jp.co.aw.practice.jdbc.service;

import static jp.co.aw.practice.jdbc.dbcp.ConnectionUtils.checkoutConnection;
import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.closeQuietly;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.rethrow;
import static jp.co.aw.practice.jdbc.utils.DateUtils.parse;
import static jp.co.aw.practice.jdbc.utils.DateUtils.ts;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.assertZonedDateTime;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.entity.Employee;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.io.Closer;

public class EmployeeServiceTest {

    Connection connection;
    EmployeeService service;

    @Before
    public void setUp() throws Exception {
        connection = ConnectionUtils.checkoutConnection();
        connection.setAutoCommit(true);
        service = new EmployeeService();

        deleteRecords();
        insert("太郎", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), false);
        insert("二郎", "jiro@mail.com", "002-1234", parse("2015/04/02 01:02:03"), false);
        insert("さぶ", "sabu@mail.com", "003-1234", parse("2015/04/03 01:02:03"), true);
        insert("しろう", "siro@mail.com", "004-1234", parse("2015/04/04 01:02:03"), false);
    }

    @After
    public void tearDown() throws Exception {
        Closer c = Closer.create();
        if (connection != null)
            c.register(wrap(connection));
        c.close();
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

    long insert(String name, String mail, String tel, ZonedDateTime updateDate, boolean isDeleted) throws Exception {
        Closer c = Closer.create();
        try {
            PreparedStatement ps = c.register(
                    wrap(connection.prepareStatement("insert into test(name, mail, tel, update_date, is_deleted) values (?, ?, ?, ?, ?);",
                            Statement.RETURN_GENERATED_KEYS))).getCloseable();

            int index = 0;
            ps.setString(++index, name);
            ps.setString(++index, mail);
            ps.setString(++index, tel);
            ps.setTimestamp(++index, ts(updateDate));
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

    static Employee findById(long id) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(Joiner.on(",").join(EmployeeService.cols())).append(" ");
        sql.append("from ").append(EmployeeService.tableName()).append(" ");
        sql.append("where ").append("id = ?");

        Closer closer = Closer.create();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            ps.setLong(1, id);
            ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
            return rs.next() ? EmployeeService.buildObject(rs) : null;
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
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
        long id = insert("太郎", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), false);

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
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/01 01:02:03"));
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
            assertZonedDateTime(e.getUpdateDate(), null);
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
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/01 01:02:03"));
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(1);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("二郎"));
            assertThat(e.getMail(), is("jiro@mail.com"));
            assertThat(e.getTel(), is("002-1234"));
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/02 01:02:03"));
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(2);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("しろう"));
            assertThat(e.getMail(), is("siro@mail.com"));
            assertThat(e.getTel(), is("004-1234"));
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/04 01:02:03"));
            assertThat(e.getIsDeleted(), is(false));
        }
    }

    @Test
    public void findById_値が取得できない() throws Exception {
        long id = insert("太郎", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), false);
        Employee ret = service.findById(id + 999L);
        assertThat(ret, is(nullValue()));
    }

    @Test
    public void findById_削除されたレコードが取得されない() throws Exception {
        long id = insert("太郎", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), true);
        Employee ret = service.findById(id);
        assertThat(ret, is(nullValue()));
    }

    @Test
    public void findById_通常() throws Exception {
        long id = insert("太郎", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), false);
        Employee ret = service.findById(id);
        assertThat(ret.getId(), is(id));
        assertThat(ret.getName(), is("太郎"));
        assertThat(ret.getMail(), is("taro@mail.com"));
        assertThat(ret.getTel(), is("001-1234"));
        assertZonedDateTime(ret.getUpdateDate(), parse("2015/04/01 01:02:03"));
        assertThat(ret.getIsDeleted(), is(false));
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
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/01 01:02:03"));
            assertThat(e.getIsDeleted(), is(false));
        }
        {
            Employee e = ret.get(1);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("二郎"));
            assertThat(e.getMail(), is("jiro@mail.com"));
            assertThat(e.getTel(), is("002-1234"));
            assertZonedDateTime(e.getUpdateDate(), parse("2015/04/02 01:02:03"));
            assertThat(e.getIsDeleted(), is(false));
        }
    }

    @Test
    public void findLikeName_エスケープ文字で正しく値が取得される() throws Exception {
        insert("%%%", "taro@mail.com", "001-1234", parse("2015/04/01 01:02:03"), false);
        insert("___", "jiro@mail.com", "002-1234", parse("2015/04/02 01:02:03"), false);
        insert("$$$", "sabu@mail.com", "003-1234", parse("2015/04/03 01:02:03"), false);

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

    @Test
    public void insert_通常() {
        long id = service.insert("テスト太郎", "test-mail@example.com", "999-1234");
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is("test-mail@example.com"));
        assertThat(db.getTel(), is("999-1234"));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test(expected = RuntimeException.class)
    public void insert_nameがnull() {
        service.insert(null, "test-mail@example.com", "999-1234");
    }

    @Test(expected = RuntimeException.class)
    public void insert_nameが空() {
        service.insert("", "test-mail@example.com", "999-1234");
    }

    @Test
    public void insert_mailとtelがnull() {
        long id = service.insert("テスト太郎", null, null);
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is(nullValue()));
        assertThat(db.getTel(), is(nullValue()));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test
    public void insert_mailとtelが空() {
        long id = service.insert("テスト太郎", "", "");
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is(nullValue()));
        assertThat(db.getTel(), is(nullValue()));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test
    public void update_通常() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        int ret = service.update(id, "テスト太郎2", "test-mail2@example.com", "111-1234");
        assertThat(ret, is(1));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎2"));
        assertThat(db.getMail(), is("test-mail2@example.com"));
        assertThat(db.getTel(), is("111-1234"));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getUpdateDate().toEpochSecond(), is(not(parse("2015/04/11 01:02:03").toEpochSecond())));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test(expected = RuntimeException.class)
    public void update_nameがnull() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        service.update(id, null, "test-mail2@example.com", "111-1234");
    }

    @Test(expected = RuntimeException.class)
    public void update_nameが空() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        service.update(id, "", "test-mail2@example.com", "111-1234");
    }

    @Test
    public void update_mailとtelがnull() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        int ret = service.update(id, "テスト太郎2", null, null);
        assertThat(ret, is(1));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎2"));
        assertThat(db.getMail(), is(nullValue()));
        assertThat(db.getTel(), is(nullValue()));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getUpdateDate().toEpochSecond(), is(not(parse("2015/04/11 01:02:03").toEpochSecond())));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test
    public void update_mailとtelが空() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        int ret = service.update(id, "テスト太郎2", "", "");
        assertThat(ret, is(1));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎2"));
        assertThat(db.getMail(), is(nullValue()));
        assertThat(db.getTel(), is(nullValue()));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getUpdateDate().toEpochSecond(), is(not(parse("2015/04/11 01:02:03").toEpochSecond())));
        assertThat(db.getIsDeleted(), is(false));
    }

    @Test
    public void update_削除レコードは更新されない() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), true);
        int ret = service.update(id, "テスト太郎2", "test-mail2@example.com", "111-1234");
        assertThat(ret, is(0));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is("test-mail@example.com"));
        assertThat(db.getTel(), is("999-1234"));
        assertZonedDateTime(db.getUpdateDate(), parse("2015/04/11 01:02:03"));
        assertThat(db.getIsDeleted(), is(true));
    }

    @Test
    public void update_対象レコードがない場合() throws Exception {
        deleteRecords();
        int ret = service.update(1L, "テスト太郎2", "test-mail2@example.com", "111-1234");
        assertThat(ret, is(0));
    }

    @Test
    public void delete_通常() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), false);
        int ret = service.delete(id);
        assertThat(ret, is(1));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is("test-mail@example.com"));
        assertThat(db.getTel(), is("999-1234"));
        assertThat(db.getUpdateDate(), is(notNullValue()));
        assertThat(db.getUpdateDate().toEpochSecond(), is(not(parse("2015/04/11 01:02:03").toEpochSecond())));
        assertThat(db.getIsDeleted(), is(true));
    }

    @Test
    public void delete_削除済みレコードは更新されない() throws Exception {
        long id = insert("テスト太郎", "test-mail@example.com", "999-1234", parse("2015/04/11 01:02:03"), true);
        int ret = service.delete(id);
        assertThat(ret, is(0));
        Employee db = findById(id);
        assertThat(db.getId(), is(notNullValue()));
        assertThat(db.getName(), is("テスト太郎"));
        assertThat(db.getMail(), is("test-mail@example.com"));
        assertThat(db.getTel(), is("999-1234"));
        assertZonedDateTime(db.getUpdateDate(), parse("2015/04/11 01:02:03"));
        assertThat(db.getIsDeleted(), is(true));
    }
}
