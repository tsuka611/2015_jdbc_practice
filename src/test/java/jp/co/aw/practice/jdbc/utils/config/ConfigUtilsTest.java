package jp.co.aw.practice.jdbc.utils.config;

import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbPassword;
import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbUrl;
import static jp.co.aw.practice.jdbc.utils.config.ConfigUtils.dbUser;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigUtilsTest {

    Map<String, String> envMap;

    @Before
    public void setUp() {
        envMap = new HashMap<>();
        ConfigUtils.envMap = envMap;

        envMap.put("DB_URL", "test_db_url");
        envMap.put("DB_USER", "test_db_user");
        envMap.put("DB_PASSWORD", "test_db_password");
    }

    @After
    public void tearDown() {
        ConfigUtils.init();
    }

    @Test
    public void extract_通常() {
        String key = "DB_URL";
        String ret = ConfigUtils.extract(key);
        String expected = "test_db_url";
        assertThat(ret, is(expected));
    }

    @Test
    public void extract_取得結果がnull() {
        String key = "DB_URL";
        envMap.remove(key);
        assertThat(ConfigUtils.extract(key), is(""));
    }

    @Test
    public void extract_取得結果が空() {
        String key = "DB_URL";
        envMap.put(key, "");
        assertThat(ConfigUtils.extract(key), is(""));
    }

    @Test
    public void dbUrl_通常() {
        assertThat(dbUrl(), is("test_db_url"));
    }

    @Test
    public void dbUrl_取得結果がnull() {
        envMap.remove("DB_URL");
        assertThat(dbUrl(), is(""));
    }

    @Test
    public void dbUrl_取得結果が空() {
        envMap.put("DB_URL", "");
        assertThat(dbUrl(), is(""));
    }

    @Test
    public void dbUser_通常() {
        assertThat(dbUser(), is("test_db_user"));
    }

    @Test
    public void dbUser_取得結果がnull() {
        envMap.remove("DB_USER");
        assertThat(dbUser(), is(""));
    }

    @Test
    public void dbUser_取得結果が空() {
        envMap.put("DB_USER", "");
        assertThat(dbUser(), is(""));
    }

    @Test
    public void dbPassword_通常() {
        assertThat(dbPassword(), is("test_db_password"));
    }

    @Test
    public void dbPassword_取得結果がnull() {
        envMap.remove("DB_PASSWORD");
        assertThat(dbPassword(), is(""));
    }

    @Test
    public void dbPassword_取得結果が空() {
        envMap.put("DB_PASSWORD", "");
        assertThat(dbPassword(), is(""));
    }
}
