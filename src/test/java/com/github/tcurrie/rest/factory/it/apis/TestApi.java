package com.github.tcurrie.rest.factory.it.apis;

import java.util.HashMap;
import java.util.Map;

public interface TestApi {
    Map<String, Object> DATA = new HashMap<>();

    void runnable();
    void consumer(Pojo m);
    Pojo producer();
    Pojo reverse(Pojo c);
    Pojo concatenate(Pojo a, Pojo b);
    int add(int a, int b);
    int sum(int... values);
}
