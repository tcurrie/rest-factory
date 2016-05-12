package com.github.tcurrie.rest.factory.it.apis;

public interface TestApi {

    void runnable();
    void consumer(Pojo m);
    Pojo producer();
    Pojo reverse(Pojo c);
    Pojo concatenate(Pojo a, Pojo b);
    int add(int a, int b);
    int sum(int... values);
    int throwsException() throws Exception;
    int throwsRuntimeException();
}
