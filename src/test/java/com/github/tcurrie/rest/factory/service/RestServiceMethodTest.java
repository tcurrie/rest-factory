package com.github.tcurrie.rest.factory.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Random;

import com.github.tcurrie.rest.factory.MethodRandomGenerator;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.proxy.MethodImplementation;
import com.google.common.collect.Lists;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.ParameterizableRandomGenerator;
import com.openpojo.random.RandomFactory;
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
import org.junit.BeforeClass;
import org.junit.Test;

public class RestServiceMethodTest {
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
        validator.validate(PojoClassFactory.getPojoClass(RestServiceMethod.class));
    }

    @Test
    public void testToString() {
        final RestServiceMethod<Object, Object> restServiceMethod = new RestServiceMethod<>(RandomFactory.getRandomValue(String.class), null, null, null);
        assertThat(restServiceMethod.toString(), is(BusinessIdentity.toString(restServiceMethod)));
    }

    @BeforeClass
    public static void initialize() {
        MethodRandomGenerator.create();
        RandomFactory.addRandomGenerator(new ParameterizableRandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(RestResponseAdaptor.Service.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return (RestResponseAdaptor.Service<Object>) Object::toString;
            }

            @Override
            public Object doGenerate(final Parameterizable parameterizedType) {
                return (RestResponseAdaptor.Service<Object>) Object::toString;
            }
        });
        RandomFactory.addRandomGenerator(new ParameterizableRandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(MethodImplementation.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                final Object bean = RandomFactory.getRandomValue(String.class);
                return MethodImplementation.create(bean, bean.getClass().getMethods()[new Random().nextInt(bean.getClass().getMethods().length)]);
            }

            @Override
            public Object doGenerate(final Parameterizable parameterizedType) {
                final Object bean = RandomFactory.getRandomValue(ParameterizableFactory.getInstance(parameterizedType.getParameterTypes().get(0)));

                return MethodImplementation.create(bean, bean.getClass().getMethods()[new Random().nextInt(bean.getClass().getMethods().length)]);
            }
        });
    }

}
