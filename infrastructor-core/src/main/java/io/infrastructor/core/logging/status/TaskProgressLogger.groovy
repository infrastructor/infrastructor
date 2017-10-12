package io.infrastructor.core.logging.status

import static io.infrastructor.core.logging.ConsoleLogger.*

class TaskProgressLogger {
    
    def name = ''
    def nodes = [:].asSynchronized() 
    def listener = {}
    def progress = 0
    
    def synchronized increase() {
        progress++
        listener()
    }
    
    def updateStatus(def nodeName, def status) {
        nodes[nodeName] = status
        listener()
    }
    
    public String statusLine() {
        def status = new StringBuilder()
        status << "> task: '$name', progress: $progress / ${nodes.size()} node|s" << "\n"
        nodes.each { node, nodeStatus ->
            status << "  node: '$node', status: $nodeStatus" << "\n"
        }
        return status
    }
    
    public static void withTaskProgressStatus(def name, Closure closure) {
        debug "withTaskProgressStatus: $name"
        
        def logger = new TaskProgressLogger(name: name)
        
        try {
            addStatusLogger logger
            closure(logger)
        } finally {
            removeStatusLogger logger
        }
    }
}

