package io.infrastructor.core.inventory.aws

import static io.infrastructor.core.utils.AmazonEC2Utils.amazonEC2
import static io.infrastructor.core.processing.ActionPlanRunner.setup
import static io.infrastructor.cli.ConsoleLogger.*

public class ManagedAwsInventory {
    
    def amazonEC2
    def managedZones = []
    
    public ManagedAwsInventory(def amazonEC2) {
        this.amazonEC2 = amazonEC2
    }
    
    def managedZone(Closure setup) {
        managedZone([:], closure)
    }
    
    def managedZone(Map params, Closure setup) {
        def managedZone = new AwsManagedZone(params)
        managedZone.with(setup)
        managedZone.tags = managedZone.tags.collectEntries { k, v -> [(k as String), (v as String)] }
        managedZones << managedZone
    }
    
    public static ManagedAwsInventory managedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        def awsInventory = new ManagedAwsInventory(amazonEC2(awsAccessKey, awsSecretKey, awsRegion))
        closure.delegate = awsInventory
        closure()
        return awsInventory
    }
    
    def setup(Closure definition = {}) {
        managedZones*.initialize(amazonEC2)
        managedZones*.createInstances(amazonEC2)
        managedZones*.updateInstances(amazonEC2)
        setup(getManagedNodes(), definition)
        managedZones*.removeInstances(amazonEC2)
    }
    
    def getManagedNodes() {
        managedZones*.getInventory().flatten()
    }
    
    def dry() {
        info "DRY: analyzing changes..."
        managedZones*.initialize(amazonEC2)
        
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

