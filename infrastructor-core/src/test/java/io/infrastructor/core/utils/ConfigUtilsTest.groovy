package io.infrastructor.core.utils

import org.junit.Test

class ConfigUtilsTest {
    @Test
    void loadConfigFromFile() {
        def conf = ConfigUtils.config("build/resources/test/config/test.conf")
        
        assert conf
        assert conf.message == 'loaded!'
        assert conf.context.internal == 'internal'
    }
    
    
    @Test(expected = FileNotFoundException)
    void loadMissingConfigFromFile() {
        def conf = ConfigUtils.config("build/resources/test/config/missing.conf")
    }
}

