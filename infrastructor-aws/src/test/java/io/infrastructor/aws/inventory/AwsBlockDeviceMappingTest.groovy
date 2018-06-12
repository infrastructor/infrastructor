package io.infrastructor.aws.inventory

import org.junit.Test

import static io.infrastructor.aws.inventory.AwsBlockDeviceMapping.awsBlockDeviceMapping

class AwsBlockDeviceMappingTest {
    
    @Test
    void compareEqualDeviceBlockMappings() {
        
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
    void compareDeviceBlockMappingsAsSet() {
        
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
    void compareDeviceBlockMappings_changedName() {
        
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
    void compareDeviceBlockMappings_changedDeleteOnTermination() {
        
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
    void compareDeviceBlockMappings_changedEncrypted() {
        
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
    void compareDeviceBlockMappings_changedIops() {
        
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
    void compareDeviceBlockMappings_changedVolumeSize() {
        
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
    void compareDeviceBlockMappings_changedVolumeType() {
        
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
    
   
    @Test
    void emptyFieldComparison_name() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    void emptyFieldComparison_deleteOnTermination() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    void emptyFieldComparison_encrypted() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    void emptyFieldComparison_iops() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    void emptyFieldComparison_volumeSize() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeType = 'gp2'
        }
        
        assert mappingA == mappingB
    }
    
    @Test
    void emptyFieldComparison_volumeType() {
        
        def mappingA = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def mappingB = awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = true
            encrypted = true
            iops = 100
            volumeSize = 8
        }
        
        assert mappingA == mappingB
    }
}
