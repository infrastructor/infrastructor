package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class UploadFolderTest extends InventoryAwareTestBase {

    @Test
    void "upload a folder to a remote host"() {
        withUser(DEVOPS) { inventory ->
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
    void "upload a folder to a remote host with sudo and a password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        user = 'root'
                        source = 'build/resources/test/upload'
                        target = '/opt/test'
                        sudopass = 'sudops'
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

    @Test(expected = TaskExecutionException)
    void "upload a folder to a remote host with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    def result = upload {
                        user = 'root'
                        source = 'build/resources/test/upload'
                        target = '/opt/test'
                        sudopass = 'wrong'
                    }
                }
            }
        }
    }

    @Test
    void "upload a folder to a remote host with includes"() {
        withUser(DEVOPS) { inventory ->
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
    void "upload a folder to a remote host with nested includes"() {
        withUser(DEVOPS) { inventory ->
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
    void "upload a folder to a remote host with multiple nested includes"() {
        withUser(DEVOPS) { inventory ->
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
    void "upload a folder to a remote host with excludes and includes"() {
        withUser(DEVOPS) { inventory ->
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
    void "upload a folder to a remote host with excludes only"() {
        withUser(DEVOPS) { inventory ->
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

