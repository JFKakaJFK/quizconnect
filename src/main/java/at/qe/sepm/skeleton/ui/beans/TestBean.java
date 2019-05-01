package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.utils.RepeatPaginator;
import at.qe.sepm.skeleton.utils.ScrollPaginator;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestBean {

    private List<String> list;
    private RepeatPaginator paginator;
    private ScrollPaginator<String> scrollPaginator;

    @PostConstruct
    public void init() {
        this.list = new ArrayList<>();
        for(int i = 0; i < 500; i++){
            this.list.add("Item " + i);
        }

        paginator = new RepeatPaginator(this.list);
        scrollPaginator = new ScrollPaginator<>(this.list);
    }

    public RepeatPaginator getPaginator() {
        return paginator;
    }

    public ScrollPaginator<String> getScrollPaginator(){
        return scrollPaginator;
    }

}