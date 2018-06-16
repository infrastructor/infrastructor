package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.JsonUtils.json

class JsonUtilsTest {
    @Test
    void parseDataFromString() {
        def result = json '{ "message": "simple" }'

        assert result.message == "simple"
    }
    
    @Test
    void parseDataFromStream() {
        def result = json (new ByteArrayInputStream('{ "message": "simple" }' as byte []))

        assert result.message == "simple"
    }
}

