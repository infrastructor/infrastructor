package io.infrastructor.core.inventory.aws.managed

import static io.infrastructor.cli.logging.ConsoleLogger.*
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromEC2

public class Route53 {
    
    def hostedZoneId
    def records = []
    
    def recordSet(Map params) {
        recordSet(params, {})
    }    
    
    def recordSet(Closure closure) {
        recordSet([:], closure)
    }
    
    def recordSet(Map params, Closure closure) {
        def record = new RecordSet(params)
        record.with(closure)
        records << record
    }
    
    def apply(def amazonEC2, def amazonRoute53) {
        info "route53 - applying DNS name for zone $hostedZoneId"
        records*.apply(amazonRoute53, hostedZoneId, fromEC2(amazonEC2))
    }
}

