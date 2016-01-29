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
 * Use the {@link Messaging2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 * This Fragment will handle all messaging between user and group
 */
public class Messaging2Fragment extends Fragment {

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
    
    public static Messaging2Fragment newInstance(String text) {
        Messaging2Fragment fragment = new Messaging2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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

    @Override
    public void onDestroy() {
        mMessageService.removeMessageClientListener(mMessageClientListener);
        getActivity().unbindService(mServiceConnection);
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMessageService = (MessageService.MessageServiceInterface) iBinder;
            mMessageService.addMessageClientListener(mMessageClientListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMessageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(getActivity(), "Message failed to send.", Toast.LENGTH_LONG).show();
        }

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

        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {



            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            mMessageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
        }

        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}

        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}
    }
}