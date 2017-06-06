package io.infrastructor.core.inventory.aws

import org.junit.Test
import org.junit.experimental.categories.Category

@Category(AwsCategory.class)
public class AmazonEC2UtilsTest extends AwsTestBase {
    
    @Test
    public void findSubnetIdByName() {
        assert 'subnet-fd7b3b95' == AmazonEC2Utils.findSubnetIdByName(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION, 'subnet-x')   
    }
}

