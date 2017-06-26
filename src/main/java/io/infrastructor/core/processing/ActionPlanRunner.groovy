package io.infrastructor.core.processing

import io.infrastructor.core.utils.ProgressPrinter
import static org.fusesource.jansi.Ansi.Color.GREEN;

public class ActionPlanRunner {
    
    def hosts = []
    def tasks = []
    
    private final def printer = new ProgressPrinter()
    
    def static setup(def hosts, Closure closure) {
        def actionPlanRunner =  new ActionPlanRunner(hosts: hosts)
        closure.delegate = actionPlanRunner
        closure()
        actionPlanRunner.execute()
    }
    
    def nodes(String tag, Closure closure) {
        nodes([tags: { tag as Boolean }], closure)
    }
    
    def nodes(Closure closure) {
        nodes([:], closure)
    }
    
    def nodes(Map params, Closure closure) {
        debug "creating nodes configuration: $params"
        def nodes = new Nodes(params) 
        nodes.closure = closure
        tasks << nodes
    }
    
    def execute() {
        
        def total = (tasks*.filteredNodes(hosts).flatten()).size()
        info "Total: $total"
        printer.setTotal(total)
        
        int i = 0
        
        printer.setStatus("")
        info("initializing")
        sleep(1000)
        
        printer.setStatus("EXECUTING")
        tasks.each {
            info(":task #${i} - ${it.description}")
            it.execute(hosts, printer)
            info(":done!", GREEN)
        }
        
        printer.finish("Done.\n")
        println ""
    }
    
    public void info(String message) {
        printer.print("INFO: $message")
    }
    
    public void info(String message, def color) {
        printer.print("INFO: $message", color)
    }
    
    public void debug(String message) {
        printer.print("DEBUG: $message")
    }
}
