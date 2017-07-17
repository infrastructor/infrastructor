package io.infrastructor.core.inventory.aws


public class AwsTestBase {
    def AWS_ACCESS_KEY_ID     = System.getProperty("awsAccessKeyId")
    def AWS_ACCESS_SECRET_KEY = System.getProperty("awsAccessSecretKey")
    def AWS_REGION            = System.getProperty("awsRegion")
}

