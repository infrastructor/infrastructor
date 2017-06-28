package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.ActionProcessingException
import javax.validation.constraints.NotNull

public class WaitForPortAction {
    
    @NotNull
    def port
    def delay = 1000
    def attempts = 3
    
    def execute(def node, def logger) {
        logger.info("waiting for node: $node.host:$port, id: $node.id")
        
        for(int i = 0; i < attempts; i++) {
            logger.debug("attempt $i, trying to connect to $node.host:$port")
            
            def result = attempt(node.host)
            if (result)  {
                logger.debug("connection to $node.host:$port succeed!")
                return
            }
            sleep(delay)
        }
        
        throw new ActionProcessingException("unable to connect to $node.host:$port, attempts: $attempts, delay: $delay")
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
                try {socket.close()} catch(Exception e) {}
            }
        }
    }
}

