package io.infrastructor.core.utils

import groovy.json.JsonSlurper

class JsonUtils {
    
    def static json(def content) {
        new JsonSlurper().parse(content)
    }
    
    def static json(String content) {
        new JsonSlurper().parseText(content)
    }
}

