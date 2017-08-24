package pe.edu.upc.speechplugin;
/**
 * Created by RICHI on 22/08/2017.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import java.util.ArrayList;
import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.SpeechRecognizer.createSpeechRecognizer;

public class SpeechService extends RecognitionService implements RecognitionListener {

    public static SpeechRecognizer mSpeech;
    public static Intent recognizeIntent;
    public static Context mContext;
    static String TAG = "VOICE RECOGNITION";

    public static void StartListenning(UnityPlayerActivity activity) {
        Log.e(TAG, "START THE SERVICE! ");

        if( UnityPlayer.currentActivity != null) {
            SendDebug("Start service");
            Intent intent = new Intent(activity, SpeechService.class);
            activity.startService(intent);
        }
    }

    //The service is created and voice recognition service has been started
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        super.onCreate();
        mContext = getApplicationContext();
        mSpeech = createSpeechRecognizer(this);
        mSpeech.setRecognitionListener(this);
        recognizeIntent = RecognizerIntent.getVoiceDetailsIntent(mContext);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mSpeech.startListening(recognizeIntent);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(mSpeech != null){
            mSpeech.stopListening();
            mSpeech.destroy();
        }
        Log.e(TAG, "Service stopped");
        super.onDestroy();
    }

    public void SendMessage(String message){
        UnityPlayer.UnitySendMessage("MainCamera", "onActivityResult", message);
        stopSelf();
    }

    public static void SendDebug(String message){
        UnityPlayer.UnitySendMessage("MainCamera", "setText", message);
    }

    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {
    }

    @Override
    protected void onCancel(Callback listener) {
    }

    @Override
    protected void onStopListening(Callback listener) {
        SendDebug("StopListening");
    }


    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB){
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        mSpeech.stopListening();
    }

    @Override
    public void onError(int error) {
        try {
            String message;
            switch (error)
            {
                case ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case ERROR_NETWORK:
                    message = "Network error";
                    break;
                case ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case ERROR_SERVER:
                    message = "error from server";
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }
            Log.e(TAG, message);
            SendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResults(Bundle results) {
        Log.e(TAG, "onResults");
        ArrayList<String> _array = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.e(TAG, "Total results: " + String.valueOf(_array.size()));
        String result = _array.size() > 0 ? _array.get(0) : "No results";
        SendMessage(result);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        //checkForCommands(partialResults);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }
}
