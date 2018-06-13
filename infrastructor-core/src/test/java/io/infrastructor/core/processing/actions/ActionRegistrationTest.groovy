package io.infrastructor.core.processing.actions

import io.infrastructor.core.utils.ActionRegistrationException
import org.junit.Test

import static io.infrastructor.core.utils.ActionRegistrationUtils.action

public class ActionRegistrationTest extends ActionTestBase {

    @Test
    void loadAndRegisterAnExternalAction() {
        def result = [:]
        inventory.provisionAs('root') {
            action name: 'createDirectory', file: 'build/resources/test/apply_action/directory.groovy'

            task actions: {
                createDirectory target_name: '/var/simple'
                result = shell 'ls /var'
            }
        }
        
        assert result.output.contains('simple')
    }

    @Test
    void registerALocalAction() {
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
    void registerALocalActionWithMultilineParams() {
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
    void registerALocalActionWithMultilineMixedParams() {
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
    void registerALocalActionWithoutParameters() {
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
    void reRegisterAnExistingAction() { 
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
    void registerActionWithSimplifiedMultilineDefinition() { 
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
    void registerActionWithNullName() { 
        action name: null, closure: { shell "mkdir /var/simple" }
    }
    
    @Test(expected = ActionRegistrationException) 
    void registerActionWithEmptyName() { 
        action name: '', closure: { shell "mkdir /var/simple" }
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