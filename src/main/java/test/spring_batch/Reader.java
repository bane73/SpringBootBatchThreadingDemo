package test.spring_batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class Reader implements ItemReader<Model> {

	/*
	 * This just simulates a file-parser that will read a log file, one log entry at a time,
	 * and return a single log entry on each call to read().
	 * 
	 * NUM_ENTRIES_PER_FILE is to simulate an entire file
	 * (ie: after read() being called 10 times, we've finished an entire file)
	 * 
	 * NUM_FILES is to simulate an entire folder of files
	 * (ie: after read() being called 100K times, we've finished reading all files)
	 */

	private static int COUNTER = 0;
	private static final int NUM_ENTRIES_PER_FILE = 10;
	private static final int NUM_FILES = 1000;

	@Override
	public Model read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		COUNTER++;

		// done reading all files (ie: all read-work is done)
		if (COUNTER > (NUM_FILES * NUM_ENTRIES_PER_FILE)) {
			return null;
		}

		// return new Model(COUNTER);
		return null;
	}

	public boolean isDoneWithFile() {

		// done reading a file (ie: trigger the writer to write the entries from the current file)
		if (COUNTER % NUM_ENTRIES_PER_FILE == 0) {
			return true;
		}

		return false;
	}

}
