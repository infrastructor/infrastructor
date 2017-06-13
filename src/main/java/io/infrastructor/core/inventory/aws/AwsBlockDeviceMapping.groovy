package io.infrastructor.core.inventory.aws

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class AwsBlockDeviceMapping {
    def name = ''
    def deleteOnTermination = true
    def encrypted
    def iops
    def volumeSize = 8
    def volumeType = 'gp2'
    
    public static def awsBlockDeviceMapping(Closure closure) {
        def blockDeviceMapping = new AwsBlockDeviceMapping()
        blockDeviceMapping.with(closure)
        blockDeviceMapping
    }
}

