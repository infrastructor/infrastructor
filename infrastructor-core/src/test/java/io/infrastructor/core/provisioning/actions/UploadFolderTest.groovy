package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class UploadFolderTest extends InventoryAwareTestBase {
    @Test
    void uploadFolderContentToRemoteHost() {
        withInventory { inventory ->
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
        withInventory { inventory ->
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
                        assert find(/file.exta/) && !find(/file.extb/)
                    }

                    shell("ls /opt/test/nested").output.with {
                        assert !find(/file.exta/) && !find(/file.extb/)
                    }
                }
            }
        }
    }
    
    @Test
    void uploadFolderContentToRemoteHostWithIncludesNested() {
        withInventory { inventory ->
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
        withInventory { inventory ->
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
        withInventory { inventory ->
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
        withInventory { inventory ->
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

