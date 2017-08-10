package io.infrastructor.core.utils

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

class HttpGetAction {
    
    def url
    
    def execute() {
        HttpClient client = HttpClientBuilder.create().build()
        HttpResponse response = client.execute(new HttpGet(url))
        return [
            code: response.getStatusLine().getStatusCode(),
            content: response.getEntity().getContent()
        ]
    }
}

