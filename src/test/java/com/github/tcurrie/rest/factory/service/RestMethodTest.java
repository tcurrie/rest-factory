package com.github.tcurrie.rest.factory.service;

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
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertThat;

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
        final RestMethod<Object, Object> restMethod = new RestMethod<>(RandomFactory.getRandomValue(String.class), null, null, null, null);
        assertThat(restMethod.toString(), CoreMatchers.is(BusinessIdentity.toString(restMethod)));
    }

    @BeforeClass
    public static void initialize() throws NoSuchMethodException {
        final Method method = Object.class.getMethod("toString");

        RandomFactory.addRandomGenerator(new RandomGenerator() {
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
