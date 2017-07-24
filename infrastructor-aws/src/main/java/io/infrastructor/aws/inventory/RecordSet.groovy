package io.infrastructor.aws.inventory

import com.amazonaws.services.route53.model.Change
import com.amazonaws.services.route53.model.ChangeAction
import com.amazonaws.services.route53.model.ChangeBatch
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ResourceRecord
import com.amazonaws.services.route53.model.ResourceRecordSet

import static io.infrastructor.core.logging.ConsoleLogger.*

public class RecordSet {
    
    def name
    def type
    def ttl
    def resources
    def usePublicIp
    
    def apply(def amazonRoute53, def hostedZoneId, AwsNodes nodes) {
        info "route53 name '$name' - filtering nodes"
        
        def target = nodes.filter(resources)
        info "route53 name '$name' - apply for nodes: ${target.nodes}"

        if (target.nodes.size() > 0) {
            def records = []
            if (usePublicIp) {
                debug "route53 name $name - update the record set with public IPs"
                (target.nodes*.publicIp).each { records << new ResourceRecord(it) }
            } else {
                debug "route53 name $name - update the record set with private IPs"
                (target.nodes*.privateIp).each { records << new ResourceRecord(it) }
            }
            
            ResourceRecordSet recordSet = new ResourceRecordSet(name, type)
            recordSet.setTTL(ttl)
            recordSet.setResourceRecords(records)
            
            def changeRequest = new ChangeResourceRecordSetsRequest(
                hostedZoneId, 
                new ChangeBatch([new Change(ChangeAction.UPSERT, recordSet)])
            )
            
            amazonRoute53.changeResourceRecordSets(changeRequest)
            
        } else {
            info "route53 name $name - no instances have been found. trying to remove existing DNS record."
            
            def result = amazonRoute53.listResourceRecordSets(new ListResourceRecordSetsRequest(hostedZoneId))
            
            for (ResourceRecordSet resourceRecordSet : result.getResourceRecordSets()) {
                debug "record set found '${resourceRecordSet.getName()}'"
                if (resourceRecordSet.getName() == (name + '.')) {
                    info "removing record set '${resourceRecordSet.getName()}'"
                    def deleteRequest = new ChangeResourceRecordSetsRequest()
                    deleteRequest.setHostedZoneId(hostedZoneId)
                    deleteRequest.setChangeBatch(new ChangeBatch([new Change(ChangeAction.DELETE, resourceRecordSet)]))
                    amazonRoute53.changeResourceRecordSets(deleteRequest)
                }
            }
        }
    }
}
 
