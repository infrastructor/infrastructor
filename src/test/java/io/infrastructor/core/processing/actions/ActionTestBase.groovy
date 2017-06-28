package io.infrastructor.core.processing.actions

import java.util.Arrays
import java.util.List
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized.class)
public abstract class ActionTestBase {

    @Parameter
    public DockerInventoryDecorator inventory

    @Parameters
    public static List<DockerInventoryDecorator> data() {
        [new DockerInventoryDecorator("infrastructor/ubuntu-sshd"), 
         new DockerInventoryDecorator("infrastructor/centos-sshd")]
    }
}
