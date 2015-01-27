package com.xt.core.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Albert
 */
public class Tar extends Zip {

    public Tar(File rootFile, OutputStream outputStream) {
        super(rootFile, outputStream);
    }

    @Override
    protected ArchiveOutputStream getArchiveOutputStream() {
        TarArchiveOutputStream taos = new TarArchiveOutputStream(outputStream);
        //taos.
        return taos;
    }

    @Override
    protected void zipFile(ArchiveOutputStream zaos, String base, File file) throws IOException {
        String entryName = getEntryName(base, file);
        zaos.putArchiveEntry(new TarArchiveEntry(file, entryName));
        // zaos.putArchiveEntry(new TarArchiveEntry(file));
        IOUtils.copy(new FileInputStream(file), zaos);
        zaos.closeArchiveEntry();
    }

}
