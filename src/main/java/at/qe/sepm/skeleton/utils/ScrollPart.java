package at.qe.sepm.skeleton.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A Frame for the Items of the {@link ScrollPaginator}.
 *
 * This class is a simple wrapper for the {@link List} type, it simply
 * return either the list items or an empty list, depending on wheter
 * {@link this#initialized} is true.
 *
 * @param <T>
 */
public class ScrollPart <T> {

    private final List<T> empty = new ArrayList<>(0);
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
        return initialized ? items : empty;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
