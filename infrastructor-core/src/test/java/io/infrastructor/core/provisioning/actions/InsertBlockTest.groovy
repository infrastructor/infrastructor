package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class InsertBlockTest extends InventoryAwareTestBase {
    
    @Test
    void insertBlockAtTheBeginningOfAFile() {
        withUser('devops') { inventory ->
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
        withUser('devops') { inventory ->
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
    
    @Test
    void insertBlockWithoutPermissions() {
        withUser('devops') { inventory ->
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

                    def result = insertBlock {
                        target = '/tmp/test.txt'
                        block = "line 0\n"
                        position = END
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/Permission denied/)
                }
            }
        }
    }
    
    @Test
    void insertBlockToUnexistedFileReturnError() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    def result = insertBlock {
                        target = '/tmp/test.txt'
                        block = "line 0\n"
                        position = END
                    }.exitcode != 0
                }
            }
        }
    }
    
    @Test
    void insertBlockWithUnknownOwner() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    def result = insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        owner = 'unknown'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid spec/)
                }
            }
        }
    }
    
    @Test
    void insertBlockWithUnknownGroup() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    def result = insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        group = 'unknown'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid group/)
                }
            }
        }
    }
    
    @Test
    void insertBlockWithInvalidMode() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    def result = insertBlock {
                        target = '/tmp/test.txt'
                        block = "dummy"
                        position = END
                        mode = '888'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid mode/)
                }
            }
        }
    }
}

