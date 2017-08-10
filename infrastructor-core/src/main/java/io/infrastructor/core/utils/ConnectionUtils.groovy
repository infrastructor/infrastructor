package io.infrastructor.core.utils

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
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
                socket = new Socket()
                socket.connect(new InetSocketAddress(host, port), 1000);
                return true
            } catch (IOException e) {
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

