package io.infrastructor.cli.logging.status

import static io.infrastructor.cli.logging.ProgressLogger.*

class TextStatusLogger {
	
    def text
    
    def listener = {}
    
    public String status() {
        text
    }
    
    public void status(def text) {
        this.text = text
        listener()
    }
    
    public def leftShift(def text) {
        status(text)
    }
    
    public static void withTextStatus(Closure closure) {
        def status = new TextStatusLogger()
        try {
            addStatusLogger status
            closure(status)
        } finally {
            removeStatusLogger status
        }
    }
}

