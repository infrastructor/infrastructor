package io.infrastructor.core.utils

import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class ParallelUtils {

    def static executeParallel(def collection, def threads, def closure) {
        def executor = Executors.newFixedThreadPool(threads as Integer)
        
        try {
            def futures = collection.collect { item -> executor.submit { closure(item) } }
            try {
                futures*.get()
            } catch (ExecutionException ex) {
                throw ex.getCause()
            }
        } finally {
            executor.shutdown() 
        }
    }
}

