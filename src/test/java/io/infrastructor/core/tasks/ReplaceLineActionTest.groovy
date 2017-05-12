package io.infrastructor.core.tasks

import org.testng.annotations.Test

public class ReplaceLineActionTest extends TaskTestBase {
    
    @Test
    public void replaceLineInFile() {
        inventory.setup {
            nodes('as:root') {
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
}

