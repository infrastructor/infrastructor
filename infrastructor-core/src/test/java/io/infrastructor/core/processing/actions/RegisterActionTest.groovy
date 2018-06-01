package io.infrastructor.core.processing.actions

import org.junit.Test

import static io.infrastructor.core.utils.ActionRegistrationUtils.*
import static io.infrastructor.core.utils.GroovyShellUtils.load

public class RegisterActionTest extends ActionTestBase {

    @Test
    public void loadAndRegisterAnExternalAction() {
        def result = [:]
        inventory.provisionAs('root') {

            def action = load 'build/resources/test/apply_action/directory.groovy'
            
            register name: 'createDirectory', action: action
            
            task actions: {
                createDirectory target_name: '/var/simple'
                result = shell 'ls /var'
            }
        }
        
        assert result.output.contains('simple')
    }

    @Test
    public void registerALocalAction() {
        def result = [:]
        inventory.provisionAs('root') {
            def action = { params -> shell "mkdir $params.name" }

            register name: 'mydirectory', action: action

            task actions: {
                mydirectory name: '/var/simple'
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('simple')
    }
}