package com.github.tcurrie.rest.factory.service;

import javax.servlet.http.HttpServletRequest;

interface RequestDelegate {
    RestMethod getHandler(HttpServletRequest request);
}
