package io.infrastructor.cli.settings

import org.junit.Test

class ApplicationSettingsTest {
    
    private static final TEST = 'test'
    private static final LIVE = 'live'
    
    @Test
    public void loadSettingWithProfile() {
        def test = ApplicationSettings.systemSettings(new File('build/resources/test/settings/settings.groovy'), TEST)
        assert test
        assert test.message == TEST
        
        def live = ApplicationSettings.systemSettings(new File('build/resources/test/settings/settings.groovy'), LIVE)
        assert live
        assert live.message == LIVE
    }
    
    @Test
    public void loadSettingWithUnexistedProfile() {
        def settings = ApplicationSettings.systemSettings(new File('build/resources/test/settings/settings.groovy'), 'unknown')
        assert settings
        assert settings.test.message == TEST
        assert settings.live.message == LIVE
    }
}

