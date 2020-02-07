package top.totoro.swing.test;

import top.totoro.swing.widget.view.RecyclerView;

import javax.swing.*;
import java.awt.*;

public class SwingTest extends JFrame {

    private RecyclerView view;
    private int index = 0;

    public SwingTest() {
        super("Swing测试");
        setSize(400, 200);
        getContentPane().setLayout(new BorderLayout());
        JPanel parent = new JPanel();
        JButton button = new JButton("修改数据");
        add(parent, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        setVisible(true);
        view = new RecyclerView(parent);
        view.setBackground(Color.white);
        RecycleViewTestAdapter adapter = new RecycleViewTestAdapter();
        view.setAdapter(adapter);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button.addActionListener(e -> {
            view.setOrientation(RecyclerView.HORIZONTAL);
            if (index % 2 == 0) {
                RecycleViewTestAdapter.data.remove(0);
                RecycleViewTestAdapter.data.add(0,new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
                RecycleViewTestAdapter.data.add(0, new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
            }
            else
                RecycleViewTestAdapter.data.remove(2);
            index++;
            adapter.notifyDataSetChange();
        });
    }

    public static void main(String[] args) {
        new SwingTest();
    }

}
