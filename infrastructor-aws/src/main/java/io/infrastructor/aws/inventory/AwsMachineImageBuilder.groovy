package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.AmazonEC2
import io.infrastructor.core.inventory.BasicInventory
import io.infrastructor.core.inventory.Inventory

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.*
import static io.infrastructor.core.logging.ConsoleLogger.error
import static io.infrastructor.core.logging.ConsoleLogger.info
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.provisioning.ProvisioningContext.provision
import static io.infrastructor.core.utils.ConnectionUtils.canConnectTo
import static io.infrastructor.core.utils.RetryUtils.retry
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
    def recreate = false
    @NotNull
    def AwsNode awsNode

    @Min(0l)
    int waitingCount = 100
    @Min(0l)
    int waitingDelay = 3000

    private static final String STATUS_HEADER = "> aws image builder:"

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

            info "staring image creation for '$imageName'"

            statusLine "$STATUS_HEADER checking for an existing image with name '$imageName'"
            def oldImage = findImage(amazonEC2, imageName)

            info "checking if there is an existing image with the same name"

            if (oldImage && recreate) {
                info "removing the existing image '$oldImage.imageId'"
                deregisterImage(amazonEC2, oldImage.imageId)
            } else if (oldImage && !recreate) {
                error "image '$imageName' - '$oldImage.imageId' already exists"
                throw new AwsMachineImageBuilderException(
                        "Image '$imageName' - '$oldImage.imageId' already exists. " +
                                "Please use property 'recreate = true' if you want to rebuild the image.")
            }

            info "creating a base EC2 instance"

            statusLine "$STATUS_HEADER waiting for the base EC2 instance is running"
            awsNode.create(amazonEC2, usePublicIp)
            statusLine "$STATUS_HEADER waiting for the instance SSH connectivity is available"
            retry(waitingCount, waitingDelay) {
                assert canConnectTo(host: awsNode.host, port: awsNode.port)
            }

            info "configuring the base EC2 instance '$awsNode.id'"

            statusLine "$STATUS_HEADER configuring the instance"

            Inventory inventory = new BasicInventory()
            inventory << awsNode

            provision(inventory, closure)

            info "stopping the base EC2 instance '$awsNode.id'"

            awsNode.stop(amazonEC2)
            statusLine "$STATUS_HEADER waiting for the base EC2 instance is stopped"
            waitForInstanceState(amazonEC2, awsNode.id, waitingCount, waitingDelay, 'stopped')

            statusLine "$STATUS_HEADER creating an image"

            info "creating an image '$imageName'"
            def newImageId = createImage(amazonEC2, imageName, awsNode.id)

            statusLine "$STATUS_HEADER waiting for image is available"
            waitForImageState(amazonEC2, newImageId, waitingCount, waitingDelay, 'available')

            info "image creation is done: '$newImageId'"

            if (terminateInstance) {
                statusLine "$STATUS_HEADER terminating the instance"
                awsNode.remove(amazonEC2)
            }

            statusLine "$STATUS_HEADER image creation proccess has been done"
            return newImageId
        }
    }
}

