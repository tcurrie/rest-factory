package simple.rest.factory.service;

import java.util.List;

public interface RestMethodDictionary {
    List<MethodDescription> getMethods();

    final class MethodDescription {
        private final String uri;
        private final String method;
        private final String bean;

        public static MethodDescription create(final RestMethod h) {
            return new MethodDescription(h.getUri(), h.getMethod().getName(), h.getBean().getClass().getCanonicalName());
        }

        private MethodDescription(final String uri, final String method, final String bean) {
            this.uri = uri;
            this.method = method;
            this.bean = bean;
        }

        public String getUri() {
            return uri;
        }

        public String getMethod() {
            return method;
        }

        public String getBean() {
            return bean;
        }

        @Override
        public String toString() {
            return "MethodDescription{" +
                    "uri='" + uri + '\'' +
                    ", method='" + method + '\'' +
                    ", bean='" + bean + '\'' +
                    '}';
        }
    }
}
