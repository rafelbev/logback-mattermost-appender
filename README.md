This is a simple [Logback](http://logback.qos.ch/) appender which pushes logs to [Mattermost](https://mattermost.com/) channel.

# How to setup

Add dependency to com.github.maricn:logback-mattermost-appender:1.2.0 in your pom.xml.

Add MattermostAppender configuration to logback.xml file

```
	<?xml version="1.0" encoding="UTF-8" ?>
	<configuration>
		...
		<appender name="MATTERMOST" class="com.github.maricn.logback.MattermostAppender">
			<!-- Mattermost API token -->
			<token>1111111111-1111111-11111111-111111111</token>
			<!-- Mattermost incoming webhook uri. Uncomment the lines below to use incoming webhook uri instead of API token. -->
			<!--
			<webhookUri>https://hooks.mattermost.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX</webhookUri>
			-->
			<!-- Channel that you want to post - default is #general -->
			<channel>#api-test</channel>
			<!-- Formatting (you can use Mattermost formatting - URL links, code formatting, etc.) -->
			<layout class="ch.qos.logback.classic.PatternLayout">
				<pattern>%-4relative [%thread] %-5level %class - %msg%n</pattern>
			</layout>
			<!-- Username of the messages sender -->
			<username>${HOSTNAME}</username>
			<!-- Emoji to be used for messages -->
			<iconEmoji>:stuck_out_tongue_winking_eye:</iconEmoji>
		</appender>

		<!-- Currently recommended way of using Mattermost appender -->
		<appender name="ASYNC_MATTERMOST" class="ch.qos.logback.classic.AsyncAppender">
			<appender-ref ref="MATTERMOST" />
			<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
				<level>ERROR</level>
			</filter>
		</appender>

		<root>
			<level value="ALL" />
			<appender-ref ref="ASYNC_MATTERMOST" />
		</root>

	</configuration>
```
