package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class ReplaceActionTest extends InventoryAwareTestBase {

    @Test
    void "replace all occurrences in a file using a regex"() {
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
    void "replace all occurrences in a file using a regex, sudo and a password"() {
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

                    replace {
                        user = 'root'
                        target = '/test.txt'
                        regexp = /(?m)line/
                        content = "another"
                        all = true
                        sudopass = 'sudops'
                    }

                    assert shell(command: "cat /test.txt", user: 'root', sudopass: 'sudops').output == """\
                    another 1
                    another 2
                    """.stripMargin().stripIndent()
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void "replace all occurrences in a file using a regex, sudo and a wrong password"() {
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

                    replace {
                        user = 'root'
                        target = '/test.txt'
                        regexp = /(?m)line/
                        content = "another"
                        all = true
                        sudopass = 'wrong'
                    }
                }
            }
        }
    }

    @Test
    void "replace first occurrence in a file using a regex"() {
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
    void "replace a text block with an unknown owner"() {
        withUser(DEVOPS) { inventory ->
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
    void "replace a text block with an unknown group"() {
        withUser(DEVOPS) { inventory ->
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
    void "replace a text block with an invalid mode"() {
        withUser(DEVOPS) { inventory ->
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