package com.github.tcurrie.rest.factory;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestUriFactoryTest {
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

    private void assertVersionForParentPart(final String parentPart, final String expected) {
        final RestUriFactory mockFactory = Mockito.mock(RestUriFactory.class);


        Mockito.doCallRealMethod().when(mockFactory).getVersion(Matchers.any());
        Mockito.when(mockFactory.getCanonicalPart(Matchers.eq(RestUriFactory.class), Matchers.eq(-2))).thenReturn(parentPart);

        final String actual = mockFactory.getVersion(RestUriFactory.class);

        assertThat(parentPart, actual, is(expected));
    }
}
