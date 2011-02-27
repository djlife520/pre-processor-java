package au.songdi.javapc.tag;

import java.util.Iterator;
import au.songdi.javapc.DestFileWriter;

/* For processing /** else **/
/** 
 * @author Di SONG
 * @version 0.1
 */

public final class ElseProcessor extends TagProcessor {

	void doExportProcess(Iterator it,DestFileWriter writer) {
		// TODO Auto-generated method stub
		// sample: /** #else **/
		while (it.hasNext()) {
			String line = (String)it.next();
			TagProcessor p = null;
			if ((p = TagSelector.getTagProcessor(line)) != null) {
				// do with a status process class
				p.process(it,writer);
				if (p instanceof EndifProcessor)
					return;
			} else {
				// write to destfile
				writer.writeln(line);
			}

		}
	}
}
