package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.logging.ConsoleLogger.*

public class WaitForPortAction {
    
    @NotNull
    def port
    def delay = 1000
    def attempts = 3
    
    def execute(def node) {
       info("waiting for node: $node.host:$port, id: $node.id")
        
        for(int i = 0; i < attempts; i++) {
            debug("attempt $i, trying to connect to $node.host:$port")
            
            def result = attempt(node.host)
            if (result)  {
                debug("connection to $node.host:$port succeed!")
                return
            }
            sleep(delay)
        }
        
        throw new ActionExecutionException("unable to connect to $node.host:$port, attempts: $attempts, delay: $delay")
    }
    
    def attempt(def host) {
        def socket = null
        try {
            socket = new Socket()
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true
        } catch (Exception e) {
            return false
        } finally {
            if(socket != null) {
                try { socket.close() } catch(Exception e) {}
            }
        }
    }
}

