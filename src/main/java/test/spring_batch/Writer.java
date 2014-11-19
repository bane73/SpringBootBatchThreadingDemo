package test.spring_batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class Writer implements ItemWriter<Model> {

	private static final List<Model> models = new ArrayList<Model>();

	@Override
	public void write(List<? extends Model> models) throws Exception {
		System.out.println("\n\n*** Writing " + models.size() + " models.");

		int firstId = -1;
		int lastId = -1;
		int totalModels = 0;
		for (int i = 0, j = models.size(); i < j; i++) {

			Model m = models.get(i);
			totalModels++;
			if (i == 0) {
				firstId = m.getId();
			}
			else if (i == (j - 1)) {
				lastId = m.getId();
			}

			Writer.models.add(m);
		}

		System.out.println("     Processed " + totalModels + " models with ids " + firstId + "-" + lastId);
		System.out.println("     System-wide there are " + Writer.models.size() + " models.");
	}

}
