package de.dechasa.mergify.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import de.dechasa.mergify.R;
import de.dechasa.mergify.spotify.MergePattern;

public class MergePatternDialog extends DialogFragment {

    public interface OnClickListener {
        void onSave(MergePattern pattern);
    }

    private OnClickListener delegate;
    private int selected;

    private final String[] choices = new String[]{
            MergePattern.APPEND.toString(),
            MergePattern.SHUFFLE.toString(),
            MergePattern.ALTERNATE.toString()
    };

    public MergePatternDialog(MergePattern current) {
        for (int i = 0; i < choices.length; i++) {
            if (choices[i].equals(current.toString())) {
                selected = i;
                break;
            }
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            delegate = (MergePatternDialog.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement MergePatternDialog.OnClickListener");
        }
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_dialog_select_merge_pattern)
                // Single Choice Item List with all Pattern
                .setSingleChoiceItems(choices, selected, (dialog, which) -> selected = which)
                // send result to Delegate
                .setPositiveButton(R.string.save, (dialog, id) ->
                        delegate.onSave(MergePattern.fromString(choices[selected])))
                // Dismiss on Cancel
                .setNegativeButton(R.string.cancel, (dialog, id) -> MergePatternDialog.this.dismiss());

        return builder.create();
    }
}
