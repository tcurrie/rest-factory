package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUri;
import com.google.common.collect.Lists;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.ParameterizableRandomGenerator;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.reflection.Parameterizable;
import com.openpojo.reflection.impl.ParameterizableFactory;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.BusinessKeyMustExistRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.BusinessIdentityTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.assertThat;

public class RestClientMethodTest {

    @BeforeClass
    public static void beforeClass() {
        RandomFactory.addRandomGenerator(new RandomGenerator() {
            @SuppressWarnings("unchecked")
            @Override
            public Collection<Class<?>> getTypes() {
                return Arrays.asList(new Class[]{Method.class});
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return getClass().getMethods()[new Random().nextInt(getClass().getMethods().length)];
            }
        });
        RandomFactory.addRandomGenerator(new ParameterizableRandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(RestResponseAdaptor.Client.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return (RestResponseAdaptor.Client<Object>) s -> s.split(",");
            }

            @Override
            public Object doGenerate(final Parameterizable parameterizedType) {
                final Object result = RandomFactory.getRandomValue(ParameterizableFactory.getInstance(parameterizedType.getParameterTypes().get(0)));
                return (RestResponseAdaptor.Client<Object>) s -> result;
            }
        });
    }


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
        validator.validate(PojoClassFactory.getPojoClass(RestClientMethod.class));
    }

    @Test
    public void testToString() {
        final String uri = RandomFactory.getRandomValue(String.class);
        final String baseUrl = RandomFactory.getRandomValue(String.class);
        @SuppressWarnings("unchecked") final RestClientMethod restMethod = new RestClientMethod(null, RestUri.create(()->baseUrl, uri), null, null, null);
        assertThat(restMethod.toString(), CoreMatchers.is(BusinessIdentity.toString(restMethod)));
    }


}
