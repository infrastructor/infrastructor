package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

import static io.infrastructor.core.utils.GroovyShellUtils.load

class ApplyActionTest extends InventoryAwareTestBase {

    @Test
    void applyActionsDefinedInExternalClosure() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def the_closure = { params -> shell(params.command) }

                    apply {
                        closure = the_closure
                        params = [command: "echo 'message' > ~/simple"]
                    }

                    def result = shell("cat ~/simple")
                    assert result.output.contains("message")
                }
            }
        }
    }

    @Test
    void loadActionFromExternalFileInsideNodesSection() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def directory_action_set = load 'build/resources/test/apply_action/directory.groovy'
                    apply(closure: directory_action_set, params: [target_name: '~/simple'])

                    def result = shell command: "ls ~"
                    assert result.output.contains("simple")
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void loadActionFromUnxesistingFile() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    load 'build/resources/test/apply_action/missing.groovy'
                }
            }
        }
    }
}

