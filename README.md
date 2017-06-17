This is a simple [Logback](http://logback.qos.ch/) appender which pushes logs to a [Mattermost](https://mattermost.com/) channel.

# How to setup

Add dependency to com.github.rafelbev:logback-mattermost-appender:1.0.0 in your pom.xml.

Add MattermostAppender configuration to logback.xml file

```
	<?xml version="1.0" encoding="UTF-8" ?>
	<configuration>
		...
		<appender name="MATTERMOST" class="com.github.rafelbev.logback.MattermostAppender">
			<!-- Mattermost incoming webhook uri. Uncomment the lines below to use incoming webhook uri instead of API token. -->
			<webhookUri>https://mattermost.server.com/hooks/XXXXXXXXXXXXXXXXXXXXXXXX</webhookUri>
			<!-- Channel that you want to post to -->
			<channel>#api-test</channel>
			<!-- Formatting (you can use Mattermost formatting - URL links, code formatting, etc.) -->
			<layout class="ch.qos.logback.classic.PatternLayout">
				<pattern>%m\nLogLevel: %-5level\nClass: %logger{2}\n</pattern>
			</layout>
			<!-- Username of the messages sender -->
			<username>${HOSTNAME}</username>
			<!-- Icon to use in template -->
			<iconUri>https://logback.qos.ch/images/logos/lblogo.jpg</iconUri>
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
