package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class InsertBlockTest extends InventoryAwareTestBase {
    
    @Test
    void "insert a text block at the beginning of the file"() {
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
    void "insert a text block with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
                        """
                        sudopass = 'sudops'
                    }

                    insertBlock {
                        user = 'root'
                        target = '/test.txt'
                        block = "line 0\n"
                        position = START
                        sudopass = 'sudops'
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

    @Test(expected = TaskExecutionException)
    void "insert a text block with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
                        """
                        sudopass = 'sudops'
                    }

                    insertBlock {
                        user = 'root'
                        target = '/test.txt'
                        block = "line 0\n"
                        position = START
                        sudopass = 'wrong'
                    }
                }
            }
        }
    }
    
    @Test
    void "insert a text block at the end of the file"() {
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
    void "insert a block without file permissions"() {
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
    void "insert a text block when the target file does not exists"() {
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
    void "insert a text block and assign an unexisting owner"() {
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
    void "insert a text block and assign an unexisting group"() {
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
    void "insert a text block and assign an invalid mode"() {
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

