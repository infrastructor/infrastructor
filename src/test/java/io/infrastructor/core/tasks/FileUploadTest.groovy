package io.infrastructor.core.tasks

import org.testng.annotations.Test


public class FileUploadTest extends TaskTestBase {
    
    @Test
    public void uploadAFileToRemoteHost() {
        inventory.setup {
            nodes('as:root') {
                user  name: 'test'
                group name: 'testgroup'
                
                upload {
                    source = 'resources/fileupload.txt'
                    target = '/fileupload.txt'
                    owner = 'test'
                    group = 'testgroup'
                }
                
                assert shell("ls -alh /fileupload.txt").output.find(/test testgroup/)
                assert shell("cat /fileupload.txt").output.find(/simple/)
            }
        }
    } 
    
    @Test
    public void uploadAFileToRemoteHostWithoutPermissions() {
        inventory.setup {
            nodes('as:devops') {
                def result = upload {
                    source = 'resources/fileupload.txt'
                    target = '/fileupload.txt'
                }
                
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void uploadAFileToRemoteHostWithSudo() {
        inventory.setup {
            nodes('as:devops') {
                def result = upload {
                    sudo = true
                    source = 'resources/fileupload.txt'
                    target = '/fileupload.txt'
                }
                
                assert result.exitcode == 0
                assert shell("cat /fileupload.txt").output.find(/simple/)
            }
        }
    }
    
    @Test
    public void uploadFileWithUnknownOwner() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = upload source: 'resources/fileupload.txt', target: '/tmp/simple.txt', owner: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
 
    @Test
    public void uploadFileWithUnknownGroup() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = upload source: 'resources/fileupload.txt', target: '/tmp/simple.txt', group: 'doesnotexist'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    @Test
    public void uploadFileWithInvalidMode() {
        inventory.setup {
            nodes('as:root') {
                // execute
                def result = upload source: 'resources/fileupload.txt', target: '/tmp/simple.txt', mode: '8888'
                
                // assert
                assert result.exitcode != 0
            }
        }
    }
    
    
    @Test
    public void decryptAndUploadFileToRemoteHost() {
        inventory.setup {
            nodes('as:devops') {
                def result = upload {
                    sudo = true
                    source = 'resources/encrypted_full.tmpl'
                    target = '/fileupload.txt'
                    decryptionKey = 'secret'
                }
                
                assert result.exitcode == 0
                assert shell("cat /fileupload.txt").output.find(/secret message/)
            }
        }
    }
}

