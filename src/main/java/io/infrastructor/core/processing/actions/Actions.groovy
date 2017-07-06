package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeTaskExecutionException
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.validation.ValidationHelper.validate
import io.infrastructor.core.inventory.CommandExecutionException

class Actions {
    
    public static final String DIRECTORY     = 'directory' 
    public static final String FETCH         = 'fetch' 
    public static final String FILE          = 'file' 
    public static final String GROUP         = 'group' 
    public static final String INSERT_BLOCK  = 'insertBlock'
    public static final String REPLACE       = 'replace' 
    public static final String REPLACE_LINE  = 'replaceLine' 
    public static final String SHELL         = 'shell' 
    public static final String TEMPLATE      = 'template' 
    public static final String UPLOAD        = 'upload' 
    public static final String USER          = 'user' 
    public static final String WAIT_FOR_PORT = 'waitForPort' 
    
    def static final actions = [
        (DIRECTORY):     DirectoryAction.class,
        (FETCH):         FetchAction.class,
        (FILE):          FileAction.class,
        (GROUP):         GroupAction.class,
        (INSERT_BLOCK):  InsertBlockAction.class,
        (REPLACE):       ReplaceAction.class,
        (REPLACE_LINE):  ReplaceLineAction.class,
        (SHELL):         ShellAction.class,
        (TEMPLATE):      TemplateAction.class,
        (UPLOAD):        FileUploadAction.class,
        (USER):          UserAction.class,
        (WAIT_FOR_PORT): WaitForPortAction.class
    ]
    
    def static execute(String name, Map params, Closure closure) {
        try {
            def action = actions[name].newInstance(params)
            action.with(closure)
            validate(action)
            action.execute(ActionContext.node())
        } catch (CommandExecutionException ex) {
            throw new NodeTaskExecutionException("remote command failed",   [action: name, result: ex.result])
        } catch (ValidationException ex) {
            throw new NodeTaskExecutionException("action validation error", [action: name, result: ex.result])
        } catch (ActionProcessingException ex) {
            throw new NodeTaskExecutionException("action processing error", [action: name, message: ex.message])
        }
    }
    
    
    // apply
    
    def static apply(Map params) {
        apply(params, {})
    }
    
    def static apply(Closure closure) {
        apply([:], closure)
    }
    
    def static apply(Map params, Closure closure) {
        def action = new ApplyAction(params)
        action.with(closure)
        validate(action)
        action.execute()
    }
    
    // directory
    
    def static directory(Map params) {
        directory(params, {})
    }
    
    def static directory(Closure closure) {
        directory([:], closure)
    }
    
    def static directory(Map params, Closure closure) {
        execute(DIRECTORY, params, closure)
    }
    
    
    // fetch
    
    def static fetch(Map params) {
        fetch(params, {})
    }
    
    def static fetch(Closure closure) {
        fetch([:], closure)
    }
    
    def static fetch(Map params, Closure closure) {
        execute(FETCH, params, closure)
    }
    
    
    // file
    
    def static file(Map params) {
        file(params, {})
    }
    
    def static file(Closure closure) {
        file([:], closure)
    }
    
    def static file(Map params, Closure closure) {
        execute(FILE, params, closure)
    }
    
    
    // upload
    
    def static upload(Map params) {
        upload(params, {})
    }
    
    def static upload(Closure closure) {
        upload([:], closure)
    }
    
    def static upload(Map params, Closure closure) {
        execute(UPLOAD, params, closure)
    }
    
    
    // group
    
    def static group(Map params) {
        group(params, {})
    }
    
    def static group(Closure closure) {
        group([:], closure)
    }
    
    def static group(Map params, Closure closure) {
        execute(GROUP, params, closure)
    }
    
    
    // input
    
    def static input(Map params) {
        input(params, {})
    }
    
    def static input(Closure closure) {
        input([:], closure)
    }
    
    def static input(Map params, Closure closure) {
        def action = new InputAction(params)
        action.with(closure)
        validate(action)
        action.execute()
    }
    
    
    // insertBlock
    
    def static insertBlock(Map params) {
        insertBlock(params, {})
    }
    
    def static insertBlock(Closure closure) {
        insertBlock([:], closure)
    }
    
    def static insertBlock(Map params, Closure closure) {
        execute(INSERT_BLOCK, params, closure)
    }
    
    
    // replace
    
    def static replace(Map params) {
        replace(params, {})
    }
    
    def static replace(Closure closure) {
        replace([:], closure)
    }
    
    def static replace(Map params, Closure closure) {
        execute(REPLACE, params, closure)
    }

    
    // replaceLine
    
    def static replaceLine(Map params) {
        replaceLine(params, {})
    }
    
    def static replaceLine(Closure closure) {
        replaceLine([:], closure)
    }
    
    def static replaceLine(Map params, Closure closure) {
        execute(REPLACE_LINE, params, closure)
    }
    
    
    // shell
    
    def static shell(String command) {
        shell([command: command], {})
    }
    
    def static shell(Map params) {
        shell(params, {})
    }
     
    def static shell(Closure closure) {
        shell([:], closure)
    }
    
    def static shell(Map params, Closure closure) {
        execute(SHELL, params, closure)
    }
    
    
    // template
    
    def static template(Map params) {
        template(params, {})
    }
    
    def static template(Closure closure) {
        template([:], closure)
    }
    
    def static template(Map params, Closure closure) {
        execute(TEMPLATE, params, closure)
    }
   
    
    // user
    
    def static user(Map params) {
        user(params, {})
    }
    
    def static user(Closure closure) {
        user([:], closure)
    }
    
    def static user(Map params, Closure closure) {
        execute(USER, params, closure)
    }
    
    
    // waitForPort
    
    def static waitForPort(Map params) {
        waitForPort(params, {})
    }
    
    def static waitForPort(Closure closure) {
        waitForPort([:], closure)
    }
    
    def static waitForPort(Map params, Closure closure) {
        execute(WAIT_FOR_PORT, params, closure)
    }
}

