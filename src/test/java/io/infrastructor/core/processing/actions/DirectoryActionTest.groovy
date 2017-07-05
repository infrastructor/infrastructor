package io.infrastructor.core.processing.actions

import org.junit.Test

import static io.infrastructor.core.processing.actions.Actions.*

public class DirectoryActionTest extends ActionTestBase {
    
    @Test
    public void createDirectoryAsRoot() {
        inventory.setup {
            nodes('as:root') {
                // execute
                user  name: "testuser"
                group name: "testgroup"
                directory target: '/var/simple', owner: 'testuser', group: 'testgroup', mode: '0600'
                // assert
                def result = shell("ls -dalh /var/simple")
                assert result.output.contains("simple")
                assert result.output.contains("testuser testgroup")
                assert result.output.contains("drw------")
            }
        }
    }
    
    @Test
    public void createDirectoryAsDevopsWithSudo() {
        inventory.setup {
            nodes('as:devops') {
                // execute
                directory sudo: true, target: '/etc/simple', owner: 'devops', group: 'devops', mode: '0600'
                // assert
                def result = shell("ls -dalh /etc/simple")
                assert result.output.contains("simple")
                assert result.output.contains("devops devops")
                assert result.output.contains("drw------")
            }
        }
    }

    @Test
    public void createDirectoryAsDevopsWithoutSudo() {
        inventory.setup {
            nodes('as:devops') {
                // execute
                def result = directory target: '/etc/simple'
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void createDirectoryWithUnknownOwner() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = directory target: '/etc/simple', owner: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
 
    @Test
    public void createDirectoryWithUnknownGroup() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = directory target: '/etc/simple', group: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void createDirectoryWithInvalidMode() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = directory target: '/etc/simple', mode: '8888'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
}

