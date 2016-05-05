package com.github.tcurrie.rest.factory;

import com.google.common.collect.ImmutableMap;
import com.openpojo.random.RandomFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestUriFactoryTest {

    @Mock private RestUriFactory mockFactory;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateInterfaceUris() throws NoSuchMethodException {
        ImmutableMap.<Class,Map<Method,String>>builder()
                .put(Runnable.class, ImmutableMap.<Method,String>builder()
                    .put(Runnable.class.getMethod("run"), "/runnable/v1/run").build())
                .put(Future.class, ImmutableMap.<Method,String>builder()
                        .put(Future.class.getMethod("cancel", boolean.class), "/future/v1/cancel")
                        .put(Future.class.getMethod("get"), "/future/v1/get")
                        .put(Future.class.getMethod("isCancelled"), "/future/v1/is-cancelled")
                        .put(Future.class.getMethod("isDone"), "/future/v1/is-done")
                .build())
                .build().entrySet().forEach(t->
            t.getValue().entrySet().forEach(m->{
                final String expected = m.getValue();
                final String actual = RestUriFactory.getInstance().create(t.getKey(), m.getKey());
                assertThat(actual, is(expected));
            })
        );
    }

    @Test
    public void testCreateInterfaceVersionMethodUri() throws NoSuchMethodException {
        final Class type = RestUriFactoryTest.class;
        final Method method = type.getMethod("before");
        Mockito.doCallRealMethod().when(mockFactory).create(type, method);

        final String i = RandomFactory.getRandomValue(String.class);
        final String v = RandomFactory.getRandomValue(String.class);
        final String m = RandomFactory.getRandomValue(String.class);
        Mockito.when(mockFactory.getInterfaceName(Matchers.eq(type))).thenReturn(i);
        Mockito.when(mockFactory.getVersion(Matchers.eq(type))).thenReturn(v);
        Mockito.when(mockFactory.getMethod(Matchers.eq(method))).thenReturn(m);

        final String expected = "/" + i + "/" + v + "/" + m;

        final String actual = mockFactory.create(type, method);

        assertThat(actual, is(expected));
    }

    @Test
    public void testInterfaceNameIsLowerHyphenatedSimpleName() {
        final String expected = RandomFactory.getRandomValue(String.class);

        Mockito.doCallRealMethod().when(mockFactory).getInterfaceName(Matchers.any(Class.class));
        Mockito.when(mockFactory.lowerHyphenate(Matchers.eq(RestUriFactory.class.getSimpleName()))).thenReturn(expected);

        final String actual = mockFactory.getInterfaceName(RestUriFactory.class);

        assertThat(actual, is(expected));
    }

    @Test
    public void testVersionIsV1ForNoParent() {
        assertVersionForParentPart(null, "v1");
    }

    @Test
    public void testVersionFoundFromParent() {
        ImmutableMap.<String,String>builder()
                .put("", "v1")
                .put("any", "v1")
                .put("v1", "v1")
                .put("v2", "v2")
                .put("v1.1", "v1.1")
                .build().entrySet().forEach(e -> assertVersionForParentPart(e.getKey(), e.getValue()));
    }

    @Test
    public void testGetCanonicalParts() {
        final String[] parts = { "com", "github", "tcurrie", "rest", "factory", "RestUriFactoryTest" };

        IntStream.range(-6, 6).forEach(i -> {
            final String expected = i < 0 ? parts[parts.length + i] : parts[i];
            final String actual = RestUriFactory.getInstance().getCanonicalPart(RestUriFactoryTest.class, i);
            assertThat(actual, is(expected));
        });
    }

    @Test
    public void testMethodIsLowerHyphenatedName() throws NoSuchMethodException {
        final Method method = this.getClass().getMethod("before");
        final String expected = RandomFactory.getRandomValue(String.class);

        Mockito.doCallRealMethod().when(mockFactory).getMethod(Matchers.any(Method.class));
        Mockito.when(mockFactory.lowerHyphenate(Matchers.eq(method.getName()))).thenReturn(expected);

        final String actual = mockFactory.getMethod(method);

        assertThat(actual, is(expected));
    }


    @Test
    public void testLowerAndHyphenate() {
        ImmutableMap.<String,String>builder()
                .put("aB", "a-b")
                .put("ab", "ab")
                .put("AB", "ab")
                .put("Ab", "ab")
                .put("aBcD", "a-bc-d")
                .put("AbCd", "ab-cd")
                .put("a1B2", "a1-b2")
                .put("1a2B", "1a2-b")
                .put("a_b", "a-b")
                .put("a_B", "a-b")
                .put("a_BC", "a-bc")
                .put("a_bC", "a-b-c")
                .put("_aB", "a-b")
                .put("aB_", "a-b")
                .build().entrySet().forEach(e->assertThat(e.getKey(), RestUriFactory.getInstance().lowerHyphenate(e.getKey()), is(e.getValue())));
    }

    private void assertVersionForParentPart(final String parentPart, final String expected) {
        Mockito.doCallRealMethod().when(mockFactory).getVersion(Matchers.any(Class.class));
        Mockito.when(mockFactory.getCanonicalPart(Matchers.eq(RestUriFactory.class), Matchers.eq(-2))).thenReturn(parentPart);

        final String actual = mockFactory.getVersion(RestUriFactory.class);

        assertThat(parentPart, actual, is(expected));
    }
}
