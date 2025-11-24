package net.cybercake.hystats.utils;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {

    public static <A, B> Map<A, B> singleItemMap(A a, B b) {
        Map<A, B> map = new HashMap<>();
        map.put(a, b);
        System.out.println("Singleton map created: " + map);
        return map;
    }

}
