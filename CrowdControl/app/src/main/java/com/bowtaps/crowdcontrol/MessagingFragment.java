package com.bowtaps.crowdcontrol;

import android.app.Activity;
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
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.List;

public class MessagingFragment extends Fragment {

    private GroupModel recipientGroup;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private MessageAdapter messageAdapter;
    private ListView messagesList;
    private String currentUserId;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageClientListener messageClientListener = new MyMessageClientListener();

    public static MessagingFragment newInstance(String text) {
        MessagingFragment fragment = new MessagingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MessagingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    //get previous messages from parse & display
    private void populateMessageHistory() {
        /*
        String[] userIds = {currentUserId, recipientId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage message = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        if (messageList.get(i).get("senderId").toString().equals(currentUserId)) {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                        } else {
                            messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                        }
                    }
                }
            }
        });
        */
    }

    private void sendMessage() {
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }

        List<? extends UserProfileModel> recipients = recipientGroup.getGroupMembers();
        recipients.remove(CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile());
        messageService.sendMessage(recipients, messageBody);
        messageBodyField.setText("");
    }

    @Override
    public void onDestroy() {
        messageService.removeMessageClientListener(messageClientListener);
        getActivity().unbindService(serviceConnection);
        getActivity().stopService(new Intent(getActivity(), MessageService.class));
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
            Log.d("MessagingFragment", "Message failed to send: " + message.getTextBody());
            Log.d("MessagingFragment", failureInfo.getSinchError().getMessage());
        }

        @Override
        public void onIncomingMessage(MessageClient client, final Message message) {
            /*
            if (message.getSenderId().equals(recipientId)) {
            */
                final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
    /*
                //only add message to parse database if it doesn't already exist there
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
                query.whereEqualTo("sinchId", message.getMessageId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                        if (e == null) {
                            if (messageList.size() == 0) {
                                ParseObject parseMessage = new ParseObject("ParseMessage");
                                parseMessage.put("senderId", currentUserId);
                                parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                                parseMessage.put("messageText", writableMessage.getTextBody());
                                parseMessage.put("sinchId", message.getMessageId()); 
                                parseMessage.saveInBackground();
    */
                                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
            /*
                            }
                        }
                    }
                });
            }
            */
            Log.d("MessagingFragment", "Message received: " + messageBody);
        }

        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
            Log.d("MessageActivity", "Message sent: " + message.getTextBody());
        }

        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //getActivity().setContentView(R.layout.messaging);
        View v = inflater.inflate(R.layout.messaging, container, false);

        messagesList = (ListView) v.findViewById(R.id.list_messages);
        messageAdapter = new MessageAdapter(getActivity());
        messagesList.setAdapter(messageAdapter);

        getActivity().startService(new Intent(getActivity().getApplicationContext(), MessageService.class));
        getActivity().bindService(new Intent(getActivity(), MessageService.class), serviceConnection, getActivity().BIND_AUTO_CREATE);

        Intent intent = getActivity().getIntent();
        recipientGroup = CrowdControlApplication.getInstance().getModelManager().getCurrentGroup();
        currentUserId = CrowdControlApplication.getInstance().getModelManager().getCurrentUser().getProfile().getId();


        //populateMessageHistory();

        messageBodyField = (EditText) v.findViewById(R.id.messageBodyField);

        v.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return v;
    }
}
