package io.infrastructor.core.processing.actions

import org.junit.Test

class UploadFolderTest extends ActionTestBase {
    @Test
    void uploadFolderContentToRemoteHost() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert find(/file.exta/) && find(/file.extb/)
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludes() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    includes = '*.exta'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert !find(/file.exta/) && !find(/file.extb/)
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludesNested() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    includes = '**/*.exta'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludesNestedMultiple() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    includes = '**/*.exta *.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithExcludes() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    includes = '**/*'
                    excludes = 'nested/*.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithExcludesOnly() {
        inventory.provisionAs('devops') {
            task actions: {
                def result = upload {
                    user = 'root'
                    source = 'build/resources/test/upload'
                    excludes = 'nested/*.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                
                shell("ls /opt/test/").output.with {
                    assert find(/file.exta/) && find(/file.extb/)
                }
                
                shell("ls /opt/test/nested").output.with {
                    assert find(/file.exta/) && !find(/file.extb/)
                }
            }
        }
    }
}

