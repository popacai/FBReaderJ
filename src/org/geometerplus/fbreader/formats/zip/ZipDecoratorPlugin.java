package org.geometerplus.fbreader.formats.zip;

import java.util.List;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

public class ZipDecoratorPlugin extends FormatPlugin {

	public ZipDecoratorPlugin(FormatPlugin formatPluginReference) {
		//TODO: Should this not require files to be named <filename>.<basetype>.zip?
		super(formatPluginReference.supportedFileType() + ".zip");
		myFormatPluginReference = formatPluginReference;
	}

	@Override
	public void readMetaInfo(Book book) throws BookReadingException {
		myFormatPluginReference.readMetaInfo(book);
	}

	@Override
	public void readModel(BookModel model) throws BookReadingException {
		myFormatPluginReference.readModel(model);
	}

	@Override
	public void readUids(Book book) throws BookReadingException {
		myFormatPluginReference.readUids(book);
	}

	@Override
	public ZLImage readCover(ZLFile file) {
		//This does not seem to need to worry about zip format
		return myFormatPluginReference.readCover(file);
	}

	@Override
	public String readAnnotation(ZLFile file) {
		return myFormatPluginReference.readAnnotation(file);
	}

	@Override
	public Type type() {
		return myFormatPluginReference.type();
	}

	@Override
	public ZLFile realBookFile(ZLFile file) throws BookReadingException {
		return extractFile(file);
	}

	//This code was originally in FB2Util.java
	private ZLFile extractFile(ZLFile file) {
		final String name = file.getShortName().toLowerCase();
		if (name.endsWith(supportedFileType()) && file.isArchive()) {
			//ZLFile apparently already unzips everything.
			// By the look of it, it handles a number of other formats too
			final List<ZLFile> children = file.children();
			if (children == null) {
				return null;
			}
			ZLFile candidate = null;
			for (ZLFile item : children) {
				if (myFormatPluginReference.supportedFileType().equals(item.getExtension())) {
					if (candidate == null) {
						candidate = item;
					} else {
						return null;
					}
				}
			}
			return candidate;
		} else {
			//TODO: This should really never happen.  Should it throw an error?
			return file;
		}
	}
	
	private final FormatPlugin myFormatPluginReference;
}
