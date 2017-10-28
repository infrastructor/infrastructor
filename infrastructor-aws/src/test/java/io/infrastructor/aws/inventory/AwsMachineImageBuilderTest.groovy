package io.infrastructor.aws.inventory

import org.junit.Test
import io.infrastructor.core.validation.ValidationException

class AwsMachineImageBuilderTest {
    
    void validationSuccess() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
        }
    }
    
    @Test(expected = ValidationException)
    void missing_awsAccessKeyId() {
        AwsMachineImageBuilder.awsMachineImage {
            // awsAccessKeyId     = '...'
            awsAccessSecretKey = ''
            awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
        }
    }
    
    @Test(expected = ValidationException)
    void missing_awsAccessSecretKey() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            // awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
        }
    }
    
    @Test(expected = ValidationException)
    void missing_awsRegion() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            // awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
        }
    }
    
    @Test(expected = ValidationException)
    void missing_imageName() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            // imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
        }
    }
    
    @Test(expected = ValidationException)
    void missing_usePublicIp() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            // imageName          = "test-image"
            usePublicIp        = null
            node {
                name           = 'dummy'
            }
        }
    }
    
    @Test(expected = ValidationException)
    void negative_waitingDelay() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
            
            waitingDelay = -1
        }
    }
    
    @Test(expected = ValidationException)
    void negative_waitingCount() {
        AwsMachineImageBuilder.awsMachineImage {
            awsAccessKeyId     = '...'
            awsAccessSecretKey = '...'
            awsRegion          = 'eu-west-1'
            imageName          = "test-image"
            usePublicIp        = true
            node {
                name           = 'dummy'
            }
            
            waitingCount = -1
        }
    }
}

