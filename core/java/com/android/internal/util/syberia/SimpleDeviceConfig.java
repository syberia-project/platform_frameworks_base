/*
 * Copyright (C) 2020 The Proton AOSP Project
 * Copyright (C) 2023 TheParasiteProject
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.util.evolution;

import android.content.Context;
import android.content.ContentResolver;
import android.provider.DeviceConfig;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.android.internal.R;

public class SimpleDeviceConfig {
    private static final String TAG = "SimpleDeviceConfig";
    private static final String PACKAGE_GMS = "com.google.android.gms";

    private static final String NOT_HAVE_NAMESPACE = "not_have_namespace";
    private static final String NOT_HAVE_CONFIG = "not_have_config";

    private static Set<String> rawPropertiesSet = new HashSet<>();
    private static Map<String, String> fullKeyValMap = new HashMap<>();
    private static Set<String> nameSpaceSet = new HashSet<>();

    private static Set<String> rawPropertiesSetSoft = new HashSet<>();

    public static void updateDefaultConfigs(Context context) {
        updateConfig(context, R.array.configs_base, false);
        updateConfig(context, R.array.configs_base_soft, true);

        updateConfig(context, R.array.configs_device, false);
    }

    private static void updateConfig(Context context, int configArray, boolean isSoft) {
        // Set current properties
        String[] rawProperties = context.getResources().getStringArray(configArray);
        for (String property : rawProperties) {
            // Format: namespace/key=value
            String[] kv = property.split("=", 2);
            String fullKey = kv[0];
            String[] nsKey = fullKey.split("/");

            String namespace = nsKey[0];
            String key = nsKey[1];
            String value = "";
            if (kv.length > 1) {
                value = kv[1];
            }

            // Skip soft configs that already have values
            if (!isSoft || DeviceConfig.getString(namespace, key, null) == null) {
                DeviceConfig.setProperty(namespace, key, value, false);
            }
        }
    }

    public static String modifyValue(ContentResolver cr, String namespace, String name, String value) {
        try {
            Log.d(TAG, "modifyValue Called: "+cr.getPackageName()+", "+namespace+"/"+name);

            if (!cr.getPackageName().contains(PACKAGE_GMS)) return value;

            if (updateValue(cr.getCallingContext(), namespace, "").equals(NOT_HAVE_NAMESPACE)) return value;

            final String ModifiedValue = updateValue(cr.getCallingContext(), namespace, name);

            if (ModifiedValue.equals(NOT_HAVE_CONFIG)) return value;

            return ModifiedValue;
        } catch (Exception e) {
            Log.e(TAG, "modifyValue: "+namespace+"/"+name+" "+"failed", e);
            return value;
        }
    }

    public static Map<String, String> modifyProperties(ContentResolver cr, String namespace, HashMap<String, String> mMap) {
        try {
            if (!cr.getPackageName().contains(PACKAGE_GMS)) return mMap;

            if (updateValue(cr.getCallingContext(), namespace, "").equals(NOT_HAVE_NAMESPACE)) return mMap;

            for (Map.Entry<String, String> entry : mMap.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();

                Log.d(TAG, "modifyProperties: Get "+cr.getPackageName()+", "+namespace+"/"+name);

                String ModifiedValue = modifyValue(cr, namespace, name, value);
                if (!ModifiedValue.equals(NOT_HAVE_CONFIG) && !ModifiedValue.equals(value)) {

                    Log.d(TAG, "modifyProperties: Modify to "+cr.getPackageName()+", "+namespace+"/"+name+"="+ModifiedValue);

                    entry.setValue(ModifiedValue);
                }
            }

            return updateProperties(cr.getCallingContext(), namespace, mMap);
        } catch (Exception e) {
            Log.e(TAG, "modifyProperties: failed", e);
            return mMap;
        }
    }

    public static void initConfig(Context context) {
        if (rawPropertiesSet.isEmpty()) {
            Collections.addAll(rawPropertiesSet,
                    context.getResources().getStringArray(R.array.configs_base));
            Collections.addAll(rawPropertiesSetSoft,
                    context.getResources().getStringArray(R.array.configs_base_soft));

            Collections.addAll(rawPropertiesSet,
                    context.getResources().getStringArray(R.array.configs_device));
        }

        if (fullKeyValMap.isEmpty() || nameSpaceSet.isEmpty()) {
            for (String property : rawPropertiesSet) {
                // Format: namespace/key=value
                String[] fullKey = property.split("=", 2);
                String[] namespaceKey = fullKey[0].split("/");
                String value = "";
                if (fullKey.length > 1) {
                    value = fullKey[1];
                }

                fullKeyValMap.put(fullKey[0], value);
                nameSpaceSet.add(namespaceKey[0]);
            }
            // Add soft configs that currently doesn't have values
            // in device config storage
            for (String property : rawPropertiesSetSoft) {
                // Format: namespace/key=value
                String[] fullKey = property.split("=", 2);
                String[] namespaceKey = fullKey[0].split("/");
                String value = "";
                if (fullKey.length > 1) {
                    value = fullKey[1];
                }

                if (DeviceConfig.getString(fullKey[0], value, null) == null) {
                    fullKeyValMap.put(fullKey[0], value);
                    nameSpaceSet.add(namespaceKey[0]);
                }
            }
        }
    }

    public static String updateValue(Context context, String namespace, String key) {
        initConfig(context);

        if (!nameSpaceSet.contains(namespace)) return NOT_HAVE_NAMESPACE;

        if (namespace != null && key != null && fullKeyValMap.containsKey(namespace+"/"+key)) {

            Log.d(TAG, "updateValue will change: "+namespace+"/"+key);

            return fullKeyValMap.get(namespace+"/"+key);
        }

        return NOT_HAVE_CONFIG;
    }

    public static Map<String, String> updateProperties(Context context, String namespace, HashMap<String, String> mMap) {
        initConfig(context);

        if (!nameSpaceSet.contains(namespace)) return mMap;

        if (namespace != null && !mMap.isEmpty()) {
            Map<String, String> tempMap = new HashMap<>();
            for (Map.Entry<String, String> entry : fullKeyValMap.entrySet()) {
                String[] fullKey = entry.getKey().split("/");
                String nsKey = fullKey[0];
                String name = fullKey[1];
                String value = entry.getValue();
                if (namespace.equals(nsKey) && !mMap.keySet().contains(name)) {
                    tempMap.put(name, value);
                }
            }
            Map<String, String> addOnMap = new HashMap<>();
            addOnMap.putAll(mMap);
            addOnMap.putAll(tempMap);
            return addOnMap;
        }

        return mMap;
    }
}
