package io.infrastructor.core.processing

import org.junit.Test

import static io.infrastructor.core.inventory.InlineInventory.inlineInventory

public class TaskExecutorTest {
	
//    @Test
//    public void runPlanWithAllNodes() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy"
//            node host: "testB", username: "dummy"
//            node host: "testC", username: "dummy"
//        }.setup {
//            nodes {
//                collector << node.host
//            }
//        }
//        assert collector == ['testA', 'testB', 'testC'] as Set
//    }
//    
//    @Test
//    public void runPlanWithTagedNode() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes('id:a') {
//                collector << node.host
//            }
//        }
//        assert collector == ['testA'] as Set
//    }
//        
//    @Test
//    public void runPlanWithTagedNodes() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(tags: {'id:a' || 'id:c'}) {
//                collector << node.host
//            }
//        }
//        assert collector == ['testA', 'testC'] as Set
//    }
//        
//    @Test
//    public void runPlanWithTagedNodesAndParallelism() {
//        def collector = [] as Set
//        def threadIds = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(tags: {'id:a' || 'id:c'}, parallel: 2) {
//                collector << node.host
//                threadIds << Thread.currentThread().id
//            }
//        }
//        assert collector == ['testA', 'testC'] as Set
//        assert threadIds.size() == 2
//    }
//        
//    @Test
//    public void runPlanWithParallelism() {
//        def threadIds = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(parallel: 3) {
//                threadIds << Thread.currentThread().id
//            }
//        }
//        assert threadIds.size() == 3
//    }
//        
//    @Test
//    public void runPlanWithASinlgeTagInClosure() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(tags: {'id:b'}) {
//                collector << node.host
//            }
//        }
//        assert collector.size() == 1
//        assert collector == ['testB'] as Set
//    }
//        
//
//    @Test
//    public void runPlanWithASinlgeNegationTagInClosure() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(tags: { !'id:b' }) {
//                collector << node.host
//            }
//        }
//        assert collector.size() == 2
//        assert collector == ['testA', 'testC'] as Set
//    }
//        
//    
//    @Test
//    public void runPlanWithADoubleNegationTagInClosure() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'a']
//            node host: "testB", username: "dummy", tags: [id: 'b']
//            node host: "testC", username: "dummy", tags: [id: 'c']
//        }.setup {
//            nodes(tags: { !'id:b' && !'id:a' }) {
//                collector << node.host
//            }
//        }
//        assert collector.size() == 1
//        assert collector == ['testC'] as Set
//    }
//        
//    @Test
//    public void runPlanWithAComplexTagFilteringExpression() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [a: 'tag A', b: '1']
//            node host: "testB", username: "dummy", tags: [a: 'tag B', b: '2', c: '3']
//            node host: "testC", username: "dummy", tags: [a: 'tag C']
//        }.setup {
//            nodes(tags: { 'a:tag C' || ('a:tag B' && !'c:3' && !'a:tag A') }) {
//                collector << node.host
//            }
//        }
//        assert collector.size() == 1
//        assert collector == ['testC'] as Set
//    }
//    
//    @Test
//    public void runPlanWithExpressionForNonNodes() {
//        def collector = [] as Set
//        inlineInventory {
//            node host: "testA", username: "dummy", tags: [id: 'tag A']
//            node host: "testB", username: "dummy", tags: [id: 'tag B']
//            node host: "testC", username: "dummy", tags: [id: 'tag C']
//        }.setup {
//            nodes(tags: { 'id:tag C' && 'id:tag Z' }) {
//                collector << node.host
//            }
//        }
//        assert collector.size() == 0
//    }
}

