package io.infrastructor.core.tasks

import org.testng.annotations.Test


public class FileActionTest extends TaskTestBase {
   
    @Test
    public void writeAContentToAFileOnRemoteServerWithSudo() {
        inventory.setup {
            nodes('as:devops') { 
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
        inventory.setup {
            nodes('as:devops') { 
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
        inventory.setup {
            nodes('as:root') { 
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
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = file target: '/etc/simple', content: "simple",  owner: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
 
    @Test
    public void createFileWithUnknownGroup() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = file target: '/etc/simple', content: "simple", group: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void createFileWithInvalidMode() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = file target: '/etc/simple', content: "simple", mode: '8888'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
}

