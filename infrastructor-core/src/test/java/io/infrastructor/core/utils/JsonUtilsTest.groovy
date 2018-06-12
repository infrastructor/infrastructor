package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.JsonUtils.json

class JsonUtilsTest {
    @Test
    public void parseDataFromString() {
        def result = json '{ "message": "simple" }'
        
        assert result.message == "simple"
    }
    
    @Test
    public void parseDataFromStream() {
        def result = json (new ByteArrayInputStream('{ "message": "simple" }' as byte []))
        
        assert result.message == "simple"
    }
}

