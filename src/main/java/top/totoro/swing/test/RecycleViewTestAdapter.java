package top.totoro.swing.test;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.layout.LayoutInflater;
import top.totoro.swing.widget.view.ImageView;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.TextView;
import top.totoro.swing.widget.view.View;

import java.util.LinkedList;
import java.util.List;

public class RecycleViewTestAdapter extends RecyclerView.Adapter<RecycleViewTestAdapter.ViewHolder> {

    public static List<String[]> data = new LinkedList<>();

    public int size = 30;

    static {
        data.add(new String[]{"第一项", "This is first item.", "by 黄龙淼"});
        data.add(new String[]{"第二项", "This is second item.", "by 淼龙黄"});
        data.add(new String[]{"第三项", "This is third item.", "by 龙淼黄"});
        data.add(new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
    }

    @Override
    public ViewHolder onCreateViewHolder(BaseLayout parent) {
        return new ViewHolder(LayoutInflater.inflate(parent, "item.xml", false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position, int viewType) {
//        viewHolder.tv.setText("abc");
//        viewHolder.iv.setBackgroundImage("img/collect.png");
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;
        public ImageView iv;

        public ViewHolder(View item) {
            super(item);
            tv = (TextView) item.findViewById("item_tv");
            iv = (ImageView) item.findViewById("item_iv");
        }
    }
}
