package net.cybercake.hystats.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    public static <A, B> Map<A, B> singleItemMap(A a, B b) {
        Map<A, B> map = new HashMap<>();
        map.put(a, b);
        return map;
    }

    public static <A> List<A> singleItemList(A a) {
        List<A> list = new ArrayList<>();
        list.add(a);
        return list;
    }

    @SuppressWarnings({"unchecked"})
    public static <A> A[] toArray(List<A> list) {
        A[] array = (A[]) new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

}
