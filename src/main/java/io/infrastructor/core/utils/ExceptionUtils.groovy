package io.infrastructor.core.utils

import org.codehaus.groovy.runtime.StackTraceUtils

class ExceptionUtils {
    
    public static def deepSanitize(Throwable ex) {
        StringWriter writer = new StringWriter();
        StackTraceUtils.printSanitizedStackTrace(ex, new PrintWriter(writer))
        writer.toString()
    }
}

