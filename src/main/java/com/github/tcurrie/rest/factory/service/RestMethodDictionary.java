package com.github.tcurrie.rest.factory.service;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.util.Set;

public interface RestMethodDictionary {
    Set<MethodDescription> getMethods();

    final class MethodDescription {
        @BusinessKey private String uri;
        private String method;
        private String bean;

        public static MethodDescription create(final RestMethod h) {
            return new MethodDescription(h.getUri(), h.getMethod().getName(), h.getBean().getClass().getCanonicalName());
        }
        private MethodDescription() {}

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
        public int hashCode() {
            return BusinessIdentity.getHashCode(this);
        }

        @Override
        public boolean equals(final Object obj) {
            return BusinessIdentity.areEqual(this, obj);
        }

        @Override
        public String toString() {
            return BusinessIdentity.toString(this);
        }
    }
}
