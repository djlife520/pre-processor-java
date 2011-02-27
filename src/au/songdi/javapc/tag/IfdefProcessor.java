package au.songdi.javapc.tag;

import java.util.Iterator;
import au.songdi.javapc.ContextManager;
import au.songdi.javapc.DestFileWriter;
import au.songdi.javapc.tag.parser.Parser;

/* For processing /** ifdef express **/
/**
 * @author Di SONG
 * @version 0.1
 */

public class IfdefProcessor extends TagProcessor {

	public IfdefProcessor(Parser p) {
		super(p);
	}

	void doExportProcess(Iterator it, DestFileWriter writer) {
		// TODO Auto-generated method stub
		this.doProcess(it, writer);
	}

	/**
	 * The core method for processing
	 * 
	 * @param it
	 *            Iterator for reading each line
	 * @param writer
	 *            Destination file writer
	 * @return void
	 * 
	 */

	protected void doProcess(Iterator it, DestFileWriter writer) {
		String tagline = (String) it.next();
		String[] express = this.parser.parseExpress(tagline);
		if (express == null)
			return;
		if (this.parser.checkExpress(express)) {

			this.recordIfBlockOnly(it, writer);

		} else {

			this.recordElseBlockOnly(it, writer);
		}
	}
	
	/**
	 * Only process #ifdef block, and ignore #else block
	 * 
	 * @param it
	 *            Iterator for reading each line
	 * @param writer
	 *            Destination file writer
	 * @return void
	 * 
	 */

	protected void recordIfBlockOnly(Iterator it, DestFileWriter writer) {
		// if the value of the key is not defined
		// write following lines before else block into disfile
		while (it.hasNext()) {
			String line = (String) it.next();
			TagProcessor p = null;
			if ((p = TagSelector.getTagProcessor(line)) != null) {
				// if there is only one if block, no else block, use p
				// instanceof EndifProcessor to judge
				if (p instanceof EndifProcessor) {
					p.process(it, writer);
					return;
				}
				// if meet a else tag, jump off all lines are in else block
				if (p instanceof ElseProcessor) {
					new NotNeedElseProcessor().process(it, writer);
					return;
				}
				p.process(it, writer);
			} else {
				// this is a line of source code and write it into destfile
				writer.writeln(line);
			}

		}
	}
	
	/**
	 * Only process #else block, and ignore #ifdef block
	 * 
	 * @param it
	 *            Iterator for reading each line
	 * @param writer
	 *            Destination file writer
	 * @return void
	 * 
	 */

	protected void recordElseBlockOnly(Iterator it, DestFileWriter writer) {
		boolean export = ContextManager.getContext().isExport();
		int count_if = 1;
		while (it.hasNext()) {
			String line = (String) it.next();
			TagProcessor p = null;
			if ((p = TagSelector.getTagProcessor(line)) != null) {

				if ((p instanceof ElseProcessor) && (count_if == 1)) {
					p.process(it, writer);
					return;
				} else {
					if (!export)
						writer.writeln(line);
					if ((p instanceof IfdefProcessor)
							|| (p instanceof IfndefProcessor)) {
						count_if++;
					} else if (p instanceof EndifProcessor) {
						count_if--;
						if (count_if == 0)
							p.process(it, writer);
						return;
					}
				}

			} else {
				if (!export)
					writer.writeCommentln(line);
			}

		}
	}
}
