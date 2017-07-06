package io.infrastructor.cli.logging.status

import static io.infrastructor.cli.logging.ConsoleLogger.*

class TextStatusLogger {
	
    def text
    
    def listener = {}
    
    public String statusLine() {
        text
    }
    
    public void setStatus(def text) {
        this.text = text
        listener()
    }
    
    public static def withTextStatus(Closure closure) {
        withTextStatus('', closure)
    }
    
    public static def withTextStatus(String initial, Closure closure) {
        def status = new TextStatusLogger(text: initial)
        try {
            addStatusLogger status
            return closure(status.&setStatus)
        } finally {
            removeStatusLogger status
        }
    }
}

