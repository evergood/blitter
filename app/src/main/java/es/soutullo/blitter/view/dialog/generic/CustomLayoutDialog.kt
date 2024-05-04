package es.soutullo.blitter.view.dialog.generic

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import es.soutullo.blitter.view.dialog.handler.IDialogHandler

/** A custom dialog whose content is given by a view object */
abstract class CustomLayoutDialog protected constructor(
    context: Context,
    handler: IDialogHandler?,
    title: String
) : CustomDialog(context, handler, title) {

    override fun prepareConcreteDialog(dialogBuilder: AlertDialog.Builder) {
        dialogBuilder.setView(this.getCustomView())
    }

    /** @return The root view of the dialog */
    protected abstract fun getCustomView(): View
}