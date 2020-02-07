package top.totoro.swing.test;

import top.totoro.swing.widget.view.RecyclerView;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class RecycleViewTestAdapter extends RecyclerView.Adapter<RecycleViewTestAdapter.ViewHolder> {

    public static List<String[]> data = new LinkedList<>();

    static {
        data.add(new String[]{"第一项", "This is first item.", "by 黄龙淼"});
        data.add(new String[]{"第二项", "This is second item.", "by 淼龙黄"});
        data.add(new String[]{"第三项", "This is third item.", "by 龙淼黄"});
        data.add(new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
    }

    @Override
    public ViewHolder onCreateViewHolder(JPanel parent) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String[] item = data.get(position);
        viewHolder.title.setText(item[0]);
        viewHolder.content.setText(item[1]);
        viewHolder.user.setText(item[2]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends JPanel {

        public JLabel title;
        public JLabel content;
        public JLabel user;

        public ViewHolder(JPanel parent) {
            setSize(500, 80);
            setLayout(null);
            title = new JLabel();
            content = new JLabel();
            user = new JLabel();
            title.setBounds(0, 0, 100, 30);
            content.setBounds(0, 30, 100, 30);
            user.setBounds(0, 60, 100, 20);
            add(title);
            add(content);
            add(user);
        }
    }
}
