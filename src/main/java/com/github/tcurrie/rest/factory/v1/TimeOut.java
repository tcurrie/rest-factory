package com.github.tcurrie.rest.factory.v1;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.util.concurrent.TimeUnit;

@SuppressWarnings({"WeakerAccess", "EqualsWhichDoesntCheckParameterClass"})
public class TimeOut {
    @BusinessKey private final long time;
    @BusinessKey private final TimeUnit timeUnit;

    public static TimeOut create(final long time, final TimeUnit timeUnit) {
        return new TimeOut(time, timeUnit);
    }

    public static TimeOut convert(final TimeOut current, final TimeUnit timeUnit) {
        return create(timeUnit.convert(current.time, current.timeUnit), timeUnit);
    }

    private TimeOut(final long time, final TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public long getTime() {
        return time;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public boolean equals(final Object o) {
        return BusinessIdentity.areEqual(this, o);
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }
}
