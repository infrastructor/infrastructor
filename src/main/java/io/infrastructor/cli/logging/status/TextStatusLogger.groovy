package io.infrastructor.cli.logging.status

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
    
}

