package com.verifai.example;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.verifai.core.pub.result.MrzDataResult;
import com.verifai.example.databinding.ActivityVerifaiResultBinding;
import com.verifai.liveness.pub.LivenessCheckListener;
import com.verifai.liveness.pub.VerifaiLiveness;
import com.verifai.liveness.pub.checks.CloseEyes;
import com.verifai.liveness.pub.checks.FaceMatching;
import com.verifai.liveness.pub.checks.LivenessCheck;
import com.verifai.liveness.pub.checks.Tilt;
import com.verifai.liveness.pub.result.FaceMatchingCheckResult;
import com.verifai.liveness.pub.result.LivenessCheckResultBase;
import com.verifai.liveness.pub.result.LivenessCheckResults;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This activity display the results from either the Core or NFC flow.
 * In your own application you'll implement only one of the two flows,
 * this is just an example to demonstrate both cases.
 */
public class VerifaiResultActivity extends AppCompatActivity {
    private ActivityVerifaiResultBinding binding;
    private Bitmap image = null;
    private final String TAG = "RESULT_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifaiResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // If NFC has been read, use faceImage from chip
        try {
            image = MainActivity.nfcResult.getFaceImage();
        } catch (NullPointerException ignored) {
        }
        if (image == null) {
            // Otherwise the document front image scan
            image = MainActivity.coreResult.getFrontImage();
        }

        // Start the Liveness Check. A scan result is only needed for the face match. Without the
        // face match the liveness check can also run separately.
        binding.contentResult.startLivenessButton.setOnClickListener(view -> {
            VerifaiLiveness.clear(this);

            ArrayList<LivenessCheck> checks = new ArrayList<>();
            checks.add(new CloseEyes());
            checks.add(new Tilt(-25));

            // Add Face match check with either NFC face image, or document front image
            if (image != null) {
                checks.add(new FaceMatching(image));
            }

            LivenessCheckListener resultListener = new LivenessCheckListener() {
                @Override
                public void onResult(@NonNull LivenessCheckResults results) {
                    for (LivenessCheckResultBase result : results.getResultList()) {
                        Log.d(TAG, String.format(result.getCheck().getInstruction()));
                        Log.d(TAG, String.format(String.valueOf(result.getStatus())));
                        if (result instanceof FaceMatchingCheckResult) {
                            FaceMatchingCheckResult faceMatchResult = (FaceMatchingCheckResult) result;
                            Log.d(TAG, String.format("Face match?: %b", faceMatchResult.getMatch()));
                            try {
                                Log.d(TAG, String.format("Face match confidence: %f", faceMatchResult.getConfidence() * 100));
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }
                }

                @Override
                public void onError(@NotNull Throwable throwable) {
                    Log.e("main", throwable.getMessage());
                }
            };

            VerifaiLiveness.start(this, resultListener, checks);
        });

        // Display MRZ results from either the Core or NFC flow:
        MrzDataResult mrzResult = MainActivity.coreResult.getMrzData();
        // In case of the NFC flow:
        if (mrzResult == null) {
            mrzResult = MainActivity.nfcResult.getMrzData();
        }
        if (mrzResult != null) {
            binding.contentResult.mrzValue.setText(mrzResult.getMrzString());
            binding.contentResult.firstNameValue.setText(mrzResult.getGivenNames());
            binding.contentResult.lastNameValue.setText(mrzResult.getSurname());
        }

        if (image != null) {
            binding.contentResult.image.setImageBitmap(image);
        }
    }
}
