package io.infrastructor.core.processing

class TaskExecutionException extends RuntimeException {
    
    def task
    def exceptions
        
    public TaskExecutionException(def task, def exceptions) {
        this.task = task
        this.exceptions = exceptions
    }
}

