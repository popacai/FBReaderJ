/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.book;

import java.util.LinkedList;
import java.util.List;

public abstract class Filter implements Tagable {
	//Filter abstract methods
	public abstract boolean matches(Book book);

	//Default filter methods
	public boolean hasChildren() {
		return false;
	}
	
	public Filter getFirst() {
		return null;
	}

	public Filter getSecond() {
		return null;
	}
	
	//Default tagable methods
	@Override
	public boolean isSingleTag() {
		return true;
	}
	
	@Override
	public String getTag() {
		return "filter";
	}

	//Implementations of Filter
	public final static class Empty extends Filter {
		public boolean matches(Book book) {
			return true;
		}

		@Override
		public String[] getAttributes() {
			return attr;
		}
		
		private static final String[] attr = {"type", "empty"};
	}

	public final static class ByAuthor extends Filter {
		public final Author Author;

		public ByAuthor(Author author) {
			Author = author;
		}

		public boolean matches(Book book) {
			final List<Author> bookAuthors = book.authors();
			return
				Author.NULL.equals(Author) ? bookAuthors.isEmpty() : bookAuthors.contains(Author);
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "author",
				"displayName", Author.DisplayName,
				"sorkKey", Author.SortKey	//TODO: This looks like a typo!
			};
			return attr;
		}
	}

	public final static class ByTag extends Filter {
		public final Tag Tag;

		public ByTag(Tag tag) {
			Tag = tag;
		}

		public boolean matches(Book book) {
			final List<Tag> bookTags = book.tags();
			return
				Tag.NULL.equals(Tag) ? bookTags.isEmpty() : bookTags.contains(Tag);
		}

		public String[] getAttributes() {
			final LinkedList<String> lst = new LinkedList<String>();
			for (Tag t = Tag; t != null; t = t.Parent) {
				lst.add(0, t.Name);
			}
			final String[] attr = new String[lst.size() * 2 + 2];
			int index = 0;
			attr[index++] = "type";
			attr[index++] = "tag";
			int num = 0;
			for (String name : lst) {
				attr[index++] = "name" + num++;
				attr[index++] = name;
			}
			return attr;
		}
	}

	public final static class ByLabel extends Filter {
		public final String Label;

		public ByLabel(String label) {
			Label = label;
		}

		public boolean matches(Book book) {
			return book.labels().contains(Label);
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "label",
				"displayName", Label
			};
			return attr;
		}
	}

	public final static class ByPattern extends Filter {
		public final String Pattern;

		public ByPattern(String pattern) {
			Pattern = pattern != null ? pattern.toLowerCase() : "";
		}

		public boolean matches(Book book) {
			return book != null && !"".equals(Pattern) && book.matches(Pattern);
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "pattern",
				"pattern", Pattern
			};
			return attr;
		}
	}

	public final static class ByTitlePrefix extends Filter {
		public final String Prefix;

		public ByTitlePrefix(String prefix) {
			Prefix = prefix != null ? prefix : "";
		}

		public boolean matches(Book book) {
			return book != null && Prefix.equals(book.firstTitleLetter());
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "title-prefix",
				"prefix", Prefix
			};
			return attr;
		}
	}

	public final static class BySeries extends Filter {
		public final Series Series;

		public BySeries(Series series) {
			Series = series;
		}

		public boolean matches(Book book) {
			final SeriesInfo info = book.getSeriesInfo();
			return info != null && Series.equals(info.Series);
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "series",
				"title", Series.getTitle()
			};
			return attr;
		}
	}

	public final static class HasBookmark extends Filter {
		public boolean matches(Book book) {
			return book != null && book.HasBookmark;
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = {
				"type", "has-bookmark"
			};
			return attr;
		}
	}

	public final static class And extends Filter {
		public final Filter First;
		public final Filter Second;

		public And(Filter first, Filter second) {
			First = first;
			Second = second;
		}

		public boolean matches(Book book) {
			return First.matches(book) && Second.matches(book);
		}

		public boolean hasChildren() {
			return true;
		}
		
		public Filter getFirst() {
			return First;
		}

		public Filter getSecond() {
			return Second;
		}

		@Override
		public boolean isSingleTag() {
			return false;
		}
		
		@Override
		public String getTag() {
			return "and";
		}

		@Override
		public String[] getAttributes() {
			final String[] attr = null;
			return attr;
		}
	}

	public final static class Or extends Filter {
		public final Filter First;
		public final Filter Second;

		public Or(Filter first, Filter second) {
			First = first;
			Second = second;
		}

		public boolean matches(Book book) {
			return First.matches(book) || Second.matches(book);
		}

		public boolean hasChildren() {
			return true;
		}
		
		public Filter getFirst() {
			return First;
		}

		public Filter getSecond() {
			return Second;
		}
		
		@Override
		public boolean isSingleTag() {
			return false;
		}

		@Override
		public String getTag() {
			return "or";
		}
		
		@Override
		public String[] getAttributes() {
			final String[] attr = null;
			return attr;
		}
	}
}
