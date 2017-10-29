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
    
    private static final String STATUS_HEARED = "> aws image builder:"
    
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
            
            statusLine "$STATUS_HEARED checking for an existing image with name '$imageName'"
            def oldImageId = findImageId(amazonEC2, imageName)
            
            if (oldImageId && recreate) {
                info "removing image '$imageName' - '$oldImageId"
                deregisterImage(amazonEC2, oldImageId)
            } else if (oldImageId && !recreate)  {
                error "image '$imageName' - '$oldImageId' already exists"
                throw new AwsMachineImageBuilderException(
                    "Image '$imageName' - '$oldImageId' already exists. " + 
                    "Please use property 'recreate = true' if you want to rebuild the image.")
            }            
            
            statusLine "$STATUS_HEARED creating a base EC2 instance"
            awsNode.create(amazonEC2, usePublicIp)
            info "the base instance has been created with id: '$awsNode.id' and host: '$awsNode.host'"
            
            statusLine "$STATUS_HEARED waiting for the instance SSH connectivity is available"
            retry(waitingCount, waitingDelay) {
                assert canConnectTo(host: awsNode.host, port: awsNode.port)
            }
         
            statusLine "$STATUS_HEARED configuring the instance '$awsNode.id'"
            provision([awsNode], closure)
            info "instance configuration has been done"
            
            statusLine "$STATUS_HEARED stopping the instance '$awsNode.id' to speed up image build"
            awsNode.stop(amazonEC2)
            waitForInstanceState(amazonEC2, awsNode.id, waitingCount, waitingDelay, 'stopped')
        
            info "creating an image"
            statusLine "$STATUS_HEARED creating an image '$imageName'"
            def newImageId = createImage(amazonEC2, imageName, awsNode.id)
        
            statusLine "$STATUS_HEARED waiting for image '$imageName' - '$newImageId' is available"
            waitForImageState(amazonEC2, newImageId, waitingCount, waitingDelay, 'available')

            info "image is ready: $newImageId"
            
            if (terminateInstance) { 
                statusLine "$STATUS_HEARED terminating the instance"
                awsNode.remove(amazonEC2) 
            }
            
            statusLine "$STATUS_HEARED image creation proccess has been done"
            return newImageId
        }
    }
}

