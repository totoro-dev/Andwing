package top.totoro.swing.test;

import top.totoro.swing.widget.view.HorizontalScrollBar;
import top.totoro.swing.widget.simulate.RecyclerView;
import top.totoro.swing.widget.simulate.Toast;

import javax.swing.*;
import java.awt.*;

public class SwingTest extends JFrame {

    private RecyclerView view;
    private int index = 0;

    public SwingTest() {
        super("Swing测试");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        testOpaque();
//        testRecyclerView();
        testToast();
    }

    private void testOpaque() {
        setSize(400, 200);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.white);
        JPanel container = new JPanel(null);
        container.setOpaque(false);

        HorizontalScrollBar content = new HorizontalScrollBar();
        content.setBarWidth(100);
        content.setBarX(0);
//        VerticalScrollBar content = new VerticalScrollBar();
//        content.setBarY(0);
//        content.setBarHeight(100);
        content.setBounds(100, 25, 100, 100);
        content.setVisible(true);
        container.add(content);

        add(container, BorderLayout.CENTER);
        setVisible(true);
    }

    private void testRecyclerView() {
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
        button.addActionListener(e -> {
//            view.setOrientation(RecyclerView.HORIZONTAL);
            if (index % 2 == 0) {
                RecycleViewTestAdapter.data.remove(0);
                RecycleViewTestAdapter.data.add(0, new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
                RecycleViewTestAdapter.data.add(0, new String[]{"第四项", "This is forth item.", "by 黄龙猫"});
                view.setOrientation(RecyclerView.VERTICAL);
//                view.setHorizontalScrollBar(new ScrollBarTest());
                view.setVerticalScrollBar(new ScrollBarTest());
            } else
                RecycleViewTestAdapter.data.remove(2);
            index++;
            adapter.notifyDataSetChange();
        });
    }

    public void testToast(){
        setLocation(400,200);
        setSize(400, 200);
        getContentPane().setBackground(Color.BLACK);
        setVisible(true);
        Toast.makeText(this,"11黄龙淼abcd").show(Toast.LONG);
        Toast.makeText(this,"22，，22abcd").show(Toast.LONG);
        Toast.makeText(this,"33??33").show(Toast.LONG);
    }

    public static void main(String[] args) {
        new SwingTest();
    }

}
