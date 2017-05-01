package io.infrastructor.core.tasks

import io.infrastructor.core.inventory.Node
import io.infrastructor.core.processing.ActionProcessingException
import static io.infrastructor.cli.ConsoleLogger.*


public class WaitForPortAction {
    
    def port
    def delay = 1000
    def attempts = 3
    
    def execute(Node node) {
        info("waiting for $node.host:$port [id: $node.id]...")
        
        for(int i = 0; i < attempts; i++) {
            debug("attempt $i, trying to connect to $node.host:$port")
            
            def result = attempt(node.host)
            if (result)  {
                debug("connection to $node.host:$port succeed!")
                return
            }
            sleep(delay)
        }
        
        throw new ActionProcessingException("trying to connect to $node.host:$port failed after $attempts attempts with $delay delay")
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

