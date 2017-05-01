package io.infrastructor.core.utils


public class FlatUUID {
    
    def static flatUUID() {
        UUID.randomUUID().toString().replaceAll("-", "")
    }
}

