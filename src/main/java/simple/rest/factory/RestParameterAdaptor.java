package simple.rest.factory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;

public interface RestParameterAdaptor {
    interface Client extends Function<Object[], String> {
        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestParameterAdaptor.class.getName());
            private static final ObjectMapper MAPPER = new ObjectMapper();
            public static Client create(final Method method) {
                return p -> {
                    try {
                        return MAPPER.writeValueAsString(p);
                    } catch (final JsonProcessingException e) {
                        throw RestFactoryException.create(LOGGER, "Failed to map [{0}].", e, p);
                    }
                };
            }
        }
    }

    interface Service extends Function<HttpServletRequest, Object[]> {
        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestParameterAdaptor.class.getName());
            private static final ObjectMapper MAPPER = new ObjectMapper();

            public static Service create(final Method method) {
                if (method.getParameterCount() == 0) {
                    return r -> new Object[0];
                } else {
                    final int parameters = method.getParameterCount();
                    validateParameters(method);

                    return r -> {
                        final Object[] args = new Object[parameters];
                        try {
                            final JsonParser p = new JsonFactory().createParser(r.getReader());
                            System.out.println(p.nextToken());
                            for (int i = 0; i < parameters; i++) {
                                System.out.println(p.nextToken());
                                args[i] = MAPPER.readValue(p, method.getParameters()[i].getType());
                            }
                            return args;
                        } catch (final Exception e) {
                            throw RestFactoryException.create(LOGGER, "Failed to read arguments, got [{0}].", e, Arrays.toString(args));
                        }
                    };
                }
            }

            private static void validateParameters(final Method method) {
                Arrays.stream(method.getParameters())
                        .filter(Factory::notPrimitive)
                        .filter(Factory::notPrimitiveArray)
                        .forEach(p->{
                    try {
                        p.getType().getDeclaredConstructor();
                    } catch (final NoSuchMethodException e) {
                        throw RestFactoryException.create(LOGGER, "Can not wire method adaptor for [{0}], parameter [{1}] type has no default constructor.", e, method, p);
                    }
                });
            }

            private static boolean notPrimitive(final Parameter p) {
                return !p.getType().isPrimitive();
            }

            private static boolean notPrimitiveArray(final Parameter p) {
                return !p.getType().isArray() || !p.getType().getComponentType().isPrimitive();
            }
        }
    }

}
