package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.v1.ExceptionWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;

import java.io.IOException;
import java.lang.reflect.Method;

public interface EchoResponseAdaptor {
    RestResponseAdaptor.Service<Object[]> SERVICE = RestResponseAdaptor.Service.Factory.create();

    interface Client {
        RestMethodVerificationResult apply(String url, Object[] args, String response) throws IOException;
        class Factory {
            private Factory() {
                throw new RestFactoryException("Can not construct instance of Factory class.");
            }
            private static final ObjectMapper mapper = new ObjectMapper();
            public static Client create(final Method method) {
                final String api = method.toGenericString();
                final JsonAdaptor methodArgAdaptor = JsonAdaptor.Factory.create(method);
                final JavaType exceptionWrapperType = mapper.getTypeFactory().constructType(ExceptionWrapper.class);
                return (url, args, response) -> {
                    final JsonNode n = mapper.readTree(response);
                    if (n.get("success").asBoolean()) {
                        final Object[] result = methodArgAdaptor.apply(n.get("result").toString());
                        return RestMethodVerificationResult.createSuccess(url, api, args, result);
                    } else {
                        final ExceptionWrapper ew = mapper.readValue(n.get("exception").toString(), exceptionWrapperType);
                        return RestMethodVerificationResult.createFailure(url, api, args, RestExceptionAdaptor.Client.Factory.create(ew));
                    }
                };
            }
        }
    }
}
