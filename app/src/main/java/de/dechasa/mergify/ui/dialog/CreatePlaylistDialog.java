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

import java.util.Map;
import java.util.Objects;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.DialogCreatePlaylistBinding;
import de.dechasa.mergify.spotify.PlaylistOptionBuilder;

public class CreatePlaylistDialog extends DialogFragment {

    public interface OnClickListener {
        void onPositive(Map<String, Object> options);
    }

    private OnClickListener delegate;
    private DialogCreatePlaylistBinding binding;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            delegate = (CreatePlaylistDialog.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement CreatePlaylistDialog.OnClickListener");
        }
    }

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogCreatePlaylistBinding.inflate(requireActivity().getLayoutInflater());

        /* Disable Feedback TextView on Edit */
        binding.edPlaylistName.addTextChangedListener(editWatcher);

        /* Set the CheckBox OnClickListener */
        binding.checkPlaylistPublic.setOnClickListener(checkListener);
        binding.checkPlaylistCollaborative.setOnClickListener(checkListener);

        /* Build AlertDialog */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setTitle(R.string.title_dialog_create_playlist)
                .setPositiveButton(R.string.create, null)
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
     * Check valid options for Playlist and send to Delegate
     * @param dialog this Dialog
     */
    private void onPositive(AlertDialog dialog) {
        String input = binding.edPlaylistName.getText()
                .toString()
                .trim();

        PlaylistOptionBuilder builder = new PlaylistOptionBuilder()
                .setName(input)
                .setDescription(binding.edPlaylistDescription.getText().toString())
                .setVisible(binding.checkPlaylistPublic.isChecked())
                .setCollaborative(binding.checkPlaylistCollaborative.isChecked());

        if (!input.isEmpty()) {
            delegate.onPositive(builder.build());
            dialog.dismiss();
        } else {
            binding.txtPlaylistNameFeedback.setVisibility(View.VISIBLE);
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
            binding.txtPlaylistNameFeedback.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    /**
     * OnClickListener for the CheckBoxes
     * Apply Spotify Rules for Collaborative/Private Playlist
     *   Only one of them can be true
     */
    private final View.OnClickListener checkListener = check -> {
        int checkID = check.getId();

        if (checkID == binding.checkPlaylistPublic.getId()) {
            if (binding.checkPlaylistPublic.isChecked()) {
                binding.checkPlaylistCollaborative.setChecked(false);
            }
        } else if (checkID == binding.checkPlaylistCollaborative.getId()) {
            if (binding.checkPlaylistCollaborative.isChecked()) {
                binding.checkPlaylistPublic.setChecked(false);
            }
        }
    };
}
