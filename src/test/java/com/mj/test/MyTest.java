package com.mj.test;

import com.mj.common.util.Jsons;

import java.util.HashMap;

/**
 * @author MJ
 */
public class MyTest {
    public static void main(String[] args) {
        System.out.println(Jsons.getString(new HashMap<String, Object>() {{
            put("name", "MJ");
            put("age", 18);
        }}));
    }
}
