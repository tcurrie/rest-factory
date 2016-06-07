package com.github.tcurrie.rest.factory.v1;

import com.github.tcurrie.rest.factory.proxy.Methods;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.BusinessKeyMustExistRule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.BusinessIdentityTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestMethodVerificationResultTest {

    @Test
    public void testStructure() throws NoSuchMethodException {
        final Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new BusinessKeyMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .with(new BusinessIdentityTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(RestMethodVerificationResult.class));
    }

    @Test
    public void testToString() {
        final RestMethodVerificationResult restMethod = RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), RandomFactory.getRandomValue(String.class), RandomFactory.getRandomValue(String[].class), RandomFactory.getRandomValue(String[].class));
        assertThat(restMethod.toString(), is(BusinessIdentity.toString(restMethod)));
    }

    @Test
    public void testGetErrorsIncludesFailed() {
        final String api = Methods.TypeFactory.get(Foo.class, "bar").toGenericString();
        final String[] args = RandomFactory.getRandomValue(String[].class);
        final Set<RestMethodVerificationResult> expected = Stream.of(
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), api, null, null),
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), api, RandomFactory.getRandomValue(String[].class), null),
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), api, RandomFactory.getRandomValue(String[].class), new RuntimeException(RandomFactory.getRandomValue(String.class)))
        ).collect(Collectors.toSet());
        final Set<RestMethodVerificationResult> initial = Stream.of(
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, null, null),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, args, args)
        ).collect(Collectors.toSet());
        initial.addAll(expected);

        final Set<RestMethodVerificationResult> actual = RestMethodVerificationResult.getErrorsAndMissing(initial, Foo.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetErrorsIncludesUnMatchedArgs() {
        final String api = Methods.TypeFactory.get(Foo.class, "bar").toGenericString();
        final String[] args = RandomFactory.getRandomValue(String[].class);
        final Set<RestMethodVerificationResult> expected = Stream.of(
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, args, null),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, null, args),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, args, RandomFactory.getRandomValue(String[].class))
        ).collect(Collectors.toSet());
        final Set<RestMethodVerificationResult> initial = Stream.of(
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, null, null),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), api, args, args)
        ).collect(Collectors.toSet());
        initial.addAll(expected);

        final Set<RestMethodVerificationResult> actual = RestMethodVerificationResult.getErrorsAndMissing(initial, Foo.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetErrorsIncludesMissingApi() {
        final String api = Methods.TypeFactory.get(Foo.class, "bar").toGenericString();
        final String other = RandomFactory.getRandomValue(String.class);
        final String[] args = RandomFactory.getRandomValue(String[].class);
        final Set<RestMethodVerificationResult> expected = Stream.of(
                RestMethodVerificationResult.createFailure("???", api, null, new RestFactoryException("Failed to find result."))
        ).collect(Collectors.toSet());
        final Set<RestMethodVerificationResult> initial = Stream.of(
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), other, null, null),
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), other, RandomFactory.getRandomValue(String[].class), null),
                RestMethodVerificationResult.createFailure(RandomFactory.getRandomValue(String.class), other, RandomFactory.getRandomValue(String[].class), new RuntimeException(RandomFactory.getRandomValue(String.class))),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), other, args, null),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), other, null, args),
                RestMethodVerificationResult.createSuccess(RandomFactory.getRandomValue(String.class), other, args, RandomFactory.getRandomValue(String[].class))
        ).collect(Collectors.toSet());

        final Set<RestMethodVerificationResult> actual = RestMethodVerificationResult.getErrorsAndMissing(initial, Foo.class);
        assertThat(actual, is(expected));
    }

    @SuppressWarnings("unused")
    private interface Foo {
        void bar();
    }

}
