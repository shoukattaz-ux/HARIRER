package com.example.update

import org.json.JSONObject

/**
 * Parses the self-hosted `update.json`, for example:
 * {"versionCode":4,"versionName":"1.3","apkUrl":"https://…/hairlook-1.3.apk",
 *  "minSupportedVersionCode":1,"releaseNotes":"New AI looks"}
 */
object UpdateManifestParser {

    fun parse(json: String): UpdatePolicy.UpdateManifest? = try {
        val obj = JSONObject(json)
        val versionCode = obj.getInt("versionCode")
        val apkUrl = obj.getString("apkUrl")
        UpdatePolicy.UpdateManifest(
            versionCode = versionCode,
            versionName = obj.optString("versionName", versionCode.toString()),
            apkUrl = apkUrl,
            minSupportedVersionCode = obj.optInt("minSupportedVersionCode", 0),
            releaseNotes = obj.optString("releaseNotes", "")
        )
    } catch (e: Exception) {
        null
    }
}
