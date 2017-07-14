package io.infrastructor.core.utils

import java.util.concurrent.Executors
import java.util.concurrent.ExecutionException

class ParallelUtils {

    public static def executeParallel(def collection, def threads, def closure) {
        def executor = Executors.newFixedThreadPool(threads)
        
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

