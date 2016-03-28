package com.bowtaps.crowdcontrol;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bowtaps.crowdcontrol.messaging.ModelTextMessage;
import com.bowtaps.crowdcontrol.messaging.SinchTextMessage;
import com.bowtaps.crowdcontrol.model.ConversationModel;
import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.MessageModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * This Fragment will handle all messaging between user and group
 */
public class MessagingFragment extends Fragment implements GroupService.GroupUpdatesListener {

    private String mRecipientId;
    private EditText mMessageBodyField;
    private String mMessageBody;
    private MessageService.MessageServiceInterface mMessageService;
    private MessageAdapter mMessageAdapter;
    private ListView mMessagesList;
    private String mCurrentUserId;
    private ServiceConnection mServiceConnection = new MyServiceConnection();
    private MessageClientListener mMessageClientListener = new MyMessageClientListener();
    private GroupModel mRecipientGroup;
    private ConversationModel mRecipientConversation;

    /**
     * Creates the Fragment
     *
     * @param text  This was a placeholder argument from the tabs adapter
     * @return
     */
    public static MessagingFragment newInstance(String text) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the messaging fragment during which the fragment is bound to the messaging service.
     * This also sets up what members are to receive messages from user, and displays messages from
     * the conversation.
     *
     * @see MessageService
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().bindService(new Intent(getActivity(), MessageService.class), mServiceConnection, getActivity().BIND_AUTO_CREATE);

        mRecipientGroup = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
        mCurrentUserId = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile().getId();
        if (mRecipientGroup.getCachedConversations().isEmpty()) {
            try {
                CrowdControlApplication.getInstance().getModelManager().fetchConversations();
            } catch (Exception e) {
                Log.d("MessagingFragment", e.getMessage());
                mRecipientConversation = null;
            }
        }
        if (mRecipientGroup.getCachedConversations().isEmpty()) {
            mRecipientConversation = null;
        } else {
            mRecipientConversation = mRecipientGroup.getCachedConversations().get(0);
            mRecipientConversation.addParticipant(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
            mRecipientConversation.saveInBackground(null);
        }

        mMessageAdapter = new MessageAdapter(getActivity());
        populateMessageHistory();
    }

    /**
     * This Creates the actual view being held by this fragment. This view will display messages
     * and will have handles to a container that is used to send messages.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.messaging, container, false);
        //v.setContentView(R.layout.messaging)

        mMessagesList = (ListView) v.findViewById(R.id.list_messages);
        mMessagesList.setAdapter(mMessageAdapter);

        mMessageBodyField = (EditText) v.findViewById(R.id.messageBodyField);

        v.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return v;
    }

    //get previous messages from parse & display
    private void populateMessageHistory() {
        try {
            CrowdControlApplication.getInstance().getModelManager().fetchMessages(mRecipientConversation);
        } catch (Exception e) {
            Log.d("MessagingFragment", e.getMessage());
        }

        if (mRecipientConversation != null) {
            MessageModel message;
            List<? extends MessageModel> cachedMessages = mRecipientConversation.getCachedMessages();
            ListIterator<? extends MessageModel> it = cachedMessages.listIterator(cachedMessages.size());

            while (it.hasPrevious()) {
                message = it.previous();
                mMessageAdapter.addMessage(new ModelTextMessage(message), message.getFrom().getId().equals(mCurrentUserId) ? MessageAdapter.DIRECTION_OUTGOING : MessageAdapter.DIRECTION_INCOMING);
            }
        }
    }

    /**
     * This uses the messaging service to send a message to the given recipients
     *
     * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
     */
    private void sendMessage() {
        if (mRecipientConversation == null) {
            Log.d("MessagingFragment", "Unable to send message; no known conversation");
            return;
        }

        mMessageBody = mMessageBodyField.getText().toString();
        if (mMessageBody.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        List<? extends UserProfileModel> recipients = mRecipientConversation.getParticipants();
        recipients.remove(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
        mMessageService.sendMessage(recipients, mMessageBody);

        List<? extends MessageModel> models = CrowdControlApplication.getInstance().getModelManager().createMessage(mRecipientConversation, mMessageBody);
        if (!models.isEmpty()) {
            MessageModel model = models.get(0);
            mMessageAdapter.addMessage(new ModelTextMessage(model), MessageAdapter.DIRECTION_OUTGOING);
        }
        mMessageBodyField.setText("");
    }

    /**
     * unbinds the service
     *
     * @see MessageService
     */
    @Override
    public void onDestroy() {
        mMessageService.removeMessageClientListener(mMessageClientListener);
        getActivity().unbindService(mServiceConnection);
        super.onDestroy();
    }

    @Override
    public void onReceivedGroupUpdate(GroupModel group) {
        mRecipientGroup = group;

        if (mRecipientConversation == null && !group.getCachedConversations().isEmpty()) {
            mRecipientConversation = group.getCachedConversations().get(0);
            mRecipientConversation.addParticipant(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
            mRecipientConversation.saveInBackground(null);
            populateMessageHistory();
        }
    }

    /**
     * This connects to the messaging service allowing the fragment to send and receive messages
     *
     * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
     */
    private class MyServiceConnection implements ServiceConnection {

        /**
         * Call back method to MessageServiceInterface
         * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
         *
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessageService = (MessageService.MessageServiceInterface) iBinder;
            mMessageService.addMessageClientListener(mMessageClientListener);
        }

        /**
         * Remove the service if disconnected
         * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
         *
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMessageService = null;
        }
    }

    /**
     * Listener that listens for messages and sends messages over the messaging service
     *
     * @see MessageService
     */
    private class MyMessageClientListener implements MessageClientListener {

        /**
         * This tells the user when a message failed
         *
         * @param client    client though which the message was sent
         * @param message   the failed message
         * @param failureInfo   Why it failed
         */
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
            Log.d("Messaging Fragment", failureInfo.getSinchError().getMessage());
        }

        /**
         * Callback method - used whenever the client receives a message
         * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
         *
         * @param client
         * @param message
         */
        @Override
        public void onIncomingMessage(MessageClient client, final Message message) {
            UserProfileModel sender = null;
            for (UserProfileModel participant : mRecipientConversation.getParticipants()) {
                if (participant .getId().equals(message.getSenderId())) {
                    sender = participant;
                    break;
                }
            }
            mMessageAdapter.addMessage(new SinchTextMessage(message, sender), MessageAdapter.DIRECTION_INCOMING);
            Log.d("MessagingFragment", "Message received: " + mMessageBody);
        }

        /**
         * Callback method - used whenever the user attempts to send a message
         * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
         *
         * @param client
         * @param message
         * @param recipientId
         */
        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            // TODO
        }

        /**
         * Callback method, triggered once a message has been successfully delivered to its
         * destination.
         * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
         *
         * @param client
         * @param deliveryInfo
         */
        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}
    }
}