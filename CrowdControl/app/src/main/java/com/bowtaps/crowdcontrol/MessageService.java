package com.bowtaps.crowdcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bowtaps.crowdcontrol.messaging.SinchTextMessage;
import com.bowtaps.crowdcontrol.messaging.TextMessage;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;

public class MessageService extends Service implements SinchClientListener {

    private static final String APP_KEY = "52ccd19d-4487-4645-8abf-b13edd57bffd";
    private static final String APP_SECRET = "Ke+GKezurEqC46z33Da5Ig==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";
    private final MessageServiceInterface serviceInterface = new MessageServiceInterface();
    private SinchClient sinchClient = null;
    private MessageClient messageClient = null;
    private UserProfileModel currentUser;
    private LocalBroadcastManager broadcaster;
    private Intent broadcastIntent = new Intent("com.bowtaps.crowdcontrol.GroupNavigationActivity");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUser = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile();

        if (currentUser != null && !isSinchClientStarted()) {
            startSinchClient(currentUser.getId());
        }

        broadcaster = LocalBroadcastManager.getInstance(this);

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSinchClient(String username) {
        sinchClient = Sinch.getSinchClientBuilder().context(this).userId(username).applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        sinchClient.addSinchClientListener(this);

        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.checkManifest();
        sinchClient.start();
    }

    private boolean isSinchClientStarted() {
        return sinchClient != null && sinchClient.isStarted();
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        broadcastIntent.putExtra("success", false);
        broadcaster.sendBroadcast(broadcastIntent);

        sinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        broadcastIntent.putExtra("success", true);
        broadcaster.sendBroadcast(broadcastIntent);

        client.startListeningOnActiveConnection();
        messageClient = client.getMessageClient();
    }

    @Override
    public void onClientStopped(SinchClient client) {
        sinchClient = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceInterface;
    }

    @Override
    public void onLogMessage(int level, String area, String message) {
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
    }

    public String sendMessage(List<? extends UserProfileModel> recipients, String textBody) {
        if (messageClient != null) {
            String userId;
            userId = currentUser.getId();
            if (!recipients.isEmpty()) {
                userId = recipients.get(0).getId();
            }
            Log.d("Messaging Service", "Number Of recipients: " + recipients.size());
            WritableMessage message = new WritableMessage(userId, textBody);
            for (int i = 1; i < recipients.size(); i++) {
                userId = recipients.get(i).getId();
                message.addRecipient(userId);
            }
            messageClient.send(message);
            return message.getMessageId();
        }
        return null;
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (messageClient != null) {
            messageClient.removeMessageClientListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    public class MessageServiceInterface extends Binder {
        public String sendMessage(List<? extends UserProfileModel> recipients, String textBody) {
            return MessageService.this.sendMessage(recipients, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }

        public boolean isSinchClientStarted() {
            return MessageService.this.isSinchClientStarted();
        }
    }
}

