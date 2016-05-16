package com.github.tcurrie.rest.factory;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.RandomFactory;
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
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class RestUriTest {
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
