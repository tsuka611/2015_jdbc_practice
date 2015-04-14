package jp.co.aw.practice.jdbc.utils.config;

import java.util.Collections;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Strings;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigUtils {

    static Map<String, String> envMap;
    static {
        init();
    }

    protected static void init() {
        val m = System.getenv();
        envMap = Collections.unmodifiableMap(m);
    }

    static String extract(String key) {
        String val = envMap.get(key);
        log.debug("Env value get key[{}], value[{}]", key, val);
        return Strings.nullToEmpty(val);
    }

    public static String dbUrl() {
        return extract("DB_URL");
    }

    public static String dbUser() {
        return extract("DB_USER");
    }

    public static String dbPassword() {
        return extract("DB_PASSWORD");
    }
}
