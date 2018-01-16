package com.lzx.nicemusic.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Process;
import android.util.Base64;

import com.lzx.nicemusic.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证调用包是否有权浏览{@link android.service.media.MediaBrowserService}.
 *
 * @author lzx
 * @date 2018/1/16
 */

public class PackageValidator {

    /**
     * Map allowed callers' certificate keys to the expected caller information.
     */
    private final Map<String, ArrayList<CallerInfo>> mValidCertificates;

    public PackageValidator(Context ctx) {
        mValidCertificates = readValidCertificates(ctx.getResources().getXml(
                R.xml.allowed_media_browser_callers));
    }

    private Map<String, ArrayList<CallerInfo>> readValidCertificates(XmlResourceParser parser) {
        HashMap<String, ArrayList<CallerInfo>> validCertificates = new HashMap<>();
        try {
            int eventType = parser.next();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG
                        && parser.getName().equals("signing_certificate")) {

                    String name = parser.getAttributeValue(null, "name");
                    String packageName = parser.getAttributeValue(null, "package");
                    boolean isRelease = parser.getAttributeBooleanValue(null, "release", false);
                    String certificate = parser.nextText().replaceAll("\\s|\\n", "");

                    CallerInfo info = new CallerInfo(name, packageName, isRelease);

                    ArrayList<CallerInfo> infos = validCertificates.get(certificate);
                    if (infos == null) {
                        infos = new ArrayList<>();
                        validCertificates.put(certificate, infos);
                    }

                    infos.add(info);
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
        }
        return validCertificates;
    }

    /**
     * @return false if the caller is not authorized to get data from this MediaBrowserService
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCallerAllowed(Context context, String callingPackage, int callingUid) {
        // Always allow calls from the framework, self app or development environment.
        if (Process.SYSTEM_UID == callingUid || Process.myUid() == callingUid) {
            return true;
        }

        if (isPlatformSigned(context, callingPackage)) {
            return true;
        }

        PackageInfo packageInfo = getPackageInfo(context, callingPackage);
        if (packageInfo == null) {
            return false;
        }
        if (packageInfo.signatures.length != 1) {
            return false;
        }
        String signature = Base64.encodeToString(
                packageInfo.signatures[0].toByteArray(), Base64.NO_WRAP);

        // Test for known signatures:
        ArrayList<CallerInfo> validCallers = mValidCertificates.get(signature);
        if (validCallers == null) {
            if (mValidCertificates.isEmpty()) {

            }
            return false;
        }

        // Check if the package name is valid for the certificate:
        StringBuffer expectedPackages = new StringBuffer();
        for (CallerInfo info : validCallers) {
            if (callingPackage.equals(info.packageName)) {

                return true;
            }
            expectedPackages.append(info.packageName).append(' ');
        }


        return false;
    }

    /**
     * @return true if the installed package signature matches the platform signature.
     */
    private boolean isPlatformSigned(Context context, String pkgName) {
        PackageInfo platformPackageInfo = getPackageInfo(context, "android");

        // Should never happen.
        if (platformPackageInfo == null || platformPackageInfo.signatures == null
                || platformPackageInfo.signatures.length == 0) {
            return false;
        }

        PackageInfo clientPackageInfo = getPackageInfo(context, pkgName);

        return (clientPackageInfo != null && clientPackageInfo.signatures != null
                && clientPackageInfo.signatures.length > 0 &&
                platformPackageInfo.signatures[0].equals(clientPackageInfo.signatures[0]));
    }

    /**
     * @return {@link PackageInfo} for the package name or null if it's not found.
     */
    private PackageInfo getPackageInfo(Context context, String pkgName) {
        try {
            final PackageManager pm = context.getPackageManager();
            return pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    private final static class CallerInfo {
        final String name;
        final String packageName;
        final boolean release;

        public CallerInfo(String name, String packageName, boolean release) {
            this.name = name;
            this.packageName = packageName;
            this.release = release;
        }
    }
}
