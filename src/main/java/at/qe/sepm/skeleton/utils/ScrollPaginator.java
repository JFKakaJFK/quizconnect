package at.qe.sepm.skeleton.utils;

import java.util.ArrayList;
import java.util.List;

public class ScrollPaginator <T> {

    private static final int DEFAULT_ITEMS = 18;

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
        int fromIndex = 0;
        int toIndex;
        this.parts = new ArrayList<>(((int) (((float) total) / (float) partSize)) + 1);
        // TODO first part 2x normal part size?
        int initial = 1;
        parts.add(new ScrollPart<>(all.subList(0, Math.min(total, initial * partSize))));
        fromIndex += initial * partSize;
        while(fromIndex < total){
            toIndex = Math.min(fromIndex + partSize, total);
            parts.add(new ScrollPart<>(all.subList(fromIndex, toIndex)));
            fromIndex += partSize;
        }

        initNext();
    }

    public void initNext(){
        if(initialized == parts.size()){
            return;
        }
        parts.get(initialized++).setInitialized(true);
    }

    public boolean isInitialized(){
        return initialized >= parts.size();
    }

    public void setInitialized(){}

    public List<ScrollPart<T>> getParts(){
        return parts;
    }
}
