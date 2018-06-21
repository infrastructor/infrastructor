package io.infrastructor.aws.inventory

import io.infrastructor.aws.inventory.utils.AmazonEC2Utils
import io.infrastructor.aws.inventory.utils.AmazonRoute53Utils
import io.infrastructor.core.inventory.BasicInventory

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus

class ManagedAwsInventory {

    def amazonEC2
    def amazonRoute53

    def ec2s = []
    def route53s = []

    ManagedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion) {
        this.amazonEC2 = AmazonEC2Utils.amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
        this.amazonRoute53 = AmazonRoute53Utils.amazonRoute53(awsAccessKey, awsSecretKey, awsRegion)
    }

    def ec2(Map params, Closure closure) {
        withTextStatus { statusLine ->
            statusLine '[AWS] initializing EC2 managed set'
            def ec2 = new EC2(params)
            ec2.with(closure)
            ec2.initialize(amazonEC2)
            ec2s << ec2
        }
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

    def static managedAwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, def closure) {
        def awsInventory = new ManagedAwsInventory(awsAccessKey, awsSecretKey, awsRegion)
        closure.delegate = awsInventory
        closure()
        return awsInventory
    }

    def provision(Closure closure = {}) {
        withTextStatus { statusLine ->
            statusLine '[AWS] creating aws instances'
            ec2s*.createInstances(amazonEC2)

            statusLine '[AWS] updating aws instances'
            ec2s*.updateInstances(amazonEC2)

            statusLine '[AWS] provisioning aws instances'
            new BasicInventory(nodes: getNodes()).provision(closure)

            statusLine '[AWS] removing aws instances'
            ec2s*.removeInstances(amazonEC2)

            statusLine '[AWS] updating route53 records'
            route53s*.apply(amazonEC2, amazonRoute53)
        }
    }

    def getNodes() {
        ec2s*.getInventory().flatten().collectEntries { [(it.name): it] }
    }

    def dry() {
        info "DRY: analyzing changes..."
        info sprintf('%20s %28s %22s  %s', [defColor('STATE'), defColor('INSTANCE ID'), defColor('PRIVATE IP'), defColor('NAME')])
        getNodes().values().each {
            def coloredState
            switch (it.state) {
                case 'created':
                    coloredState = green("CREATED")
                    break
                case 'removed':
                    coloredState = red("REMOVED")
                    break
                case 'updated':
                    coloredState = yellow("UPDATED")
                    break
                case '':
                    coloredState = blue('UNMODIFIED')
                    break
            }
            info sprintf('%20s %28s %22s  %s', [coloredState, cyan(it.id ?: ''), cyan(it.privateIp ?: ''), defColor(it.name)])
        }
    }
}

