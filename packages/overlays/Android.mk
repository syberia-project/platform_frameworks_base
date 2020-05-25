# Copyright (C) 2019 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := frameworks-base-overlays
LOCAL_REQUIRED_MODULES := \
	AccentColorBlackOverlay \
	AccentColorCinnamonOverlay \
	AccentColorOceanOverlay \
	AccentColorOrchidOverlay \
	AccentColorSpaceOverlay \
	AccentColorGreenOverlay \
	AccentColorPurpleOverlay \
	DisplayCutoutEmulationCornerOverlay \
	DisplayCutoutEmulationDoubleOverlay \
	DisplayCutoutEmulationTallOverlay \
	FontAclonicaSourceOverlay \
	FontAmaranteSourceOverlay \
	FontBariolSourceOverlay \
	FontCagliostroSourceOverlay \
	FontComicSansSourceOverlay \
	FontCoolstorySourceOverlay \
	FontGoogleSansSourceOverlay \
	FontLGSmartGothicSourceOverlay \
	FontNotoSerifSourceOverlay \
	FontOneplusSlateSource \
	FontRosemarySourceOverlay \
	FontSamsungOneSourceOverlay \
	FontSonySketchSourceOverlay \
	FontSurferSourceOverlay \
	FontNokiaPureSourceOverlay \
	FontFifa2018SourceOverlay \
	FontOswaldSourceOverlay \
	FontExo2SourceOverlay \
	FontUbuntuRegularSourceOverlay \
	FontRobotoCondensedLightSourceOverlay \
	FontStoropiaSourceOverlay \
	FontRoundedEleganceSourceOverlay \
	FontCaviarDreamsSourceOverlay \
	FontSanFranciscoDisplayProSourceOverlay \
	FontDomkratSourceOverlay \
	FontFakedesSourceOverlay \
	FontHondaSourceOverlay \
	FontHortensiaSourceOverlay \
	FontMinusmanSourceOverlay \
	FontNovaSourceOverlay \
	FontTaurusSourceOverlay \
	FontTechnicalSourceOverlay \
	IconPackCircularAndroidOverlay \
	IconPackCircularLauncherOverlay \
	IconPackCircularSettingsOverlay \
	IconPackCircularSystemUIOverlay \
	IconPackCircularThemePickerOverlay \
	IconPackRoundedAndroidOverlay \
	IconPackRoundedLauncherOverlay \
	IconPackRoundedSettingsOverlay \
	IconPackRoundedSystemUIOverlay \
	IconPackRoundedThemePickerOverlay \
	IconShapeCylinderOverlay \
	IconShapeHexagonOverlay \
	IconShapeRoundedHexagonOverlay \
	IconShapeRoundedRectOverlay \
	IconShapeSquareOverlay \
	IconShapeSquircleOverlay \
	IconShapeTeardropOverlay \
	NavigationBarMode3ButtonOverlay \
	NavigationBarMode2ButtonOverlay \
	NavigationBarModeGesturalOverlay \
	NavigationBarModeGesturalOverlayNarrowBack \
	NavigationBarModeGesturalOverlayWideBack \
	NavigationBarModeGesturalOverlayExtraWideBack \
	NavigationBarGesturalOverlayHideNav

include $(BUILD_PHONY_PACKAGE)
include $(CLEAR_VARS)

LOCAL_MODULE := frameworks-base-overlays-debug

include $(BUILD_PHONY_PACKAGE)
include $(call first-makefiles-under,$(LOCAL_PATH))
