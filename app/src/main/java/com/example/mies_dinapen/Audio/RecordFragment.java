package com.example.mies_dinapen.Audio;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mies_dinapen.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;

    private ImageButton listBtn;
    private ImageButton recordBtn;
    private TextView filenameText;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile;

    private Chronometer timer;

    public RecordFragment() {
        // Required empty public constructor
    }

/////Aqui los audios se graban///**////////


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Intitialize Variables
        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_filename);

        /* Setting up on click listener
           - Class must implement 'View.OnClickListener' and override 'onClick' method
         */
        listBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        /*  Check, which button is pressed and do the task accordingly
        */
        switch (v.getId()) {
            case R.id.record_list_btn:
                /*
                Navigation Controller
                Part of Android Jetpack, used for navigation between both fragments
                 */
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", null);
                    alertDialog.setTitle("Audio Still recording");
                    alertDialog.setMessage("Are you sure, you want to stop the recording?");
                    alertDialog.create().show();
                } else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            case R.id.record_btn:
                if(isRecording) {
                    //Stop Recording
                    stopRecording();

                    // Change button image and set Recording state to false
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
                    isRecording = false;
                } else {
                    //Check permission to record audio
                    if(checkPermissions()) {
                        //Start Recording
                        startRecording();

                        // Change button image and set Recording state to false
                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        //Stop Timer, very obvious
        timer.stop();

        //Change text on page to file saved
        filenameText.setText("Recording Stopped, File Saved : " + recordFile);

        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        //Start timer from 0
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        //int count=0;

        //Get app external directory path getExternalFilesDir
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("_yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        int count=0, auto;

        for(int i=0; i<=0; i++ ){
            auto=count++;
            recordFile = "Incidentes_"+auto
                    + formatter.format(now) + ".mp3";
        }

        /*recordFile = "Incidentes_"+count++
                + formatter.format(now) + ".mp3";
        System.out.println("AUDIOS nombre archivo " + recordFile);*/

        filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        System.out.println("audio"+ mediaRecorder);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

//
        try {
            mediaRecorder.prepare();
            //Start Recording
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }
}
