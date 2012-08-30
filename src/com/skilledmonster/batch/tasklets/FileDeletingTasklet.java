package com.skilledmonster.batch.tasklets;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Tasklet for deleting files
 * @author Jagadeesh
 *
 */
public class FileDeletingTasklet implements Tasklet, InitializingBean {

    private Resource directory;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	// get the directory 
        File dir = directory.getFile();
        
        if( dir != null ) {
        	// get list of all files
            File[] files = dir.listFiles();
        
            if( files != null){
            	// delete files iteratively
            	for (int i = 0; i < files.length; i++) {
            		boolean deleted = files[i].delete();
            		if (!deleted) {
            			throw new UnexpectedJobExecutionException("Could not delete file " + 
                                                              files[i].getPath());
            		}
            	}
            }
        }
        return RepeatStatus.FINISHED;
    }

    public void setDirectoryResource(Resource directory) {
        this.directory = directory;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(directory, "directory must be set");
    } 
}
