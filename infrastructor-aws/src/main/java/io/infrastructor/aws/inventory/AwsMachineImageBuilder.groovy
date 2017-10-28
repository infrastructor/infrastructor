package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.AmazonEC2

import io.infrastructor.core.inventory.Inventory
import javax.validation.constraints.NotNull
import javax.validation.constraints.Min

import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.*
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.processing.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate
import static io.infrastructor.core.utils.ConnectionUtils.canConnectTo
import static io.infrastructor.core.utils.RetryUtils.retry

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
    def recreate = false
    @NotNull
    def AwsNode awsNode

    @Min(0l)
    int waitingCount = 100
    @Min(0l)
    int waitingDelay = 3000
    
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
            AmazonEC2 amazonEC2 = amazonEC2(awsAccessKeyId, awsAccessSecretKey, awsRegion)
            
            statusLine "> aws machine image builder: checking for an existing image with name '$imageName'"
            
            def oldImageId = findImageId(amazonEC2, imageName)
            
            if (oldImageId && recreate) {
                info "image '$imageName' - '$oldImageId' already exists, deregistering"
                deregisterImage(amazonEC2, oldImageId)
            } else if (oldImageId && !recreate)  {
                error "Image '$imageName' - '$oldImageId' already exists."
                throw new AwsMachineImageBuilderException("Image '$imageName' - '$oldImageId' already exists. Please use property 'recreate = true' if you want to rebuild the image.")
            } else {
                info "image '$imageName' is not available yet, moving on"
            }
            
            statusLine "> aws machine image builder: creating a temporary EC2 instance"
            awsNode.create(amazonEC2, usePublicIp)
            info "the temporary EC2 instance has been created with id: '$awsNode.id' and host: '$awsNode.host'"
            
            statusLine "> aws machine image builder: waiting for the EC2 instance SSH connectivity is available"
            retry(waitingCount, waitingDelay) {
                assert canConnectTo(host: awsNode.host, port: awsNode.port)
            }
            
            info "starting a provisioning process"
            statusLine "> aws machine image builder: provisioning the instance '$awsNode.id'"
            provision([awsNode], closure)
            info "the provisioning has finished"
            
            statusLine "> aws machine image builder: stopping the instance '$awsNode.id' to speed up image build"
            awsNode.stop(amazonEC2)
            waitForInstanceState(amazonEC2, awsNode.id, waitingCount, waitingDelay, 'stopped')
        
            info "creating an image"
            statusLine "> aws machine image builder: creating an image '$imageName'"
            def newImageId = createImage(amazonEC2, imageName, awsNode.id)
        
            statusLine "> aws machine image builder: waiting for image '$imageName' - '$newImageId' is available"
            waitForImageState(amazonEC2, newImageId, waitingCount, waitingDelay, 'available')

            info "the image is ready: $newImageId"
            
            if (terminateInstance) { 
                statusLine "> aws machine image builder: terminating the temporary instance"
                awsNode.remove(amazonEC2) 
            }
            
            statusLine "> aws machine image builder: image creation proccess is complete"
            return newImageId
        }
    }
}

