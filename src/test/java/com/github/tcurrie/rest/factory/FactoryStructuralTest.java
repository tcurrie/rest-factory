package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.model.RestFactoryException;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

public class FactoryStructuralTest {

    private static final int EXPECTED_FACTORIES_DECLARED = 11;
    private static final Predicate<PojoClass> HAS_NO_INSTANCE_METHODS = p -> p.getPojoMethods().stream().noneMatch(m -> !m.isStatic() && !m.isConstructor());
    private static final Predicate<PojoClass> IS_NOT_INTERFACE = p -> !p.isInterface();
    private static final Predicate<PojoClass> IS_NOT_THROWABLE = p -> !Throwable.class.isAssignableFrom(p.getClazz());
    private static final Predicate<PojoClass> IS_NOT_ENUM = p -> !p.isEnum();

    @Test
    public void factoriesMustNotBeConstructable() {
        final List<PojoClass> pojoClasses = PojoClassFactory.getPojoClassesRecursively("com.github.tcurrie", null);

        final AtomicInteger foundFactories = new AtomicInteger(0);
        pojoClasses.stream()
                .filter(IS_NOT_INTERFACE)
                .filter(IS_NOT_ENUM)
                .filter(IS_NOT_THROWABLE)
                .filter(HAS_NO_INSTANCE_METHODS)
                .forEach(p->p.getPojoConstructors().forEach(c->{
                    Assert.assertThat("Expected [" + p + "] to be a factory with a private constuctor.", c.isPrivate(), is(true));
                    try {
                        c.invoke(null);
                        Assert.fail("Expected construction of [" + p + "] to fail.");
                    } catch (ReflectionException e) {
                        Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(InvocationTargetException.class));
                        Assert.assertThat(e.getCause().getCause(), CoreMatchers.instanceOf(RestFactoryException.class));
                        Assert.assertThat(e.getCause().getCause().getMessage(), is("Can not construct instance of Factory class."));
                    }
                    foundFactories.incrementAndGet();
                }));
        assertEquals("Added / removed Loggers?", EXPECTED_FACTORIES_DECLARED, foundFactories.get());
    }

}
