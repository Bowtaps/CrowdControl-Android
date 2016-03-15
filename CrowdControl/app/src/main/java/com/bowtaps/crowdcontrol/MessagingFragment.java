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

import com.bowtaps.crowdcontrol.model.GroupModel;
import com.bowtaps.crowdcontrol.model.UserProfileModel;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;


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
        //setContentView(R.layout.messaging);



        getActivity().bindService(new Intent(getActivity(), MessageService.class), mServiceConnection, getActivity().BIND_AUTO_CREATE);


        //Intent intent = getIntent();
        mRecipientGroup = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
        //mRecipientId = ;
        //mCurrentUserId = ParseUser.getCurrentUser().getObjectId();

        //mMessagesList = (ListView) findViewById(R.id.listMessages);
        mMessageAdapter = new MessageAdapter(getActivity());
        //mMessagesList.setAdapter(mMessageAdapter);
        populateMessageHistory();

        //mMessageBodyField = (EditText) findViewById(R.id.messageBodyField);

//        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMessage();
//            }
//        });
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
//        String[] userIds = {mCurrentUserId, mRecipientId};
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
//        query.whereContainedIn("senderId", Arrays.asList(userIds));
//        query.whereContainedIn("mRecipientId", Arrays.asList(userIds));
//        query.orderByAscending("createdAt");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
//                if (e == null) {
//                    for (int i = 0; i < messageList.size(); i++) {
//                        WritableMessage message = new WritableMessage(messageList.get(i).get("mRecipientId").toString(), messageList.get(i).get("messageText").toString());
//                        if (messageList.get(i).get("senderId").toString().equals(mCurrentUserId)) {
//                            mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
//                        } else {
//                            mMessageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
//                        }
//                    }
//                }
//            }
//        });
    }

    /**
     * This uses the messaging service to send a message to the given recipients
     *
     * @see com.bowtaps.crowdcontrol.MessageService.MessageServiceInterface
     */
    private void sendMessage() {
        mMessageBody = mMessageBodyField.getText().toString();
        if (mMessageBody.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        List<? extends UserProfileModel> recipients = mRecipientGroup.getGroupMembers();
        recipients.remove(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());

        mMessageService.sendMessage(recipients, mMessageBody);
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
//            if (message.getSenderId().equals(mRecipientId)) {
                final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

                //only add message to parse database if it doesn't already exist there
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
//                query.whereEqualTo("sinchId", message.getMessageId());
//                query.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> messageList, com.parse.ParseException e) {
//                        if (e == null) {
//                            if (messageList.size() == 0) {
//                                ParseObject parseMessage = new ParseObject("ParseMessage");
//                                parseMessage.put("senderId", mCurrentUserId);
//                                parseMessage.put("mRecipientId", writableMessage.getRecipientIds().get(0));
//                                parseMessage.put("messageText", writableMessage.getTextBody());
//                                parseMessage.put("sinchId", message.getMessageId());
//                                parseMessage.saveInBackground();

                mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
//                            }
//                        }
//                    }
//                });
                Log.d("MessagingFragment", "Message received: " + mMessageBody);
//            }
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



            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
        }

        /**
         * Callback Method - asks the receiver if the message was received
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