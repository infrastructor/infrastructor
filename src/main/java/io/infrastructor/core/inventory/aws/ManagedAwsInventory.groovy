package io.infrastructor.core.inventory.aws

import io.infrastructor.core.inventory.aws.ec2.EC2
import io.infrastructor.core.inventory.aws.route53.Route53
import io.infrastructor.core.utils.AmazonEC2Utils
import io.infrastructor.core.utils.AmazonRoute53Utils

import static io.infrastructor.cli.ConsoleLogger.*
import static io.infrastructor.core.processing.ActionPlanRunner.setup

public class ManagedAwsInventory {
    
    def amazonEC2
    def amazonRoute53
    
    def ec2s = []
    def route53s = []
    
    public ManagedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion) {
        this.amazonEC2     = AmazonEC2Utils.amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
        this.amazonRoute53 = AmazonRoute53Utils.amazonRoute53(awsAccessKey, awsSecretKey, awsRegion)
    }
    
    def ec2(Closure setup) {
        ec2([:], closure)
    }
    
    def ec2(Map params, Closure setup) {
        def ec2 = new EC2(params)
        ec2.with(setup)
        ec2.initialize(amazonEC2)
        ec2s << ec2
    }
    
    def route53(Map params) {
        route53(params, {})
    }
    
    def route53(Closure closure) {
        route53([:], closure)
    }
    
    def route53(Map params, Closure closure) {
        def route53 = new Route53(params)
        route53.with(closure)
        route53s << route53
    }
    
    public static ManagedAwsInventory managedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        def awsInventory = new ManagedAwsInventory(awsAccessKey, awsSecretKey, awsRegion)
        closure.delegate = awsInventory
        closure()
        return awsInventory
    }
    
    def setup(Closure definition = {}) {
        ec2s*.createInstances(amazonEC2)
        ec2s*.updateInstances(amazonEC2)
        setup(getNodes(), definition)
        ec2s*.removeInstances(amazonEC2)
        route53s*.apply(amazonEC2, amazonRoute53)
    }
    
    def getNodes() {
        ec2s*.getInventory().flatten()
    }
    
    def dry() {
        info "DRY: analyzing changes..."
        printf ('%20s %28s %22s  %s\n', [defColor('STATE'), defColor('INSTANCE ID'), defColor('PRIVATE IP'), defColor('NAME')])
        getNodes().each {
            def coloredState
            switch (it.state) {
                case 'created':
                    coloredState =   green("CREATED")
                    break
                case 'removed':
                    coloredState =     red("REMOVED")
                    break
                case 'updated':
                    coloredState =  yellow("UPDATED")
                    break
                case '':
                    coloredState = blue('UNMODIFIED')
                    break
            }
            printf ('%20s %28s %22s  %s\n', [coloredState, cyan(it.instanceId ?: ''), cyan(it.privateIp ?: ''), defColor(it.name)])
        } 
    }
}

