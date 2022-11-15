		
/**
* Wunderground Driver
*
*  Maintained by Derek Osborn
*
*  This driver was originally written by @mattw01 and @Cobra
*  Modified and fixed by @dJOS
*  Additional contributions by @thebearmay @sburke781 @Busthead @swade @kampto
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
*  Last Update 11/08/2022
*
*	v6.10.0 - @swade fixed all my loFi code and made lots of improvements under the hood 
*			- it is highly recommended that you use the "CleaState" function and then force a manual poll after upgrading to this version.
*	v6.9.4 - Helps with WU only sending 6 days history for some PWS
*	v6.9.3 - additional Logic to deal with only 6 days of rain data by @swade
*	v6.9.2 - Enabled the manual entry of Location (lat/long) for Forecasts etc
*	v6.8.3 - Added Error Checks when WU doesn't return all days of rain history
*	v6.8.2 - Removed option PWS functionality - it just broke too many things - added version reporting
*	v6.7.1 - Bug Fixes by @swade
*	v6.7.0 - Added Rain History Tile and Today/Tonight forecast header when forecast changes by @swade
*	v6.6.0 - Implement Weather Warning Dashboard Tile + made 12:01am default day start for new installs + Forecast Data code restructure
*	v6.5.1 - Implement Weather Warnings and Codes 
*	v6.4.0 - Implement Day/night switching for most forecast items
*	v6.3.2 - Fix a rain forecast bug
*	v6.3.1 - Bug Fix for null on line 538 error
*	v6.3.0 - Added 3 Day Weather Forecast Dashboard tile and additional data ingestion developed by @swade 
*	v6.2.4 - Not all instances of log.info were checking if txtEnable == true
*	v6.2.3 - Replaced logSet with txtEnable to conform with built-in drivers and consolidate Hubitat Preference Manager entries for better user experience
*	v6.2.2 - Actually Fixed the Day/Night ForecastDayAfterTomorrow Icon switch over bug
*	v6.2.1 - Fixed Day/Night ForecastDayAfterTomorrow Icon switch over bug
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
*/

def version() {
    return "6.10.0"
}

metadata {
    definition (name: "Wunderground Driver", namespace: "dJOS", author: "Derek Osborn", importUrl: "https://raw.githubusercontent.com/dJOS1475/Hubitat_WU_Driver/main/WU_Driver.groovy") {
        capability "Actuator"
        capability "Sensor"
        capability "Temperature Measurement"
        capability "Illuminance Measurement"
        capability "Relative Humidity Measurement"
        
//       command "dayRainChange"     // test code **************
//       command "clearRainHistory"  // test code  *************

//        command "forceUpdateOn"
//       command "forceUpdateOff"
        command "poll", [[name:"Start a Manual Poll of Weather Underground Data"]]
//        command "forcePoll"
// 	    command "resetPollCount"
 	    command "clearState", [[name:"Use Only 1 time to Clear State Variables no Longer Used"]]
        
        attribute "dayOrNight", "string"
        attribute "formatLanguage", "string"
 	    attribute "formatUnit", "string"
        attribute "rainHistoryDays", "number"
 	    attribute "forecastTimeName", "string"
        attribute "htmlRainTile", "string"
        attribute "precip_Yesterday", "string"
        attribute "precip_Last3Days", "string"
 	    attribute "precip_Last5Days", "string"
 	    attribute "precip_Last7Days", "string"
 	    attribute "temperatureMaxToday", "string"
    	attribute "temperatureMaxTomorrow", "string"
	    attribute "temperatureMaxDayAfterTomorrow", "string"
	    attribute "temperatureMinToday", "string"
	    attribute "temperatureMinTomorrow", "string"
	    attribute "temperatureMinDayAfterTomorrow", "string"
	    attribute "forcastPhraseToday", "string"
	    attribute "forcastPhraseTomorrow", "string"
	    attribute "forcastPhraseDayAfterTomorrow", "string"
	    attribute "precipChanceToday", "string"
 	    attribute "precipChanceTomorrow", "string"
	    attribute "precipChanceDayAfterTomorrow", "string"
	    attribute "sunriseTimeLocal", "String"
	    attribute "sunsetTimeLocal", "String"
	    attribute "html3dayfcst", "string"
 	    
 	    attribute "htmlWarnings", "string"
 	    attribute "htmlToday", "string"
 	    attribute "htmlTomorrow", "string"
 	    attribute "htmlDayAfterTomorrow", "string"
		attribute "today", "string"
		attribute "tomorrow", "string"
 	    attribute "dayAfterTomorrow", "string"
        attribute "uvDescription", "string"
        attribute "uvIndex", "number"
        attribute "snowRange", "number"
        attribute "qpfSnow", "number"
        attribute "thunderCategory", "string"
        attribute "thunderIndex", "number"
        attribute "precipType", "string"
        attribute "solarradiation", "number"
        attribute "illuminance", "number"
        attribute "observation_time", "string"
        attribute "weather", "string"
        attribute "feelsLike", "number"
		attribute "forecastTodayIcon", "string"
        attribute "forecastTomorrowIcon", "string"
        attribute "forecastDayAfterTomorrowIcon", "string"
		attribute "city", "string"
        attribute "state", "string"
        attribute "percentPrecip", "number"
        attribute "wind_string", "string"
        attribute "pressure", "decimal"
        attribute "dewpoint", "number"
        attribute "visibility", "number"
        attribute "forecastHigh", "number"
        attribute "forecastLow", "number"
        attribute "forecastToday", "string"
        attribute "forecastTomorrow", "string"
        attribute "forecastDayAfterTomorrow", "string"
        attribute "forecastTemp", "string"
		attribute "forecastShort", "string"
        attribute "wind_dir", "string"
		attribute "wind_degree", "string"
        attribute "wind_gust", "number"
        attribute "precip_rate", "number"
        attribute "precip_today", "number"
        attribute "wind", "number"
		attribute "windPhrase", "string"
		attribute "windPhraseForecast", "string"
        attribute "UV", "number"
       	attribute "UVHarm", "string"
        attribute "pollsSinceReset", "number"
        attribute "temperatureUnit", "string"
        attribute "distanceUnit", "string"
        attribute "pressureUnit", "string"
        attribute "rainUnit", "string"
        attribute "summaryFormat", "string"
        attribute "alert", "string"
        attribute "elevation", "number"
        attribute "stationID", "string"
		attribute "stationType", "string"
        attribute "weatherSummary", "string"
        attribute "weatherSummaryFormat", "string"
        attribute "fCstRainToday", "number"
        attribute "fCstRainTomorrow", "number"
        attribute "fCstRainDayAfterTomorrow", "number"
        attribute "moonPhase", "string"        
 		// attribute "DriverAuthor", "string"
        // attribute "DriverVersion", "string"
		attribute "humidity", "number"
		attribute "station_location", "string"
        attribute "elevation", "number"
        // attribute "rainYesterday", "number"
        // attribute "rainDayBeforeYesterday", "number"
        attribute "lastUpdateCheck", "string"
        attribute "lastPollTime", "string"
        attribute "cloudCover", "number"
        attribute "weatherWarning", "string"
        attribute "weatherWarningCode", "string"
        attribute "weatherWarningTomorrow", "string"
        attribute "weatherWarningCodeTomorrow", "string"
		attribute "weatherWarningDATomorrow", "string"
        attribute "weatherWarningCodeDATomorrow", "string"
        attribute "moonIllumination", "number"
        attribute "latitude", "decimal"
		attribute "longitude", "decimal"
        attribute "latitudeCust", "decimal"
		attribute "longitudeCust", "decimal"
    }
    preferences() {
        section("Query Inputs"){
			input name: "about", type: "paragraph", element: "paragraph", title: "Wunderground Driver", description: "v.${version()}"
			input "apiKey", "text", required: true, title: "API Key"
            input "pollLocation", "text", required: true, title: "Personal Weather Station ID"
			input "unitFormat", "enum", required: true, title: "Unit Format",  options: ["Imperial", "Metric", "UK Hybrid"]
            if(unitFormat == "UK Hybrid"){input "unitElevation", "bool", required: false, title: "Use Metric for elevation (m)", defaultValue: false}
            input "language", "enum", required: true, title: "Language",  options: ["US", "GB"], defaultValue: US
            input "useIcons", "bool", required: false, title: "Use WU Icons (Optional)", defaultValue: true
			if(useIcons){
			input "iconHeight1", "text", required: true, title: "Icon Height", defaultValue: 100
			input "iconWidth1", "text", required: true, title: "Icon Width", defaultValue: 100}			
            //input "pollIntervalLimit", "number", title: "Poll Interval Limit:", required: true, defaultValue: 1
            input "autoPoll", "bool", required: false, title: "Enable Auto Poll"
            input "pollInterval", "enum", title: "Auto Poll Interval:", required: false, defaultValue: "5 Minutes", options: ["5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "1 Hour", "3 Hours"]
            //input "cutOff", "time", title: "New Day Starts", required: true, defaultValue: "00:01"
            input "gpsCoords", "bool", title: "Use custom GPS Coordinates for Forecast?", defaultvalue: false, submitOnChange: true
			if (gpsCoords) {
			input "latitudeCust", "text", title: "Enter Latitude in decimals, EX: 37.48644", defaultValue: 0, width: 6, required: false
			input "longitudeCust", "text", title: "Enter Longitude in decimals, EX: -121.932309", defaultValue: 0, width: 6, required: false}
            input "threedayforecast", "bool", title: "Create a 3-Day Forecast Tile", required: false, defaultValue: false
            input "rainhistory", "bool", title: "Create a 7-Day Rain History Tile", required: false, defaultValue: false
            if(threedayforecast && rainhistory){
            input "raindaysdisplay", "enum", title: "Rain History Days to Display On 3-Day Forecast Tile: (Requires 3-day Forecast and Rain History Tiles) (1st/2nd number Days depends on WU returning 6 or 7 day history) ", required: false, defaultValue: "No selection", options: ["None", "Yesterday", "Last 2/3-Days", "Last 4/5-Days", "Last 6/7-Days"]}
			input "txtEnable", "bool", title: "Enable Detailed logging<br>(auto off in 15 minutes)", required: false, defaultValue: false
			}
    }
}

def updated() {
    if(txtEnable){log.debug "updated called"}
    //state.NumOfPolls = 0
    
    unschedule()
    poll()

    //def changeOver = cutOff
    //schedule(changeOver, resetPollCount)
    unschedule("changeOver")  //needed to remove unused method    
    
    unschedule("dayRainChange")  //needed to remove unused method    
    //schedule(changeOver, dayRainChange)
    
    def pollIntervalCmd = (settings?.pollInterval ?: "5 Minutes").replace(" ", "")
    if(txtEnable == true){log.info "poll IntervalCmd: $pollIntervalCmd"}
    if(autoPoll)
        "runEvery${pollIntervalCmd}"(pollSchedule)
    
    if(txtEnable){runIn(1800, logsOff)}
}

def clearState()
{
    state.clear()
    log.info "WU Driver State Cleared."
}

//def forceUpdateOn(){
    //state.force = true 
//    if(txtEnable == true){log.info "Force attribute refresh set to 'true'"}
//}
//def forceUpdateOff(){
    //state.force = null
//    if(txtEnable == true){log.info "Force attribute refresh set to 'false'"}
//}

//def resetPollCount(){
	//state.NumOfPolls = -1
//    if(txtEnable == true){log.info "Poll counter reset.."}
//    forcePoll()
//}

//def clearRainHistory(){
//    state.dayBeforeYesterdayRain = 0
//    state.yesterdayRain = 0   
//    sendEvent(name: "rainYesterday", value: state.yesterdayRain, isStateChange: true )  
//    sendEvent(name: "rainDayBeforeYesterday", value: state.dayBeforeYesterdayRain, isStateChange: true )    
//    }
    
//def dayRainChange(){
//	state.rainToday = (location.todayRain) 
//	state.dayBeforeYesterdayRain = state.yesterdayRain
//	state.yesterdayRain = state.rainToday 
//	sendEvent(name: "rainYesterday", value: state.yesterdayRain, isStateChange: state.force )  
//	sendEvent(name: "rainDayBeforeYesterday", value: state.dayBeforeYesterdayRain, isStateChange: state.force )    
//  }

def pollSchedule(){
    poll()
}

//def parse(String description) {}

//def poll() {
//	if(now() - state.lastPoll > (pollIntervalLimit * 60000))
//        forcePoll()
//    else{log.warn "Poll called before interval threshold was reached"}
//}

def formatUnit(){
	if(unitFormat == "Imperial"){
        sendEvent(name: "formatUnit", value: "e")  
		//state.unit = "e"
        //if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "Metric"){
        sendEvent(name: "formatUnit", value: "m")  
		//state.unit = "m"
        //if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "UK Hybrid"){
        sendEvent(name: "formatUnit", value: "h")  
		//state.unit = "h"
        //if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(language == "US"){
        sendEvent(name: "formatLanguage", value: "en-US")  
		//state.languagef = "en-US"
	}
	if(language == "GB"){
        sendEvent(name: "formatLanguage", value: "en-GB")  
		//state.languagef = "en-GB"
		//if(txtEnable == true){log.info "state.languagef = $state.languagef"}
	}
    if(txtEnable == true){log.info "formatUnit = ${device.currentValue('formatUnit')}"}
    if(txtEnable == true){log.info "formatLanguage = ${device.currentValue('formatLanguage')}"}

}

def poll(){
    if(txtEnable == true){log.debug "WU: Poll called"}
    unschedule("changeOver")  //needed to remove unused method 
    unschedule("dayRainChange")  //needed to remove unused method    
    //state.NumOfPolls = (state.NumOfPolls) + 1
    //sendEvent(name: "pollsSinceReset", value: state.NumOfPolls, isStateChange: state.force )
    def date = new Date()
    //state.LastTime1 = date.format('HH:mm', location.timeZone)
    sendEvent(name: "lastPollTime", value: date.format('HH:mm', location.timeZone))
    locationCoOrds()

    poll1()
    pauseExecution(5000)
	
    poll2()
	pauseExecution(5000)
    
    if (rainhistory)
    {
        poll3()
        pauseExecution(5000)
    }
    
    updateTile1()
    updateTile2()
    updateTile3()
    updateTile4()

    if(txtEnable == true){log.info "3-Day Forecast Tile: $threedayforecast"}
    wu3dayfcst()
   
    if(txtEnable == true){log.info "7-Day Rain History: $rainhistory"}
    rainTile()
}

def locationCoOrds(){
    
    def polllatitude
    def polllongitude
    
    if(gpsCoords){
        polllatitude = device.currentValue('latitudeCust')
        polllongitude = device.currentValue('longitudeCust')
		//state.latt1 = latitudeCust
		//state.long1 = longitudeCust
		//sendEvent(name: "latitude", value: device.currentValue('latitudeCust'), isStateChange: state.force )
		//sendEvent(name: "longitude", value: device.currentValue('longitudeCust'), isStateChange: state.force )
		}
		else{    
        polllatitude = location.getLatitude()
        polllongitude = location.getLongitude()
		//state.latt1 = (location.getLatitude())
		//state.long1 = (location.getLongitude())
		//sendEvent(name: "latitude", value: location.getLatitude(), isStateChange: state.force )
		//sendEvent(name: "longitude", value: location.getLongitude(), isStateChange: state.force )
		}
	sendEvent(name: "latitude", value: polllatitude)
	sendEvent(name: "longitude", value: polllongitude)
    if(txtEnable == true){log.info "latitude: $polllatitude"}
    if(txtEnable == true){log.info "longitude: $polllongitude"}
}	

def poll1(){
    formatUnit()  
    def params1 = [uri: "https://api.weather.com/v2/pws/observations/current?stationId=${pollLocation}&format=json&units=${device.currentValue('formatUnit')}&apiKey=${apiKey}"]
    asynchttpGet("pollHandler1", params1)   
}
	
def pollHandler1(resp, data) {
	if(resp.getStatus() == 200 || resp.getStatus() == 207) {
		obs = parseJson(resp.data)
        if(txtEnable == true){log.debug "Response poll1 = $obs"}		// log the data returned by WU
       
    		def illume = (obs.observations.solarRadiation[0])
            if(illume){
            sendEvent(name: "illuminance", value: obs.observations.solarRadiation[0], unit: "lux")
            sendEvent(name: "solarradiation", value: obs.observations.solarRadiation[0], unit: "W")
            }
            if(!illume){
            sendEvent(name: "illuminance", value: "No Data")
            sendEvent(name: "solarradiation", value: "No Data")
            }
            sendEvent(name: "stationID", value: obs.observations.stationID[0])
            if(txtEnable == true){log.debug "stationID: $obs.observations.stationID"}
			sendEvent(name: "stationType", value: obs.observations.softwareType[0])
            if(txtEnable == true){log.debug "stationType: $obs.observations.softwareType"}
            sendEvent(name: "station_location", value: obs.observations.neighborhood[0])
            if(txtEnable == true){log.debug "station_location: $obs.observations.neighborhood"}
			sendEvent(name: "humidity", value: obs.observations.humidity[0])
            sendEvent(name: "observation_time", value: obs.observations.obsTimeLocal[0])
            sendEvent(name: "wind_degree", value: obs.observations.winddir[0])		

        if(unitFormat == "Imperial"){
            sendEvent(name: "precip_rate", value: obs.observations.imperial.precipRate[0])
            //state.todayRain = obs.observations.imperial.precipTotal[0]
            //if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: obs.observations.imperial.precipTotal[0])
			sendEvent(name: "feelsLike", value: obs.observations.imperial.windChill[0], unit: "F")
            sendEvent(name: "temperature", value: obs.observations.imperial.temp[0], unit: "F")
			sendEvent(name: "wind", value: obs.observations.imperial.windSpeed[0], unit: "mph")
            sendEvent(name: "wind_gust", value: obs.observations.imperial.windGust[0]) 
			sendEvent(name: "dewpoint", value: obs.observations.imperial.dewpt[0], unit: "F")
			sendEvent(name: "pressure", value: obs.observations.imperial.pressure[0])
			sendEvent(name: "elevation", value: obs.observations.imperial.elev[0])
			}
		if(unitFormat == "Metric"){
            sendEvent(name: "precip_rate", value: obs.observations.metric.precipRate[0])
            //state.todayRain = obs.observations.metric.precipTotal[0]
            //if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: obs.observations.metric.precipTotal[0])
			sendEvent(name: "feelsLike", value: obs.observations.metric.windChill[0], unit: "C")  
            sendEvent(name: "temperature", value: obs.observations.metric.temp[0], unit: "C")
			sendEvent(name: "wind", value: obs.observations.metric.windSpeed[0], unit: "kph")
            sendEvent(name: "wind_gust", value: obs.observations.metric.windGust[0])
			sendEvent(name: "dewpoint", value: obs.observations.metric.dewpt[0], unit: "C")
			sendEvent(name: "pressure", value: obs.observations.metric.pressure[0])	
			sendEvent(name: "elevation", value: obs.observations.metric.elev[0])
			}
		if(unitFormat == "UK Hybrid"){
            sendEvent(name: "precip_rate", value: obs.observations.uk_hybrid.precipRate[0])
            //state.todayRain = obs.observations.uk_hybrid.precipTotal[0]
            //if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: obs.observations.uk_hybrid.precipTotal[0])
			sendEvent(name: "feelsLike", value: obs.observations.uk_hybrid.windChill[0], unit: "C")
            sendEvent(name: "temperature", value: obs.observations.uk_hybrid.temp[0], unit: "C")
			sendEvent(name: "wind", value: obs.observations.uk_hybrid.windSpeed[0], unit: "mph")
            sendEvent(name: "wind_gust", value: obs.observations.uk_hybrid.windGust[0])
			sendEvent(name: "dewpoint", value: obs.observations.uk_hybrid.dewpt[0], unit: "C")
			sendEvent(name: "pressure", value: obs.observations.uk_hybrid.pressure[0])
            if(unitElevation){
            def elevation1 = obs.observations.uk_hybrid.elev[0]
            def elevation2 = elevation1.toFloat()
            def elevation = (elevation2 * 0.3048).toInteger()
			sendEvent(name: "elevation", value: elevation,unit: "m")
            }
            else {sendEvent(name: "elevation", value: obs.observations.uk_hybrid.elev[0], unit: "ft")}
			}
			//state.lastPoll = now()
} else {
        def res = resp.getStatus()
		log.warn "WU weather api did not return data from poll1 - $res"
	}
}        	

def poll2(){
    formatUnit()
    def params2 = [uri: "https://api.weather.com/v3/wx/forecast/daily/5day?geocode=${device.currentValue('latitude')},${device.currentValue('longitude')}&units=${device.currentValue('formatUnit')}&language=${device.currentValue('formatlanguage')}&format=json&apiKey=${apiKey}"]
    if(txtEnable == true){log.debug "Poll2 - latitude = ${device.currentValue('latitude')} -- longitude = ${device.currentValue('longitude')}"}
    asynchttpGet("pollHandler2", params2)
}
    
def pollHandler2(resp1, data) {
	if(resp1.getStatus() == 200 || resp1.getStatus() == 207) {
		obs1 = parseJson(resp1.data)
        if(txtEnable == true){log.debug "Response poll2 = $obs1"}		// log the data returned by WU
// Weather General Data
            sendEvent(name: "temperatureMaxToday", value: obs1.temperatureMax[0])
        	sendEvent(name: "temperatureMaxToday", value: obs1.calendarDayTemperatureMax[0])
            sendEvent(name: "temperatureMaxTomorrow", value: obs1.calendarDayTemperatureMax[1])
            sendEvent(name: "temperatureMaxDayAfterTomorrow", value: obs1.calendarDayTemperatureMax[2])
            sendEvent(name: "temperatureMinToday", value: obs1.calendarDayTemperatureMin[0])
            sendEvent(name: "temperatureMinTomorrow", value: obs1.calendarDayTemperatureMin[1])
            sendEvent(name: "temperatureMinDayAfterTomorrow", value: obs1.calendarDayTemperatureMin[2]) 
            sendEvent(name: "forcastPhraseTomorrow", value: obs1.daypart[0].wxPhraseLong[2])
            sendEvent(name: "forcastPhraseDayAfterTomorrow", value: obs1.daypart[0].wxPhraseLong[4])     
            sendEvent(name: "precipChanceTomorrow", value: obs1.daypart[0].precipChance[2])
            sendEvent(name: "precipChanceDayAfterTomorrow", value: obs1.daypart[0].precipChance[4])           
            sendEvent(name: "sunsetTimeLocal", value: obs1.sunsetTimeLocal[0])
            sendEvent(name: "sunriseTimeLocal", value: obs1.sunriseTimeLocal[0])            
            sendEvent(name: "today", value: obs1.dayOfWeek[0])
            sendEvent(name: "tomorrow", value: obs1.dayOfWeek[1])
            sendEvent(name: "dayAfterTomorrow", value: obs1.dayOfWeek[2])   
			sendEvent(name: "fCstRainTomorrow", value: obs1.daypart[0].qpf[2])
            sendEvent(name: "fCstRainDayAfterTomorrow", value: obs1.daypart[0].qpf[1])  
            sendEvent(name: "forecastShort", value: obs1.narrative[0])	   
			sendEvent(name: "forecastTomorrow", value: obs1.daypart[0].narrative[2])
			sendEvent(name: "forecastDayAfterTomorrow", value: obs1.daypart[0].narrative[4]) 
			sendEvent(name: "forecastHigh", value: obs1.temperatureMax[0])
			sendEvent(name: "forecastLow", value: obs1.temperatureMin[0])
			sendEvent(name: "moonPhase", value: obs1.moonPhase[0])
            
            if(txtEnable == true){log.info "Day or Night: " + obs1.daypart[0].dayOrNight} //daypartInitials
            if(txtEnable == true){log.info "D/N Name : " + obs1.daypart[0].daypartName} //daypartNames
            String DN
            if (obs1.daypart[0].dayOrNight[0] == null)
            {
                DN = "N"
            }
            else
            {
                DN = "D"
            }
            if(txtEnable == true){log.info "Day or Night: " + DN}
            sendEvent(name: "dayOrNight", value: DN)
        	//state.dayOrNight = (obs1.daypart[0].dayOrNight[0])
        	//if(state.dayOrNight == null){	
            if(device.currentValue('dayOrNight') == "N"){
// Weather Nightime Data
                sendEvent(name: "forecastTimeName", value: obs1.daypart[0].daypartName[1])
                sendEvent(name: "forcastPhraseToday", value: obs1.daypart[0].wxPhraseLong[1])
				sendEvent(name: "precipChanceToday", value: obs1.daypart[0].precipChance[1])
				sendEvent(name: "precipType", value: obs1.daypart[0].precipType[1])
				sendEvent(name: "cloudCover", value: obs1.daypart[0].precipChance[1])        	
				sendEvent(name: "uvDescription", value: obs1.daypart[0].uvDescription[1])
				sendEvent(name: "uvIndex", value: obs1.daypart[0].uvIndex[1])
				sendEvent(name: "thunderCategory", value: obs1.daypart[0].thunderCategory[1])
				sendEvent(name: "thunderIndex", value: obs1.daypart[0].thunderCategory[1])				
				sendEvent(name: "snowRange", value: obs1.daypart[0].snowRange[1])				
				sendEvent(name: "qpfSnow", value: obs1.daypart[0].qpfSnow[1]) 
				sendEvent(name: "fCstRainToday", value: obs1.daypart[0].qpf[1])
				sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[1])
				sendEvent(name: "weather", value: (obs1.daypart[0].narrative[1]))
				sendEvent(name: "wind_dir", value: obs1.daypart[0].windDirectionCardinal[1])
				sendEvent(name: "windPhrase", value: obs1.daypart[0].windPhrase[1])
				sendEvent(name: "windPhraseForecast", value: obs1.daypart[0].windPhrase[1])			
				sendEvent(name: "UVHarm", value: obs1.daypart[0].uvDescription[1])	     
        	}
// Weather Daytime Data
        	else {
                sendEvent(name: "forecastTimeName", value: obs1.daypart[0].daypartName[0])
				sendEvent(name: "forcastPhraseToday", value: obs1.daypart[0].wxPhraseLong[0])
				sendEvent(name: "precipChanceToday", value: obs1.daypart[0].precipChance[0])
				sendEvent(name: "precipType", value: obs1.daypart[0].precipType[0])
				sendEvent(name: "cloudCover", value: obs1.daypart[0].precipChance[0])         
				sendEvent(name: "uvDescription", value: obs1.daypart[0].uvDescription[0])
				sendEvent(name: "uvIndex", value: obs1.daypart[0].uvIndex[0])
				sendEvent(name: "thunderCategory", value: obs1.daypart[0].thunderCategory[0])
				sendEvent(name: "thunderIndex", value: obs1.daypart[0].thunderCategory[0])				
				sendEvent(name: "snowRange", value: obs1.daypart[0].snowRange[0])
				sendEvent(name: "qpfSnow", value: obs1.daypart[0].qpfSnow[0]) 
				sendEvent(name: "fCstRainToday", value: obs1.daypart[0].qpf[0])
				sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[0])
				sendEvent(name: "weather", value: (obs1.daypart[0].narrative[0]))
				sendEvent(name: "wind_dir", value: obs1.daypart[0].windDirectionCardinal[0])
				sendEvent(name: "windPhrase", value: obs1.daypart[0].windPhrase[0])
				sendEvent(name: "windPhraseForecast", value: obs1.daypart[0].windPhrase[0])			
				sendEvent(name: "UVHarm", value: obs1.daypart[0].uvDescription[0])
            }  
               
// Weather Warnings Data
			//if(state.dayOrNight == null){	
            if(device.currentValue('dayOrNight') == "N"){
				
                // state.weatherWarning = (obs1.daypart[0].qualifierPhrase[1])
				String weatherWarning = (obs1.daypart[0].qualifierPhrase[1])
                if(weatherWarning == null){sendEvent(name: "weatherWarning", value: "None")}
				else {sendEvent(name: "weatherWarning", value: weatherWarning)}	             		
				
                //state.weatherWarningCode = (obs1.daypart[0].qualifierCode[1])
				String weatherWarningCode = (obs1.daypart[0].qualifierCode[1])
                if(weatherWarningCode == null){sendEvent(name: "weatherWarningCode", value: "None")}
				else {sendEvent(name: "weatherWarningCode", value: weatherWarningCode)}
            }            
            else{	
				
                //state.weatherWarning = (obs1.daypart[0].qualifierPhrase[0])
                String weatherWarning = (obs1.daypart[0].qualifierPhrase[0])
				if(weatherWarning == null){sendEvent(name: "weatherWarning", value: "None")}
				else {sendEvent(name: "weatherWarning", value: weatherWarning)}	             		
				
                //state.weatherWarningCode = (obs1.daypart[0].qualifierCode[0])
                String weatherWarningCode = (obs1.daypart[0].qualifierCode[0])
				if(weatherWarningCode == null){sendEvent(name: "weatherWarningCode", value: "None")}
				else {sendEvent(name: "weatherWarningCode", value: weatherWarningCode)}         
				}
					
			//state.weatherWarningTommorrow = (obs1.daypart[0].qualifierPhrase[2])
            String weatherWarningTommorrow = (obs1.daypart[0].qualifierPhrase[2])
			if(weatherWarningTommorrow == null){sendEvent(name: "weatherWarningTomorrow", value: "None")}
			else {sendEvent(name: "weatherWarningTomorrow", value: weatherWarningTommorrow)}	              		
			
            //state.weatherWarningCodeTomorrow = (obs1.daypart[0].qualifierCode[2])
            String weatherWarningCodeTomorrow = (obs1.daypart[0].qualifierCode[2])
			if(weatherWarningCodeTomorrow == null){sendEvent(name: "weatherWarningCodeTomorrow", value: "None")}
			else {sendEvent(name: "weatherWarningCodeTomorrow", value: weatherWarningCodeTomorrow)}
				
			//state.weatherWarningDATomorrow = (obs1.daypart[0].qualifierPhrase[4])
            String weatherWarningDATomorrow = (obs1.daypart[0].qualifierPhrase[4])
			if(weatherWarningDATomorrow == null){sendEvent(name: "weatherWarningDATomorrow", value: "None")}
			else {sendEvent(name: "weatherWarningDATomorrow", value: weatherWarningDATomorro)}	              		
			
            //state.weatherWarningCodeDATomorrow = (obs1.daypart[0].qualifierCode[4])
            String weatherWarningCodeDATomorrow = (obs1.daypart[0].qualifierCode[4])
			if(weatherWarningCodeDATomorrow == null){sendEvent(name: "weatherWarningCodeDATomorrow", value: "None")}
			else {sendEvent(name: "weatherWarningCodeDATomorrow", value: weatherWarningCodeDATomorrow)}
            
// Weather Icons Logic
			//state.dayOrNight = (obs1.daypart[0].dayOrNight[0])
            iconURL1 = "https://github.com/dJOS1475/Hubitat_WU_Driver/raw/main/wuIcons/"

            if(useIcons){
			//if(state.dayOrNight == null){
            if(device.currentValue('dayOrNight') == "N"){
				//state.iconCode1 = (obs1.daypart[0].iconCode[1])
				sendEvent(name: "forecastTodayIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[1]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
                //state.iconCode2 = (obs1.daypart[0].iconCode[2])
				sendEvent(name: "forecastTomorrowIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[2]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
				//state.iconCode3 = (obs1.daypart[0].iconCode[4])		
				sendEvent(name: "forecastDayAfterTomorrowIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[4]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
				}				
			else{	
				//state.iconCode1 = (obs1.daypart[0].iconCode[0])
				sendEvent(name: "forecastTodayIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[0]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
				//state.iconCode2 = (obs1.daypart[0].iconCode[2])
				sendEvent(name: "forecastTomorrowIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[2]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
				//state.iconCode3 = (obs1.daypart[0].iconCode[4])		
				sendEvent(name: "forecastDayAfterTomorrowIcon", value: "<img src='" + iconURL1 + (obs1.daypart[0].iconCode[4]) + ".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>")
				}
            //iconURL1 = "https://github.com/dJOS1475/Hubitat_WU_Driver/raw/main/wuIcons/"
            //state.icon1 = "<img src='" +iconURL1 +state.iconCode1 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			//state.icon2 = "<img src='" +iconURL1 +state.iconCode2 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			//state.icon3 = "<img src='" +iconURL1 +state.iconCode3 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			
            //sendEvent(name: "forecastTodayIcon", value: state.icon1, isStateChange: state.force )
			//sendEvent(name: "forecastTomorrowIcon", value: state.icon2, isStateChange: state.force )
			//sendEvent(name: "forecastDayAfterTomorrowIcon", value: state.icon3, isStateChange: state.force )
			} 
        } 
      else {
        def res1 = resp1.getStatus()
		log.warn "WU weather api did not return data from poll2 - $res1"
	}  
}

def poll3(){
    formatUnit()  
    def params3 = [uri: "https://api.weather.com/v2/pws/dailysummary/7day?stationId=${pollLocation}&format=json&units=${device.currentValue('formatUnit')}&apiKey=${apiKey}"]
    asynchttpGet("pollHandler3", params3)
}

def pollHandler3(resp1, data) {
	if(resp1.getStatus() == 200 || resp1.getStatus() == 207) {
		obs2 = parseJson(resp1.data)
        if(txtEnable == true){log.debug "Response poll3 = $obs2"}		// log the data returned by WU

        def rain7List

        if(unitFormat == "Imperial")
        {
            rain7List = (obs2.summaries.imperial.precipTotal) as String // .toString()
            if(txtEnable == true){log.info "7DayRain: $obs2.summaries.imperial.precipTotal[0]"}
        }
        else
            if(unitFormat == "Metric")
            {
                rain7List = (obs2.summaries.metric.precipTotal) as String // .toString()
                if(txtEnable == true){log.info "7DayRain: $obs2.summaries.metric.precipTotal[0]"}
            }
            else
                if(unitFormat == "UK Hybrid")
                {
                    rain7List = (obs2.summaries.uk_hybrid.precipTotal) as String // .toString()
                    if(txtEnable == true){log.info "7DayRain: $obs2.summaries.uk_hybrid.precipTotal[0]"}
                }
        
        def day7List = []
        day7List = (obs2.summaries.obsTimeLocal)
        if(txtEnable == true){log.info "day7List: $day7List"}
        
        int daysCount = day7List.size()
        sendEvent(name: "rainHistoryDays", value: daysCount)
        if(txtEnable == true){log.info "Count: $daysCount"}
        
        if(txtEnable == true){log.info "Orig: $rain7List"}
        rain7List = rain7List.replace("[","")
        rain7List = rain7List.replace("]","")
        if(txtEnable == true){log.info "Repl: $rain7List"}
        
        try{
            def BigDecimal bd6
                if (daysCount == 7)
            {
                (day6, day5, day4, day3, day2, day1, day0) = rain7List.tokenize( ',' )
                if(txtEnable == true){log.info "Day 6: $day6, Day 5: $day5, Day 4: $day4, Day 3: $day3, Day 2: $day2, Day 1: $day1, Day 0: $day0"}
    
                if (day6.trim() != 'null' && day6.trim() != '')
                {
                    if (day6 != null) {bd6 = day6.toBigDecimal()} else {bd6 = 0}
                }
                else
                {
                    bd6 = 0.00
                }
            }
            if (daysCount == 6)
            {
                (day5, day4, day3, day2, day1, day0) = rain7List.tokenize( ',' )
                if(txtEnable == true){log.info "Day 5: $day5, Day 4: $day4, Day 3: $day3, Day 2: $day2, Day 1: $day1, Day 0: $day0"}
    
            bd6 = 0.00
            }
        
            def BigDecimal bd5
            if (day5.trim() != 'null' && day5.trim() != '')
            {
                if (day5 != null) {bd5 = day5.toBigDecimal()} else {bd5 = 0}
            }
            else
            {
                bd5 = 0.00
            }
    
            def BigDecimal bd4
            if (day4.trim() != 'null' && day4.trim() != '')
            {
                if (day4 != null) {bd4 = day4.toBigDecimal()} else {bd4 = 0}
            }
            else
            {
                bd4 = 0.00
            }
    
            def BigDecimal bd3
            if (day3.trim() != 'null' && day3.trim() != '')
            {
                if (day3 != null) {bd3 = day3.toBigDecimal()} else {bd3 = 0}
            }
            else
            {
            bd3 = 0.00
            }
    
            def BigDecimal bd2
            if (day2.trim() != 'null' && day2.trim() != '')
            {
                if (day2 != null) {bd2 = day2.toBigDecimal()} else {bd2 = 0}
            }
            else
            {
                bd2 = 0.00
            }
    
            def BigDecimal bd1
            if (day1.trim() != 'null' && day1.trim() != '')
            {
                if (day1 != null) {bd1 = day1.toBigDecimal()} else {bd1 = 0}
            }
            else
            {
                bd1 = 0.00
            }
    
            def BigDecimal bd0
            if (day0.trim() != 'null' && day0.trim() != '')
            {
                if (day0 != null) {bd0 = day0.toBigDecimal()} else {bd0 = 0}
            }
            else
            {
                bd0 = 0.00
            }

            if(txtEnable == true){log.info "$bd6, $bd5, $bd4, $bd3, $bd2, $bd1, $bd0"}
            BigDecimal bdAll7 = bd6 + bd5 + bd4 + bd3 + bd2 + bd1 + bd0        
            String bdString7 = String.valueOf(bdAll7)
            if(txtEnable == true){log.info "7Days: " + bdString7}
            sendEvent(name: "precip_Last7Days", value: bdString7)        
            
            BigDecimal bdAll5 = bd5 + bd4 + bd3 + bd2 + bd1 + bd0       
            String bdString5 = String.valueOf(bdAll5)
            if(txtEnable == true){log.info "5Days: " + bdString5}
            sendEvent(name: "precip_Last5Days", value: bdString5)
            
            BigDecimal bdAll3 = bd3 + bd2 + bd1 + bd0        
            String bdString3 = String.valueOf(bdAll3)
            if(txtEnable == true){log.info "3Days: " + bdString3}
            sendEvent(name: "precip_Last3Days", value: bdString3)
            
            BigDecimal bdYesterday = bd1
            String bdStringYesterday = String.valueOf(bdYesterday)
            if(txtEnable == true){log.info "Yesterday: " + bdStringYesterday}
            sendEvent(name: "precip_Yesterday", value: bdStringYesterday)
        }
        catch(Exception e)
        {
            log.warn "WU did not return all rain values on this attempt. Missing at least 1 day's rain. Will retry on next interval."
            log.warn "error: $e"
        }   
    } 
    else {
        def res1 = resp1.getStatus()
		log.warn "WU weather api did not return data from poll3 - $res1"
	}  
}

// HTML Tiles Logic
def updateTile1() {
	if(txtEnable == true){log.debug "updateTile1 called"}		// log the data returned by WU//	
	htmlToday ="<div style='line-height:1.0; font-size:1em;'><br>Weather for ${device.currentValue('station_location')}<br></div>"
	htmlToday +="<div style='line-height:50%;'><br></div>"
	htmlToday +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>Forecast for ${device.currentValue('today')}<br></div>"
	htmlToday +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('forecastToday')}<br></div>"
	sendEvent(name: "htmlToday", value: "$htmlToday")
	if(txtEnable == true){log.debug "htmlToday contains ${htmlToday}"}		// log the data returned by WU//	
	if(txtEnable == true){log.debug "${htmlToday.length()}"}		// log the data returned by WU//	
	}
	
def updateTile2() {
	if(txtEnable == true){log.debug "updateTile2 called"}		// log the data returned by WU//	
	htmlTomorrow ="<div style='line-height:1.0; font-size:1em;'><br>Weather for ${device.currentValue('station_location')}<br></div>"
	htmlTomorrow +="<div style='line-height:50%;'><br></div>"
	htmlTomorrow +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>Forecast for ${device.currentValue('tomorrow')}<br></div>"
	htmlTomorrow +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('forecastTomorrow')}<br></div>"
	sendEvent(name: "htmlTomorrow", value: "$htmlTomorrow")
	if(txtEnable == true){log.debug "htmlTomorrow contains ${htmlTomorrow}"}		// log the data returned by WU//	
	if(txtEnable == true){log.debug "${htmlTomorrow.length()}"}		// log the data returned by WU//	
	}
	
def updateTile3() {
	if(txtEnable == true){log.debug "updateTile3 called"}		// log the data returned by WU//		
	htmlDayAfterTomorrow ="<div style='line-height:1.0; font-size:1em;'><br>Weather for ${device.currentValue('station_location')}<br></div>"
	htmlDayAfterTomorrow +="<div style='line-height:50%;'><br></div>"
	htmlDayAfterTomorrow +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>Forecast for ${device.currentValue('dayAfterTomorrow')}<br></div>"
	htmlDayAfterTomorrow +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('forecastDayAfterTomorrow')}<br></div>"
	sendEvent(name: "htmlDayAfterTomorrow", value: "$htmlDayAfterTomorrow")
	if(txtEnable == true){log.debug "htmlDayAfterTomorrow contains ${htmlDayAfterTomorrow}"}		// log the data returned by WU//	
	if(txtEnable == true){log.debug "${htmlDayAfterTomorrow.length()}"}		// log the data returned by WU//
	}

def updateTile4() {
	if(txtEnable == true){log.debug "updateTile4 called"}		// log the data returned by WU//		
	htmlWarnings ="<div style='line-height:1.0; font-size:1em;'><br>Weather Warnings for ${device.currentValue('station_location')}<br></div>"
	htmlWarnings +="<div style='line-height:50%;'><br></div>"
	htmlWarnings +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('today')}: ${device.currentValue('weatherWarning')}<br></div>"
	htmlWarnings +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('tomorrow')}: ${device.currentValue('weatherWarningTomorrow')}<br></div>"
	htmlWarnings +="<div style='line-height:1.0; font-size:0.75em; text-align: left;'><br>${device.currentValue('dayAfterTomorrow')}: ${device.currentValue('weatherWarningDATomorrow')}<br></div>"
	sendEvent(name: "htmlWarnings", value: "$htmlWarnings")
	if(txtEnable == true){log.debug "htmlWarnings contains ${htmlWarnings}"}		// log the data returned by WU//	
	if(txtEnable == true){log.debug "${htmlWarnings.length()}"}		// log the data returned by WU//
	}

// HTML 3 Day Forecast Tile Logic
def wu3dayfcst() {
    if(txtEnable == true){log.info "3-Day Forecast: $threedayforecast"}

    String sTD='<td>'
    String sTR='<tr><td>'
   	String my3day
    
    if(txtEnable == true){log.info "3-Day Forecast: $threedayforecast"}
    if(threedayforecast)
    {
        String iconSunrise = '<img src=https://tinyurl.com/icnqz/wsr.png>'
        String iconSunset = '<img src=https://tinyurl.com/icnqz/wss.png>'
        String degreeSign
        String sunriseLocal
        String strSunrise
        String sunsetLocal
        String strSunset
        String strRainToday = ''
        String lastPoll
        String s1stHeader
        String s2ndHeader
        String s3rdHeader
        
        if(unitFormat == "Imperial")
        {
            degreeSign = "°F"
        }
        else
            if(unitFormat == "Metric")
            {
                degreeSign = "°C"
            }
            else
                if(unitFormat == "UK Hybrid")
                {
                    degreeSign = "°C"
                }

        //if(txtEnable == true){log.info "state.unit = $state.unit"}
        if(txtEnable == true){log.info "Unit Format = $unitFormat"}
        if(txtEnable == true){log.info "DegreeSign = $degreeSign"}

        int Tstart = "${device.currentValue('sunriseTimeLocal')}".indexOf('T')
        int Tstop1 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstart)
        int Tstop2 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstop1+1)
        sunriseLocal = "${device.currentValue('sunriseTimeLocal')}".substring(Tstart+1, Tstop2)
        strSunrise = "${convert24to12(sunriseLocal)}" 
        if(txtEnable == true){log.info "Sunrise = $strSunrise"}
    
        Tstart = "${device.currentValue('sunsetTimeLocal')}".indexOf('T')
        Tstop1 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstart)
        Tstop2 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstop1+1)
        sunsetLocal = "${device.currentValue('sunsetTimeLocal')}".substring(Tstart+1, Tstop2)
        strSunset = "${convert24to12(sunsetLocal)}"
        if(txtEnable == true){log.info "Sunset = $strSunset"}
    
        //("${device.currentValue('precip_today')}" != '')
        BigDecimal rainToday
        if ("${device.currentValue('precip_today')}")
        {
            rainToday = "${device.currentValue('precip_today')}".toBigDecimal()
            if(rainToday > 0.00)
            {
                strRainToday = ' / ' + rainToday.toString()
            }
        }

        if(txtEnable == true){log.info "rainToday = $rainToday"}
    
        lastPoll = convert24to12("${device.currentValue('lastPollTime')}")
    
    	my3day = '<table >'
        my3day += '<TR>' 
    	my3day += '<th>' + "<B> ${device.currentValue('station_location')}</B>" + '</th>'
    	my3day += '<th style="min-width:5%"></th>'
    	my3day += '<th>' + "${device.currentValue("forecastTimeName")}" + '</th>'
    	my3day += '<th style="min-width:5%"></th>'
    	my3day += '<th>' + "${device.currentValue('tomorrow')}" + '</th>'
    	my3day += '<th style="min-width:5%"></th>'
    	my3day += '<th>' + "${device.currentValue('dayAfterTomorrow')}" + '</th>'
        my3day += sTR
        my3day += "Now " + "${device.currentValue('temperature')} " + degreeSign + "<br>Feels ${device.currentValue('feelsLike')} "+ degreeSign + "<br>Humidity ${device.currentValue('humidity')}" + '%'
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('forecastTodayIcon')}" 
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('forecastTomorrowIcon')}" 
	    my3day += sTD
    	my3day += sTD + "${device.currentValue('forecastDayAfterTomorrowIcon')}"
    	my3day += sTR
    	my3day += ""
	    my3day += sTD
    	my3day += sTD + "${device.currentValue('forcastPhraseToday')}"
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('forcastPhraseTomorrow')}"
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('forcastPhraseDayAfterTomorrow')}"
    	my3day += sTR
    	my3day += 'High/Low'
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('temperatureMaxToday')}" + degreeSign + ' ' + "${device.currentValue('temperatureMinToday')}" + degreeSign
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('temperatureMaxTomorrow')}" + degreeSign + ' ' + "${device.currentValue('temperatureMinTomorrow')}" + degreeSign
	    my3day += sTD
    	my3day += sTD + "${device.currentValue('temperatureMaxDayAfterTomorrow')}" + degreeSign + ' ' + "${device.currentValue('temperatureMinDayAfterTomorrow')}" + degreeSign 
    	my3day += sTR
    	my3day += 'Chance Precip' 
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('precipChanceToday')}" + "%" + strRainToday 
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('precipChanceTomorrow')}" + "%" 
    	my3day += sTD
    	my3day += sTD + "${device.currentValue('precipChanceDayAfterTomorrow')}" + "%" 
        my3day += '<tr> <td colspan="7">'  //blank line

        if (rainhistory) 
        {
            int totalRainDays = device.currentValue('rainHistoryDays')
            if(totalRainDays == 7)
            {
                s1stHeader = "Last 3 Days:"
                s2ndHeader = "Last 5 Days:"
                s3rdHeader = "Last 7 Days:"
            }
            else
            {
                s1stHeader = "Last 2 Days:"
                s2ndHeader = "Last 4 Days:"
                s3rdHeader = "Last 6 Days:"
            }
            
            if(txtEnable == true){log.info "Rain History Days: $raindaysdisplay"}
            switch(raindaysdisplay) {        
                case "No selection": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                case "None": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                 case "Yesterday": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + 'Yesterday:' + " ${device.currentValue('precip_Yesterday')} " + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                 case "Last 2/3-Days": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + s1stHeader + " ${device.currentValue('precip_Last3Days')} " + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                 case "Last 4/5-Days": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + s2ndHeader + " ${device.currentValue('precip_Last5Days')} " + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                 case "Last 6/7-Days": 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + s3rdHeader + " ${device.currentValue('precip_Last7Days')} " + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
                 default: 
                    my3day += '<tr style="font-size:75%"> <td colspan="7">' + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
                    break; 
            }
        }
        else
        {
            my3day += '<tr style="font-size:75%"> <td colspan="7">' + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
        }
    	my3day += '</table>'

        if(txtEnable == true){log.info 'html3dayfcst length: (' + my3day.length() + ')'}

        if(my3day.length() > 1024) {
		    log.error('Too much data to display.</br></br>Current threedayfcstTile length (' + my3day.length() + ') exceeds maximum tile length by ' + (my3day.length() - 1024).toString()  + ' characters.')
            my3day = '<table>' + sTR + 'Error! Tile greater than 1024 characters. ' + sTR + my3day.length() + ' exceeds maximum tile length by ' + (my3day.length() - 1024).toString() + ' characters.' + sTR + 'Replacing 3 day with todays forecast<br>' + "${device.currentValue('htmlToday')}" + '</table>'
    	}
    }
    else
    {
        my3day = '<table>' + sTR + 'Not Configured!' + '</table>'
    }
	sendEvent(name: 'html3dayfcst', value: my3day.take(1024))

}

// HTML Tiles Logic
def rainTile() {
    
   	String htmlRainTile
    String s1stHeader
    String s2ndHeader
    String s3rdHeader
    
    int totalRainDays = device.currentValue('rainHistoryDays')
    if(totalRainDays == 7)
    {
        s1stHeader = "Last 3 Days:"
        s2ndHeader = "Last 5 Days:"
        s3rdHeader = "Last 7 Days:"
    }
    else
    {
        s1stHeader = "Last 2 Days:"
        s2ndHeader = "Last 4 Days:"
        s3rdHeader = "Last 6 Days:"
    }

    if(txtEnable == true){log.debug "rainTile called"}
    htmlRainTile ="<table>"
    htmlRainTile +='<tr style="font-size:80%"><td>' +  "${device.currentValue('station_location')}<br>$totalRainDays Day Rain History"
    if(rainhistory)
    {
        htmlRainTile +='<tr style="font-size:85%"><td>Yesterday:' + " ${device.currentValue('precip_Yesterday')}"
        htmlRainTile +='<tr style="font-size:85%"><td>' + s1stHeader + " ${device.currentValue('precip_Last3Days')}"
        htmlRainTile +='<tr style="font-size:85%"><td>' + s2ndHeader + " ${device.currentValue('precip_Last5Days')}"
        htmlRainTile +='<tr style="font-size:85%"><td>' + s3rdHeader + " ${device.currentValue('precip_Last7Days')}"
        htmlRainTile +='<tr style="font-size:85%"><td> &nbsp;&nbsp;&nbsp;'  //blank line
    	htmlRainTile +='</table>'
    }
    else
    {
        htmlRainTile +='<tr style="font-size:95%"><td>Not Configured!' + '</table>'
    }

    sendEvent(name: "htmlRainTile", value: "$htmlRainTile")
  	if(txtEnable == true){log.debug "htmlRainTile contains ${htmlRainTile}"}		        // log the data returned by WU//	
    if(txtEnable == true){log.debug "htmlRainTile length: ${htmlRainTile.length()}"}		// log the data returned by WU//

}

String convert24to12(String input )
{
    if ( input.indexOf(":") == -1 )  
        throw ("")

    final String []temp = input.split(":")

    if ( temp.size() != 2 )
        throw ("")  // Add your throw code
                    // This does not support time string with seconds

    int h = temp[0] as int  // if h or m is not a number then exception
    int m = temp[1] as int  // java.lang.NumberFormatException will be raised
                            // that can be cached or just terminate the program
    String dn

    if ( h < 0 || h > 23 )
        throw("")  // add your own throw code
                   // hour can't be less than 0 or larger than 24

    if ( m < 0 || m > 59 )
        throw("")  // add your own throw code 
                   // minutes can't be less than 0 or larger than 60
    
    String mPad = ""
    String strM = m.toString()
    if ( strM.length() == 1 )
        mPad = "0" // minutes less the 1 char append a zero
    
    if ( h == 0 ){
        h = 12
        dn = "AM"
    } else if ( h == 12 ) {
        dn = "PM"
    } else if ( h > 12 ) {
        h = h - 12
        dn = "PM"
    } else {
        dn = "AM"
    }

    return h.toString() + ":" + mPad + m.toString() + " " + dn.toString()
}
	
def logsOff() {
	log.warn "Debug logging disabled..."
	device.updateSetting("txtEnable", [value: "false", type: "bool"])}
