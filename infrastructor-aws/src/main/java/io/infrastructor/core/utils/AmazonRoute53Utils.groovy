package io.infrastructor.core.utils

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.route53.AmazonRoute53
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder
import com.amazonaws.services.route53.model.ListResourceRecordSetsRequest
import com.amazonaws.services.route53.model.ResourceRecord
import com.amazonaws.services.route53.model.ResourceRecordSet

class AmazonRoute53Utils {
    
    public static AmazonRoute53 amazonRoute53(def awsAccessKey, def awsSecretKey, def awsRegion) {
        AmazonRoute53ClientBuilder standard = AmazonRoute53ClientBuilder.standard().
            withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() { awsAccessKey }

                    @Override
                    public String getAWSSecretKey() { awsSecretKey }
                }))
        standard.setRegion(awsRegion)
        standard.build()
    }
    
    public static def findDnsRecordSet(def amazonRoute53, def hostedZoneId, def name) {
        def result = amazonRoute53.listResourceRecordSets(new ListResourceRecordSetsRequest(hostedZoneId))
        for (ResourceRecordSet recordSet : result.getResourceRecordSets()) {
            if (recordSet.getName() == (name.endsWith('.') ? name : (name + '.'))) {
                def record = [:]
                record.with {
                    name = recordSet.name
                    type = recordSet.type
                    ttl  = recordSet.TTL
                    records = recordSet.resourceRecords*.getValue()
                }
                return record
            }
        }
    }
}

