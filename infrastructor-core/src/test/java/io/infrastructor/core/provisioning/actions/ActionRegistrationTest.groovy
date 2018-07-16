package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.utils.ActionRegistrationException
import org.junit.Test

import static io.infrastructor.core.utils.ActionRegistrationUtils.action

class ActionRegistrationTest extends InventoryAwareTestBase {

    @Test
    void loadAndRegisterAnExternalAction() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'createDirectory', file: 'build/resources/test/apply_action/directory.groovy'

                task actions: {
                    createDirectory target_name: '~/simple'
                    result = shell 'ls ~'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test
    void registerALocalAction() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'mydirectory', closure: { params -> shell "mkdir $params.name" }

                task actions: {
                    mydirectory(name: '~/simple')
                    result = shell 'ls ~'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test
    void registerALocalActionWithMultilineParams() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'createFile', closure: { params ->
                    file {
                        target = params.file
                        content = params.content
                    }
                }

                task actions: {
                    createFile {
                        file = "test.txt"
                        content = "simple"
                    }
                    result = shell 'cat test.txt'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test
    void registerALocalActionWithMultilineMixedParams() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'createFile', closure: { params ->
                    file {
                        target = params.file
                        content = params.content
                    }
                }

                task actions: {
                    createFile(file: "test.txt") {
                        content = "simple"
                    }
                    result = shell 'cat test.txt'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test
    void registerALocalActionWithoutParameters() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'mydirectory', closure: { shell "mkdir ~/simple" }

                task actions: {
                    mydirectory()
                    result = shell 'ls ~'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test
    void reRegisterAnExistingAction() {
        def result = [:]

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                action name: 'mydirectory', closure: { shell "mkdir ~/simple" }
                action name: 'mydirectory', closure: { shell "mkdir ~/another" }

                task actions: {
                    mydirectory()
                    result = shell 'ls ~'
                }
            }
        }

        assert result.output.contains('another')
    }

    @Test
    void registerActionWithSimplifiedMultilineDefinition() {
        def result = [:]

        action('mydirectory') {
            shell "mkdir ~/simple"
        }

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task actions: {
                    mydirectory()
                    result = shell 'ls ~'
                }
            }
        }

        assert result.output.contains('simple')
    }

    @Test(expected = ActionRegistrationException)
    void registerActionWithNullName() {
        action name: null, closure: { shell "mkdir ~/simple" }
    }

    @Test(expected = ActionRegistrationException)
    void registerActionWithEmptyName() {
        action name: '', closure: { shell "mkdir ~/simple" }
    }

    @Test(expected = ActionRegistrationException)
    void registerActionWithNullClosure() {
        action name: 'test', closure: null
    }

    @Test(expected = ActionRegistrationException)
    void registerActionWithUnspecifiedFile() {
        action name: 'test', file: ''
    }

    @Test(expected = ActionRegistrationException)
    void registerActionWithMissingFile() {
        action name: 'test', file: 'missing'
    }
}