package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.BusinessKeyMustExistRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.BusinessIdentityTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RestMethodTest {
    @Test
    public void testStructure() throws NoSuchMethodException {
        final Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new BusinessKeyMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .with(new BusinessIdentityTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(RestMethod.class));
    }

    @Test
    public void testToString() {
        final RestMethod<Object, Object> restMethod = new RestMethod<>(RandomFactory.getRandomValue(String.class), null, null, null);
        assertThat(restMethod.toString(), is(BusinessIdentity.toString(restMethod)));
    }

    @Test
    public void testWrapsIOExceptionOnWrite() throws IOException {
        final RestMethod<Object, Object> restMethod = new RestMethod<>(RandomFactory.getRandomValue(String.class), null, null, null);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final String result = RandomFactory.getRandomValue(String.class);
        final IOException ioException = new IOException(RandomFactory.getRandomValue(String.class));
        Mockito.doThrow(ioException).when(response).getWriter();
        try {
            restMethod.writeResponse(response, result, HttpServletResponse.SC_OK);
            fail("Expected RestFactoryException.");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to write response [" + result + "] with status [" + HttpServletResponse.SC_OK + "]."));
            assertThat(e.getCause(), is(ioException));
        }
    }

    @BeforeClass
    public static void initialize() throws NoSuchMethodException {
        final Method method = Object.class.getMethod("toString");

        RandomFactory.addRandomGenerator(new RandomGenerator() {
            @SuppressWarnings("unchecked")
            @Override
            public Collection<Class<?>> getTypes() {
                return Arrays.asList(new Class[]{Method.class});
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return method;
            }
        });
    }

}
