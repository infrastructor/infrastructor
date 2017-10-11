package io.infrastructor.core.processing.actions

import org.junit.Test

class UploadFolderTest extends ActionTestBase {
    @Test
    public void uploadFolderContentToRemoteHost() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert shell("ls /opt/test/").output.find(/file.extb/)
                assert shell("ls /opt/test/nested").output.find(/file.exta/)
                assert shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
    
    @Test
    public void uploadFolderContentToRemoteHostWithIncludes() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    includes = '*.exta'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert !shell("ls /opt/test/").output.find(/file.extb/)
                assert !shell("ls /opt/test/nested").output.find(/file.exta/)
                assert !shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
    
    @Test
    public void uploadFolderContentToRemoteHostWithIncludesNested() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    includes = '**/*.exta'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert !shell("ls /opt/test/").output.find(/file.extb/)
                assert shell("ls /opt/test/nested").output.find(/file.exta/)
                assert !shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
    
    @Test
    public void uploadFolderContentToRemoteHostWithIncludesNestedMultiple() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    includes = '**/*.exta *.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert shell("ls /opt/test/").output.find(/file.extb/)
                assert shell("ls /opt/test/nested").output.find(/file.exta/)
                assert !shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
    
    @Test
    public void uploadFolderContentToRemoteHostWithExcludes() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    includes = '**/*'
                    excludes = 'nested/*.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert shell("ls /opt/test/").output.find(/file.extb/)
                assert shell("ls /opt/test/nested").output.find(/file.exta/)
                assert !shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
    
    @Test
    public void uploadFolderContentToRemoteHostWithExcludesOnly() {
        inventory.provision {
            task filter: {'as:devops'}, actions: {
                def result = upload {
                    sudo = true
                    source = 'build/resources/test/upload'
                    excludes = 'nested/*.extb'
                    target = '/opt/test'
                }
                
                assert result.exitcode == 0
                assert shell("ls /opt/test/").output.find(/file.exta/)
                assert shell("ls /opt/test/").output.find(/file.extb/)
                assert shell("ls /opt/test/nested").output.find(/file.exta/)
                assert !shell("ls /opt/test/nested").output.find(/file.extb/)
            }
        }
    }
}

