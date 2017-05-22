package de.l3s.boilerpipe.sax;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * An {@link de.l3s.boilerpipe.sax.InputSourceable} for {@link de.l3s.boilerpipe.sax.HTMLFetcher}.
 * 
 * @author Christian Kohlschütter
 */
public class HTMLDocument implements InputSourceable {
	private final Charset charset;
	private final byte[] data;

	public HTMLDocument(final byte[] data, final Charset charset) {
		this.data = data;
		this.charset = charset;
	}
	
	public HTMLDocument(final String data) {
		Charset cs = Charset.forName("utf-8");
		this.data = data.getBytes(cs);
		this.charset = cs;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public InputSource toInputSource() {
		final InputSource is = new InputSource(new ByteArrayInputStream(data));
		is.setEncoding(charset.name());
		return is;
	}
}
