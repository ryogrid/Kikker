package jp.ryo.informationPump.server.data;

import java.util.Date;

public class CollabSortBox {
    public Double eval_point;
    public String url;
    public Date crawled_date;
    public int view_counts;

    public CollabSortBox(String url,Double eval_point,Date crawled_date,int view_counts) {
        this.eval_point = eval_point;
        this.url = url;
        this.crawled_date = crawled_date;
        this.view_counts = view_counts;
    }
}
