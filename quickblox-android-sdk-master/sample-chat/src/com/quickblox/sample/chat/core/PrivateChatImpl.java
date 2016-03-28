package com.quickblox.sample.chat.core;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBIsTypingListener;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBMessageSentListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.sample.chat.ui.activities.ChatActivity;
import com.quickblox.sample.chat.ui.adapters.ChatAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

public class PrivateChatImpl extends QBMessageListenerImpl<QBPrivateChat> implements Chat, QBPrivateChatManagerListener,
        QBMessageSentListener<QBPrivateChat>, QBIsTypingListener<QBPrivateChat>, QBMessageStatusListener {

    private static final String TAG = "PrivateChatManagerImpl";

    private ChatActivity chatActivity;

    private QBPrivateChatManager privateChatManager;
    private QBPrivateChat privateChat;

    public PrivateChatImpl(ChatActivity chatActivity, Integer opponentID) {
        this.chatActivity = chatActivity;

        initManagerIfNeed();

        // initIfNeed private chat
        //
        privateChat = privateChatManager.getChat(opponentID);
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(opponentID, this);
        }else{
            privateChat.addMessageListener(this);
            privateChat.addMessageSentListener(this);
            privateChat.addIsTypingListener(this);
        }
    }

    private void initManagerIfNeed(){
        if(privateChatManager == null){
            privateChatManager = QBChatService.getInstance().getPrivateChatManager();
            privateChatManager.addPrivateChatManagerListener(this);

            QBMessageStatusesManager messageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
            messageStatusesManager.addMessageStatusListener(this);

        }
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        message.setMarkable(true);
        privateChat.sendMessage(message);
    }

    public void sendIsTypingNotification() throws XMPPException, SmackException.NotConnectedException {
        privateChat.sendIsTypingNotification();
    }

    public void sendStopTypingNotification() throws XMPPException, SmackException.NotConnectedException {
        privateChat.sendStopTypingNotification();
    }


    @Override
    public void release() {
        Log.w(TAG, "release private chat");
        privateChat.removeMessageListener(this);
        privateChatManager.removePrivateChatManagerListener(this);
    }

    @Override
    public void processMessage(QBPrivateChat chat, QBChatMessage message) {
        Log.w(TAG, "new incoming message: " + message);
        sendMessageAsRead(message);
        chatActivity.showMessage(message);
    }

    public void sendMessageAsRead(QBChatMessage message) {
        if(message.isMarkable() || true)
        {
            try {
                privateChat.readMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processError(QBPrivateChat chat, QBChatException error, QBChatMessage originChatMessage){

    }

    @Override
    public void chatCreated(QBPrivateChat incomingPrivateChat, boolean createdLocally) {
        if(!createdLocally){
            privateChat = incomingPrivateChat;
            privateChat.addMessageListener(PrivateChatImpl.this);
            privateChat.addMessageSentListener(PrivateChatImpl.this);
        }

        Log.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
    }

    @Override
    public void processMessageSent(QBPrivateChat qbPrivateChat, QBChatMessage qbChatMessage) {
        Toast.makeText(chatActivity, "message was sent to " + qbChatMessage.getRecipientId(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processMessageFailed(QBPrivateChat qbPrivateChat, QBChatMessage qbChatMessage) {
        Toast.makeText(chatActivity, "message sent failed to " + qbChatMessage.getRecipientId(), Toast.LENGTH_LONG).show();
    }



    @Override
    public void processUserIsTyping(QBPrivateChat qbPrivateChat, Integer integer) {
        if(Looper.getMainLooper() == Looper.myLooper())
        Log.e("TAg", integer + "processUserIsTyping");
        chatActivity.setStatus(true);
    }

    @Override
    public void processUserStopTyping(QBPrivateChat qbPrivateChat, Integer integer) {
        Log.e("TAg", integer + "processUserStopTyping");
        chatActivity.setStatus(false);
    }

    @Override
    public void processMessageDelivered(String messageId, String dialogId, Integer userId)
    {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setId(messageId);
        qbChatMessage.setDialogId(dialogId);
        qbChatMessage.setRecipientId(userId);

        chatActivity.updateDeliveredStatus(qbChatMessage);
    }

    @Override
    public void processMessageRead(String messageId, String dialogId, Integer userId)
    {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setId(messageId);
        qbChatMessage.setDialogId(dialogId);
        qbChatMessage.setRecipientId(userId);

        chatActivity.updateReadStatus(qbChatMessage);
    }
}
