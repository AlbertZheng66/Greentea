package com.xt.comm.download;

import com.xt.core.proc.impl.fs.FileService;
import com.xt.gt.sys.SystemConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * 从文件路径直接加载待下载的文件。
 * @author Albert
 */
public class SimpleLoader implements Loader {

    /**
     * 文件下载的基础路径。
     */
    private String basePathName = SystemConfiguration.getInstance().readString("simpleLoader.basePath", "downloads");
    
    /**
     * 文件服务类
     */
    private FileService fileService;

    public SimpleLoader() {
    }

    /**
     * 直接从指定文件夹下读取所有文件名称。
     * @param dirName 路径名称
     * @param onlyFiles 是否包含子路径名称
     * @return 返回指定文件夹下读取所有文件信息，如果文件夹，则抛出空指针异常。
     */
    public List<FileInfo> getFiles(String dirName, boolean onlyFiles) {
        System.out.println("basePathName=" + basePathName);
        String dirPath = createFileName(basePathName, dirName);
        if (!fileService.exists(dirPath)) {
            throw new DownloadServiceException(String.format("路径[%s]不存在。", dirPath));
        }
        String[] fileNames = fileService.list(dirPath);
        List<FileInfo> fileInfos = new ArrayList(fileNames.length);
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            // 不显示子路径的信息
            String fullName = createFileName(dirPath, fileName);
            if (onlyFiles && fileService.isDirectory(fullName)) {
                continue;
            }
            FileInfo fileInfo = createFileInfo(fullName);
            fileInfos.add(fileInfo);
        }

        return fileInfos;
    }

    private FileInfo createFileInfo(String fileName) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setTitle(fileService.getName(fileName));
        long lastMod = fileService.getLastModified(fileName);
        if (lastMod > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(lastMod));
            fileInfo.setLastModifiedTime(cal);
        }
        // 推断出文件类型
        final String[] segs = fileName.split("[.]");
        if (segs != null && segs.length > 1) {
            String ext = segs[segs.length - 1];
            System.out.println("---------fileInfo.ext=" + ext);
            if (TYPE_MAPPING.containsKey(ext)) {
                fileInfo.setType(TYPE_MAPPING.get(ext));
            }
        }
        System.out.println("---------fileInfo.type=" + fileInfo.getType());
        fileInfo.setSize(fileService.length(fileName));
        fileInfo.setDirectory(fileService.isDirectory(fileName));
        return fileInfo;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 加载指定路径下的所有文件
     */
    public FileInfo getFileInfo(String fileName) {
        System.out.println("basePathName=" + basePathName);
        String fullName = createFileName(basePathName, fileName); 
        if (!fileService.exists(fullName)) {
            throw new DownloadServiceException(String.format("文件[%s]不存在。", fullName));
        }
        FileInfo fileInfo = createFileInfo(fullName);
        return fileInfo;
    }
    
    private String createFileName(String path, String fileName) {
        if (StringUtils.isEmpty(path)) {
            return fileName;
        } 
        if (StringUtils.isEmpty(fileName)) {
            throw new DownloadServiceException("文件名不能为空。");
        } 
        StringBuilder fullFileName = new StringBuilder(path);
        String sep = File.separator;
        if (path.endsWith(sep) && fileName.startsWith(sep)) {
            fullFileName.deleteCharAt(fullFileName.length() - 1);  // 删除重复的路径分隔符
        } else if (!(path.endsWith(sep) 
                || fileName.startsWith(sep))) {
            // 都没有文件分隔符的情况下，补充上文件分隔符
            fullFileName.append(sep);
        }
        fullFileName.append(fileName);
        return fullFileName.toString();
    }

    public void setBasePathName(String basePathName) {
        this.basePathName = basePathName;
    }

    public String getBasePathName() {
        return basePathName;
    }
    
    

    static {
        /**
         * 这个表来自于网路信息，按照字母序进行排序，有多个前缀是重叠的，取最后一个生效字母
         */
        TYPE_MAPPING.put("flv", "video/x-flv");
        TYPE_MAPPING.put("mkv", "video/x-matroska");
        TYPE_MAPPING.put("3dm", "x-world/x-3dmf");
        TYPE_MAPPING.put("3dmf", "x-world/x-3dmf");
        TYPE_MAPPING.put("a", "application/octet-stream");
        TYPE_MAPPING.put("aab", "application/x-authorware-bin");
        TYPE_MAPPING.put("aam", "application/x-authorware-map");
        TYPE_MAPPING.put("aas", "application/x-authorware-seg");
        TYPE_MAPPING.put("abc", "text/vnd.abc");
        TYPE_MAPPING.put("acgi", "text/html");
        TYPE_MAPPING.put("afl", "video/animaflex");
        TYPE_MAPPING.put("ai", "application/postscript");
        TYPE_MAPPING.put("aif", "audio/aiff");
        TYPE_MAPPING.put("aif", "audio/x-aiff");
        TYPE_MAPPING.put("aifc", "audio/aiff");
        TYPE_MAPPING.put("aifc", "audio/x-aiff");
        TYPE_MAPPING.put("aiff", "audio/aiff");
        TYPE_MAPPING.put("aiff", "audio/x-aiff");
        TYPE_MAPPING.put("aim", "application/x-aim");
        TYPE_MAPPING.put("aip", "text/x-audiosoft-intra");
        TYPE_MAPPING.put("ani", "application/x-navi-animation");
        TYPE_MAPPING.put("aos", "application/x-nokia-9000-communicator-add-on-software");
        TYPE_MAPPING.put("aps", "application/mime");
        TYPE_MAPPING.put("arc", "application/octet-stream");
        TYPE_MAPPING.put("arj", "application/arj");
        TYPE_MAPPING.put("arj", "application/octet-stream");
        TYPE_MAPPING.put("art", "image/x-jg");
        TYPE_MAPPING.put("asf", "video/x-ms-asf");
        TYPE_MAPPING.put("asm", "text/x-asm");
        TYPE_MAPPING.put("asp", "text/asp");
        TYPE_MAPPING.put("asx", "application/x-mplayer2");
        TYPE_MAPPING.put("asx", "video/x-ms-asf");
        TYPE_MAPPING.put("asx", "video/x-ms-asf-plugin");
        TYPE_MAPPING.put("au", "audio/basic");
        TYPE_MAPPING.put("au", "audio/x-au");
        TYPE_MAPPING.put("avi", "application/x-troff-msvideo");
        TYPE_MAPPING.put("avi", "video/avi");
        TYPE_MAPPING.put("avi", "video/msvideo");
        TYPE_MAPPING.put("avi", "video/x-msvideo");
        TYPE_MAPPING.put("avs", "video/avs-video");
        TYPE_MAPPING.put("bcpio", "application/x-bcpio");
        TYPE_MAPPING.put("bin", "application/mac-binary");
        TYPE_MAPPING.put("bin", "application/macbinary");
        TYPE_MAPPING.put("bin", "application/octet-stream");
        TYPE_MAPPING.put("bin", "application/x-binary");
        TYPE_MAPPING.put("bin", "application/x-macbinary");
        TYPE_MAPPING.put("bm", "image/bmp");
        TYPE_MAPPING.put("bmp", "image/bmp");
        TYPE_MAPPING.put("bmp", "image/x-windows-bmp");
        TYPE_MAPPING.put("boo", "application/book");
        TYPE_MAPPING.put("book", "application/book");
        TYPE_MAPPING.put("boz", "application/x-bzip2");
        TYPE_MAPPING.put("bsh", "application/x-bsh");
        TYPE_MAPPING.put("bz", "application/x-bzip");
        TYPE_MAPPING.put("bz2", "application/x-bzip2");
        TYPE_MAPPING.put("c", "text/plain");
        TYPE_MAPPING.put("c", "text/x-c");
        TYPE_MAPPING.put("c++", "text/plain");
        TYPE_MAPPING.put("cat", "application/vnd.ms-pki.seccat");
        TYPE_MAPPING.put("cc", "text/plain");
        TYPE_MAPPING.put("cc", "text/x-c");
        TYPE_MAPPING.put("ccad", "application/clariscad");
        TYPE_MAPPING.put("cco", "application/x-cocoa");
        TYPE_MAPPING.put("cdf", "application/cdf");
        TYPE_MAPPING.put("cdf", "application/x-cdf");
        TYPE_MAPPING.put("cdf", "application/x-netcdf");
        TYPE_MAPPING.put("cer", "application/pkix-cert");
        TYPE_MAPPING.put("cer", "application/x-x509-ca-cert");
        TYPE_MAPPING.put("cha", "application/x-chat");
        TYPE_MAPPING.put("chat", "application/x-chat");
        TYPE_MAPPING.put("class", "application/java");
        TYPE_MAPPING.put("class", "application/java-byte-code");
        TYPE_MAPPING.put("class", "application/x-java-class");
        TYPE_MAPPING.put("com", "application/octet-stream");
        TYPE_MAPPING.put("com", "text/plain");
        TYPE_MAPPING.put("conf", "text/plain");
        TYPE_MAPPING.put("cpio", "application/x-cpio");
        TYPE_MAPPING.put("cpp", "text/x-c");
        TYPE_MAPPING.put("cpt", "application/mac-compactpro");
        TYPE_MAPPING.put("cpt", "application/x-compactpro");
        TYPE_MAPPING.put("cpt", "application/x-cpt");
        TYPE_MAPPING.put("crl", "application/pkcs-crl");
        TYPE_MAPPING.put("crl", "application/pkix-crl");
        TYPE_MAPPING.put("crt", "application/pkix-cert");
        TYPE_MAPPING.put("crt", "application/x-x509-ca-cert");
        TYPE_MAPPING.put("crt", "application/x-x509-user-cert");
        TYPE_MAPPING.put("csh", "application/x-csh");
        TYPE_MAPPING.put("csh", "text/x-script.csh");
        TYPE_MAPPING.put("css", "application/x-pointplus");
        TYPE_MAPPING.put("css", "text/css");
        TYPE_MAPPING.put("cxx", "text/plain");
        TYPE_MAPPING.put("dcr", "application/x-director");
        TYPE_MAPPING.put("deepv", "application/x-deepv");
        TYPE_MAPPING.put("def", "text/plain");
        TYPE_MAPPING.put("der", "application/x-x509-ca-cert");
        TYPE_MAPPING.put("dif", "video/x-dv");
        TYPE_MAPPING.put("dir", "application/x-director");
        TYPE_MAPPING.put("dl", "video/dl");
        TYPE_MAPPING.put("dl", "video/x-dl");
        TYPE_MAPPING.put("doc", "application/msword");
        TYPE_MAPPING.put("dot", "application/msword");
        TYPE_MAPPING.put("dp", "application/commonground");
        TYPE_MAPPING.put("drw", "application/drafting");
        TYPE_MAPPING.put("dump", "application/octet-stream");
        TYPE_MAPPING.put("dv", "video/x-dv");
        TYPE_MAPPING.put("dvi", "application/x-dvi");
        TYPE_MAPPING.put("dwf", "drawing/x-dwf (old)");
        TYPE_MAPPING.put("dwf", "model/vnd.dwf");
        TYPE_MAPPING.put("dwg", "application/acad");
        TYPE_MAPPING.put("dwg", "image/vnd.dwg");
        TYPE_MAPPING.put("dwg", "image/x-dwg");
        TYPE_MAPPING.put("dxf", "application/dxf");
        TYPE_MAPPING.put("dxf", "image/vnd.dwg");
        TYPE_MAPPING.put("dxf", "image/x-dwg");
        TYPE_MAPPING.put("dxr", "application/x-director");
        TYPE_MAPPING.put("el", "text/x-script.elisp");
        TYPE_MAPPING.put("elc", "application/x-bytecode.elisp (compiled elisp)");
        TYPE_MAPPING.put("elc", "application/x-elc");
        TYPE_MAPPING.put("env", "application/x-envoy");
        TYPE_MAPPING.put("eps", "application/postscript");
        TYPE_MAPPING.put("es", "application/x-esrehber");
        TYPE_MAPPING.put("etx", "text/x-setext");
        TYPE_MAPPING.put("evy", "application/envoy");
        TYPE_MAPPING.put("evy", "application/x-envoy");
        TYPE_MAPPING.put("exe", "application/octet-stream");
        TYPE_MAPPING.put("f", "text/plain");
        TYPE_MAPPING.put("f", "text/x-fortran");
        TYPE_MAPPING.put("f77", "text/x-fortran");
        TYPE_MAPPING.put("f90", "text/plain");
        TYPE_MAPPING.put("f90", "text/x-fortran");
        TYPE_MAPPING.put("fdf", "application/vnd.fdf");
        TYPE_MAPPING.put("fif", "application/fractals");
        TYPE_MAPPING.put("fif", "image/fif");
        TYPE_MAPPING.put("fli", "video/fli");
        TYPE_MAPPING.put("fli", "video/x-fli");
        TYPE_MAPPING.put("flo", "image/florian");
        TYPE_MAPPING.put("flx", "text/vnd.fmi.flexstor");
        TYPE_MAPPING.put("fmf", "video/x-atomic3d-feature");
        TYPE_MAPPING.put("for", "text/plain");
        TYPE_MAPPING.put("for", "text/x-fortran");
        TYPE_MAPPING.put("fpx", "image/vnd.fpx");
        TYPE_MAPPING.put("fpx", "image/vnd.net-fpx");
        TYPE_MAPPING.put("frl", "application/freeloader");
        TYPE_MAPPING.put("funk", "audio/make");
        TYPE_MAPPING.put("g", "text/plain");
        TYPE_MAPPING.put("g3", "image/g3fax");
        TYPE_MAPPING.put("gif", "image/gif");
        TYPE_MAPPING.put("gl", "video/gl");
        TYPE_MAPPING.put("gl", "video/x-gl");
        TYPE_MAPPING.put("gsd", "audio/x-gsm");
        TYPE_MAPPING.put("gsm", "audio/x-gsm");
        TYPE_MAPPING.put("gsp", "application/x-gsp");
        TYPE_MAPPING.put("gss", "application/x-gss");
        TYPE_MAPPING.put("gtar", "application/x-gtar");
        TYPE_MAPPING.put("gz", "application/x-compressed");
        TYPE_MAPPING.put("gz", "application/x-gzip");
        TYPE_MAPPING.put("gzip", "application/x-gzip");
        TYPE_MAPPING.put("gzip", "multipart/x-gzip");
        TYPE_MAPPING.put("h", "text/plain");
        TYPE_MAPPING.put("h", "text/x-h");
        TYPE_MAPPING.put("hdf", "application/x-hdf");
        TYPE_MAPPING.put("help", "application/x-helpfile");
        TYPE_MAPPING.put("hgl", "application/vnd.hp-hpgl");
        TYPE_MAPPING.put("hh", "text/plain");
        TYPE_MAPPING.put("hh", "text/x-h");
        TYPE_MAPPING.put("hlb", "text/x-script");
        TYPE_MAPPING.put("hlp", "application/hlp");
        TYPE_MAPPING.put("hlp", "application/x-helpfile");
        TYPE_MAPPING.put("hlp", "application/x-winhelp");
        TYPE_MAPPING.put("hpg", "application/vnd.hp-hpgl");
        TYPE_MAPPING.put("hpgl", "application/vnd.hp-hpgl");
        TYPE_MAPPING.put("hqx", "application/binhex");
        TYPE_MAPPING.put("hqx", "application/binhex4");
        TYPE_MAPPING.put("hqx", "application/mac-binhex");
        TYPE_MAPPING.put("hqx", "application/mac-binhex40");
        TYPE_MAPPING.put("hqx", "application/x-binhex40");
        TYPE_MAPPING.put("hqx", "application/x-mac-binhex40");
        TYPE_MAPPING.put("hta", "application/hta");
        TYPE_MAPPING.put("htc", "text/x-component");
        TYPE_MAPPING.put("htm", "text/html");
        TYPE_MAPPING.put("html", "text/html");
        TYPE_MAPPING.put("htmls", "text/html");
        TYPE_MAPPING.put("htt", "text/webviewhtml");
        TYPE_MAPPING.put("htx", "text/html");
        TYPE_MAPPING.put("ice", "x-conference/x-cooltalk");
        TYPE_MAPPING.put("ico", "image/x-icon");
        TYPE_MAPPING.put("idc", "text/plain");
        TYPE_MAPPING.put("ief", "image/ief");
        TYPE_MAPPING.put("iefs", "image/ief");
        TYPE_MAPPING.put("iges", "application/iges");
        TYPE_MAPPING.put("iges", "model/iges");
        TYPE_MAPPING.put("igs", "application/iges");
        TYPE_MAPPING.put("igs", "model/iges");
        TYPE_MAPPING.put("ima", "application/x-ima");
        TYPE_MAPPING.put("imap", "application/x-httpd-imap");
        TYPE_MAPPING.put("inf", "application/inf");
        TYPE_MAPPING.put("ins", "application/x-internett-signup");
        TYPE_MAPPING.put("ip", "application/x-ip2");
        TYPE_MAPPING.put("isu", "video/x-isvideo");
        TYPE_MAPPING.put("it", "audio/it");
        TYPE_MAPPING.put("iv", "application/x-inventor");
        TYPE_MAPPING.put("ivr", "i-world/i-vrml");
        TYPE_MAPPING.put("ivy", "application/x-livescreen");
        TYPE_MAPPING.put("jam", "audio/x-jam");
        TYPE_MAPPING.put("jav", "text/plain");
        TYPE_MAPPING.put("jav", "text/x-java-source");
        TYPE_MAPPING.put("java", "text/plain");
        TYPE_MAPPING.put("java", "text/x-java-source");
        TYPE_MAPPING.put("jcm", "application/x-java-commerce");
        TYPE_MAPPING.put("jfif", "image/jpeg");
        TYPE_MAPPING.put("jfif", "image/pjpeg");
        TYPE_MAPPING.put("jfif-tbnl", "image/jpeg");
        TYPE_MAPPING.put("jpe", "image/jpeg");
        TYPE_MAPPING.put("jpe", "image/pjpeg");
        TYPE_MAPPING.put("jpeg", "image/jpeg");
// TYPE_MAPPING.put("jpeg", "image/pjpeg");
        TYPE_MAPPING.put("jpg", "image/jpeg");
        TYPE_MAPPING.put("jpg", "image/pjpeg");
        TYPE_MAPPING.put("jps", "image/x-jps");
        TYPE_MAPPING.put("js", "application/x-javascript");
        TYPE_MAPPING.put("js", "application/javascript");
// TYPE_MAPPING.put("js", "application/ecmascript");
// TYPE_MAPPING.put("js", "text/javascript");
// TYPE_MAPPING.put("js", "text/ecmascript");
        TYPE_MAPPING.put("jut", "image/jutvision");
        TYPE_MAPPING.put("kar", "audio/midi");
        TYPE_MAPPING.put("kar", "music/x-karaoke");
        TYPE_MAPPING.put("ksh", "application/x-ksh");
        TYPE_MAPPING.put("ksh", "text/x-script.ksh");
        TYPE_MAPPING.put("la", "audio/nspaudio");
        TYPE_MAPPING.put("la", "audio/x-nspaudio");
        TYPE_MAPPING.put("lam", "audio/x-liveaudio");
        TYPE_MAPPING.put("latex", "application/x-latex");
        TYPE_MAPPING.put("lha", "application/lha");
        TYPE_MAPPING.put("lha", "application/octet-stream");
        TYPE_MAPPING.put("lha", "application/x-lha");
        TYPE_MAPPING.put("lhx", "application/octet-stream");
        TYPE_MAPPING.put("list", "text/plain");
        TYPE_MAPPING.put("lma", "audio/nspaudio");
        TYPE_MAPPING.put("lma", "audio/x-nspaudio");
        TYPE_MAPPING.put("log", "text/plain");
        TYPE_MAPPING.put("lsp", "application/x-lisp");
        TYPE_MAPPING.put("lsp", "text/x-script.lisp");
        TYPE_MAPPING.put("lst", "text/plain");
        TYPE_MAPPING.put("lsx", "text/x-la-asf");
        TYPE_MAPPING.put("ltx", "application/x-latex");
        TYPE_MAPPING.put("lzh", "application/octet-stream");
        TYPE_MAPPING.put("lzh", "application/x-lzh");
        TYPE_MAPPING.put("lzx", "application/lzx");
        TYPE_MAPPING.put("lzx", "application/octet-stream");
        TYPE_MAPPING.put("lzx", "application/x-lzx");
        TYPE_MAPPING.put("m", "text/plain");
        TYPE_MAPPING.put("m", "text/x-m");
        TYPE_MAPPING.put("m1v", "video/mpeg");
        TYPE_MAPPING.put("m2a", "audio/mpeg");
        TYPE_MAPPING.put("m2v", "video/mpeg");
        TYPE_MAPPING.put("m3u", "audio/x-mpequrl");
        TYPE_MAPPING.put("man", "application/x-troff-man");
        TYPE_MAPPING.put("map", "application/x-navimap");
        TYPE_MAPPING.put("mar", "text/plain");
        TYPE_MAPPING.put("mbd", "application/mbedlet");
        TYPE_MAPPING.put("mc$", "application/x-magic-cap-package-1.0");
        TYPE_MAPPING.put("mcd", "application/mcad");
        TYPE_MAPPING.put("mcd", "application/x-mathcad");
        TYPE_MAPPING.put("mcf", "image/vasa");
        TYPE_MAPPING.put("mcf", "text/mcf");
        TYPE_MAPPING.put("mcp", "application/netmc");
        TYPE_MAPPING.put("me", "application/x-troff-me");
        TYPE_MAPPING.put("mht", "message/rfc822");
        TYPE_MAPPING.put("mhtml", "message/rfc822");
        TYPE_MAPPING.put("mid", "application/x-midi");
        TYPE_MAPPING.put("mid", "audio/midi");
        TYPE_MAPPING.put("mid", "audio/x-mid");
        TYPE_MAPPING.put("mid", "audio/x-midi");
        TYPE_MAPPING.put("mid", "music/crescendo");
        TYPE_MAPPING.put("mid", "x-music/x-midi");
        TYPE_MAPPING.put("midi", "application/x-midi");
        TYPE_MAPPING.put("midi", "audio/midi");
        TYPE_MAPPING.put("midi", "audio/x-mid");
        TYPE_MAPPING.put("midi", "audio/x-midi");
        TYPE_MAPPING.put("midi", "music/crescendo");
        TYPE_MAPPING.put("midi", "x-music/x-midi");
        TYPE_MAPPING.put("mif", "application/x-frame");
        TYPE_MAPPING.put("mif", "application/x-mif");
        TYPE_MAPPING.put("mime", "message/rfc822");
        TYPE_MAPPING.put("mime", "www/mime");
        TYPE_MAPPING.put("mjf", "audio/x-vnd.audioexplosion.mjuicemediafile");
        TYPE_MAPPING.put("mjpg", "video/x-motion-jpeg");
        TYPE_MAPPING.put("mm", "application/base64");
        TYPE_MAPPING.put("mm", "application/x-meme");
        TYPE_MAPPING.put("mme", "application/base64");
        TYPE_MAPPING.put("mod", "audio/mod");
        TYPE_MAPPING.put("mod", "audio/x-mod");
        TYPE_MAPPING.put("moov", "video/quicktime");
        TYPE_MAPPING.put("mov", "video/quicktime");
        TYPE_MAPPING.put("movie", "video/x-sgi-movie");
        TYPE_MAPPING.put("mp2", "audio/mpeg");
        TYPE_MAPPING.put("mp2", "audio/x-mpeg");
        TYPE_MAPPING.put("mp2", "video/mpeg");
        TYPE_MAPPING.put("mp2", "video/x-mpeg");
        TYPE_MAPPING.put("mp2", "video/x-mpeq2a");
        TYPE_MAPPING.put("mp3", "audio/mpeg3");
        TYPE_MAPPING.put("mp3", "audio/x-mpeg-3");
        TYPE_MAPPING.put("mp3", "video/mpeg");
        TYPE_MAPPING.put("mp3", "video/x-mpeg");
        TYPE_MAPPING.put("mpa", "audio/mpeg");
        TYPE_MAPPING.put("mpa", "video/mpeg");
        TYPE_MAPPING.put("mpc", "application/x-project");
        TYPE_MAPPING.put("mpe", "video/mpeg");
        TYPE_MAPPING.put("mpeg", "video/mpeg");
        TYPE_MAPPING.put("mpg", "audio/mpeg");
        TYPE_MAPPING.put("mpg", "video/mpeg");
        TYPE_MAPPING.put("mpga", "audio/mpeg");
        TYPE_MAPPING.put("mpp", "application/vnd.ms-project");
        TYPE_MAPPING.put("mpt", "application/x-project");
        TYPE_MAPPING.put("mpv", "application/x-project");
        TYPE_MAPPING.put("mpx", "application/x-project");
        TYPE_MAPPING.put("mrc", "application/marc");
        TYPE_MAPPING.put("ms", "application/x-troff-ms");
        TYPE_MAPPING.put("mv", "video/x-sgi-movie");
        TYPE_MAPPING.put("my", "audio/make");
        TYPE_MAPPING.put("mzz", "application/x-vnd.audioexplosion.mzz");
        TYPE_MAPPING.put("nap", "image/naplps");
        TYPE_MAPPING.put("naplps", "image/naplps");
        TYPE_MAPPING.put("nc", "application/x-netcdf");
        TYPE_MAPPING.put("ncm", "application/vnd.nokia.configuration-message");
        TYPE_MAPPING.put("nif", "image/x-niff");
        TYPE_MAPPING.put("niff", "image/x-niff");
        TYPE_MAPPING.put("nix", "application/x-mix-transfer");
        TYPE_MAPPING.put("nsc", "application/x-conference");
        TYPE_MAPPING.put("nvd", "application/x-navidoc");
        TYPE_MAPPING.put("o", "application/octet-stream");
        TYPE_MAPPING.put("oda", "application/oda");
        TYPE_MAPPING.put("omc", "application/x-omc");
        TYPE_MAPPING.put("omcd", "application/x-omcdatamaker");
        TYPE_MAPPING.put("omcr", "application/x-omcregerator");
        TYPE_MAPPING.put("p", "text/x-pascal");
        TYPE_MAPPING.put("p10", "application/pkcs10");
        TYPE_MAPPING.put("p10", "application/x-pkcs10");
        TYPE_MAPPING.put("p12", "application/pkcs-12");
        TYPE_MAPPING.put("p12", "application/x-pkcs12");
        TYPE_MAPPING.put("p7a", "application/x-pkcs7-signature");
        TYPE_MAPPING.put("p7c", "application/pkcs7-mime");
        TYPE_MAPPING.put("p7c", "application/x-pkcs7-mime");
        TYPE_MAPPING.put("p7m", "application/pkcs7-mime");
        TYPE_MAPPING.put("p7m", "application/x-pkcs7-mime");
        TYPE_MAPPING.put("p7r", "application/x-pkcs7-certreqresp");
        TYPE_MAPPING.put("p7s", "application/pkcs7-signature");
        TYPE_MAPPING.put("part", "application/pro_eng");
        TYPE_MAPPING.put("pas", "text/pascal");
        TYPE_MAPPING.put("pbm", "image/x-portable-bitmap");
        TYPE_MAPPING.put("pcl", "application/vnd.hp-pcl");
        TYPE_MAPPING.put("pcl", "application/x-pcl");
        TYPE_MAPPING.put("pct", "image/x-pict");
        TYPE_MAPPING.put("pcx", "image/x-pcx");
        TYPE_MAPPING.put("pdb", "chemical/x-pdb");
        TYPE_MAPPING.put("pdf", "application/pdf");
        TYPE_MAPPING.put("pfunk", "audio/make");
        TYPE_MAPPING.put("pfunk", "audio/make.my.funk");
        TYPE_MAPPING.put("pgm", "image/x-portable-graymap");
        TYPE_MAPPING.put("pgm", "image/x-portable-greymap");
        TYPE_MAPPING.put("pic", "image/pict");
        TYPE_MAPPING.put("pict", "image/pict");
        TYPE_MAPPING.put("pkg", "application/x-newton-compatible-pkg");
        TYPE_MAPPING.put("pko", "application/vnd.ms-pki.pko");
        TYPE_MAPPING.put("pl", "text/plain");
        TYPE_MAPPING.put("pl", "text/x-script.perl");
        TYPE_MAPPING.put("plx", "application/x-pixclscript");
        TYPE_MAPPING.put("pm", "image/x-xpixmap");
        TYPE_MAPPING.put("pm", "text/x-script.perl-module");
        TYPE_MAPPING.put("pm4", "application/x-pagemaker");
        TYPE_MAPPING.put("pm5", "application/x-pagemaker");
        TYPE_MAPPING.put("png", "image/png");
        TYPE_MAPPING.put("pnm", "application/x-portable-anymap");
        TYPE_MAPPING.put("pnm", "image/x-portable-anymap");
        TYPE_MAPPING.put("pot", "application/mspowerpoint");
        TYPE_MAPPING.put("pot", "application/vnd.ms-powerpoint");
        TYPE_MAPPING.put("pov", "model/x-pov");
        TYPE_MAPPING.put("ppa", "application/vnd.ms-powerpoint");
        TYPE_MAPPING.put("ppm", "image/x-portable-pixmap");
        TYPE_MAPPING.put("pps", "application/mspowerpoint");
        TYPE_MAPPING.put("pps", "application/vnd.ms-powerpoint");
        TYPE_MAPPING.put("ppt", "application/mspowerpoint");
        TYPE_MAPPING.put("ppt", "application/powerpoint");
        TYPE_MAPPING.put("ppt", "application/vnd.ms-powerpoint");
        TYPE_MAPPING.put("ppt", "application/x-mspowerpoint");
        TYPE_MAPPING.put("ppz", "application/mspowerpoint");
        TYPE_MAPPING.put("pre", "application/x-freelance");
        TYPE_MAPPING.put("prt", "application/pro_eng");
        TYPE_MAPPING.put("ps", "application/postscript");
        TYPE_MAPPING.put("psd", "application/octet-stream");
        TYPE_MAPPING.put("pvu", "paleovu/x-pv");
        TYPE_MAPPING.put("pwz", "application/vnd.ms-powerpoint");
        TYPE_MAPPING.put("py", "text/x-script.phyton");
        TYPE_MAPPING.put("pyc", "applicaiton/x-bytecode.python");
        TYPE_MAPPING.put("qcp", "audio/vnd.qcelp");
        TYPE_MAPPING.put("qd3", "x-world/x-3dmf");
        TYPE_MAPPING.put("qd3d", "x-world/x-3dmf");
        TYPE_MAPPING.put("qif", "image/x-quicktime");
        TYPE_MAPPING.put("qt", "video/quicktime");
        TYPE_MAPPING.put("qtc", "video/x-qtc");
        TYPE_MAPPING.put("qti", "image/x-quicktime");
        TYPE_MAPPING.put("qtif", "image/x-quicktime");
        TYPE_MAPPING.put("ra", "audio/x-pn-realaudio");
        TYPE_MAPPING.put("ra", "audio/x-pn-realaudio-plugin");
        TYPE_MAPPING.put("ra", "audio/x-realaudio");
        TYPE_MAPPING.put("ram", "audio/x-pn-realaudio");
        TYPE_MAPPING.put("ras", "application/x-cmu-raster");
        TYPE_MAPPING.put("ras", "image/cmu-raster");
        TYPE_MAPPING.put("ras", "image/x-cmu-raster");
        TYPE_MAPPING.put("rast", "image/cmu-raster");
        TYPE_MAPPING.put("rexx", "text/x-script.rexx");
        TYPE_MAPPING.put("rf", "image/vnd.rn-realflash");
        TYPE_MAPPING.put("rgb", "image/x-rgb");
        TYPE_MAPPING.put("rm", "application/vnd.rn-realmedia");
        TYPE_MAPPING.put("rm", "audio/x-pn-realaudio");
        TYPE_MAPPING.put("rmi", "audio/mid");
        TYPE_MAPPING.put("rmm", "audio/x-pn-realaudio");
        TYPE_MAPPING.put("rmp", "audio/x-pn-realaudio");
        TYPE_MAPPING.put("rmp", "audio/x-pn-realaudio-plugin");
        TYPE_MAPPING.put("rng", "application/ringing-tones");
        TYPE_MAPPING.put("rng", "application/vnd.nokia.ringing-tone");
        TYPE_MAPPING.put("rnx", "application/vnd.rn-realplayer");
        TYPE_MAPPING.put("roff", "application/x-troff");
        TYPE_MAPPING.put("rp", "image/vnd.rn-realpix");
        TYPE_MAPPING.put("rpm", "audio/x-pn-realaudio-plugin");
        TYPE_MAPPING.put("rt", "text/richtext");
        TYPE_MAPPING.put("rt", "text/vnd.rn-realtext");
        TYPE_MAPPING.put("rtf", "application/rtf");
        TYPE_MAPPING.put("rtf", "application/x-rtf");
        TYPE_MAPPING.put("rtf", "text/richtext");
        TYPE_MAPPING.put("rtx", "application/rtf");
        TYPE_MAPPING.put("rtx", "text/richtext");
        TYPE_MAPPING.put("rv", "video/vnd.rn-realvideo");
        TYPE_MAPPING.put("s", "text/x-asm");
        TYPE_MAPPING.put("s3m", "audio/s3m");
        TYPE_MAPPING.put("saveme", "application/octet-stream");
        TYPE_MAPPING.put("sbk", "application/x-tbook");
        TYPE_MAPPING.put("scm", "application/x-lotusscreencam");
        TYPE_MAPPING.put("scm", "text/x-script.guile");
        TYPE_MAPPING.put("scm", "text/x-script.scheme");
        TYPE_MAPPING.put("scm", "video/x-scm");
        TYPE_MAPPING.put("sdml", "text/plain");
        TYPE_MAPPING.put("sdp", "application/sdp");
        TYPE_MAPPING.put("sdp", "application/x-sdp");
        TYPE_MAPPING.put("sdr", "application/sounder");
        TYPE_MAPPING.put("sea", "application/sea");
        TYPE_MAPPING.put("sea", "application/x-sea");
        TYPE_MAPPING.put("set", "application/set");
        TYPE_MAPPING.put("sgm", "text/sgml");
        TYPE_MAPPING.put("sgm", "text/x-sgml");
        TYPE_MAPPING.put("sgml", "text/sgml");
        TYPE_MAPPING.put("sgml", "text/x-sgml");
        TYPE_MAPPING.put("sh", "application/x-bsh");
        TYPE_MAPPING.put("sh", "application/x-sh");
        TYPE_MAPPING.put("sh", "application/x-shar");
        TYPE_MAPPING.put("sh", "text/x-script.sh");
        TYPE_MAPPING.put("shar", "application/x-bsh");
        TYPE_MAPPING.put("shar", "application/x-shar");
        TYPE_MAPPING.put("shtml", "text/html");
        TYPE_MAPPING.put("shtml", "text/x-server-parsed-html");
        TYPE_MAPPING.put("sid", "audio/x-psid");
        TYPE_MAPPING.put("sit", "application/x-sit");
        TYPE_MAPPING.put("sit", "application/x-stuffit");
        TYPE_MAPPING.put("skd", "application/x-koan");
        TYPE_MAPPING.put("skm", "application/x-koan");
        TYPE_MAPPING.put("skp", "application/x-koan");
        TYPE_MAPPING.put("skt", "application/x-koan");
        TYPE_MAPPING.put("sl", "application/x-seelogo");
        TYPE_MAPPING.put("smi", "application/smil");
        TYPE_MAPPING.put("smil", "application/smil");
        TYPE_MAPPING.put("snd", "audio/basic");
        TYPE_MAPPING.put("snd", "audio/x-adpcm");
        TYPE_MAPPING.put("sol", "application/solids");
        TYPE_MAPPING.put("spc", "application/x-pkcs7-certificates");
        TYPE_MAPPING.put("spc", "text/x-speech");
        TYPE_MAPPING.put("spl", "application/futuresplash");
        TYPE_MAPPING.put("spr", "application/x-sprite");
        TYPE_MAPPING.put("sprite", "application/x-sprite");
        TYPE_MAPPING.put("src", "application/x-wais-source");
        TYPE_MAPPING.put("ssi", "text/x-server-parsed-html");
        TYPE_MAPPING.put("ssm", "application/streamingmedia");
        TYPE_MAPPING.put("sst", "application/vnd.ms-pki.certstore");
        TYPE_MAPPING.put("step", "application/step");
        TYPE_MAPPING.put("stl", "application/sla");
        TYPE_MAPPING.put("stl", "application/vnd.ms-pki.stl");
        TYPE_MAPPING.put("stl", "application/x-navistyle");
        TYPE_MAPPING.put("stp", "application/step");
        TYPE_MAPPING.put("sv4cpio", "application/x-sv4cpio");
        TYPE_MAPPING.put("sv4crc", "application/x-sv4crc");
        TYPE_MAPPING.put("svf", "image/vnd.dwg");
        TYPE_MAPPING.put("svf", "image/x-dwg");
        TYPE_MAPPING.put("svr", "application/x-world");
        TYPE_MAPPING.put("svr", "x-world/x-svr");
        TYPE_MAPPING.put("swf", "application/x-shockwave-flash");
        TYPE_MAPPING.put("t", "application/x-troff");
        TYPE_MAPPING.put("talk", "text/x-speech");
        TYPE_MAPPING.put("tar", "application/x-tar");
        TYPE_MAPPING.put("tbk", "application/toolbook");
        TYPE_MAPPING.put("tbk", "application/x-tbook");
        TYPE_MAPPING.put("tcl", "application/x-tcl");
        TYPE_MAPPING.put("tcl", "text/x-script.tcl");
        TYPE_MAPPING.put("tcsh", "text/x-script.tcsh");
        TYPE_MAPPING.put("tex", "application/x-tex");
        TYPE_MAPPING.put("texi", "application/x-texinfo");
        TYPE_MAPPING.put("texinfo", "application/x-texinfo");
        TYPE_MAPPING.put("text", "application/plain");
        TYPE_MAPPING.put("text", "text/plain");
        TYPE_MAPPING.put("tgz", "application/gnutar");
        TYPE_MAPPING.put("tgz", "application/x-compressed");
        TYPE_MAPPING.put("tif", "image/tiff");
        TYPE_MAPPING.put("tif", "image/x-tiff");
        TYPE_MAPPING.put("tiff", "image/tiff");
        TYPE_MAPPING.put("tiff", "image/x-tiff");
        TYPE_MAPPING.put("tr", "application/x-troff");
        TYPE_MAPPING.put("tsi", "audio/tsp-audio");
        TYPE_MAPPING.put("tsp", "application/dsptype");
        TYPE_MAPPING.put("tsp", "audio/tsplayer");
        TYPE_MAPPING.put("tsv", "text/tab-separated-values");
        TYPE_MAPPING.put("turbot", "image/florian");
        TYPE_MAPPING.put("txt", "text/plain");
        TYPE_MAPPING.put("uil", "text/x-uil");
        TYPE_MAPPING.put("uni", "text/uri-list");
        TYPE_MAPPING.put("unis", "text/uri-list");
        TYPE_MAPPING.put("unv", "application/i-deas");
        TYPE_MAPPING.put("uri", "text/uri-list");
        TYPE_MAPPING.put("uris", "text/uri-list");
        TYPE_MAPPING.put("ustar", "application/x-ustar");
        TYPE_MAPPING.put("ustar", "multipart/x-ustar");
        TYPE_MAPPING.put("uu", "application/octet-stream");
        TYPE_MAPPING.put("uu", "text/x-uuencode");
        TYPE_MAPPING.put("uue", "text/x-uuencode");
        TYPE_MAPPING.put("vcd", "application/x-cdlink");
        TYPE_MAPPING.put("vcs", "text/x-vcalendar");
        TYPE_MAPPING.put("vda", "application/vda");
        TYPE_MAPPING.put("vdo", "video/vdo");
        TYPE_MAPPING.put("vew", "application/groupwise");
        TYPE_MAPPING.put("viv", "video/vivo");
        TYPE_MAPPING.put("viv", "video/vnd.vivo");
        TYPE_MAPPING.put("vivo", "video/vivo");
        TYPE_MAPPING.put("vivo", "video/vnd.vivo");
        TYPE_MAPPING.put("vmd", "application/vocaltec-media-desc");
        TYPE_MAPPING.put("vmf", "application/vocaltec-media-file");
        TYPE_MAPPING.put("voc", "audio/voc");
        TYPE_MAPPING.put("voc", "audio/x-voc");
        TYPE_MAPPING.put("vos", "video/vosaic");
        TYPE_MAPPING.put("vox", "audio/voxware");
        TYPE_MAPPING.put("vqe", "audio/x-twinvq-plugin");
        TYPE_MAPPING.put("vqf", "audio/x-twinvq");
        TYPE_MAPPING.put("vql", "audio/x-twinvq-plugin");
        TYPE_MAPPING.put("vrml", "application/x-vrml");
        TYPE_MAPPING.put("vrml", "model/vrml");
        TYPE_MAPPING.put("vrml", "x-world/x-vrml");
        TYPE_MAPPING.put("vrt", "x-world/x-vrt");
        TYPE_MAPPING.put("vsd", "application/x-visio");
        TYPE_MAPPING.put("vst", "application/x-visio");
        TYPE_MAPPING.put("vsw", "application/x-visio");
        TYPE_MAPPING.put("w60", "application/wordperfect6.0");
        TYPE_MAPPING.put("w61", "application/wordperfect6.1");
        TYPE_MAPPING.put("w6w", "application/msword");
        TYPE_MAPPING.put("wav", "audio/wav");
        TYPE_MAPPING.put("wav", "audio/x-wav");
        TYPE_MAPPING.put("wb1", "application/x-qpro");
        TYPE_MAPPING.put("wbmp", "image/vnd.wap.wbmp");
        TYPE_MAPPING.put("web", "application/vnd.xara");
        TYPE_MAPPING.put("wiz", "application/msword");
        TYPE_MAPPING.put("wk1", "application/x-123");
        TYPE_MAPPING.put("wmf", "windows/metafile");
        TYPE_MAPPING.put("wml", "text/vnd.wap.wml");
        TYPE_MAPPING.put("wmlc", "application/vnd.wap.wmlc");
        TYPE_MAPPING.put("wmls", "text/vnd.wap.wmlscript");
        TYPE_MAPPING.put("wmlsc", "application/vnd.wap.wmlscriptc");
        TYPE_MAPPING.put("word", "application/msword");
        TYPE_MAPPING.put("wp", "application/wordperfect");
        TYPE_MAPPING.put("wp5", "application/wordperfect");
        TYPE_MAPPING.put("wp5", "application/wordperfect6.0");
        TYPE_MAPPING.put("wp6", "application/wordperfect");
        TYPE_MAPPING.put("wpd", "application/wordperfect");
        TYPE_MAPPING.put("wpd", "application/x-wpwin");
        TYPE_MAPPING.put("wq1", "application/x-lotus");
        TYPE_MAPPING.put("wri", "application/mswrite");
        TYPE_MAPPING.put("wri", "application/x-wri");
        TYPE_MAPPING.put("wrl", "application/x-world");
        TYPE_MAPPING.put("wrl", "model/vrml");
        TYPE_MAPPING.put("wrl", "x-world/x-vrml");
        TYPE_MAPPING.put("wrz", "model/vrml");
        TYPE_MAPPING.put("wrz", "x-world/x-vrml");
        TYPE_MAPPING.put("wsc", "text/scriplet");
        TYPE_MAPPING.put("wsrc", "application/x-wais-source");
        TYPE_MAPPING.put("wtk", "application/x-wintalk");
        TYPE_MAPPING.put("xbm", "image/x-xbitmap");
        TYPE_MAPPING.put("xbm", "image/x-xbm");
        TYPE_MAPPING.put("xbm", "image/xbm");
        TYPE_MAPPING.put("xdr", "video/x-amt-demorun");
        TYPE_MAPPING.put("xgz", "xgl/drawing");
        TYPE_MAPPING.put("xif", "image/vnd.xiff");
        TYPE_MAPPING.put("xl", "application/excel");
        TYPE_MAPPING.put("xla", "application/excel");
        TYPE_MAPPING.put("xla", "application/x-excel");
        TYPE_MAPPING.put("xla", "application/x-msexcel");
        TYPE_MAPPING.put("xlb", "application/excel");
        TYPE_MAPPING.put("xlb", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xlb", "application/x-excel");
        TYPE_MAPPING.put("xlc", "application/excel");
        TYPE_MAPPING.put("xlc", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xlc", "application/x-excel");
        TYPE_MAPPING.put("xld", "application/excel");
        TYPE_MAPPING.put("xld", "application/x-excel");
        TYPE_MAPPING.put("xlk", "application/excel");
        TYPE_MAPPING.put("xlk", "application/x-excel");
        TYPE_MAPPING.put("xll", "application/excel");
        TYPE_MAPPING.put("xll", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xll", "application/x-excel");
        TYPE_MAPPING.put("xlm", "application/excel");
        TYPE_MAPPING.put("xlm", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xlm", "application/x-excel");
        TYPE_MAPPING.put("xls", "application/excel");
        TYPE_MAPPING.put("xls", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xls", "application/x-excel");
        TYPE_MAPPING.put("xls", "application/x-msexcel");
        TYPE_MAPPING.put("xlt", "application/excel");
        TYPE_MAPPING.put("xlt", "application/x-excel");
        TYPE_MAPPING.put("xlv", "application/excel");
        TYPE_MAPPING.put("xlv", "application/x-excel");
        TYPE_MAPPING.put("xlw", "application/excel");
        TYPE_MAPPING.put("xlw", "application/vnd.ms-excel");
        TYPE_MAPPING.put("xlw", "application/x-excel");
        TYPE_MAPPING.put("xlw", "application/x-msexcel");
        TYPE_MAPPING.put("xm", "audio/xm");
        TYPE_MAPPING.put("xml", "application/xml");
        TYPE_MAPPING.put("xml", "text/xml");
        TYPE_MAPPING.put("xmz", "xgl/movie");
        TYPE_MAPPING.put("xpix", "application/x-vnd.ls-xpix");
        TYPE_MAPPING.put("xpm", "image/x-xpixmap");
        TYPE_MAPPING.put("xpm", "image/xpm");
        TYPE_MAPPING.put("x-png", "image/png");
        TYPE_MAPPING.put("xsr", "video/x-amt-showrun");
        TYPE_MAPPING.put("xwd", "image/x-xwd");
        TYPE_MAPPING.put("xwd", "image/x-xwindowdump");
        TYPE_MAPPING.put("xyz", "chemical/x-pdb");
        TYPE_MAPPING.put("z", "application/x-compress");
        TYPE_MAPPING.put("z", "application/x-compressed");
        TYPE_MAPPING.put("zip", "application/x-compressed");
        TYPE_MAPPING.put("zip", "application/x-zip-compressed");
        TYPE_MAPPING.put("zip", "application/zip");
        TYPE_MAPPING.put("zip", "multipart/x-zip");
        TYPE_MAPPING.put("zoo", "application/octet-stream");
        TYPE_MAPPING.put("zsh", "text/x-script.zsh");

        // 附加映射表
        Map<String, String> appendix = SystemConfiguration.getInstance().readMap("loader.type.mapping");
        if (appendix != null) {
            TYPE_MAPPING.putAll(appendix);
        }
    }
}
