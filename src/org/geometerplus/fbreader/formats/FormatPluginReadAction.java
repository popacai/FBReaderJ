package org.geometerplus.fbreader.formats;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

public interface FormatPluginReadAction {
	public void readMetaInfo(Book book) throws BookReadingException;
	public void readModel(BookModel model) throws BookReadingException;
	public void readUids(Book book) throws BookReadingException;
	public ZLImage readCover(ZLFile file);
	public String readAnnotation(ZLFile file);
}
