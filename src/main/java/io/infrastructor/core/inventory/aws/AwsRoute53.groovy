package io.infrastructor.core.inventory.aws

import static io.infrastructor.cli.ConsoleLogger.info
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromEC2

public class AwsRoute53 {
    
    def hostedZoneId
    def dnsList = []
    
    def dns(Map params) {
        dns(params, {})
    }    
    
    def dns(Closure closure) {
        dns([:], closure)
    }
    
    def dns(Map params, Closure closure) {
        def awsRoute53Dns = new AwsRoute53Dns(params)
        awsRoute53Dns.with(closure)
        dnsList << awsRoute53Dns
    }
    
    def apply(def amazonEC2, def amazonRoute53) {
        info "route53 - applying DNS name for zone $hostedZoneId"
        dnsList*.apply(amazonRoute53, hostedZoneId, fromEC2(amazonEC2))
    }
}

