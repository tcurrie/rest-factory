package com.github.tcurrie.rest.factory.v1;

import java.util.Set;

public interface RestClientMonitor {
    Set<RestMethodVerificationResult> verifyClients();

}
