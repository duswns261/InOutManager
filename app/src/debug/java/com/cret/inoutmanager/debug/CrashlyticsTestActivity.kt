package com.cret.inoutmanager.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.R
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Debug-only entry point for manually verifying Firebase Crashlytics fatal and
 * non-fatal reporting. Launched via adb explicit intent; not reachable from the
 * production app UI or navigation graph.
 */
class CrashlyticsTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CrashlyticsTestScreen(
                        onRecordNonFatal = ::recordNonFatal,
                        onForceCrash = ::forceCrash,
                    )
                }
            }
        }
    }

    private fun recordNonFatal() {
        FirebaseCrashlytics.getInstance()
            .recordException(IllegalStateException("Synthetic non-fatal test exception"))
    }

    private fun forceCrash() {
        throw RuntimeException("Synthetic fatal test crash")
    }
}

@Composable
private fun CrashlyticsTestScreen(
    onRecordNonFatal: () -> Unit,
    onForceCrash: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(text = stringResource(R.string.crashlytics_test_title))
        Text(text = stringResource(R.string.crashlytics_test_description))
        Button(onClick = onRecordNonFatal) {
            Text(text = stringResource(R.string.crashlytics_test_non_fatal_button))
        }
        Button(onClick = onForceCrash) {
            Text(text = stringResource(R.string.crashlytics_test_fatal_button))
        }
    }
}
