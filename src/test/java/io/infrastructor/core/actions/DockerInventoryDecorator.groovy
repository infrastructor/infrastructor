package io.infrastructor.core.actions

import io.infrastructor.core.inventory.docker.InlineDockerInventory

public class DockerInventoryDecorator {

    private final String imageName
    
    public DockerInventoryDecorator(String imageName) {
        this.imageName = imageName
    }

    public void setup(Object closure) {
        def inventory

        try {
            inventory = InlineDockerInventory.inlineDockerInventory {
                node image: imageName, tags: ['as': 'root'],   username: 'root',   keyfile: 'resources/itest.pem'
                node image: imageName, tags: ['as': 'devops'], username: 'devops', password: 'devops'
            }
            
            inventory.setup(closure)
        } finally {
            inventory?.shutdown()
        }
    }
    
}
