package simple.rest.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface RestResponseAdaptor<U> extends Function<U, Consumer<HttpServletResponse>> {
    final class Factory {
        private static final Logger LOGGER = Logger.getLogger(RestResponseAdaptor.class.getName());
        private static final ObjectMapper MAPPER = new ObjectMapper();

        public static <U> RestResponseAdaptor<U> create() {
            return result -> (response -> {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("Content-Type", "application/json");
                try {
                    LOGGER.log(Level.FINE, "Adapting result [{0}] to response.", new Object[]{result});
                    MAPPER.writeValue(response.getWriter(), result);
                } catch (final IOException e) {
                    throw RestFactoryException.create(LOGGER, Level.WARNING, "Failed to adapt result [{0}] to response.", e, result);
                }
            });
        }
    }
}
