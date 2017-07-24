package io.infrastructor.core.inventory.aws

import static io.infrastructor.core.utils.ConfigUtils.config

public class AwsTestBase {
    def final AWS_ACCESS_KEY_ID     = System.getProperty("awsAccessKeyId")
    def final AWS_ACCESS_SECRET_KEY = System.getProperty("awsAccessSecretKey")
    def final AWS_REGION            = System.getProperty("awsRegion")
    def final cfg                   = config('build/resources/test/aws-test.conf')
}

