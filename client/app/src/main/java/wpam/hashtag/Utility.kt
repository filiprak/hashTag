package wpam.hashtag

import android.util.Log
import android.content.Context
import android.content.pm.ApplicationInfo  
import android.content.pm.PackageManager

public fun GetMetaData(context: Context, name: String): String? {
    try {
        val ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        val bundle = ai.metaData;
        return bundle.getString(name);
    } catch (e: PackageManager.NameNotFoundException) {
        val tag_key = "log_tag"
        val tag = if (name != tag_key) GetMetaData(context, tag_key) else "";
        Log.e(tag, "Unable to load meta-data: " + e.message);
    }
    return null;
}
