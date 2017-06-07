package io.infrastructor.core.inventory.aws

import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Instance

import static io.infrastructor.cli.ConsoleLogger.debug

public class AwsNodeUtils {
    
    def static findAwsNodesWithTags(def amazonEC2, def tags) {
        def reservations = amazonEC2.describeInstances().getReservations();
        def allExistingRunningInstances = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten()
        
        def awsNodes = allExistingRunningInstances.collect { AwsNode.convert(it) }
        return awsNodes.findAll { it.tags.intersect(tags) == tags }
    }
    
    def static waitForInstanceStateIsRunning(def amazonEC2, String instanceId, int attempts, int interval) {
        for (int i = attempts; i > 0; i--) {
            DescribeInstancesRequest request = new DescribeInstancesRequest()
            request.setInstanceIds([instanceId])
            DescribeInstancesResult result = amazonEC2.describeInstances(request)
            Instance instance = result.getReservations().get(0).getInstances().get(0)
            debug "wait for instance '$instanceId' state is running, current state: ${instance.getState().getCode()}"
            if (instance.getState().getCode() == 16) { // instance is running
                return instance
            }
            sleep(interval)
        }
        throw new RuntimeException("timeout waiting for instance $instanceId state is running after $attempts attempts. node: $this")
    }
    
}

