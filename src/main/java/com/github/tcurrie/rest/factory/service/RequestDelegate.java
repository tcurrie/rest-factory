package com.github.tcurrie.rest.factory.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

interface RequestDelegate {
    List<RestServiceMethod> getHandlers(HttpServletRequest request);
}
