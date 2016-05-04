package simple.rest.factory.client;

import org.springframework.web.client.RestTemplate;
import simple.rest.factory.RestParameterAdaptor;
import simple.rest.factory.RestUriFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class RestClientFactory {
    private static final Logger LOGGER = Logger.getLogger(RestClientFactory.class.getName());

    public static <T> T create(final Class<T> service, final Supplier<String> urlSupplier) {
        final Map<Method, Function<Object[], Object>> methodHandlers =
                Arrays.stream(service.getMethods()).collect(
                        Collectors.toMap(
                                m -> m,
                                m -> {
                                    final Supplier<String> methodUrlSupplier = createMethodUrlSupplier(urlSupplier, service, m);
                                    final RestParameterAdaptor.Client methodArgs = RestParameterAdaptor.Client.Factory.create(m);
                                    final Class<?> methodResult = createMethodResult(m);
                                    return args -> {
                                        final String url = methodUrlSupplier.get();
                                        final String body = methodArgs.apply(args);
                                        LOGGER.log(Level.INFO, "For method [{0}] and args [{1}], posting to [{2}] with [{3}]", new Object[]{m, args, url, body});
                                        //TODO Remove dependency on Spring's rest template and/or at least handle timeouts!
                                        return new RestTemplate().postForObject(url, body, methodResult);
                                    };
                                }
                        )
                );
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, (proxy, method, args) -> methodHandlers.get(method).apply(args));
    }

    private static <T> Supplier<String> createMethodUrlSupplier(final Supplier<String> urlSupplier, final Class<T> service, final Method method) {
        final String methodUri = RestUriFactory.create(service, method);
        return () -> removeSlash(urlSupplier.get()) + methodUri;
    }

    private static String removeSlash(final String url) {
        return url.replaceAll("/*$", "");
    }

    private static Class<?> createMethodResult(final Method method) {
        return method.getReturnType();
    }


}
