package com.github.tcurrie.rest.factory;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoggerStructuralTest {
    private static final int EXPECTED_LOGGERS_DECLARED = 13;

    @Test
    public void loggersMustBeDeclaredPrivateStaticFinalAndUseEnclosingClassName() {
        final List<PojoClass> pojoClasses = PojoClassFactory.getPojoClassesRecursively("com.github.tcurrie", null);

        final AtomicInteger foundLoggers = new AtomicInteger(0);
        pojoClasses.stream().forEach(pojoClass->{
            pojoClass.getPojoFields().stream().filter(field -> field.getType().equals(Logger.class)).forEach(
                    field -> {
                        foundLoggers.incrementAndGet();
                        assertEquals("Field name should be LOGGER", "LOGGER", field.getName());
                        assertTrue("Logger must be private", field.isPrivate());
                        assertTrue("Logger must be static " + pojoClass, field.isStatic());
                        assertTrue("Logger must be final", field.isFinal());
                        final Logger instance = (Logger) field.get(null);
                        assertEquals("Logger must be for enclosing class", pojoClass.getName(), instance.getName());
                    });
        });
        assertEquals("Added / removed Loggers?", EXPECTED_LOGGERS_DECLARED, foundLoggers.get());
    }

}
