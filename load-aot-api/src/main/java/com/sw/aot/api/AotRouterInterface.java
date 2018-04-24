package com.sw.aot.api;

import java.util.HashMap;

public interface AotRouterInterface {

    HashMap<String, String> getMethodMap();

    HashMap<String, Class<?>> getClassMap();
}
