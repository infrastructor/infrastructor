package io.infrastructor.core.inventory.aws

import org.junit.Test

import static io.infrastructor.core.inventory.aws.AwsBlockDeviceMapping.awsBlockDeviceMapping

public class AwsBlockDeviceMappingTest {
    
    @Test
    public void compareEqualDeviceBlockMappings() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    public void compareDeviceBlockMappingsAsSet() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert ([mappingA] as Set) == ([mappingB] as Set)
    }
    
    @Test
    public void compareDeviceBlockMappings_changedName() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda2'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA != mappingB
    }
    
    @Test
    public void compareDeviceBlockMappings_changedDeleteOnTermination() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA != mappingB
    }
    
    @Test
    public void compareDeviceBlockMappings_changedEncrypted() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = true
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA != mappingB
    }
    
    @Test
    public void compareDeviceBlockMappings_changedIops() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = true
            iops = 3000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA != mappingB
    }
    
    @Test
    public void compareDeviceBlockMappings_changedVolumeSize() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = true
            iops = 1000
            volumeSize = 16
            volumeType = 'gp2'
        }
        
        assert mappingA != mappingB
    }
    
    
    @Test
    public void compareDeviceBlockMappings_changedVolumeType() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = true
            iops = 1000
            volumeSize = 8
            volumeType = 'st1'
        }
        
        assert mappingA != mappingB
    }
}

