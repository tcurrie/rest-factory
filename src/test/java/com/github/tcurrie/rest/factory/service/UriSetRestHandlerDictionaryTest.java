package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethod;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.openpojo.random.RandomFactory;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UriSetRestHandlerDictionaryTest {
    private RestServiceMethod dictionaryMethod;
    private String url;
    private String context;
    private String servlet;
    private HttpServletRequest request;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Before
    public void createRequest() {
        dictionaryMethod = RestMethodFactory.create((RestMethodDictionary)()->null).findFirst().get();
        url = RandomFactory.getRandomValue(String.class) + "://" + RandomFactory.getRandomValue(String.class);
        context = "/" + RandomFactory.getRandomValue(String.class);
        servlet = "/" + RandomFactory.getRandomValue(String.class);
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(context + servlet + dictionaryMethod.getUri());
        when(request.getRequestURL()).thenReturn(new StringBuffer(url + context + servlet + dictionaryMethod.getUri()));
        when(request.getServletPath()).thenReturn(servlet);
        when(request.getContextPath()).thenReturn(context);
    }

    @Test
    public void testCreatesWithSortedSet() {
        final Set<RestServiceMethod> expected = RestMethodFactory.create(new TestService()).collect(Collectors.toSet());

        final UriSetRestHandlerDictionary d = UriSetRestHandlerDictionary.create(expected.stream());

        expected.forEach(h -> {
            when(request.getRequestURI()).thenReturn(h.getUri());
            final List<RestServiceMethod> strategies = d.getHandlers(request);
            assertThat(strategies, hasItem(h));
            strategies.forEach(s-> assertThat(s.getUri(), is(h.getUri())));
        });
    }

    @Test
    public void testCreationFailsForDuplicateImplementations() {
        final List<RestServiceMethod> expected = RestMethodFactory.create(new TestService()).collect(Collectors.toList());
        try {
            UriSetRestHandlerDictionary.create(Stream.of(expected.get(0), expected.get(0)));
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is(Strings.format("Illegal method overloading [{}] vs [{}].  Rest method will not accurately reflect the compile time behavior.", expected.get(0), expected.get(0))));
        }
    }

    @Test
    public void testAddsRestMethodDictionaryForAllMethods() throws Throwable {
        final Set<RestServiceMethod> methods = RestMethodFactory.create(new TestService()).collect(Collectors.toSet());

        final UriSetRestHandlerDictionary d = UriSetRestHandlerDictionary.create(methods.stream());

        methods.add(dictionaryMethod);
        final Set<RestMethod> expected = methods.stream().map(
                m->RestMethod.create(url + context + servlet + m.getUri(), m.getImplementation().getApi())
        ).collect(Collectors.toSet());

        final RestServiceMethod actualDictionaryMethod = d.getHandlers(request).get(0);

        final String result = actualDictionaryMethod.invoke(null);
        System.out.println(result);
        final Set<RestMethod> actual = adapt(result);

        assertThat(actual, is(expected));
    }

    @SuppressWarnings("unchecked")
    private Set<RestMethod> adapt(final String result) throws Throwable {
        final RestResponseAdaptor.Client adaptor = RestResponseAdaptor.Client.Factory.create(Methods.TypeFactory.get(RestMethodDictionary.class, "getMethods"));
        return (Set<RestMethod>) adaptor.apply(result);
    }

}
