package io.infrastructor.core.processing.actions

import static io.infrastructor.core.inventory.docker.InlineDockerInventory.inlineDockerInventory

public class DockerInventoryDecorator {

    private final String imageName
    
    public DockerInventoryDecorator(String imageName) {
        this.imageName = imageName
    }

    public void provisionAs(String username, Object closure) {
        def inventory

        try {
            inventory = inventoryBuilders[username]()
            inventory.provision(closure)
        } finally {
            inventory?.shutdown()
        }
    }
    
    
    private def inventoryBuilders = [
        'devops': {
            inlineDockerInventory {
                node image: imageName, tags: ['as': 'devops'], username: 'devops', password: 'devops'
            }
        },
        'root': {
            inlineDockerInventory {
                node image: imageName, tags: ['as': 'root'],   username: 'root',   keyfile: 'build/resources/test/itest.pem'
            }
        }
    ]
}
