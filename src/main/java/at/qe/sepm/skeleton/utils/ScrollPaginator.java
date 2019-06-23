package at.qe.sepm.skeleton.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A relatively simple implementation of the scroll to view more pattern for JSF ui:repeats
 *
 * @param <T>
 */
public class ScrollPaginator <T> {

    private static final int DEFAULT_SIZE = 18;
    private static final int INITIAL_PART_SIZE = 2;

    private int partSize;
    private int initialized;
    private List<T> all;
    private List<ScrollPart<T>> parts;

    public ScrollPaginator(int partSize) {
        this.partSize = partSize;

        this.all = new ArrayList<>(0);;
        this.initialized = 0;
        update();
    }

    public ScrollPaginator(List<T> list) {
        this.partSize = DEFAULT_SIZE;
        if(list == null){
            list = new ArrayList<>(0);
        }
        this.all = list;
        this.initialized = 0;
        update();
    }

    public ScrollPaginator(List<T> list, int partSize) {
        this.partSize = partSize;
        if(list == null){
            list = new ArrayList<>(0); // Dont waste Space if the list is null
        }
        this.all = list;
        this.initialized = 0;
        update();
    }

    /**
     * Replaces the current {@link List} with a new one. The initialization is reset.
     *
     * @param updated
     */
    public void updateList(List<T> updated){
        this.all = updated != null ? updated : all;
        this.initialized = 0;
        update();
    }

    /**
     * Updates the {@link ScrollPaginator}'s {@link ScrollPart}s. The first part is
     * bigger than the following parts.
     */
    private void update(){
        if(all.isEmpty()){
            this.parts = new ArrayList<>(0);
            return;
        }
        this.parts = new ArrayList<>(((int) (((float) all.size()) / (float) partSize)) + 1);
        // first Part gets more items
        parts.add(new ScrollPart<>(all.subList(0, Math.min(all.size(), INITIAL_PART_SIZE * partSize))));
        int fromIndex = INITIAL_PART_SIZE * partSize; // no need to adjust for small lists w/ only one part

        while(fromIndex < all.size()){
            parts.add(new ScrollPart<>(all.subList(fromIndex, Math.min(fromIndex + partSize, all.size()))));
            fromIndex += partSize;
        }

        initNext();
    }

    /**
     * Initializes the _next {@link ScrollPart} of the {@link ScrollPaginator}
     */
    public void initNext(){
        if(initialized >= parts.size()){
            return;
        }
        parts.get(initialized++).setInitialized(true);
    }

    /**
     * Returns true if all {@link ScrollPart}s have been initialized.
     *
     * @return
     */
    public boolean isInitialized(){
        return initialized >= parts.size();
    }

    public List<ScrollPart<T>> getParts(){
        return parts;
    }
}
