package io.infrastructor.core.inventory.aws

public class AwsNodesBuilder {
    
    def nodes = []
    
    def node(Map params) { 
        node(params, {}) 
    }
    
    def node(def closure) { 
        node([:], closure) 
    }
    
    def node(Map params, def closure) { 
        def node = new AwsNode(params)
        node.with(closure) 
        nodes << node
    }
    
    public static def build(def closure) {
        def builder = new AwsNodesBuilder()
        closure.delegate = builder
        closure()
        new AwsNodes(nodes: builder.nodes)
    }
    
    public static AwsNodes fromEC2(def amazonEC2) {
        def reservations = amazonEC2.describeInstances().getReservations();
        def nodes = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten().collect { AwsNode.fromEC2(it) }
        
        return new AwsNodes(nodes: nodes)
    }
    
    public static AwsNodes fromNodes(def nodes) {
        new AwsNodes(nodes: nodes)
    }
}

