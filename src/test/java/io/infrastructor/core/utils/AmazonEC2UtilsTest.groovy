package io.infrastructor.core.utils

import org.testng.annotations.Test


public class AmazonEC2UtilsTest extends AwsTestBase {
    
    @Test(groups = ['aws'])
    public void findSubnetIdByName() {
        assert 'subnet-fd7b3b95' == AmazonEC2Utils.findSubnetIdByName(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION, 'subnet-x')   
    }
}

