package com.github.tcurrie.rest.factory;

import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.function.Supplier;

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

public class RestUriTest {

    @BeforeClass
    public static void beforeClass() {
        RandomFactory.addRandomGenerator(new RandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(RestUri.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                final String url = RandomFactory.getRandomValue(String.class);
                final String file = RandomFactory.getRandomValue(String.class);
                return RestUri.create(() -> url, file);
            }
        });

        RandomFactory.addRandomGenerator(new ParameterizableRandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(Supplier.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                final String result = RandomFactory.getRandomValue(String.class);
                return (Supplier<Object>) () -> result;
            }

            @Override
            public Object doGenerate(final Parameterizable parameterizedType) {
                final Object result = RandomFactory.getRandomValue(ParameterizableFactory.getInstance(parameterizedType.getParameterTypes().get(0)));
                return (Supplier<Object>) () -> result;
            }
        });
    }

    @Test
    public void testStructure() {
        final Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new BusinessKeyMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .with(new BusinessIdentityTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(RestUri.class));
    }

    @Test
    public void testToString() {
        final String uri = RandomFactory.getRandomValue(String.class);
        final String baseUrl = RandomFactory.getRandomValue(String.class);
        final RestUri restUri = RestUri.create(()->baseUrl, uri);
        assertThat(restUri.toString(), CoreMatchers.is(BusinessIdentity.toString(restUri)));
    }


}
