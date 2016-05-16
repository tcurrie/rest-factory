package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.model.RestFactoryException;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public final class Strings {
    private Strings() {
        throw RestFactoryException.create("Can not construct instance of Factory class.");
    }

    public static String format(final String message, final Object... parameters) {
        if (message == null) {
            return Arrays.toString(parameters);
        } else if (parameters == null || parameters.length == 0) {
            return message;
        } else if (getMarkerCount(message) < parameters.length) {
            return format("Message[{}], parameters[{}]. Format failed.", message, parameters);
        } else {
            return MessageFormatter.arrayFormat(message, parameters).getMessage();
        }
    }

    private static int getMarkerCount(final String message) {
        int count = 0;
        for (int index = message.indexOf("{}"); index > -1; index = message.indexOf("{}", index + 2)) {
            count++;
        }
        return count;
    }

    public static String getStackTrace(final Throwable exception) {
        final StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}
