package at.qe.sepm.skeleton.utils;

import java.util.ArrayList;
import java.util.List;

public class ScrollPaginator <T> {

    private static final int DEFAULT_ITEMS = 60;

    private int total;
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
        this.partSize = DEFAULT_ITEMS;
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
            list = new ArrayList<>(0);
        }
        this.all = list;
        this.initialized = 0;
        update();
    }

    public void updateList(List<T> updated){
        this.all = updated != null ? updated : all;
        update();
    }

    // TODO needs to trigger whole repeat reload
    private void update(){
        this.total = all.size();
        int itemsLeft = total;
        int fromIndex = 0;
        int toIndex;
        this.parts = new ArrayList<>(((int) (((float) total) / (float) partSize)) + 1);
        while(itemsLeft > 0){
            toIndex = itemsLeft > partSize ? fromIndex + partSize : total;
            parts.add(new ScrollPart<>(all.subList(fromIndex, toIndex)));
            fromIndex += partSize;
            itemsLeft -= partSize;
        }

        initNext();
    }

    public void initNext(){
        if(initialized == parts.size()){
            return;
        }
        parts.get(initialized++).setInitialized(true);
    }

    public void initLess(){
        if(initialized == 1){ // at least one part is shown
            return;
        }
        parts.get(--initialized).setInitialized(false);
    }

    public boolean isInitialized(){
        return initialized >= parts.size();
    }

    public void setInitialized(){}

    public List<ScrollPart<T>> getParts(){
        return parts;
    }
}
