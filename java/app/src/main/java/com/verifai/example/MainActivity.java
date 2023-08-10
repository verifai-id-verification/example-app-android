package com.verifai.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.verifai.core.pub.CoreConfiguration;
import com.verifai.core.pub.Verifai;
import com.verifai.core.pub.VerifaiLogger;
import com.verifai.core.pub.exceptions.LicenseNotValidException;
import com.verifai.core.pub.listeners.ResultListener;
import com.verifai.core.pub.result.CoreResult;
import com.verifai.example.databinding.ActivityMainBinding;
import com.verifai.nfc.pub.VerifaiNfc;
import com.verifai.nfc.pub.listeners.NfcResultListener;
import com.verifai.nfc.pub.result.NfcResult;

import org.jetbrains.annotations.NotNull;

/**
 * This activity shows how to start the Core flow and the NFC (Core + NFC) flow.
 * In your own application, you'll implement only one of the two.
 */
public class MainActivity extends AppCompatActivity {
    public static CoreResult coreResult;
    public static NfcResult nfcResult;

    public MainActivity() {
        coreResult = null;
        nfcResult = null;
    }

    /**
     * Start the activity and initialize
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Use the license that has been obtained from the Verifai dashboard
        String license = BuildConfig.verifaiLicense;
        Verifai.setLicense(this, license);

        // Optional: Attach a Logger
        Verifai.logger = new VerifaiLogger() {
            private final String tag = "v-example";

            @Override
            public void log(@NotNull Throwable throwable) {

            }

            @Override
            public void log(@NotNull String s) {

            }
        };

        binding.startButton.setOnClickListener(v -> start());
        binding.startNfcButton.setOnClickListener(v -> {
            try {
                startNfc();
            } catch (LicenseNotValidException e) {
                Log.e("main", e.getMessage());
            }
        });
    }


    /**
     * Start the Verifai core flow
     */
    private void start() {
        CoreConfiguration configuration = new CoreConfiguration();
        configuration.setEnableVisualInspection(true);
        Verifai.configure(configuration);
        ResultListener resultListener = new ResultListener() {
            @Override
            public void onSuccess(@NonNull CoreResult coreResult) {
                MainActivity.coreResult = coreResult;
                Intent intent = new Intent(MainActivity.this, VerifaiResultActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                Log.e("main", throwable.getMessage());
            }
        };
        Verifai.start(this, resultListener, "java-example-core");
    }

    /**
     * Start the Verifai NFC flow (includes the core)
     */
    private void startNfc() throws LicenseNotValidException {
        NfcResultListener nfcResultListener = new NfcResultListener() {
            @Override
            public void onSuccess(@NotNull NfcResult nfcResult) {
                MainActivity.nfcResult = nfcResult;
                Intent intent = new Intent(MainActivity.this, VerifaiResultActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(@NotNull Throwable throwable) {
                Log.e("main", throwable.getMessage());
            }
        };

        VerifaiNfc.start(this, nfcResultListener, "java-example-nfc");
    }
}
