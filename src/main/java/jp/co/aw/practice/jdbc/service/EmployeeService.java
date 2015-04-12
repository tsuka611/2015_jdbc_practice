package jp.co.aw.practice.jdbc.service;

import static com.google.common.base.Preconditions.checkArgument;
import static jp.co.aw.practice.jdbc.dbcp.ConnectionUtils.checkoutConnection;
import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.closeQuietly;
import static jp.co.aw.practice.jdbc.utils.CloseUtils.rethrow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
