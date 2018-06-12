package io.infrastructor.core.utils

import io.infrastructor.core.validation.ValidationException
import org.junit.Test

import static io.infrastructor.core.utils.ConnectionUtils.canConnectTo

class ConnectionUtilsTest {
    @Test
    void checkAvailbleConnection() {
        assert canConnectTo(host: 'google.com', port: 443)
        assert canConnectTo(host: 'google.com') { port = 443 }
    }
    
    @Test
    void checkUnvailbleConnection() {
        assert canConnectTo(host: 'google.com', port: 444) == false
        assert false == canConnectTo {
            host = 'google.com'
            port = 444
        }
    }
    
    @Test(expected = ValidationException)
    void invalidHost() {
        assert canConnectTo(port: 443)
    }
    
    @Test(expected = ValidationException)
    void invalidPort() {
        assert canConnectTo(host: 'google.com', port: 0)
    }
}

