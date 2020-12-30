package de.dechasa.mergify.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.DialogAddPlaylistBinding;
import de.dechasa.mergify.spotify.PlaylistData;

/**
 * Template for a Simple Dialog with EditText
 */
public class AddPlaylistDialog extends DialogFragment {

    public interface OnClickListener {
        void onPositive(String text);
    }

    protected DialogAddPlaylistBinding binding;
    protected OnClickListener delegate;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            delegate = (OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddPlaylistDialog.OnClickListener");
        }
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogAddPlaylistBinding.inflate(requireActivity().getLayoutInflater());

        /* Disable Feedback TextView on Edit */
        binding.edPlaylistUrl.addTextChangedListener(editWatcher);

        /* Build AlertDialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setTitle(R.string.title_dialog_add_playlist)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    /**
     * Override the OnClickListener for the Buttons
     */
    @Override
    public void onResume() {
        super.onResume();

        AlertDialog alertDialog = (AlertDialog) getDialog();

        Button positive = Objects.requireNonNull(alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positive.setOnClickListener(v -> onPositive(alertDialog));
        negative.setOnClickListener(v -> onNegative(alertDialog));
    }

    /**
     * Try extracting Playlist ID and send to Delegate
     * @param dialog this dialog
     */
    private void onPositive(AlertDialog dialog) {
        String url = binding.edPlaylistUrl.getText().toString();

        String playlist = PlaylistData.extractPlaylistID(url);
        if (playlist == null) {
            binding.txtPlaylistUrlFeedback.setVisibility(View.VISIBLE);
        } else {
            delegate.onPositive(playlist);
            dialog.dismiss();
        }
    }

    /**
     * Dismiss Dialog
     * @param dialog this dialog
     */
    private void onNegative(AlertDialog dialog) {
        dialog.dismiss();
    }

    /**
     * TextWatcher for the Playlist Name EditText
     * Disable Feedback on Text change
     */
    private final TextWatcher editWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.txtPlaylistUrlFeedback.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
}
