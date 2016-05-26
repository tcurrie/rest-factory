package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
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
    @Test
    public void testCreatesWithSortedSet() {
        final Set<RestServiceMethod> expected = RestMethodFactory.create(new TestService()).collect(Collectors.toSet());

        final UriSetRestHandlerDictionary d = UriSetRestHandlerDictionary.create(expected.stream());
        final HttpServletRequest request = mock(HttpServletRequest.class);

        expected.forEach(h -> {
            when(request.getRequestURI()).thenReturn(h.getUri());
            final List<RestServiceMethod> strategies = d.getHandlers(request);
            assertThat(strategies, hasItem(h));
            strategies.forEach(s-> {
                assertThat(s.getUri(), is(h.getUri()));
            });
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
}
