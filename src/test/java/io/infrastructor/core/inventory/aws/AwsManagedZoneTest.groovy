package io.infrastructor.core.inventory.aws

import org.testng.annotations.Test


public class AwsManagedZoneTest {
    
    @Test
    public void mergeCurrentIsEmptyTargetOneNew() {
        
        def target = []
        
        target << AwsNode.build {
            name = "node_A"
            imageId = "image_A"
            instanceType = "type_A"
            subnetId = "subnet_A"
            keyName = "keyname_A"
            securityGroupIds = ['sg_A1', "sg_A2"]
            tags = [a1: 'a1', a2: 'a2']
        } 
        
        
        def current = []

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        assert result[0].state == 'created'
    }
    
    
    @Test
    public void mergeTargetIsEmptyCurrentHasOneToRemove() {
        
        def target = []
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            imageId = "image_A"
            instanceType = "type_A"
            subnetId = "subnet_A"
            keyName = "keyname_A"
            securityGroupIds = ['sg_A1', "sg_A2"]
            tags = [a1: 'a1', a2: 'a2']
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        assert result[0].state == 'removed'
    }
    
    @Test
    public void mergeTargetOneIsSameAsCurrentOne() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            imageId = "image_A"
            instanceType = "type_A"
            subnetId = "subnet_A"
            keyName = "keyname_A"
            securityGroupIds = ['sg_A1', "sg_A2"]
            tags = [a1: 'a1', a2: 'a2']
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            imageId = "image_A"
            instanceType = "type_A"
            subnetId = "subnet_A"
            keyName = "keyname_A"
            securityGroupIds = ['sg_A1', "sg_A2"]
            tags = [a1: 'a1', a2: 'a2']
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        assert result[0].state == ''
    }
   
    @Test
    public void mergeTargetOneIsCreatedCurrentOneIsRemoved() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_B"
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 2
        
        def created = result.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        
        def removed = result.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_B'
    }
       
    
    @Test
    public void mergeRebuild_imageId() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            imageId = "image_B"
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            imageId = "image_A"
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 2
        
        def created = result.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.imageId == 'image_B'
        
        def removed = result.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.imageId == 'image_A'
    }
    
    
    @Test
    public void mergeRebuild_instanceType() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            instanceType = "B"
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            instanceType = "A"
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 2
        
        def created = result.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.instanceType == 'B'
        
        def removed = result.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.instanceType == 'A'
    }
    
    
    @Test
    public void mergeRebuild_subnetId() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            subnetId = "B"
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            subnetId = "A"
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 2
        
        def created = result.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.subnetId == 'B'
        
        def removed = result.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.subnetId == 'A'
    }
    
    
    @Test
    public void mergeRebuild_keyName() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            keyName = "B"
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            keyName = "A"
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 2
        
        def created = result.find { it.state == 'created' }
        assert created
        assert created.name == 'node_A'
        assert created.keyName == 'B'
        
        def removed = result.find { it.state == 'removed' }
        assert removed
        assert removed.name == 'node_A'
        assert removed.keyName == 'A'
    }
    
    
    @Test
    public void mergeUpdate_securityGroupIds() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            securityGroupIds = ["B"]
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            securityGroupIds = ["B", "A"]
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        
        def updated = result.find { it.state == 'updated' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.securityGroupIds == ["B"]
    }
    
    
    @Test
    public void mergeUpdate_tags() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            tags = [a: "A", b: "B"]
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            tags = [a: "A"]
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        
        def updated = result.find { it.state == 'updated' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.tags == [a: "A", b: "B"]
    }
    
    
    @Test
    public void mergeTypelessTagsComparison() {
        
        def target = []
        target << AwsNode.build {
            name = "node_A"
            tags = [1: '12', 'test': true]
        } 
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
            tags = [test: 'true', 1: 12]
        } 

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 1
        
        def updated = result.find { it.state == '' }
        assert updated
        assert updated.name == 'node_A'
        assert updated.tags == [1: '12', 'test': true]
    }
    
    
    @Test
    public void comprehensiveMerge() {
        
        def current = []
        current << AwsNode.build {
            name = "node_A"
        } 
        current << AwsNode.build {
            name = "node_B"
            tags = [a: "A"]
        } 
        current << AwsNode.build {
            name = "node_C"
            imageId = "C"
        } 
        current << AwsNode.build {
            name = "node_D"
        } 
        
        def target = []
        target << AwsNode.build { 
            name = "node_A"
        } 
        target << AwsNode.build { 
            name = "node_B"
            tags = [a: "A", b: "B"]
        } 
        target << AwsNode.build { 
            name = "node_C"
            imageId = "newC"
        } 
        target << AwsNode.build {  
            name = "node_X"
        }

        def result = AwsManagedZone.merge(current, target)
        
        assert result.size() == 6

        assert result.find { it.name == 'node_A' && it.state == ''}
        assert result.find { it.name == 'node_B' && it.state == 'updated'}
        assert result.find { it.name == 'node_C' && it.state == 'created'}
        assert result.find { it.name == 'node_C' && it.state == 'removed'}
        assert result.find { it.name == 'node_D' && it.state == 'removed'}
        assert result.find { it.name == 'node_X' && it.state == 'created'}
    }

}

