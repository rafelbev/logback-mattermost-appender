package com.github.rafelbev.logback;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MattermostAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static Layout<ILoggingEvent> defaultLayout = new LayoutBase<ILoggingEvent>() {
        public String doLayout(ILoggingEvent event) {
            return "-- [" + event.getLevel() + "]" +
                    event.getLoggerName() + " - " +
                    event.getFormattedMessage().replaceAll("\n", "\n\t");
        }
    };

    private String webhookUri;
    private String channel;
    private String iconUri;
    private String username = "logback-mattermost-appender";
    private Layout<ILoggingEvent> layout = defaultLayout;

    private int timeout = 30_000;

    @Override
    protected void append(final ILoggingEvent evt) {
        try {
            sendMessageWithWebhookUri(evt);
        } catch (Exception ex) {
            ex.printStackTrace();
            addError("Error posting log to mattermost (" + webhookUri + "): " + evt, ex);
        }
    }

    private void sendMessageWithWebhookUri(final ILoggingEvent evt) throws IOException {
        String[] parts = layout.doLayout(evt).split("\n", 2);

        Map<String, Object> message = new HashMap<>();
        message.put("text", parts[0]);

        if (isNotBlank(channel)) {
            message.put("channel", channel);
        }
        if (isNotBlank(username)) {
            message.put("username", username);
        }
        if (isNotBlank(iconUri)) {
            message.put("icon_url", iconUri);
        }

        // Send the lines below the first line as an attachment.
        if (parts.length > 1) {
            Map<String, String> attachment = new HashMap<>();
            attachment.put("text", parts[1]);
            message.put("attachments", Arrays.asList(attachment));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        final byte[] bytes = objectMapper.writeValueAsBytes(message);

        postMessage(webhookUri, "application/json", bytes);
    }

    private void postMessage(String uri, String contentType, byte[] bytes) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestProperty("Content-Type", contentType);

        final OutputStream os = conn.getOutputStream();
        os.write(bytes);

        os.flush();
        os.close();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(final Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getWebhookUri() {
        return webhookUri;
    }

    public void setWebhookUri(String webhookUri) {
        this.webhookUri = webhookUri;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }
}
