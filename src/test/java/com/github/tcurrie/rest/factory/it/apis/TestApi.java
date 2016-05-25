package com.github.tcurrie.rest.factory.it.apis;

import java.util.Set;

public interface TestApi {
    void runnable();
    void consumer(Pojo m);
    Pojo producer();
    Pojo reverse(Pojo c);
    Pojo concatenate(Pojo a, Pojo b);
    Set<Pojo> dedup(Pojo... values);
    Pojo min(Set<Pojo> values);
    int add(int a, int b);
    int sum(int... values);
    int throwsException() throws Exception;
    int throwsRuntimeException();
    String join(String[] values);
//    String join(String[] values, String separator);
}
