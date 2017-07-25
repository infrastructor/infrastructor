package io.infrastructor.cli.settings

import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.utils.ConfigUtils.config
import static io.infrastructor.core.utils.ConfigUtils.normalize

class ApplicationSettings {
    
    static final String APPLICATION_SETTINGS_PATH = normalize("${System.env['_']}/../../conf/settings.groovy")
    static final File   APPLICATION_SETTINGS_FILE = new File(APPLICATION_SETTINGS_PATH)
    
    static final String SYSTEM_SETTINGS_PATH = normalize("${System.getProperty('user.home')}/.infrastructor/settings.groovy")
    static final File   SYSTEM_SETTINGS_FILE = new File(SYSTEM_SETTINGS_PATH)
    
    def static applicationSettings() {
        if (APPLICATION_SETTINGS_FILE.exists()) {
            debug "loading application settings from default file '$APPLICATION_SETTINGS_PATH'"
            return config(APPLICATION_SETTINGS_FILE)
        } 
        
        debug "application settings file '$APPLICATION_SETTINGS_PATH' doesn't exist"
        return [:]
    }
    
    def static systemSettings(def profile) {
        def sysSettings = [:]
        def appSettings = applicationSettings()
        
        if (appSettings?.system?.settings) {
            def systemSettingFile = new File(appSettings?.system?.settings)
            if (systemSettingFile.exists()) {
                debug "loading system settings from file '${appSettings?.system?.settings}'"
                sysSettings = config(systemSettingFile)
            } else {
                debug "system settings file '${appSettings?.system?.settings}' doesn't exist"
            }
        } else if (SYSTEM_SETTINGS_FILE.exists()) {
            debug "loading system settings from default file '$SYSTEM_SETTINGS_PATH'"
            sysSettings = config(SYSTEM_SETTINGS_FILE)
        } else {
            debug "no system settings file found or specified"
            return sysSettings
        }
        
        if (profile && sysSettings[profile]) {
            debug "returning profile '$profile' setting"
            return sysSettings[profile]
        } 
        
        return sysSettings
    }
}

