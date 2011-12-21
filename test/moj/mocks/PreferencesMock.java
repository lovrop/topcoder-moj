package moj.mocks;

public class PreferencesMock extends moj.Preferences {
    @Override
    protected String getStringProperty(String key, String defaultValue) {
        return defaultValue;
    }

    @Override
    protected boolean getBooleanProperty(String key, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    protected int getIntegerProperty(String key, int defaultValue) {
        return defaultValue;
    }
}
