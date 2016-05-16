package com.github.tcurrie.rest.factory;

import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class Strings {
    public static String format(final String message, final Object... parameters) {
        if (parameters.length == 0) {
            return message;
        } else {
            try {
                return MessageFormatter.arrayFormat(message, parameters).getMessage();
            } catch (final Exception e) {
                return message + " " + Arrays.toString(parameters) + "[FORMAT FAILED: " + e.getMessage() + "]";
            }
        }
    }

    public static String getStackTrace(final Throwable exception) {
        final StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}
