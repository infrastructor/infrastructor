package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class ReplaceLineActionTest extends InventoryAwareTestBase {

    @Test
    void replaceLineInFile() {
        withUser('devops') { inventory ->
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
    void replaceLineWithoutChange() {
        withUser('devops') { inventory ->
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
    void replaceLineChangeAttributes() {
        withUser('devops') { inventory ->
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

