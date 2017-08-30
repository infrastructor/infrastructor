package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateImageRequest
import com.amazonaws.services.ec2.model.CreateImageResult
import com.amazonaws.services.ec2.model.StopInstancesRequest

import io.infrastructor.core.inventory.Inventory
import javax.validation.constraints.NotNull

import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.amazonEC2
import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.waitForImageState
import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.waitForInstanceState
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.processing.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate

class AwsMachineImageBuilder {
    @NotNull
    def awsAccessKeyId     = ''
    @NotNull
    def awsAccessSecretKey = ''
    @NotNull
    def awsRegion          = ''
    @NotNull
    def imageName          = ''
    @NotNull
    def usePublicIp        = false
    @NotNull
    def AwsNode awsNode
    
    def node(Map params) { node(params, {}) }
    def node(Closure closure) { node([:], closure) }
    def node(Map params, Closure closure) {
        awsNode = new AwsNode(params)
        awsNode.with(closure)
    }
    
    def build(Closure closure) {
        info "AwsMachineImageBuilder - creating AWS node"
        AmazonEC2 amazonEC2 = amazonEC2(awsAccessKeyId, awsAccessSecretKey, awsRegion)
        awsNode.create(amazonEC2, usePublicIp)
        
        info "AwsMachineImageBuilder - provisioning AWS node"
        provision([awsNode], closure)
        
        info "AwsMachineImageBuilder - stopping the instance for faster image build"
        
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
        stopInstancesRequest.withInstanceIds(awsNode.id)
        amazonEC2.stopInstances(stopInstancesRequest)
        
        waitForInstanceState(amazonEC2, awsNode.id, 20, 3000, 'stopped')
        
        info "AwsMachineImageBuilder - creating an image"
        CreateImageRequest createImageRequest = new CreateImageRequest()
        createImageRequest.withInstanceId(awsNode.id)
        createImageRequest.withName(imageName)
        CreateImageResult result = amazonEC2.createImage(createImageRequest)
        
        info "AwsMachineImageBuilder - waiting for image $result.imageId is available"
        waitForImageState(amazonEC2, result.imageId, 90, 3000, 'available')
        
        info "AwsMachineImageBuilder - creating an image - done"
        result.imageId
    }
}

