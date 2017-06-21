package io.infrastructor.core.inventory.aws

import org.junit.Test
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.build
import static io.infrastructor.core.inventory.aws.AwsBlockDeviceMapping.awsBlockDeviceMapping

public class AwsNodesTest {

    @Test
    public void createAwsNodes() {
        AwsNodes awsNodes = build {
            node {
                id               = 'id_A'
                host             = 'host_A'
                port             = 22
                username         = 'username_A'
                password         = 'password_A'
                keyfile          = 'keyfile_A'
                tags             = ['tag_A': 'value_A']
                metadata         = [:]
                name             = 'name_A'
                imageId          = 'imageId_A'
                instanceType     = 'instanceType_A'
                subnetId         = 'subnetId_A'
                keyName          = 'keyName_A'
                securityGroupIds = []
                usePublicIp      = false
                privateIp        = 'privateIp_A'
                publicIp         = 'publicIp_A'
                state            = ''
            }
            
            node {
                id               = 'simpleId'
                host             = 'simpleHost'
                port             = 22
                username         = ''
                password         = ''
                keyfile          = ''
                tags             = [:]
                metadata         = [:]
                name             = ''
                imageId          = ''
                instanceType     = ''
                subnetId         = ''
                keyName          = ''
                securityGroupIds = []
                usePublicIp      = false
                privateIp        = ''
                publicIp         = ''
                state            = ''
            }
        }

        assert awsNodes.nodes.size() == 2
    }
    
    @Test
    public void filterNodes() {
        AwsNodes awsNodes = build {
            node {
                id   = 'id_A'
                tags = ['tag_A': 'A']
            }
            
            node {
                id   = 'id_B'
                tags = ['tag_B': 'B']
            }
            
            node {
                id   = 'id_C'
                tags = ['tag_C': 'C']
            }
        }
        
        def resultNodes = awsNodes.filter( {'tag_C:C'} )
        assert resultNodes
        assert resultNodes.nodes.size() == 1
        assert resultNodes.nodes[0].id == 'id_C'
        
        assert awsNodes.filter( {'tag_C:C' && 'tag_B:B'} ).nodes.size() == 0
        assert awsNodes.filter( {'tag_C:C' || 'tag_B:B'} ).nodes.size() == 2
    }
    
    
    @Test
    public void filterByTags() {
        AwsNodes awsNodes = build {
            node {
                id   = 'id_A'
                tags = ['a': '1']
            }
            
            node {
                id   = 'id_B'
                tags = ['a': '1', 'b': '2']
            }
            
            node {
                id   = 'id_C'
                tags = ['a': '1', 'b': '2', 'c': '3']
            }
        }
        
        def resultNodes1 = awsNodes.filterByTags([a: '1'])
        
        assert resultNodes1.nodes.size() == 3 
        assert resultNodes1.nodes.find { it.id == 'id_A' }
        assert resultNodes1.nodes.find { it.id == 'id_B' }
        assert resultNodes1.nodes.find { it.id == 'id_C' }
        
        def resultNodes2 = awsNodes.filterByTags([b: '2'])
        assert resultNodes2.nodes.size() == 2 
        assert resultNodes2.nodes.find { it.id == 'id_B' }
        assert resultNodes2.nodes.find { it.id == 'id_C' }
        
        def resultNodes3 = awsNodes.filterByTags([c: '3'])
        assert resultNodes3.nodes.size() == 1 
        assert resultNodes3.nodes.find { it.id == 'id_C' }
    }
    
    @Test
    public void usePublicNodes() {
        AwsNodes awsNodes = build {
            node {
                id = 'id_A'
                publicIp = 'public'
                privateIp = 'private'
            }
            
            node {
                id   = 'id_B'
                publicIp = 'public'
                privateIp = 'private'
            }
        }
        
        assert awsNodes.nodes.size() == 2
        assert awsNodes.nodes.find { it.id == 'id_A' }.host == 'private'
        assert awsNodes.nodes.find { it.id == 'id_B' }.host == 'private'
        
        def result = awsNodes.usePublicHost()
        assert result.nodes.size() == 2
        assert result.nodes.find { it.id == 'id_A' }.host == 'public'
        assert result.nodes.find { it.id == 'id_B' }.host == 'public'
    }
    
    @Test
    public void usePrivateNodes() {
        AwsNodes awsNodes = build {
            node {
                id = 'id_A'
                publicIp = 'public'
                privateIp = 'private'
            }
            
            node {
                id   = 'id_B'
                publicIp = 'public'
                privateIp = 'private'
            }
        }
        
        assert awsNodes.nodes.size() == 2
        assert awsNodes.nodes.find { it.id == 'id_A' }.host == 'private'
        assert awsNodes.nodes.find { it.id == 'id_B' }.host == 'private'
        
        
        def result = awsNodes.usePrivateHost()
        assert result.nodes.size() == 2
        assert result.nodes.find { it.id == 'id_A' }.host == 'private'
        assert result.nodes.find { it.id == 'id_B' }.host == 'private'
    }
    
    @Test
    public void mergeCurrentIsEmptyTargetOneNew() {
        AwsNodes target = build {
            node {
                name = "node_A"
                imageId = "image_A"
                instanceType = "type_A"
                subnetId = "subnet_A"
                keyName = "keyname_A"
                securityGroupIds = ['sg_A1', "sg_A2"]
                tags = [a1: 'a1', a2: 'a2']
            } 
        }
        
        def current = new AwsNodes()

        target.merge(current)
        
        assert target.nodes.size() == 1
        assert target.nodes[0].state == 'created'
    }
    
    
    @Test
    public void mergeTargetIsEmptyCurrentHasOneToRemove() {
        
        def target = new AwsNodes()
        
        AwsNodes current = build {
            node {
                name = "node_A"
                imageId = "image_A"
                instanceType = "type_A"
                subnetId = "subnet_A"
                keyName = "keyname_A"
                securityGroupIds = ['sg_A1', "sg_A2"]
                tags = [a1: 'a1', a2: 'a2']
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 1
        assert result.nodes[0].state == 'removed'
    }
    
    
    @Test
    public void mergeTargetOneIsSameAsCurrentOne() {
        
        def target = build {
            node {
                name = "node_A"
                imageId = "image_A"
                instanceType = "type_A"
                subnetId = "subnet_A"
                keyName = "keyname_A"
                securityGroupIds = ['sg_A1', "sg_A2"]
                tags = [a1: 'a1', a2: 'a2']
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                imageId = "image_A"
                instanceType = "type_A"
                subnetId = "subnet_A"
                keyName = "keyname_A"
                securityGroupIds = ['sg_A1', "sg_A2"]
                tags = [a1: 'a1', a2: 'a2']
            } 
        }
        
        def result = target.merge(current)
        
        assert result.nodes.size() == 1
        assert result.nodes[0].state == ''
    }


    @Test
    public void mergeTargetOneIsCreatedCurrentOneIsRemoved() {
        
        def target = build {
            node {
                name = "node_A"
            } 
        }
        
        def current = build {
            node {
                name = "node_B"
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_B'
    }
    
    
    @Test
    public void mergeRebuild_imageId() {
        
        def target = build {
            node {
                name = "node_A"
                imageId = "image_B"
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                imageId = "image_A"
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.imageId == 'image_B'
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.imageId == 'image_A'
    }
    
    
    @Test
    public void mergeRebuild_instanceType() {
        
        def target = build {
            node {
                name = "node_A"
                instanceType = "B"
            }
        }
        
        def current = build {
            node {
                name = "node_A"
                instanceType = "A"
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.instanceType == 'B'
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.instanceType == 'A'
    }

    
    @Test
    public void mergeRebuild_subnetId() {
        
        def target = build {
            node {
                name = "node_A"
                subnetId = "B"
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                subnetId = "A"
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.subnetId == 'B'
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.subnetId == 'A'
    }
    

    @Test
    public void mergeRebuild_keyName() {
        
        def target = build {
            node {
                name = "node_A"
                keyName = "B"
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                keyName = "A"
            } 
        }
        
        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.keyName == 'B'
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.keyName == 'A'
    }
    
    
    @Test
    public void mergeRebuild_blockDeviceMappings() {
        
        def target = build {
            node {
                name = "node_A"
                blockDeviceMapping {
                    name = '/dev/sda1'
                    deleteOnTermination = false
                    encrypted = false
                    iops = 1000
                    volumeSize = 8
                    volumeType = 'gp2'
                }
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                blockDeviceMapping {
                    name = '/dev/sda2'
                    deleteOnTermination = false
                    encrypted = false
                    iops = 1000
                    volumeSize = 8
                    volumeType = 'gp2'
                }
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 2
        
        def created = result.nodes.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.blockDeviceMappings[0] == awsBlockDeviceMapping {
            name = '/dev/sda1'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
        
        def removed = result.nodes.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.blockDeviceMappings[0] == awsBlockDeviceMapping {
            name = '/dev/sda2'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
    }
    
    @Test
    public void mergeUnmodifiedIfblockDeviceMappingsAreNotSet() {
        
        def target = build {
            node {
                name = "node_A"
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                blockDeviceMapping {
                    name = '/dev/sda2'
                    deleteOnTermination = false
                    encrypted = false
                    iops = 1000
                    volumeSize = 8
                    volumeType = 'gp2'
                }
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 1
        
        def removed = result.nodes.find { it.state == '' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.blockDeviceMappings[0] == awsBlockDeviceMapping {
            name = '/dev/sda2'
            deleteOnTermination = false
            encrypted = false
            iops = 1000
            volumeSize = 8
            volumeType = 'gp2'
        }
    }
    

    @Test
    public void mergeUpdate_securityGroupIds() {
        
        def target = build {
            node {
                name = "node_A"
                securityGroupIds = ["B"]
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                securityGroupIds = ["B", "A"]
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 1
        
        def updated = result.nodes.find { it.state == 'updated' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.securityGroupIds == ["B"]
    }
    
    
    @Test
    public void mergeUpdate_tags() {
        
        def target = build {
            node {
                name = "node_A"
                tags = [a: "A", b: "B"]
            } 
        }
        
        def current = build {
            node {
                name = "node_A"
                tags = [a: "A"]
            } 
        }

        def result = target.merge(current)
        
        assert result.nodes.size() == 1
        
        def updated = result.nodes.find { it.state == 'updated' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.tags == [a: "A", b: "B"]
    }
    
    
    @Test
    public void mergeTypelessTagsComparison() {
            
        def target = build {
            node {
                name = "node_A"
                tags = [1: '12', 'test': true]
            } 
        }
            
        def current = build {
            node {
                name = "node_A"
                tags = [test: 'true', 1: 12]
            } 
        }
    
        def result = target.merge(current)
            
        assert result.nodes.size() == 1
            
        def updated = result.nodes.find { it.state == '' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.tags == [1: '12', 'test': true]
    }

    
    @Test
    public void comprehensiveMerge() {
        AwsNodes current = build {
            node {
                name = "node_A"
            } 
            node {
                name = "node_B"
                tags = [a: "A"]
            } 
            node {
                name = "node_C"
                imageId = "C"
            } 
            node {
                name = "node_D"
            } 
        }
        
        AwsNodes target = build {
            node { 
                name = "node_A"
            } 
            node { 
                name = "node_B"
                tags = [a: "A", b: "B"]
            } 
            node { 
                name = "node_C"
                imageId = "newC"
            } 
            node {  
                name = "node_X"
            }
        }

        target.merge(current)
        
        assert target.nodes.size() == 6
        assert target.nodes.find { it.name == 'node_A' && it.state == ''}
        assert target.nodes.find { it.name == 'node_B' && it.state == 'updated'}
        assert target.nodes.find { it.name == 'node_C' && it.state == 'created'}
        assert target.nodes.find { it.name == 'node_C' && it.state == 'removed'}
        assert target.nodes.find { it.name == 'node_D' && it.state == 'removed'}
        assert target.nodes.find { it.name == 'node_X' && it.state == 'created'}
    }
}

