package otc.data.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;

import otc.data.Attribute;
import otc.data.AttributeDataset;
import otc.data.NumericAttribute;

public class DelimitedTextParser {
	/**
	 * The delimiter character to separate columns.
	 */
	private String delimiter = "\\s+";
	/**
	 * The start of comments.
	 */
	private String comment = "%";
	/**
	 * The placeholder of missing values in the data.
	 */
	private String missing = "?";
	/**
	 * The dataset has column names at first row.
	 */
	private boolean hasColumnNames = false;
	/**
	 * The dataset has row names at first column.
	 */
	private boolean hasRowNames = false;
	/**
	 * The attribute of dependent/response variable.
	 */
	private Attribute response = null;
	/**
	 * The column index of dependent/response variable.
	 */
	private int responseIndex = -1;

	/**
	 * Constructor with default delimiter of white space, comment line starting with
	 * '%', missing value placeholder "?", no column names, no row names.
	 */
	public DelimitedTextParser() {
	}

	/**
	 * Returns the delimiter character/string.
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Set the delimiter character/string.
	 */
	public DelimitedTextParser setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
	}

	/**
	 * Returns the character/string that starts a comment line.
	 */
	public String getCommentStartWith() {
		return comment;
	}

	/**
	 * Set the character/string that starts a comment line.
	 */
	public DelimitedTextParser setCommentStartWith(String comment) {
		this.comment = comment;
		return this;
	}

	/**
	 * Returns the missing value placeholder.
	 */
	public String getMissingValuePlaceholder() {
		return missing;
	}

	/**
	 * Set the missing value placeholder.
	 */
	public DelimitedTextParser setMissingValuePlaceholder(String missing) {
		this.missing = missing;
		return this;
	}

	/**
	 * Sets the attribute and column index (starting at 0) of dependent/response
	 * variable.
	 */
	public DelimitedTextParser setResponseIndex(Attribute response, int index) {
		if (response.getType() != Attribute.Type.NOMINAL && response.getType() != Attribute.Type.NUMERIC) {
			throw new IllegalArgumentException("The response variable is not numeric or nominal.");
		}

		this.response = response;
		this.responseIndex = index;
		return this;
	}

	/**
	 * Returns if the dataset has row names (at column 0).
	 */
	public boolean hasRowNames() {
		return hasRowNames;
	}

	/**
	 * Set if the dataset has row names (at column 0).
	 */
	public DelimitedTextParser setRowNames(boolean hasRowNames) {
		this.hasRowNames = hasRowNames;
		return this;
	}

	/**
	 * Returns if the dataset has column namesS (at row 0).
	 */
	public boolean hasColumnNames() {
		return hasColumnNames;
	}

	/**
	 * Set if the dataset has column names (at row 0).
	 */
	public DelimitedTextParser setColumnNames(boolean hasColNames) {
		this.hasColumnNames = hasColNames;
		return this;
	}

	/**
	 * Parse a dataset from given URI.
	 * 
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(URI uri) throws IOException, ParseException {
		return parse(new File(uri));
	}

	/**
	 * Parse a dataset from given URI.
	 * 
	 * @param uri
	 *            the URI of data source.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(String name, Attribute[] attributes, URI uri) throws IOException, ParseException {
		return parse(name, attributes, new File(uri));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param path
	 *            the file path of data source.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(String path) throws IOException, ParseException {
		return parse(new File(path));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param path
	 *            the file path of data source.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(Attribute[] attributes, String path) throws IOException, ParseException {
		return parse(attributes, new File(path));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param path
	 *            the file path of data source.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(String name, Attribute[] attributes, String path) throws IOException, ParseException {
		return parse(name, attributes, new File(path));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param file
	 *            the file of data source.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(File file) throws IOException, ParseException {
		String name = file.getPath();
		return parse(name, new FileInputStream(file));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param file
	 *            the file of data source.
	 * @throws java.io.IOException
	 */
	public AttributeDataset parse(String name, File file) throws IOException, ParseException {
		return parse(name, new FileInputStream(file));
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param file
	 *            the file of data source.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @throws java.io.IOException
	 */
	public AttributeDataset parse(Attribute[] attributes, File file) throws IOException, ParseException {
		String name = file.getPath();
		return parse(name, attributes, file);
	}

	/**
	 * Parse a dataset from given file.
	 * 
	 * @param file
	 *            the file of data source.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(String name, Attribute[] attributes, File file) throws IOException, ParseException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			return parse(name, attributes, reader);
		}
	}

	/**
	 * Parse a dataset from an input stream.
	 * 
	 * @param name
	 *            the name of dataset.
	 * @param stream
	 *            the input stream of data.
	 * @throws java.io.FileNotFoundException
	 */
	public AttributeDataset parse(String name, InputStream stream) throws IOException, ParseException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			return parse(name, null, reader);
		}
	}

	/**
	 * Parse a dataset from a buffered reader.
	 * 
	 * @param name
	 *            the name of dataset.
	 * @param attributes
	 *            the list attributes of data in proper order.
	 * @param reader
	 *            the buffered reader for data.
	 * @throws java.io.IOException
	 */
	private AttributeDataset parse(String name, Attribute[] attributes, BufferedReader reader)
			throws IOException, ParseException {
		String line = reader.readLine();
		while (line != null) {
			if (line.isEmpty() || line.startsWith(comment)) {
				line = reader.readLine();
			} else {
				break;
			}
		}

		if (line == null) {
			throw new IOException("Empty data source.");
		}

		String[] s = line.split(delimiter, 0);

		if (attributes == null) {
			int p = s.length;
			if (hasRowNames) {
				p--;
			}

			if (responseIndex >= s.length) {
				throw new ParseException("Invalid response variable index: " + responseIndex, responseIndex);
			}

			if (responseIndex >= 0) {
				p--;
			}

			attributes = new Attribute[p];
			for (int i = 0; i < p; i++) {
				attributes[i] = new NumericAttribute("V" + (i + 1));
			}
		}

		int ncols = attributes.length;
		int startColumn = 0;

		if (hasRowNames) {
			ncols++;
			startColumn = 1;
		}

		if (responseIndex >= 0) {
			ncols++;
		}

		if (ncols != s.length)
			throw new ParseException(String.format("%d columns, expected %d", s.length, ncols), s.length);

		AttributeDataset data = new AttributeDataset(name, attributes, response);

		if (hasColumnNames) {
			for (int i = startColumn, k = 0; i < s.length; i++) {
				if (i != responseIndex) {
					attributes[k++].setName(s[i]);
				} else {
					response.setName(s[i]);
				}
			}
		} else {
			String rowName = hasRowNames ? s[0] : null;
			double[] x = new double[attributes.length];
			double y = Double.NaN;

			for (int i = startColumn, k = 0; i < s.length; i++) {
				if (i == responseIndex) {
					y = response.valueOf(s[i]);
				} else if (missing != null && missing.equalsIgnoreCase(s[i])) {
					x[k++] = Double.NaN;
				} else {
					x[k] = attributes[k].valueOf(s[i]);
					k++;
				}
			}

			AttributeDataset.Row datum = Double.isNaN(y) ? data.add(x) : data.add(x, y);
			datum.name = rowName;
		}

		while ((line = reader.readLine()) != null) {
			if (line.isEmpty() || line.startsWith(comment)) {
				continue;
			}

			s = line.split(delimiter, 0);
			if (s.length != ncols) {
				throw new ParseException(String.format("%d columns, expected %d", s.length, ncols), s.length);
			}

			String rowName = hasRowNames ? s[0] : null;
			double[] x = new double[attributes.length];
			double y = Double.NaN;

			for (int i = startColumn, k = 0; i < s.length; i++) {
				if (i == responseIndex) {
					y = response.valueOf(s[i]);
				} else if (missing != null && missing.equalsIgnoreCase(s[i])) {
					x[k++] = Double.NaN;
				} else {
					x[k] = attributes[k].valueOf(s[i]);
					k++;
				}
			}

			AttributeDataset.Row datum = Double.isNaN(y) ? data.add(x) : data.add(x, y);
			datum.name = rowName;
		}

		return data;
	}
}
