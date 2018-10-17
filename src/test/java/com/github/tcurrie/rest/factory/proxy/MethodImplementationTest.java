package com.github.tcurrie.rest.factory.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.tcurrie.rest.factory.MethodRandomGenerator;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoElement;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.BusinessKeyMustExistRule;
import com.openpojo.validation.test.impl.BusinessIdentityTester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Before;
import org.junit.Test;

public class MethodImplementationTest {
    @Before
    public void before() {
        MethodRandomGenerator.create();
    }


    @Test
    public void testStructure() {
        final Validator validator = ValidatorBuilder.create()
                .with(new BusinessKeyMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .with(new BusinessIdentityTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(MethodImplementation.class));
    }

    @Test
    public void testToString() {
        final MethodImplementation restMethod = MethodImplementation.create(RandomFactory.getRandomValue(String.class), RandomFactory.getRandomValue(Method.class));
        assertThat(restMethod.toString(), is(BusinessIdentity.toString(restMethod)));
    }


    @Test
    public void testBusinessKey() {
        final Set<String> expected = Stream.of("type", "name", "parameters").collect(Collectors.toSet());
        final PojoClass pojo = PojoClassFactory.getPojoClass(MethodImplementation.class);
        final Set<String> businessKeys = pojo.getPojoFieldsAnnotatedWith(BusinessKey.class).stream().map(PojoElement::getName).collect(Collectors.toSet());
        assertThat(businessKeys, is(expected));
    }

    @Test
    public void testThatOverloadedMethodsAreEqual() {
        final MethodImplementation a = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", int.class));
        final MethodImplementation b = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", String.class));
        assertThat(a, is(b));
        assertThat(a.hashCode(), is(b.hashCode()));
    }

    @Test
    public void testThatOverloadedMethodsWithDiffereentArgCountAreNotEqual() {
        final MethodImplementation a = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", int.class));
        final MethodImplementation b = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", int.class, int[].class));
        assertThat(a, not(b));
        assertThat(a.hashCode(), not(b.hashCode()));
    }

    @Test
    public void testThatMethodsWithDifferentNamesAreNotEqual() {
        final MethodImplementation a = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", int.class));
        final MethodImplementation b = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "b", int.class));
        assertThat(a, not(b));
        assertThat(a.hashCode(), not(b.hashCode()));
    }

    @Test
    public void testThatMethodsWithDifferentApiAreNotEqual() {
        final MethodImplementation a = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Overloads.class, "a", int.class));
        final MethodImplementation b = MethodImplementation.create(RandomFactory.getRandomValue(String.class), Methods.TypeFactory.get(Other.class, "a", int.class));
        assertThat(a, not(b));
        assertThat(a.hashCode(), not(b.hashCode()));
    }

    @SuppressWarnings("unused")
    private interface Overloads {
        void a(int i);
        void a(String s);
        void a(int i, int... is);
        void b(int i);
    }

    @SuppressWarnings("unused")
    private interface Other {
        void a(int i);
    }

}
