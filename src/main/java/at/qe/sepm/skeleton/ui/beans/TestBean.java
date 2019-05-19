package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.utils.ScrollPaginator;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestBean { // TODO delete

    private List<String> list;
    private ScrollPaginator<String> scrollPaginator;

    @PostConstruct
    public void init() {
        this.list = new ArrayList<>();
        for(int i = 0; i < 3000; i++){
            this.list.add("Item " + i);
        }

        scrollPaginator = new ScrollPaginator<>(this.list);
    }

    public ScrollPaginator<String> getScrollPaginator(){
        return scrollPaginator;
    }

}