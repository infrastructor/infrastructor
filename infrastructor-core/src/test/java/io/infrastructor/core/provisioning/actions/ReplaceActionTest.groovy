package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class ReplaceActionTest extends InventoryAwareTestBase {

    @Test
    void replaceAllOccurrencesInFileUsingRegex() {
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

                    replace {
                        user = 'root'
                        target = '/test.txt'
                        regexp = /(?m)line/
                        content = "another"
                        all = true
                    }

                    assert shell("cat /test.txt").output == """\
                    another 1
                    another 2
                    """.stripMargin().stripIndent()
                }
            }
        }
    }

    @Test
    void replaceFirstOccurrenceInFileUsingRegex() {
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

                    replace {
                        user = 'root'
                        target = '/test.txt'
                        regexp = /(?m)line/
                        content = "another"
                        all = false
                    }

                    assert shell("cat /test.txt").output == """\
                    another 1
                    line 2
                    """.stripMargin().stripIndent()
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void replaceBlockWithUnknownOwner() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    replace {
                        target = '/tmp/test.txt'
                        regexp = /dummy/
                        content = "another"
                        owner = 'unknown'
                    }
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void replaceBlockWithUnknownGroup() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"

                    def result = replace {
                        target = '/tmp/test.txt'
                        regexp = /dummy/
                        content = "another"
                        group = 'unknown'
                    }

                    assert result.exitcode != 0
                    assert result.error.find(/invalid group/)
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void replaceBlockWithInvalidMode() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    file target: '/tmp/test.txt', content: "dummy"
                    replace {
                        target = '/tmp/test.txt'
                        regexp = /dummy/
                        content = "another"
                        mode = '888'
                    }
                }
            }
        }
    }
}

