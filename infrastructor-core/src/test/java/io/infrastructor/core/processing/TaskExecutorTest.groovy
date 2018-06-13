package io.infrastructor.core.processing

import org.junit.Test

import static io.infrastructor.core.inventory.InlineInventory.inlineInventory

public class TaskExecutorTest {
	
    @Test
    void runPlanWithAllNodes() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy"
            node host: "testB", username: "dummy"
            node host: "testC", username: "dummy"
        }.provision {
            task actions: {
                collector << node.host
            }
        }
        assert collector == ['testA', 'testB', 'testC'] as Set
    }
    
    @Test
    void runPlanWithTagedNode() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: {'id:a'}, actions: {
                collector << node.host
            }
        }
        assert collector == ['testA'] as Set
    }
        
    @Test
    void runPlanWithTagedNodes() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: {'id:a' || 'id:c'}, actions: {
                collector << node.host
            }
        }
        assert collector == ['testA', 'testC'] as Set
    }
        
    @Test
    void runPlanWithTagedNodesAndParallelism() {
        def collector = ([] as Set).asSynchronized() 
        def threadIds = ([] as Set).asSynchronized() 
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: {'id:a' || 'id:c'}, parallel: 2, actions: {
                collector << node.host
                threadIds << Thread.currentThread().id
            }
        }
        assert collector == ['testA', 'testC'] as Set
        assert threadIds.size() == 2
    }
        
    @Test
    void runPlanWithParallelism() {
        def threadIds = ([] as Set).asSynchronized() 
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task parallel: 3, actions: {
                threadIds << Thread.currentThread().id
            }
        }
        assert threadIds.size() == 3
    }
    
    @Test
    void runPlanWithParallelismNestedSyntax() {
        def threadIds = ([] as Set).asSynchronized() 
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task {
                parallel = 3
                actions = {
                    threadIds << Thread.currentThread().id
                }
            }
        }
        assert threadIds.size() == 3
    }
        
    @Test
    void runPlanWithASinlgeTagInClosure() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: {'id:b'}, actions: {
                collector << node.host
            }
        }
        assert collector.size() == 1
        assert collector == ['testB'] as Set
    }
        
    @Test
    void runPlanWithASinlgeNegationTagInClosure() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: {!'id:b'}, actions: {
                collector << node.host
            }
        }
        assert collector.size() == 2
        assert collector == ['testA', 'testC'] as Set
    }
        
    @Test
    void runPlanWithADoubleNegationTagInClosure() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.provision {
            task filter: { !'id:b' && !'id:a' }, actions: {
                collector << node.host
            }
        }
        assert collector.size() == 1
        assert collector == ['testC'] as Set
    }
        
    @Test
    void runPlanWithAComplexTagFilteringExpression() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [a: 'tag A', b: '1']
            node host: "testB", username: "dummy", tags: [a: 'tag B', b: '2', c: '3']
            node host: "testC", username: "dummy", tags: [a: 'tag C']
        }.provision {
            task filter: { 'a:tag C' || ('a:tag B' && !'c:3' && !'a:tag A') }, actions: {
                collector << node.host
            }
        }
        assert collector.size() == 1
        assert collector == ['testC'] as Set
    }
    
    @Test
    void runPlanWithExpressionForNonNodes() {
        def collector = [] as Set
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'tag A']
            node host: "testB", username: "dummy", tags: [id: 'tag B']
            node host: "testC", username: "dummy", tags: [id: 'tag C']
        }.provision {
            task filter: { 'id:tag C' && 'id:tag Z' }, actions: {
                collector << node.host
            }
        }
        assert collector.size() == 0
    }
    
    @Test
    void runActionsOnSuccess() {
        def collector = []
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'tag A']
            node host: "testB", username: "dummy", tags: [id: 'tag B']
            node host: "testC", username: "dummy", tags: [id: 'tag C']
        }.provision {
            task actions: {
                collector << node.host
            }, onSuccess: {
                task actions: { collector << node.host }
            }
        }
        assert collector.size() == 6
    }
    
    @Test
    void runActionsOnFailed() {
        def collector = []
        inlineInventory {
            node id: 'testA', host: "testA", username: "dummy", tags: [id: 'tag A']
            node id: 'testB', host: "testB", username: "dummy", tags: [id: 'tag B']
            node id: 'testC', host: "testC", username: "dummy", tags: [id: 'tag C']
        }.provision {
            task actions: {
                throw new RuntimeException("managed fail")
            }, onFailure: { 
                task actions: { collector << node.host }
            }
        }
        assert collector.size() == 3
    }
}

