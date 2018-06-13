package io.infrastructor.core.processing.actions

import org.junit.Test

public class ReplaceLineActionTest extends ActionTestBase {
    
    @Test
    void replaceLineInFile() {
        inventory.provisionAs('root') {
            task actions: {
                file {
                    target  = '/test.txt'
                    content = """\
                    line 1
                    line 2

                    line 3
                    """
                }
                
                replaceLine {
                    target = '/test.txt'
                    regexp = "^line 2"
                    line   = "new line"
                }       
                
                assert shell("cat /test.txt").output == """\
                line 1
                new line

                line 3""".stripMargin().stripIndent()
            }
        }
    }
    
    @Test
    void replaceLineWithoutChange() {
        inventory.provisionAs('root') {
            task actions: {
                file {
                    target  = '/test.txt'
                    content = """\
                    line 1
                    line 2

                    line 3
                    """
                }
                
                replaceLine {
                    target = '/test.txt'
                    regexp = "^unknown"
                    line   = "unknown"
                }       
                
                assert shell("cat /test.txt").output == """\
                line 1
                line 2

                line 3""".stripMargin().stripIndent()
            }
        }
    }
    
    @Test
    void replaceLineChangeAttributes() {
        inventory.provisionAs('root') {
            task actions: {
                file {
                    target  = '/test.txt'
                    content = "dummy content"
                }
                
                replaceLine {
                    target = '/test.txt'
                    regexp = "^unknown"
                    line   = "unknown"
                    owner  = "devops"  // user  devops already exists
                    group  = "devops"  // group devops already exists
                    mode   = '600'
                }       
                
                def result = shell("ls -dalh /test.txt")
                assert result.output.contains("test.txt")
                assert result.output.contains("devops devops")
                assert result.output.contains("-rw------")
            }
        }
    }
}

