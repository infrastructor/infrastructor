package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class UploadFolderTest extends InventoryAwareTestBase {

    @Test
    void uploadFolderContentToRemoteHost() {
        withUser('devops') { inventory ->
            inventory.provision {
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
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludes() {
        withUser('devops') { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        user = 'root'
                        source = 'build/resources/test/upload'
                        includes = '*.exta'
                        target = '/opt/test'
                    }

                    assert result.exitcode == 0

                    shell("ls /opt/test/").output.with {
                        assert find(/file.exta/) && !find(/file.extb/) && !find(/nested/)
                    }
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludesNested() {
        withUser('devops') { inventory ->
            inventory.provision {
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
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludesNestedMultiple() {
        withUser('devops') { inventory ->
            inventory.provision {
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
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithExcludes() {
        withUser('devops') { inventory ->
            inventory.provision {
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
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithExcludesOnly() {
        withUser('devops') { inventory ->
            inventory.provision {
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
}

