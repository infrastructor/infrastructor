package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateImageRequest
import com.amazonaws.services.ec2.model.CreateImageResult
import io.infrastructor.core.inventory.Inventory

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.processing.ProvisioningContext.provision
import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.amazonEC2

class AwsMachineImageBuilder {
    def awsAccessKeyId     = ''
    def awsAccessSecretKey = ''
    def awsRegion          = ''
    def imageName          = ''
    def noReboot           = false
    def waitAndRemoveNode  = false
    def usePublicIp        = false
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
        
        info "AwsMachineImageBuilder - creating an image"
        CreateImageRequest createImageRequest = new CreateImageRequest()
        createImageRequest.withInstanceId(awsNode.id)
        createImageRequest.withName(imageName)
        createImageRequest.withNoReboot(noReboot)
        
        CreateImageResult result = amazonEC2.createImage(createImageRequest)
        info "AwsMachineImageBuilder - creating an image - done"
    }
}

