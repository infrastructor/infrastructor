package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class InsertBlockTest extends InventoryAwareTestBase {
    
    @Test
    void insertBlockAtTheBeginningOfAFile() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'insertBlockAtTheBeginningOfAFile', actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
                        """
                    }

                    insertBlock {
                        user = 'root'
                        target = '/test.txt'
                        block = "line 0\n"
                        position = START
                    }

                    assert shell("cat /test.txt").output == """\
                    line 0
                    line 1
                    line 2
                    """.stripMargin().stripIndent()
                }
            }
        }
    } 
    
    @Test
    void insertBlockAtTheEndingOfAFile() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
                        """
                    }

                    insertBlock {
                        user = 'root'
                        target = '/test.txt'
                        block = "line 0\n"
                        position = END
                    }

                    assert shell("cat /test.txt").output == """\
                    line 1
                    line 2
                    line 0
                    """.stripMargin().stripIndent()
                }
            }
        }
    } 
    
    @Test(expected = TaskExecutionException)
    void insertBlockWithoutPermissions() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/tmp/test.txt'
                        content = "dummy"
                        owner = 'root'
                        group = 'root'
                        mode = '0600'
                    }

                    insertBlock {
                        target = '/tmp/test.txt'
                        block = "line 0\n"
                        position = END
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void insertBlockToUnexistedFileReturnError() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    insertBlock {
                        target = '/tmp/test.txt'
                        block = "line 0\n"
                        position = END
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void insertBlockWithUnknownOwner() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        owner = 'unknown'
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void insertBlockWithUnknownGroup() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        group = 'unknown'
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void insertBlockWithInvalidMode() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"
                    insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        mode = '888'
                    }
                }
            }
        }
    }
}

