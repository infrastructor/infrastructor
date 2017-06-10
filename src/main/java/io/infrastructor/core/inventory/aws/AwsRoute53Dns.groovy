package io.infrastructor.core.inventory.aws

import com.amazonaws.services.route53.model.Change
import com.amazonaws.services.route53.model.ChangeAction
import com.amazonaws.services.route53.model.ChangeBatch
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ResourceRecord
import com.amazonaws.services.route53.model.ResourceRecordSet

import static io.infrastructor.cli.ConsoleLogger.info
import static io.infrastructor.cli.ConsoleLogger.debug

public class AwsRoute53Dns {
    
    def name
    def type
    def ttl
    def resources
    def usePublicIp
    
    def apply(def amazonRoute53, def hostedZoneId, AwsNodes nodes) {
        info "route53 dns '$name': filtering nodes"
        
        def target = nodes.filter(resources)
        info "route53 dns '$name': apply for nodes: ${target.nodes}"

        if (target.nodes.size() > 0) {
            
            def records = []
            if (usePublicIp) {
                debug "dns $name - update using public IPs"
                (target.nodes*.publicIp).each { 
                    debug "dns $name - $it"
                    records << new ResourceRecord(it) 
                }
            } else {
                debug "dns $name - update using private IPs"
                (target.nodes*.privateIp).each { 
                    debug "dns $name - $it"
                    records << new ResourceRecord(it) 
                }
            }
            
            ResourceRecordSet recordSet = new ResourceRecordSet()
            recordSet.setName(name)
            recordSet.setType(type)
            recordSet.setTTL(ttl)
            recordSet.setResourceRecords(records)
            
            final ChangeResourceRecordSetsRequest request = 
                new ChangeResourceRecordSetsRequest(hostedZoneId, new ChangeBatch([new Change(ChangeAction.UPSERT, recordSet)]))
            
            amazonRoute53.changeResourceRecordSets(request)
            
        } else {
            
            info "dns $name - no instances have been found. trying to remove existing DNS record."
            
            def result = amazonRoute53.listResourceRecordSets(new ListResourceRecordSetsRequest(hostedZoneId))
            
            for (ResourceRecordSet resourceRecordSet : result.getResourceRecordSets()) {
                debug "found dns ${resourceRecordSet.getName()}"
                if (resourceRecordSet.getName() == (name + '.')) {
                    info "removing dns ${resourceRecordSet.getName()}"
                    def deleteRequest = new ChangeResourceRecordSetsRequest()
                    deleteRequest.setHostedZoneId(hostedZoneId)
                    deleteRequest.setChangeBatch(new ChangeBatch([new Change(ChangeAction.DELETE, resourceRecordSet)]))
                    amazonRoute53.changeResourceRecordSets(deleteRequest)
                }
            }
        }
    }
}

