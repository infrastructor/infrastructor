package io.infrastructor.core.processing.actions

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

public class DockerInventoryDecorator {

    private final String imageName
    
    public DockerInventoryDecorator(String imageName) {
        this.imageName = imageName
    }

    public void provision(Object closure) {
        def inventory

        try {
            inventory = inlineDockerInventory {
                node image: imageName, tags: ['as': 'root'],   username: 'root',   keyfile: 'build/resources/test/itest.pem'
                node image: imageName, tags: ['as': 'devops'], username: 'devops', password: 'devops'
            }
            
            inventory.provision(closure)
        } finally {
            inventory?.shutdown()
        }
    }
}