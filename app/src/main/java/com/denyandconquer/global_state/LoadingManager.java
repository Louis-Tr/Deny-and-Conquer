package com.denyandconquer.global_state;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;


public class LoadingManager {
    private static final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    public static BooleanProperty loadingProperty() {
        return isLoading;
    }

    public static void setLoading(boolean loading) {
        isLoading.set(loading);
    }

    public static boolean isLoading() {
        return isLoading.get();
    }
}
