package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class ReplaceLineActionTest extends InventoryAwareTestBase {

    @Test
    void "replace lines in a file"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
    
                        line 3
                        """
                    }

                    replaceLine {
                        user = 'root'
                        target = '/test.txt'
                        regexp = "^line 2"
                        line = "new line"
                    }

                    assert shell("cat /test.txt").output == """\
                    line 1
                    new line
    
                    line 3""".stripMargin().stripIndent()
                }
            }
        }
    }

    @Test
    void "replace lines in a file with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
    
                        line 3
                        """
                        sudopass = 'sudops'
                    }

                    replaceLine {
                        user = 'root'
                        target = '/test.txt'
                        regexp = "^line 2"
                        line = "new line"
                        sudopass = 'sudops'
                    }

                    assert shell("cat /test.txt").output == """\
                    line 1
                    new line
    
                    line 3""".stripMargin().stripIndent()
                }
            }
        }
    }


    @Test(expected = TaskExecutionException)
    void "replace lines in a file with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
    
                        line 3
                        """
                        sudopass = 'sudops'
                    }

                    replaceLine {
                        user = 'root'
                        target = '/test.txt'
                        regexp = "^line 2"
                        line = "new line"
                        sudopass = 'wrong'
                    }

                    assert shell("cat /test.txt").output == """\
                    line 1
                    new line
    
                    line 3""".stripMargin().stripIndent()
                }
            }
        }
    }

    @Test
    void "replace a line with no change"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = """\
                        line 1
                        line 2
    
                        line 3
                        """
                    }

                    replaceLine {
                        user = 'root'
                        target = '/test.txt'
                        regexp = "^unknown"
                        line = "unknown"
                    }

                    assert shell("cat /test.txt").output == """\
                    line 1
                    line 2
    
                    line 3""".stripMargin().stripIndent()
                }
            }
        }
    }

    @Test
    void "replace line and change file attributes"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    file {
                        user = 'root'
                        target = '/test.txt'
                        content = "dummy content"
                    }

                    replaceLine {
                        user = 'root'
                        target = '/test.txt'
                        regexp = "^unknown"
                        line = "unknown"
                        owner = "devops"  // user  devops already exists
                        group = "devops"  // group devops already exists
                        mode = '600'
                    }

                    def result = shell("ls -dalh /test.txt")
                    assert result.output.contains("test.txt")
                    assert result.output.contains("devops devops")
                    assert result.output.contains("-rw------")
                }
            }
        }
    }
}

