package io.infrastructor.core.utils

class FlatUUID {
    
    def static flatUUID() {
        UUID.randomUUID().toString().replaceAll("-", "")
    }
}

