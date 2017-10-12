package io.infrastructor.core.processing.actions

import org.junit.Test

public class FileActionTest extends ActionTestBase {
   
    @Test
    public void writeAContentToAFileOnRemoteServerWithSudo() {
        inventory.provisionAs('devops') {
            task actions: {
                // setup
                shell sudo: true, command: "groupadd infra"
            
                // execution
                file {
                    content = 'message'
                    target = '/test.txt'
                    owner = 'devops'
                    group = 'infra'
                    mode = '600'
                    sudo = true
                }
                
                // assertion
                def result = shell("ls -alh /test.txt")
                assert result.output.contains("test.txt")
                assert result.output.contains("devops infra")
                assert result.output.contains("-rw-------")
                
                // check file content
                assert shell("cat /test.txt").output.contains("message")
            }
        }
    }
    
    @Test
    public void writeAFileOnRemoteServerWithoutSudo() {
        inventory.provisionAs('devops') {
            task actions: {
                // execution
                def result = file {
                    content = 'message'
                    target = '/test.txt'
                    sudo = false
                }
                
                // assertion
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void writeAFileOnRemoteServerAsRoot() {
        inventory.provisionAs('root') {
            task actions: {
                // execution
                def result = file {
                    content = 'another message'
                    target = '/test.txt'
                }
                // assertion
                assert result.exitcode == 0
                assert shell("cat /test.txt").output.contains("another message")
            }
        }
    }
    
    @Test
    public void createFileWithUnknownOwner() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = file target: '/etc/simple', content: "simple",  owner: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
 
    @Test
    public void createFileWithUnknownGroup() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = file target: '/etc/simple', content: "simple", group: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void createFileWithInvalidMode() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = file target: '/etc/simple', content: "simple", mode: '8888'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
}

