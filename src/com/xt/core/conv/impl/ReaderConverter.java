
package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;
import com.xt.core.utils.IOHelper;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 *
 * @author albert
 */
public class ReaderConverter  implements BaseConverter<Reader> {

    public Class<Reader> getConvertedClass() {
        return Reader.class;
    }

    public Reader convert(String value) {
        if (value == null) {
            return null;
        }
        StringReader bais = new StringReader(value);
        return bais;
    }

    public String convertToString(Reader reader) {
        if (reader == null) {
            return null;
        }
        StringWriter writer = new StringWriter();
        IOHelper.r2w(reader, writer, true, true);
        return writer.toString();
    }

}
