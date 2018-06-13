package io.infrastructor.core.processing.actions

import org.junit.Test

public class DirectoryActionTest extends ActionTestBase {
    
    @Test
    void createDirectoryAsRoot() {
        inventory.provisionAs('root') {
            task actions: {
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
    void createDirectoryAsDevopsWithSudo() {
        inventory.provisionAs('devops') {
            task actions: {
                // execute
                directory user: 'root', target: '/etc/simple', owner: 'devops', group: 'devops', mode: '0600'
                // assert
                def result = shell("ls -dalh /etc/simple")
                assert result.output.contains("simple")
                assert result.output.contains("devops devops")
                assert result.output.contains("drw------")
            }
        }
    }
    
    @Test
    void createNestedDirectories() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                directory target: '/etc/simple/deep', mode: '600'
                
                def resultDeep = shell 'ls -ldah /etc/simple/deep'
                // assert
                assert resultDeep.exitcode == 0
                assert resultDeep.output.contains('deep')
                assert resultDeep.output.contains("root root")
                assert resultDeep.output.contains("drw-------")
                
                def resultSimple = shell 'ls -ldah /etc/simple'
                // assert
                assert resultSimple.exitcode == 0
                assert resultSimple.output.contains('simple')
                assert resultSimple.output.contains("root root")
                assert resultSimple.output.contains("drwxr-xr-x")
                
                def resultEtc = shell 'ls -ldah /etc'
                // assert
                assert resultEtc.exitcode == 0
                assert resultEtc.output.contains('etc')
                assert resultEtc.output.contains("root root")
                assert resultEtc.output.contains("drwxr-xr-x")
            }
        }
    }

    @Test
    void createDirectoryAsDevopsWithoutSudo() {
        inventory.provisionAs('devops') {
            task actions: {
                // execute
                def result = directory target: '/etc/simple'
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    void createDirectoryWithUnknownOwner() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = directory target: '/etc/simple', owner: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
 
    @Test
    void createDirectoryWithUnknownGroup() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = directory target: '/etc/simple', group: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    void createDirectoryWithInvalidMode() {
        inventory.provisionAs('root') {
            task actions: {
                // execute
                def result = directory target: '/etc/simple', mode: '8888'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    void createDirectoryWithEmptyMode() {
        inventory.provisionAs('root') {
            task actions: {
                assert directory(target: '/etc/simple/test1', mode: '').exitcode == 0
                assert shell("ls -alhd /etc/simple/test1").output.contains("drwxr-xr-x")

                assert directory(target: '/etc/simple/test2', mode: null).exitcode == 0
                assert shell("ls -alhd /etc/simple/test2").output.contains("drwxr-xr-x")
                
                assert directory(target: '/etc/simple/test3', mode: 0).exitcode == 0
                assert shell("ls -alhd /etc/simple/test3").output.contains("drwxr-xr-x")
            }
        }
    }
}

