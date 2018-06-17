package io.infrastructor.aws.inventory

import groovy.transform.ToString
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class AwsBlockDeviceMapping {

    def name
    def deleteOnTermination
    def encrypted
    def iops
    def volumeSize
    def volumeType

    static def awsBlockDeviceMapping(Closure closure) {
        def blockDeviceMapping = new AwsBlockDeviceMapping()
        blockDeviceMapping.with(closure)
        blockDeviceMapping
    }

    boolean equals(Object obj) {
        if (null == obj) {
            return false
        }
        if (this.is(obj)) {
            return true
        }
        if (obj.getClass() != getClass()) {
            return false
        }

        AwsBlockDeviceMapping bdm = (AwsBlockDeviceMapping) obj

        def builder = new EqualsBuilder()
        appendNotNull(builder, name, bdm.name)
        appendNotNull(builder, deleteOnTermination, bdm.deleteOnTermination)
        appendNotNull(builder, encrypted, bdm.encrypted)
        appendNotNull(builder, iops, bdm.iops)
        appendNotNull(builder, volumeSize, bdm.volumeSize)
        appendNotNull(builder, volumeType, bdm.volumeType)

        return builder.isEquals()
    }

    int hashCode() {
        def builder = new HashCodeBuilder(177, 457)
        if (name != null) builder.append(name)
        if (deleteOnTermination != null) builder.append(deleteOnTermination)
        if (encrypted != null) builder.append(encrypted)
        if (iops != null) builder.append(iops)
        if (volumeSize != null) builder.append(volumeSize)
        if (volumeType != null) builder.append(volumeType)

        return builder.toHashCode()
    }

    private def appendNotNull(def builder, def fieldX, def fieldY) {
        if (fieldX != null && fieldY != null) builder.append(fieldX, fieldY)
    }
}

