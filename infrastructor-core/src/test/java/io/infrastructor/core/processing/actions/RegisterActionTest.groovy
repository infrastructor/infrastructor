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
            def action = { params -> 
                file {
                    target  = params.file
                    content = params.content
                }
            }

            register name: 'createFile', action: action

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
            def action = { params -> 
                file {
                    target  = params.file
                    content = params.content
                }
            }

            register name: 'createFile', action: action

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
            def action = { shell "mkdir /var/simple" }

            register name: 'mydirectory', action: action

            task actions: {
                mydirectory name: '/var/simple'
                result = shell 'ls /var'
            }
        }

        assert result.output.contains('simple')
    }
}