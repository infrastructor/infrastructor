package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.HttpUtils.httpGet
import static io.infrastructor.core.utils.JsonUtils.json

class HttpGetActionTest {
    @Test
    void callJsonRestService() {
        def result = httpGet url: 'https://jsonplaceholder.typicode.com/posts/1'
        assert result.code == 200
        
        def post = json(result.content)
        assert post.userId == 1
        assert post.id == 1
        assert post.title.contains('sunt aut facere')
    }
    
    @Test(expected = UnknownHostException)
    void callUnknownHost() {
        def result = httpGet url: 'http://__unknown__'
    }
}

