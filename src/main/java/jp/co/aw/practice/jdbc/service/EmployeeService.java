package jp.co.aw.practice.jdbc.service;

import static com.google.common.base.Preconditions.checkArgument;
import static jp.co.aw.practice.jdbc.dbcp.ConnectionUtils.checkoutConnection;
import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.closeQuietly;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.rethrow;
import static jp.co.aw.practice.jdbc.utils.DateUtils.now;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.co.aw.practice.jdbc.entity.Employee;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.io.Closer;

public class EmployeeService {

    static String tableName() {
        return "test";
    }

    static Collection<String> cols() {
        return Arrays.asList("id", "name", "mail", "tel", "update_date", "is_deleted");
    }

    static Employee buildObject(ResultSet rs) throws SQLException {
        Employee.Builder builder = Employee.builder();
        builder.id(rs.getLong("id")).name(rs.getString("name")).mail(rs.getString("mail")).tel(rs.getString("tel")).updateDate(rs.getTimestamp("update_date"))
                .isDeleted(rs.getBoolean("is_deleted"));
        return builder.build();
    }

    public List<Employee> findAll() {
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(Joiner.on(",").join(cols())).append(" ");
        sql.append("from ").append(tableName()).append(" ");
        sql.append("where ").append("is_deleted = false ");

        Closer closer = Closer.create();
        List<Employee> ret = new LinkedList<>();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
            while (rs.next()) {
                ret.add(buildObject(rs));
            }
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
        return Collections.unmodifiableList(ret);
    }

    public Employee findById(long id) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(Joiner.on(",").join(cols())).append(" ");
        sql.append("from ").append(tableName()).append(" ");
        sql.append("where ").append("is_deleted = false ");
        sql.append("and ").append("id = ?");

        Closer closer = Closer.create();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            ps.setLong(1, id);
            ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
            return rs.next() ? buildObject(rs) : null;
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
    }

    public List<Employee> findLikeName(String name) {
        checkArgument(!Strings.isNullOrEmpty(name));
        StringBuilder sql = new StringBuilder();
        sql.append("select ").append(Joiner.on(",").join(cols())).append(" ");
        sql.append("from ").append(tableName()).append(" ");
        sql.append("where ").append("is_deleted = false ");
        sql.append("and ").append("name like ? escape '$'");

        String likeName = name.replace("$", "$$").replace("_", "$_").replace("%", "$%");

        Closer closer = Closer.create();
        List<Employee> ret = new LinkedList<>();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            ps.setString(1, "%" + likeName + "%");
            ResultSet rs = closer.register(wrap(ps.executeQuery())).getCloseable();
            while (rs.next()) {
                ret.add(buildObject(rs));
            }
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
        return Collections.unmodifiableList(ret);
    }

    public long insert(String name, String mail, String tel) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableName());
        sql.append(" (");
        sql.append(Joiner.on(",").join(Arrays.asList("name", "mail", "tel", "update_date", "is_deleted")));
        sql.append(") values (?, ?, ?, ?, false)");

        Closer closer = Closer.create();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS))).getCloseable();
            int index = 0;
            ps.setString(++index, Strings.emptyToNull(name));
            ps.setString(++index, Strings.emptyToNull(mail));
            ps.setString(++index, Strings.emptyToNull(tel));
            ps.setTimestamp(++index, now());
            ps.executeUpdate();

            ResultSet rs = closer.register(wrap(ps.getGeneratedKeys())).getCloseable();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
    }

    public int update(long id, String name, String mail, String tel) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(tableName()).append(" set ");
        sql.append("name = ?").append(",");
        sql.append("mail = ?").append(",");
        sql.append("tel = ?").append(",");
        sql.append("update_date = ?").append(" ");
        sql.append("where ").append("is_deleted = false ");
        sql.append("and ").append("id = ?");

        Closer closer = Closer.create();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            int index = 0;
            ps.setString(++index, Strings.emptyToNull(name));
            ps.setString(++index, Strings.emptyToNull(mail));
            ps.setString(++index, Strings.emptyToNull(tel));
            ps.setTimestamp(++index, now());
            ps.setLong(++index, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
    }

    public int delete(long id) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(tableName()).append(" set ");
        sql.append("update_date = ?").append(",");
        sql.append("is_deleted = true").append(" ");
        sql.append("where ").append("is_deleted = false ");
        sql.append("and ").append("id = ?");

        Closer closer = Closer.create();
        try {
            Connection c = closer.register(wrap(checkoutConnection())).getCloseable();
            PreparedStatement ps = closer.register(wrap(c.prepareStatement(sql.toString()))).getCloseable();
            int index = 0;
            ps.setTimestamp(++index, now());
            ps.setLong(++index, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw rethrow(closer, e);
        } finally {
            closeQuietly(closer);
        }
    }
}
