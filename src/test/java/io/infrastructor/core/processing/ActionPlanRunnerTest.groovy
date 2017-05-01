package io.infrastructor.core.processing

import org.testng.annotations.Test
import static io.infrastructor.core.inventory.InlineInventory.inlineInventory


public class ActionPlanRunnerTest {
	
    @Test
    public void runPlanWithAllNodes() {
        inlineInventory {
            node host: "testA", username: "dummy"
            node host: "testB", username: "dummy"
            node host: "testC", username: "dummy"
        }.setup {
            def collector = [] as Set
            nodes {
                collector << node.host
            }
            assert collector == ['testA', 'testB', 'testC'] as Set
        }
    }
    
    @Test
    public void runPlanWithTagedNode() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            nodes('id:a') {
                collector << node.host
            }
            assert collector == ['testA'] as Set
            
        }
    }
    
    @Test
    public void runPlanWithTagedNodes() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            nodes(tags: {'id:a' || 'id:c'}) {
                collector << node.host
            }
            assert collector == ['testA', 'testC'] as Set
        }
    }
    
    @Test
    public void runPlanWithTagedNodesAndParallelism() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            def threadIds = [] as Set
            nodes(tags: {'id:a' || 'id:c'}, parallel: 2) {
                collector << node.host
                threadIds << Thread.currentThread().id
            }
            assert collector == ['testA', 'testC'] as Set
            assert threadIds.size() == 2
        }
    }
    
    @Test
    public void runPlanWithParallelism() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def threadIds = [] as Set
            nodes(parallel: 3) {
                threadIds << Thread.currentThread().id
            }
            assert threadIds.size() == 3
        }
    }
    
    @Test
    public void runPlanWithASinlgeTagInClosure() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            nodes(tags: {'id:b'}) {
                collector << node.host
            }
            assert collector.size() == 1
            assert collector == ['testB'] as Set
        }
    }
    
    @Test
    public void runPlanWithASinlgeNegationTagInClosure() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            nodes(tags: { !'id:b' }) {
                collector << node.host
            }
            assert collector.size() == 2
            assert collector == ['testA', 'testC'] as Set
        }
    }
    
    @Test
    public void runPlanWithADoubleNegationTagInClosure() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'a']
            node host: "testB", username: "dummy", tags: [id: 'b']
            node host: "testC", username: "dummy", tags: [id: 'c']
        }.setup {
            def collector = [] as Set
            nodes(tags: { !'id:b' && !'id:a' }) {
                collector << node.host
            }
            assert collector.size() == 1
            assert collector == ['testC'] as Set
        }
    }
    
    @Test
    public void runPlanWithAComplexTagFilteringExpression() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [a: 'tag A', b: '1']
            node host: "testB", username: "dummy", tags: [a: 'tag B', b: '2', c: '3']
            node host: "testC", username: "dummy", tags: [a: 'tag C']
        }.setup {
            def collector = [] as Set
            nodes(tags: { 'a:tag C' || ('a:tag B' && !'c:3' && !'a:tag A') }) {
                collector << node.host
            }
            assert collector.size() == 1
            assert collector == ['testC'] as Set
        }
    }
    
    @Test
    public void runPlanWithExpressionForNonNodes() {
        inlineInventory {
            node host: "testA", username: "dummy", tags: [id: 'tag A']
            node host: "testB", username: "dummy", tags: [id: 'tag B']
            node host: "testC", username: "dummy", tags: [id: 'tag C']
        }.setup {
            def collector = [] as Set
            nodes(tags: { 'id:tag C' && 'id:tag Z' }) {
                collector << node.host
            }
            assert collector.size() == 0
        }
    }
}

