package io.infrastructor.core.utils

class FilteringUtils {

    def static match(def tags, def filter) {
        def proxy = ProxyMetaClass.getInstance(String.class)
        proxy.use {
            def closure = { context -> context.contains(delegate) }
            String.metaClass.asBoolean = closure.curry(tags)
            return (filter() as Boolean)
        }
    }
}

