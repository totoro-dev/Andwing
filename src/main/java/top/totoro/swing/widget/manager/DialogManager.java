package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.context.Dialog;

public class DialogManager {
    private static Dialog mTopDialog;

    public static Dialog getTopDialog() {
        return mTopDialog;
    }

    public static void setTopDialog(Dialog mTopDialog) {
        DialogManager.mTopDialog = mTopDialog;
    }

    public static void dismiss() {
        if (mTopDialog != null) {
            mTopDialog.dismiss();
            mTopDialog = null;
        }
    }

}
