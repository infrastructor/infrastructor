package io.infrastructor.core.utils

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.validation.ValidationHelper.validate

class ConnectionUtils {
    
    static class Endpoint {
        @NotNull
        def host
        @Min(1l)
        int port
        
        boolean isConnectable() {
            def socket = null
            try {
                debug "ConnectionUtils :: checking connection to $host:$port ..."
                socket = new Socket()
                socket.connect(new InetSocketAddress(host, port), 1000)
                debug "ConnectionUtils :: checking connection to $host:$port - success"
                return true
            } catch (IOException ex) {
                debug "ConnectionUtils :: checking connection to $host:$port - failed: $ex.message"
                return false
            } finally {
                if (socket != null) {
                    try { socket.close() } catch (IOException e) {}
                }
            }
        }
    }
    
    static boolean canConnectTo(Map params) {
        canConnectTo(params, {})
    } 

    static boolean canConnectTo(Closure closure) {
        canConnectTo([:], closure)
    } 
    
    static boolean canConnectTo(Map params, Closure closure) {
        def endpoint = new Endpoint(params)
        endpoint.with(closure)
        validate(endpoint)
        endpoint.isConnectable()
    } 
}

