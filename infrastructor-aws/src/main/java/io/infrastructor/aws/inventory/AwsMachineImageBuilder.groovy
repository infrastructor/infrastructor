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
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.processing.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate

class AwsMachineImageBuilder {
    @NotNull
    def awsAccessKeyId
    @NotNull
    def awsAccessSecretKey
    @NotNull
    def awsRegion
    @NotNull
    def imageName
    @NotNull
    def usePublicIp = false
    @NotNull
    def terminateInstance = true
    @NotNull
    def AwsNode awsNode
    
    def node(Map params) { node(params, {}) }
    def node(Closure closure) { node([:], closure) }
    def node(Map params, Closure closure) {
        awsNode = new AwsNode(params)
        awsNode.with(closure)
    }
    
    def static awsMachineImage(Map params) { awsMachineImage(params, {}) }
    def static awsMachineImage(Closure closure) { awsMachineImage([:], closure) }
    def static awsMachineImage(Map params, Closure closure) {
        AwsMachineImageBuilder builder = new AwsMachineImageBuilder(params)
        builder.with(closure)
        validate(builder)
    }
    
    def build(Closure closure) {
        withTextStatus { statusLine -> 
            statusLine "> Aws Machine Image Builder: creating a temporary EC2 instance"
            AmazonEC2 amazonEC2 = amazonEC2(awsAccessKeyId, awsAccessSecretKey, awsRegion)
            awsNode.create(amazonEC2, usePublicIp)
        
            statusLine "> Aws Machine Image Builder: provisioning the instance '$awsNode.id'"
            provision([awsNode], closure)
        
            statusLine "> Aws Machine Image Builder: stopping the instance '$awsNode.id' to speed up image build"
            awsNode.stop(amazonEC2)
            waitForInstanceState(amazonEC2, awsNode.id, 20, 3000, 'stopped')
        
            statusLine "> Aws Machine Image Builder: creating an image '$imageName'"
            CreateImageRequest createImageRequest = new CreateImageRequest()
            createImageRequest.withInstanceId(awsNode.id)
            createImageRequest.withName(imageName)
            CreateImageResult result = amazonEC2.createImage(createImageRequest)
        
            statusLine "> Aws Machine Image Builder: waiting for image '$imageName' - '$result.imageId' is available"
            waitForImageState(amazonEC2, result.imageId, 90, 3000, 'available')
        
            statusLine "> : image is ready, terminating the instance if needed"
            if (terminateInstance) { awsNode.remove(amazonEC2) }
            
            statusLine "> Aws Machine Image Builder: the image creation has been finished"
            return result.imageId
        }
    }
}

