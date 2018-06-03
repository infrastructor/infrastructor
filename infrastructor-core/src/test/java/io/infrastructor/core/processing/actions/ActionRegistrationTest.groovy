package io.infrastructor.core.processing.actions

import io.infrastructor.core.utils.ActionRegistrationException
import org.junit.Test

import static io.infrastructor.core.utils.ActionRegistrationUtils.*
import static io.infrastructor.core.utils.GroovyShellUtils.load

public class ActionRegistrationTest extends ActionTestBase {

    @Test
    public void loadAndRegisterAnExternalAction() {
        def result = [:]
        inventory.provisionAs('root') {
            def closure = load 'build/resources/test/apply_action/directory.groovy'
            action name: 'createDirectory', closure: closure
            
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
            action name: 'mydirectory', closure: { params -> shell "mkdir $params.name" }

            task actions: {
                mydirectory (name: '/var/simple') 
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('simple')
    }
    
    @Test
    public void registerALocalActionWithMultilineParams() {
        def result = [:]
        inventory.provisionAs('root') {
            action name: 'createFile', closure: { params -> 
                file {
                    target  = params.file
                    content = params.content
                }
            }

            task actions: {
                createFile {
                    file    = "/var/test.txt"
                    content = "simple"
                }
                result = shell 'cat /var/test.txt'
            }
        }

        assert result.output.contains('simple')
    }
    
    @Test
    public void registerALocalActionWithMultilineMixedParams() {
        def result = [:]
        inventory.provisionAs('root') {
            action name: 'createFile', closure: { params -> 
                file {
                    target  = params.file
                    content = params.content
                }
            }

            task actions: {
                createFile(file: "/var/test.txt") {
                    content = "simple"
                }
                result = shell 'cat /var/test.txt'
            }
        }

        assert result.output.contains('simple')
    }
    
    @Test
    public void registerALocalActionWithoutParameters() {
        def result = [:]
        inventory.provisionAs('root') {
            action name: 'mydirectory', closure: { shell "mkdir /var/simple" }

            task actions: {
                mydirectory()
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('simple')
    }

    @Test 
    public void reRegisterAnExistingAction() { 
        def result = [:]

        inventory.provisionAs('root') {
            action name: 'mydirectory', closure: { shell "mkdir /var/simple" }
            action name: 'mydirectory', closure: { shell "mkdir /var/another" }

            task actions: {
                mydirectory()
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('another')
    }

    @Test 
    public void registerActionWithSimplifiedMultilineDefinition() { 
        def result = [:]
            
        action('mydirectory') { 
            shell "mkdir /var/simple" 
        }

        inventory.provisionAs('root') {
            task actions: {
                mydirectory()
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('simple')
    }

    @Test(expected = ActionRegistrationException) 
    public void registerActionWithNullName() { 
        action name: null, closure: { shell "mkdir /var/simple" }
    }
    
    @Test(expected = ActionRegistrationException) 
    public void registerActionWithEmptyName() { 
        action name: '', closure: { shell "mkdir /var/simple" }
    }
    
    @Test(expected = ActionRegistrationException) 
    public void registerActionWithNullClosure() { 
        action name: 'test', closure: null
    }
}