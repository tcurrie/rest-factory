package com.github.tcurrie.rest.factory.proxy;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MethodsTest {
    @Test
    public void testGetMethodByName() throws NoSuchMethodException {
        final Class<?> type = FooBar.class;
        final String methodName = "foo";
        final Method expected = type.getMethod(methodName);
        final Method actual = Methods.TypeFactory.get(type, methodName);
        assertThat(actual, is(expected));
    }

    @Test
    public void testFailsGetMethodByNameWithRestFactoryException() throws NoSuchMethodException {
        final Class<?> type = FooBar.class;
        final String methodName = "baz";

        try {
            final Method actual = Methods.TypeFactory.get(type, methodName);
            Assert.fail("Should not have succeeded.  Got [" + actual + "]");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is(Strings.format("Unable to find class[{}], method[{}], args[{}].", type, methodName, new Object[0])));
        }
    }

    @Test
    public void testStreamAllMethodsThroughFactory() {
        final Class<?> type = Foo.class;
        final Set<String> expected = Stream.of("foo", "foobar").collect(Collectors.toSet());
        final Set<String> actual = Methods.TypeFactory.stream(type).map(Method::getName).collect(Collectors.toSet());
        assertThat(actual, is(expected));
    }

    @Test
    public void testStreamAllImplementedInterfacesThroughMethodFactory() {
        final FooBar bean = new FooBar() {
            @Override
            public void foo() {
            }
            @Override
            public void bar() {
            }
            @Override
            public void foobar() {
            }
        };
        final Set<String> expected = Stream.of("Foo.foo", "Bar.bar", "FooBar.foobar").collect(Collectors.toSet());
        final Set<String> actual = Methods.BeanFactory.stream(bean).map(m -> m.getDeclaringClass().getSimpleName() + "." + m.getName()).collect(Collectors.toSet());

        assertThat(actual, is(expected));
    }

    @SuppressWarnings("unused")
    private interface Foo {
        void foo();
        void foobar();
    }
    @SuppressWarnings("unused")
    private interface Bar {
        void bar();
    }
    @SuppressWarnings("unused")
    private interface FooBar extends Foo, Bar {
        void foobar();
    }
}
