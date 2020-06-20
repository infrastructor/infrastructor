package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.inventory.Node

import javax.validation.constraints.NotNull

import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.utils.FlatUUID.flatUUID
import static io.infrastructor.core.utils.NodeUtils.randomPort

@ToString(includePackage = false, includeNames = true)
class DockerNode {

    def id
    @NotNull
    def image
    @NotNull
    def username
    def password
    def keyfile
    def keypass
    Map tags = [:]
    Map metadata = [:]

    private def containerId
    private def containerPort

    def synchronized launch() {
        containerId = flatUUID()
        containerPort = randomPort()

        def command = "docker run -d -p $containerPort:22 --name $containerId $image"

        debug "Lanuching docker node with command: $command"

        def process = command.execute()
        def exitValue = process.waitFor()
        if (exitValue) {
            throw new RuntimeException("Unable to launch docker node: '$this', exit value: '$exitValue', output: ${process.text}")
        }

        return asNode()
    }

    def synchronized shutdown() {
        if (containerId) {
            def command = "docker rm -f $containerId"

            debug "Shutting down docker node with command: $command"

            def process = command.execute()
            def exitValue = process.waitFor()

            if (exitValue) {
                throw new RuntimeException("Unable to remove docker node: '$this', exit value: '$exitValue', output: ${process.text}")
            } else {
                containerId = ''
            }
        }
    }

    Node asNode() {
        new Node(id: id,
                host: 'localhost',
                port: containerPort,
                username: username,
                password: password,
                keyfile: keyfile,
                keypass: keypass,
                tags: tags,
                metadata: metadata
        )
    }

}

