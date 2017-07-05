package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.NodeTaskExecutionException
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.validation.ValidationHelper.validate
import io.infrastructor.core.inventory.CommandExecutionException

class Actions {
    
    def static final actions = [
        'directory': DirectoryAction.class,
        'fetch': FetchAction.class,
        'file': FileAction.class,
        'upload': FileUploadAction.class,
        'group': GroupAction.class,
        'input': InputAction.class,
        'insertBlock': InsertBlockAction.class,
        'replace': ReplaceAction.class,
        'replaceLine': ReplaceLineAction.class,
        'shell': ShellAction.class,
        'template': TemplateAction.class,
        'user': UserAction.class,
        'waitForPort': WaitForPortAction.class
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
    
    def static directory(Map params) {
        execute('directory', params, {})
    }
    
    def static directory(Closure closure) {
        execute('directory', [:], closure)
    }
    
    def static directory(Map params, Closure closure) {
        execute('directory', params, closure)
    }
    
    def static fetch(Map params) {
        execute('fetch', params, {})
    }
    
    def static fetch(Closure closure) {
        execute('fetch', [:], closure)
    }
    
    def static fetch(Map params, Closure closure) {
        execute('fetch', params, closure)
    }
    
    def static file(Map params) {
        execute('file', params, {})
    }
    
    def static file(Closure closure) {
        execute('file', [:], closure)
    }
    
    def static file(Map params, Closure closure) {
        execute('file', params, closure)
    }
    
    def static upload(Map params) {
        execute('upload', params, {})
    }
    
    def static upload(Closure closure) {
        execute('upload', [:], closure)
    }
    
    def static upload(Map params, Closure closure) {
        execute('upload', params, closure)
    }
    
    def static group(Map params) {
        execute('group', params, {})
    }
    
    def static group(Closure closure) {
        execute('group', [:], closure)
    }
    
    def static group(Map params, Closure closure) {
        execute('group', params, closure)
    }
    
    def static input(Map params) {
        execute('input', params, {})
    }
    
    def static input(Closure closure) {
        execute('input', [:], closure)
    }
    
    def static input(Map params, Closure closure) {
        execute('input', params, closure)
    }
    
    def static insertBlock(Map params) {
        execute('insertBlock', params, {})
    }
    
    def static insertBlock(Closure closure) {
        execute('insertBlock', [:], closure)
    }
    
    def static insertBlock(Map params, Closure closure) {
        execute('insertBlock', params, closure)
    }
    
    def static replace(Map params) {
        execute('replace', params, {})
    }
    
    def static replace(Closure closure) {
        execute('replace', [:], closure)
    }
    
    def static replace(Map params, Closure closure) {
        execute('replace', params, closure)
    }

    def static replaceLine(Map params) {
        execute('replaceLine', params, {})
    }
    
    def static replaceLine(Closure closure) {
        execute('replaceLine', [:], closure)
    }
    
    def static replaceLine(Map params, Closure closure) {
        execute('replaceLine', params, closure)
    }
    
    def static shell(String command) {
        execute('shell', [command: command], {})
    }
    
    def static shell(Map params) {
        execute('shell', params, {})
    }
     
    def static shell(Closure closure) {
        execute('shell', [:], closure)
    }
    
    def static shell(Map params, Closure closure) {
        execute('shell', params, closure)
    }
    
    def static template(Map params) {
        execute('template', params, {})
    }
    
    def static template(Closure closure) {
        execute('template', [:], closure)
    }
    
    def static template(Map params, Closure closure) {
        execute('template', params, closure)
    }
   
    def static user(Map params) {
        execute('user', params, {})
    }
    
    def static user(Closure closure) {
        execute('user', [:], closure)
    }
    
    def static user(Map params, Closure closure) {
        execute('user', params, closure)
    }
    
    def static waitForPort(Map params) {
        execute('waitForPort', params, {})
    }
    
    def static waitForPort(Closure closure) {
        execute('waitForPort', [:], closure)
    }
    
    def static waitForPort(Map params, Closure closure) {
        execute('waitForPort', params, closure)
    }
}

