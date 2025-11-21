package net.cybercake.hystats.utils;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {

    public static <A, B> Map<A, B> singletonMap(A a, B b) {
        Map<A, B> map = new HashMap<>();
        map.put(a, b);
        return map;
    }

}
