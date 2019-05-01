package at.qe.sepm.skeleton.utils;

import java.util.ArrayList;
import java.util.List;

public class ScrollPart <T> {

    private boolean initialized;
    private List<T> items;

    public ScrollPart(List<T> items){
        this.items = items;
        this.initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<T> getItems() {
        return initialized ? items : new ArrayList<>(0);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
