package io.infrastructor.core.processing.actions

import org.junit.Test

public class ReplaceActionTest extends ActionTestBase {
    
    @Test
    void replaceAllOccurrencesInFileUsingRegex() {
        inventory.provisionAs('root') {
            task actions: {
                file {
                    target  = '/test.txt'
                    content = """\
                    line 1
                    line 2
                    """
                }
                
                replace {
                    target  = '/test.txt'
                    regexp  = /(?m)line/
                    content = "another"
                    all     = true
                }       
                
                assert shell("cat /test.txt").output == """\
                another 1
                another 2
                """.stripMargin().stripIndent()
            }
        }
    }
    
    @Test
    void replaceFirstOccurrenceInFileUsingRegex() {
        inventory.provisionAs('root') {
            task actions: {
                file {
                    target = '/test.txt'
                    content = """\
                    line 1
                    line 2
                    """
                }
                
                replace {
                    target  = '/test.txt'
                    regexp  = /(?m)line/
                    content = "another"
                    all     = false
                } 
                        
                assert shell("cat /test.txt").output == """\
                another 1
                line 2
                """.stripMargin().stripIndent()
            }
        }
    }
    
    @Test
    void replaceBlockWithUnknownOwner() {
        inventory.provisionAs('devops') {
            task actions: {
                file target: '/tmp/test.txt', content: "dummy"
                
                def result = replace {
                    target  = '/tmp/test.txt'
                    regexp  = /dummy/
                    content = "another"
                    owner = 'unknown'
                } 
                
                assert result.exitcode != 0
                assert result.error.find(/invalid spec/)
            }
        }
    }
    
    @Test
    void replaceBlockWithUnknownGroup() {
        inventory.provisionAs('devops') {
            task actions: {
                file target: '/tmp/test.txt', content: "dummy"
                
                def result = replace {
                    target  = '/tmp/test.txt'
                    regexp  = /dummy/
                    content = "another"
                    group = 'unknown'
                }
                
                assert result.exitcode != 0
                assert result.error.find(/invalid group/)
            }
        }
    }
    
    @Test
    void replaceBlockWithInvalidMode() {
        inventory.provisionAs('devops') {
            task actions: {
                file target: '/tmp/test.txt', content: "dummy"
                
                def result = replace {
                    target  = '/tmp/test.txt'
                    regexp  = /dummy/
                    content = "another"
                    mode = '888'
                }
                
                assert result.exitcode != 0
                assert result.error.find(/invalid mode/)
            }
        }
    }
}

