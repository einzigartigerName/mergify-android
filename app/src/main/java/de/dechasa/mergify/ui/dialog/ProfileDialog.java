package de.dechasa.mergify.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import de.dechasa.mergify.R;
import de.dechasa.mergify.databinding.DialogProfileBinding;

public class ProfileDialog extends DialogFragment {

    public interface Action {
        void onLogout();
    }

    private DialogProfileBinding binding;
    private Action delegate;
    private String userName;
    private String imgUrl;

    public ProfileDialog(String userName, String imgUrl) {
        this.userName = userName;
        this.imgUrl = imgUrl;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            delegate = (ProfileDialog.Action) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement ProfileDialog.Action");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogProfileBinding.inflate(requireActivity().getLayoutInflater());

        binding.txtProfileName.setText(userName);

        Glide.with(this)
                .load(imgUrl)
                .placeholder(R.drawable.ic_profile_24)
                .fitCenter()
                .circleCrop()
                .into(binding.imgProfilePic);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot())
                .setTitle(R.string.title_dialog_profile)
                .setNeutralButton(R.string.logout, (dialog, which) -> delegate.onLogout());

        return builder.create();
    }
}
