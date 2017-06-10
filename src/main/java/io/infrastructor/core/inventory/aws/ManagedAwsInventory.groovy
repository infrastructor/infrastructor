package io.infrastructor.core.inventory.aws

import io.infrastructor.core.utils.AmazonEC2Utils
import io.infrastructor.core.utils.AmazonRoute53Utils

import static io.infrastructor.core.processing.ActionPlanRunner.setup
import static io.infrastructor.cli.ConsoleLogger.*

public class ManagedAwsInventory {
    
    def amazonEC2
    def amazonRoute53
    
    def managedZones = []
    def route53s = []
    
    public ManagedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion) {
        this.amazonEC2     = AmazonEC2Utils.amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
        this.amazonRoute53 = AmazonRoute53Utils.amazonRoute53(awsAccessKey, awsSecretKey, awsRegion)
    }
    
    def managedZone(Closure setup) {
        managedZone([:], closure)
    }
    
    def managedZone(Map params, Closure setup) {
        def managedZone = new AwsManagedZone(params)
        managedZone.with(setup)
        managedZone.initialize(amazonEC2)
        managedZones << managedZone
    }
    
    def route53(Map params) {
        route53(params, {})
    }
    
    def route53(Closure closure) {
        route53([:], closure)
    }
    
    def route53(Map params, Closure closure) {
        AwsRoute53 awsRoute53 = new AwsRoute53(params)
        awsRoute53.with(closure)
        route53s << awsRoute53
    }
    
    public static ManagedAwsInventory managedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        def awsInventory = new ManagedAwsInventory(awsAccessKey, awsSecretKey, awsRegion)
        closure.delegate = awsInventory
        closure()
        return awsInventory
    }
    
    def setup(Closure definition = {}) {
        managedZones*.createInstances(amazonEC2)
        managedZones*.updateInstances(amazonEC2)
        setup(getManagedNodes(), definition)
        managedZones*.removeInstances(amazonEC2)
        route53s*.apply(amazonEC2, amazonRoute53)
    }
    
    def getManagedNodes() {
        managedZones*.getInventory().flatten()
    }
    
    def dry() {
        info "DRY: analyzing changes..."
        printf ('%20s %28s %22s  %s\n', [defColor('STATE'), defColor('INSTANCE ID'), defColor('PRIVATE IP'), defColor('NAME')])
        getManagedNodes().each {
            def coloredState
            
            switch (it.state) {
                case 'created':
                    coloredState =  green("CREATED")
                    break
                case 'removed':
                    coloredState =    red("REMOVED")
                    break
                case 'updated':
                    coloredState = yellow("UPDATED")
                    break
                case '':
                    coloredState = blue('UNMODIFIED')
                    break
            }
            
            printf ('%20s %28s %22s  %s\n', [coloredState, cyan(it.instanceId ?: ''), cyan(it.privateIp ?: ''), defColor(it.name)])
        } 
    }
}

