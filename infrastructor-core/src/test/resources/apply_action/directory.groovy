return { 
    user  name: "testuser"
    group name: "testgroup"
    directory target: it.target_name, owner: 'testuser', group: 'testgroup', mode: '0600'
}