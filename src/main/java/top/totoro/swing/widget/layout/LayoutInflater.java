package top.totoro.swing.widget.layout;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.manager.LinearLayoutManager;
import top.totoro.swing.widget.view.View;

public class LayoutInflater {
    private static LinearLayoutManager manager = new LinearLayoutManager();

    public static View inflate(BaseLayout mainLayout, String res, boolean attachRoot) {
        View view = manager.inflate(mainLayout, res, attachRoot);
        manager.invalidate();
        return view;
    }
}
