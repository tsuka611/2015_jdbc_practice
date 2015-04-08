package jp.co.aw.practice.jdbc.utils.config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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

    @Test(expected = NoSuchElementException.class)
    public void extract_取得結果がnull() {
        String key = "DB_URL";
        envMap.remove(key);
        ConfigUtils.extract(key);
    }

    @Test(expected = NoSuchElementException.class)
    public void extract_取得結果が空() {
        String key = "DB_URL";
        envMap.put(key, "");
        ConfigUtils.extract(key);
    }

    @Test
    public void dbUrl_通常() {
        String ret = ConfigUtils.dbUrl();
        String expected = "test_db_url";
        assertThat(ret, is(expected));
    }

    @Test(expected = NoSuchElementException.class)
    public void dbUrl_取得結果がnull() {
        String key = "DB_URL";
        envMap.remove(key);
        ConfigUtils.dbUrl();
    }

    @Test(expected = NoSuchElementException.class)
    public void dbUrl_取得結果が空() {
        String key = "DB_URL";
        envMap.put(key, "");
        ConfigUtils.dbUrl();
    }

    @Test
    public void dbUser_通常() {
        String ret = ConfigUtils.dbUser();
        String expected = "test_db_user";
        assertThat(ret, is(expected));
    }

    @Test(expected = NoSuchElementException.class)
    public void dbUser_取得結果がnull() {
        String key = "DB_USER";
        envMap.remove(key);
        ConfigUtils.dbUser();
    }

    @Test(expected = NoSuchElementException.class)
    public void dbUser_取得結果が空() {
        String key = "DB_USER";
        envMap.put(key, "");
        ConfigUtils.dbUser();
    }

    @Test
    public void dbPassword_通常() {
        String ret = ConfigUtils.dbPassword();
        String expected = "test_db_password";
        assertThat(ret, is(expected));
    }

    @Test(expected = NoSuchElementException.class)
    public void dbPassword_取得結果がnull() {
        String key = "DB_PASSWORD";
        envMap.remove(key);
        ConfigUtils.dbPassword();
    }

    @Test(expected = NoSuchElementException.class)
    public void dbPassword_取得結果が空() {
        String key = "DB_PASSWORD";
        envMap.put(key, "");
        ConfigUtils.dbPassword();
    }
}
