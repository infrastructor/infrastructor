package io.infrastructor.core.inventory.aws


public class AwsTestBase {
    def AWS_ACCESS_KEY_ID = System.getProperty("awsAccessKeyId")
    def AWS_SECRET_KEY = System.getProperty("awsSecretKey")
    def AWS_REGION =  System.getProperty("awsRegion")
}

