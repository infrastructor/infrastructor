package io.infrastructor.core.utils

class HttpUtils {
    
    def static httpGet(Map params) {
        httpGet(params, {})
    }
    
    def static httpGet(Closure closure) { 
        httpGet([:], closure) 
    }
    
    def static httpGet(Map params, Closure closure) {
        def action = new HttpGetAction(params)
        action.with(closure)
        action.execute()
    }
}

