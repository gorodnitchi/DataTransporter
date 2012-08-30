package com.skilledmonster.batch.tasklets;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

import com.skilledmonster.batch.common.Util;

/**
 * Tasklet for doing SFTP transfer
 * 
 * @author Jagadeesh
 * 
 */
public class SftpOutboundTransferTasklet implements Tasklet {

	protected static final Log logger = LogFactory
			.getLog(CommandLineJobRunner.class);

	private String sourceDir;

	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
			throws Exception {

		ConfigurableApplicationContext context = null;

		try {
			// get the application context
			context = new ClassPathXmlApplicationContext("contextFromDW.xml");

			// create ftpChannel
			MessageChannel ftpChannel = context.getBean("ftpChannel",
					MessageChannel.class);

			// get the list of files that needs to be transfered
			String[] filesToTransfer = Util.getAllFileNames(sourceDir);

			Message<File> message = null;

			// iterative the files and transfer
			for (String fileName : filesToTransfer) {
				File f = new File(fileName);
				// build message payload
				message = MessageBuilder.withPayload(f).build();
				// transfer the file
				ftpChannel.send(message);
			}
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return RepeatStatus.FINISHED;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
}
