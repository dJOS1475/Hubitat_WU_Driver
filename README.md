/**
 * Wunderground Driver
 *
 *  Derek Osborn
 *
 *  This driver was originally written by @mattw01 and @Cobra
 *  Modified and fixed by myself: @dJOS
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Last Update 25/09/2022
 *
 *	v6.2.0 - Improved formatting, fixed debug.logging and added Station Location to the Tiles
 *	v6.1.1 - Broke 3 Day FC into individual tiles due to 1024 char limit
 *	v6.0.2 - Tile bug fixes
 *	v6.0.1 - Added a 3 Day Forecast Dashboard Tile 
 *			 (thanks to @thebearmay for his extensive HTML assistance and @sburke781 for his help with CSS)
 *	v5.7.0 - Added a 3rd day of forecast data "forecastDayAfterTomorrow" including Icon
 *	v5.6.5 - Fixed Polling Bug
 *	v5.6.4 - Removed extra fields due to excess events being generated
 *	v5.6.3 - Removed Snow fields due to excess events being generated
 *	v5.6.2 - Added 6 new fields - Thunder, Snow & UV
 *	v5.6.1 - Minor Bug Fix 
 *	v5.6.0 - add selectable language eg en-GB or en-US 
 *	v5.5.0 - WU Icons now hosted on GitHub
 *	v5.4.0 - Bug Fixes
 *	v5.3.0 - Major to changes forecastToday to auto-switch to night info including fixing icons to match
 *	v5.2.0 - Modified to add forecastToday and forecastTomorrow by Derek Osborn
 *	v5.1.0 - Modified to use latitude and longitude from the hub and add cloudCover by Derek Osborn
 *	V5.0.0 - Release by @Cobra
 *	V1.0.0 - Original @mattw01 version
 *
 *
 */
