package com.padassist.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.padassist.Data.Team;
import com.padassist.R;

import io.realm.Realm;

public class TeamSaveDialogFragment extends DialogFragment {
    private RadioGroup choiceRadioGroup;
    private EditText teamName;
    private SaveTeam saveTeam;
    private Toast toast;
    private Team team;
    private long teamIdOverwrite;
    private Realm realm = Realm.getDefaultInstance();

    public interface SaveTeam {
        public void overwriteTeam();

        public void saveNewTeam(String teamName);

        public void clearTeam();
    }

    public static TeamSaveDialogFragment newInstance(SaveTeam saveTeam, long teamIdOverwrite) {
        TeamSaveDialogFragment dialogFragment = new TeamSaveDialogFragment();
        dialogFragment.setSaveTeam(saveTeam);
        dialogFragment.setTeamIdOverwrite(teamIdOverwrite);
//        Bundle args = new Bundle();
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        View rootView = View.inflate(getActivity(), R.layout.fragment_team_save_dialog, null);
        choiceRadioGroup = (RadioGroup) rootView.findViewById(R.id.choiceRadioGroup);
        teamName = (EditText) rootView.findViewById(R.id.teamName);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Save Team");
        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (choiceRadioGroup.getCheckedRadioButtonId() == R.id.overwriteTeam) {
                        saveTeam.overwriteTeam();
                        dismiss();
                    } else if (choiceRadioGroup.getCheckedRadioButtonId() == R.id.newTeam) {
                        if (!teamName.getText().toString().equals("")) {
                            saveTeam.saveNewTeam(teamName.getText().toString());
                            dismiss();
                        } else {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(getActivity(), "Please enter a team name", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else if (choiceRadioGroup.getCheckedRadioButtonId() == R.id.clearTeam) {
                        saveTeam.clearTeam();
                        dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (realm.where(Team.class).findAll() == null || realm.where(Team.class).findAll().size() == 0 || teamIdOverwrite == 0) {
            choiceRadioGroup.getChildAt(1).setEnabled(false);
        }
        if (getArguments() != null) {
//            team = getArguments().getParcelable("team");
        }
        choiceRadioGroup.setOnCheckedChangeListener(choiceOnCheckedChangeListener);
//        Log.d("Team Save Dialog", "Team Name is: " + team.getTeamName());
    }

    private RadioGroup.OnCheckedChangeListener choiceOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.newTeam) {
                teamName.setEnabled(true);
            } else {
                teamName.setEnabled(false);
            }
            if (checkedId == R.id.overwriteTeam) {
                teamName.setText(
                        realm.where(Team.class).equalTo("teamId", 0).findFirst().getTeamName());
            } else {
                teamName.setText("");
            }
        }
    };

    public void setSaveTeam(SaveTeam saveTeam) {
        this.saveTeam = saveTeam;
    }

    public void setTeamIdOverwrite(long teamIdOverwrite){
        this.teamIdOverwrite = teamIdOverwrite;
    }

    public void show(FragmentManager manager,long teamIdOverwrite, String tag) {
        super.show(manager, tag);
        this.teamIdOverwrite = teamIdOverwrite;
    }
}
//
//
//    private View rootView;
//    private TextView okButton, cancelButton;
//    private EditText teamNameEditText;
//
//    static TeamSaveDialogFragment newInstance(){
//        TeamSaveDialogFragment f = new TeamSaveDialogFragment();
//        return f;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_team_save_dialog, container, false);
//        okButton = (TextView) rootView.findViewById(R.id.okButton);
//        cancelButton = (TextView) rootView.findViewById(R.id.cancelButton);
//        teamNameEditText = (EditText) rootView.findViewById(R.id.teamNameEditText);
//        return rootView;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        okButton.setOnClickListener(buttonOnClickListener);
//        cancelButton.setOnClickListener(buttonOnClickListener);
//        getDialog().setTitle("Save Team");
//    }
//
//    private View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if(v.equals(okButton)){
//                getDialog().dismiss();
//            }
//            if(v.equals(cancelButton)){
//                getDialog().dismiss();
//            }
//        }
//    };
