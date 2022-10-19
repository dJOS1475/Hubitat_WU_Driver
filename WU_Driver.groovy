		
/**
 * Wunderground Driver
 *
 *  Maintained by Derek Osborn
 *
 *  This driver was originally written by @mattw01 and @Cobra
 *  Modified and fixed by @dJOS
 *  Additional contributions by @thebearmay @sburke781 @Busthead @swade
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
 *  Last Update 10/18/2022
 *
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

metadata {
    definition (name: "Wunderground Driver", namespace: "dJOS", author: "Derek Osborn", importUrl: "https://raw.githubusercontent.com/dJOS1475/Hubitat_WU_Driver/main/WU_Driver.groovy") {
        capability "Actuator"
        capability "Sensor"
        capability "Temperature Measurement"
        capability "Illuminance Measurement"
        capability "Relative Humidity Measurement"
        
//       command "dayRainChange"     // test code **************
//       command "clearRainHistory"  // test code  *************

        command "forceUpdateOn"
        command "forceUpdateOff"
        command "poll"
        command "forcePoll"
 	    command "resetPollCount"
 	    
 	    attribute "html3dayfcst", "string"
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
        attribute "moonIllumination", "number"
        attribute "latitude", "decimal"
		attribute "longitude", "decimal"
 		attribute "DriverAuthor", "string"
        attribute "DriverVersion", "string"
		attribute "humidity", "number"
		attribute "station_location", "string"
        attribute "elevation", "number"
        attribute "rainYesterday", "number"
        attribute "rainDayBeforeYesterday", "number"
        attribute "lastUpdateCheck", "string"
        attribute "lastPollTime", "string"
        attribute "cloudCover", "number"
        attribute "weatherWarning", "string"
        attribute "weatherWarningCode", "string"
        attribute "weatherWarningTomorrow", "string"
        attribute "weatherWarningCodeTomorrow", "string"
		attribute "weatherWarningDATomorrow", "string"
        attribute "weatherWarningCodeDATomorrow", "string"
     
        
    }
    preferences() {
        section("Query Inputs"){
            input "apiKey", "text", required: true, title: "API Key"
            input "pollLocation", "text", required: true, title: "Station ID"
			input "unitFormat", "enum", required: true, title: "Unit Format",  options: ["Imperial", "Metric", "UK Hybrid"]
            if(unitFormat == "UK Hybrid"){input "unitElevation", "bool", required: false, title: "Use Metric for elevation (m)", defaultValue: false}
            input "language", "enum", required: true, title: "Language",  options: ["US", "GB"], defaultValue: US
            input "useIcons", "bool", required: false, title: "Use WU Icons (Optional)", defaultValue: true
			if(useIcons){
			input "iconHeight1", "text", required: true, title: "Icon Height", defaultValue: 100
			input "iconWidth1", "text", required: true, title: "Icon Width", defaultValue: 100}			
            input "pollIntervalLimit", "number", title: "Poll Interval Limit:", required: true, defaultValue: 1
            input "autoPoll", "bool", required: false, title: "Enable Auto Poll"
            input "pollInterval", "enum", title: "Auto Poll Interval:", required: false, defaultValue: "5 Minutes", options: ["5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "1 Hour", "3 Hours"]
            input "txtEnable", "bool", title: "Enable descriptionText logging", required: false, defaultValue: false
            input "cutOff", "time", title: "New Day Starts", required: true, defaultValue: "00:01"
			
        }
    }
}

def updated() {
    log.debug "updated called"
    unschedule()
    state.NumOfPolls = 0
    forcePoll()
    def pollIntervalCmd = (settings?.pollInterval ?: "5 Minutes").replace(" ", "")
    if(autoPoll)
        "runEvery${pollIntervalCmd}"(pollSchedule)
    
     def changeOver = cutOff
    schedule(changeOver, resetPollCount)
    schedule(changeOver, dayRainChange)
    
    if(txtEnable){runIn(1800, logsOff)}
}


def forceUpdateOn(){
    state.force = true 
    if(txtEnable == true){log.info "Force attribute refresh set to 'true'"}
}
def forceUpdateOff(){
    state.force = null
    if(txtEnable == true){log.info "Force attribute refresh set to 'false'"}
}

def resetPollCount(){
	state.NumOfPolls = -1
    if(txtEnable == true){log.info "Poll counter reset.."}
forcePoll()

}

def clearRainHistory(){
    state.dayBeforeYesterdayRain = 0
    state.yesterdayRain = 0   
    sendEvent(name: "rainYesterday", value: state.yesterdayRain, isStateChange: true )  
    sendEvent(name: "rainDayBeforeYesterday", value: state.dayBeforeYesterdayRain, isStateChange: true )    
        }
    
def dayRainChange(){
	state.rainToday = (location.todayRain) 
	state.dayBeforeYesterdayRain = state.yesterdayRain
	state.yesterdayRain = state.rainToday 
	sendEvent(name: "rainYesterday", value: state.yesterdayRain, isStateChange: state.force )  
	sendEvent(name: "rainDayBeforeYesterday", value: state.dayBeforeYesterdayRain, isStateChange: state.force )    
}



def pollSchedule(){forcePoll()}
def parse(String description) {
}

def poll() {
	if(now() - state.lastPoll > (pollIntervalLimit * 60000))
        forcePoll()
    else{log.debug "Poll called before interval threshold was reached"}
}



def formatUnit(){
	if(unitFormat == "Imperial"){
		state.unit = "e"
        if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "Metric"){
		state.unit = "m"
        if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "UK Hybrid"){
		state.unit = "h"
        if(txtEnable == true){log.info "state.unit = $state.unit"}
	}
	if(language == "US"){
		state.languagef = "en-US"
		if(txtEnable == true){log.info "state.languagef = $state.languagef"}
	}
	if(language == "GB"){
		state.languagef = "en-GB"
		if(txtEnable == true){log.info "state.languagef = $state.languagef"}
	}
		
	
	
}
def forcePoll(){
    if(txtEnable == true){log.debug "WU: Poll called"}
    state.NumOfPolls = (state.NumOfPolls) + 1
    sendEvent(name: "pollsSinceReset", value: state.NumOfPolls, isStateChange: state.force )
	poll1()
    pauseExecution(5000)
	poll2()
	pauseExecution(5000)
    updateTile1()
    updateTile2()
    updateTile3()
    updateTile4()
    wu3dayfcst()
    def date = new Date()
	        state.LastTime1 = date.format('HH:mm', location.timeZone)
            sendEvent(name: "lastPollTime", value: state.LastTime1)
	
}
	
def pollHandler1(resp, data) {
	if(resp.getStatus() == 200 || resp.getStatus() == 207) {
		obs = parseJson(resp.data)
        if(txtEnable == true){log.debug "Response Data1 = $obs"}		// log the data returned by WU
       
    		def illume = (obs.observations.solarRadiation[0])
            if(illume){
            sendEvent(name: "illuminance", value: obs.observations.solarRadiation[0], unit: "lux", isStateChange: state.force )
            sendEvent(name: "solarradiation", value: obs.observations.solarRadiation[0], unit: "W", isStateChange: state.force )
            }
            if(!illume){
            sendEvent(name: "illuminance", value: "No Data", isStateChange: state.force )
            sendEvent(name: "solarradiation", value: "No Data", isStateChange: state.force )
            }
            sendEvent(name: "stationID", value: obs.observations.stationID[0], isStateChange: state.force )
			sendEvent(name: "stationType", value: obs.observations.softwareType[0], isStateChange: state.force )
            sendEvent(name: "station_location", value: obs.observations.neighborhood[0], isStateChange: state.force )
			sendEvent(name: "humidity", value: obs.observations.humidity[0], isStateChange: state.force )
            sendEvent(name: "observation_time", value: obs.observations.obsTimeLocal[0], isStateChange: state.force )
            sendEvent(name: "wind_degree", value: obs.observations.winddir[0], isStateChange: state.force )		
			state.latt1 = (location.getLatitude())
			state.long1 = (location.getLongitude())
            def latt1Saved = (location.getLatitude())
            def long1Saved = (location.getLongitude()) 
            if(latt1Saved == null){sendLocationEvent(name: "lattSaved", value: state.latt1)}
            if(long1Saved == null){sendLocationEvent(name: "longSaved", value: state.long1)}                              
            if(txtEnable == true){log.debug "Poll1 - state.latt1 = $state.latt1 -- state.long1 = $state.long1 -- latt1Saved = $latt1Saved -- long1Saved = $long1Saved  "}
			sendEvent(name: "latitude", value: state.latt1, isStateChange: state.force )
			sendEvent(name: "longitude", value: state.long1, isStateChange: state.force )
        if(unitFormat == "Imperial"){
            sendEvent(name: "precip_rate", value: obs.observations.imperial.precipRate[0], isStateChange: state.force )
            state.todayRain = obs.observations.imperial.precipTotal[0]
            if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: state.todayRain, isStateChange: state.force )
			sendEvent(name: "feelsLike", value: obs.observations.imperial.windChill[0], unit: "F", isStateChange: state.force )
            sendEvent(name: "temperature", value: obs.observations.imperial.temp[0], unit: "F", isStateChange: state.force )
			sendEvent(name: "wind", value: obs.observations.imperial.windSpeed[0], unit: "mph", isStateChange: state.force )
            sendEvent(name: "wind_gust", value: obs.observations.imperial.windGust[0], isStateChange: state.force ) 
			sendEvent(name: "dewpoint", value: obs.observations.imperial.dewpt[0], unit: "F", isStateChange: state.force )
			sendEvent(name: "pressure", value: obs.observations.imperial.pressure[0], isStateChange: state.force )
			sendEvent(name: "elevation", value: obs.observations.imperial.elev[0], isStateChange: state.force )
			}
		if(unitFormat == "Metric"){
            sendEvent(name: "precip_rate", value: obs.observations.metric.precipRate[0], isStateChange: state.force )
            state.todayRain = obs.observations.metric.precipTotal[0]
            if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: state.todayRain, isStateChange: state.force )
			sendEvent(name: "feelsLike", value: obs.observations.metric.windChill[0], unit: "C", isStateChange: state.force )  
            sendEvent(name: "temperature", value: obs.observations.metric.temp[0], unit: "C", isStateChange: state.force )
			sendEvent(name: "wind", value: obs.observations.metric.windSpeed[0], unit: "kph", isStateChange: state.force )
            sendEvent(name: "wind_gust", value: obs.observations.metric.windGust[0], isStateChange: state.force )
			sendEvent(name: "dewpoint", value: obs.observations.metric.dewpt[0], unit: "C", isStateChange: state.force )
			sendEvent(name: "pressure", value: obs.observations.metric.pressure[0], isStateChange: state.force )	
			sendEvent(name: "elevation", value: obs.observations.metric.elev[0], isStateChange: state.force )
			}
		if(unitFormat == "UK Hybrid"){
            sendEvent(name: "precip_rate", value: obs.observations.uk_hybrid.precipRate[0], isStateChange: state.force )
            state.todayRain = obs.observations.uk_hybrid.precipTotal[0]
            if(state.todayRain != null){sendLocationEvent(name: "todayRain", value: state.todayRain)}
            sendEvent(name: "precip_today", value: state.todayRain , isStateChange: state.force )
			sendEvent(name: "feelsLike", value: obs.observations.uk_hybrid.windChill[0], unit: "C", isStateChange: state.force )
            sendEvent(name: "temperature", value: obs.observations.uk_hybrid.temp[0], unit: "C", isStateChange: state.force )
			sendEvent(name: "wind", value: obs.observations.uk_hybrid.windSpeed[0], unit: "mph", isStateChange: state.force )
            sendEvent(name: "wind_gust", value: obs.observations.uk_hybrid.windGust[0], isStateChange: state.force )
			sendEvent(name: "dewpoint", value: obs.observations.uk_hybrid.dewpt[0], unit: "C", isStateChange: state.force )
			sendEvent(name: "pressure", value: obs.observations.uk_hybrid.pressure[0], isStateChange: state.force )
            if(unitElevation){
            def elevation1 = obs.observations.uk_hybrid.elev[0]
            def elevation2 = elevation1.toFloat()
            def elevation = (elevation2 * 0.3048).toInteger()
			sendEvent(name: "elevation", value: elevation,unit: "m", isStateChange: state.force )
            }
            else {sendEvent(name: "elevation", value: obs.observations.uk_hybrid.elev[0], unit: "ft", isStateChange: state.force )}
			}
			state.lastPoll = now()
} else {
        def res = resp.getStatus()
		log.error "WU weather api did not return data from poll1 - $res"
	}
}        	

	
def poll1(){
    formatUnit()  
    def params1 = [uri: "https://api.weather.com/v2/pws/observations/current?stationId=${pollLocation}&format=json&units=${state.unit}&apiKey=${apiKey}"]
    asynchttpGet("pollHandler1", params1)   
}

def poll2(){
    formatUnit()
    state.latt1 = (location.getLatitude())
    state.long1 = (location.getLongitude())

    def params2 = [uri: "https://api.weather.com/v3/wx/forecast/daily/5day?geocode=${state.latt1},${state.long1}&units=${state.unit}&language=${state.languagef}&format=json&apiKey=${apiKey}"]
    if(txtEnable == true){log.debug "Poll2 - state.latt1 = $state.latt1 -- state.long1 = $state.long1"}
    asynchttpGet("pollHandler2", params2)
}

def pollHandler2(resp1, data) {
	if(resp1.getStatus() == 200 || resp1.getStatus() == 207) {
		obs1 = parseJson(resp1.data)
        if(txtEnable == true){log.debug "Response Data2 = $obs1"}		// log the data returned by WU
// Weather General Data
        	sendEvent(name: "temperatureMaxToday", value: obs1.calendarDayTemperatureMax[0], isStateChange: state.force )
            sendEvent(name: "temperatureMaxTomorrow", value: obs1.calendarDayTemperatureMax[1], isStateChange: state.force )
            sendEvent(name: "temperatureMaxDayAfterTomorrow", value: obs1.calendarDayTemperatureMax[2], isStateChange: state.force )
            sendEvent(name: "temperatureMinToday", value: obs1.calendarDayTemperatureMin[0], isStateChange: state.force )
            sendEvent(name: "temperatureMinTomorrow", value: obs1.calendarDayTemperatureMin[1], isStateChange: state.force )
            sendEvent(name: "temperatureMinDayAfterTomorrow", value: obs1.calendarDayTemperatureMin[2], isStateChange: state.force ) 
            sendEvent(name: "forcastPhraseTomorrow", value: obs1.daypart[0].wxPhraseLong[2], isStateChange: state.force )
            sendEvent(name: "forcastPhraseDayAfterTomorrow", value: obs1.daypart[0].wxPhraseLong[4], isStateChange: state.force )     
            sendEvent(name: "precipChanceTomorrow", value: obs1.daypart[0].precipChance[2], isStateChange: state.force )
            sendEvent(name: "precipChanceDayAfterTomorrow", value: obs1.daypart[0].precipChance[4], isStateChange: state.force )           
            sendEvent(name: "sunsetTimeLocal", value: obs1.sunsetTimeLocal[0], isStateChange: state.force )
            sendEvent(name: "sunriseTimeLocal", value: obs1.sunriseTimeLocal[0], isStateChange: state.force )            
            sendEvent(name: "today", value: obs1.dayOfWeek[0], isStateChange: state.force )
            sendEvent(name: "tomorrow", value: obs1.dayOfWeek[1], isStateChange: state.force )
            sendEvent(name: "dayAfterTomorrow", value: obs1.dayOfWeek[2], isStateChange: state.force )   
			sendEvent(name: "fCstRainTomorrow", value: obs1.daypart[0].qpf[2], isStateChange: state.force )
            sendEvent(name: "fCstRainDayAfterTomorrow", value: obs1.daypart[0].qpf[1], isStateChange: state.force )  
            sendEvent(name: "forecastShort", value: obs1.narrative[0], isStateChange: state.force )	   
			sendEvent(name: "forecastTomorrow", value: obs1.daypart[0].narrative[2], isStateChange: state.force )
			sendEvent(name: "forecastDayAfterTomorrow", value: obs1.daypart[0].narrative[4], isStateChange: state.force ) 
			sendEvent(name: "forecastHigh", value: obs1.temperatureMax[0], isStateChange: state.force )
			sendEvent(name: "forecastLow", value: obs1.temperatureMin[0], isStateChange: state.force )
			sendEvent(name: "moonPhase", value: obs1.moonPhase[0], isStateChange: state.force )
            
        	state.dayOrNight = (obs1.daypart[0].dayOrNight[0])
        	if(state.dayOrNight == null){	
// Weather Nightime Data
				sendEvent(name: "forcastPhraseToday", value: obs1.daypart[0].wxPhraseLong[1], isStateChange: state.force )
				sendEvent(name: "precipChanceToday", value: obs1.daypart[0].precipChance[1], isStateChange: state.force )
				sendEvent(name: "precipType", value: obs1.daypart[0].precipType[1], isStateChange: state.force )
				sendEvent(name: "cloudCover", value: obs1.daypart[0].precipChance[1], isStateChange: state.force )        	
				sendEvent(name: "uvDescription", value: obs1.daypart[0].uvDescription[1], isStateChange: state.force )
				sendEvent(name: "uvIndex", value: obs1.daypart[0].uvIndex[1], isStateChange: state.force )
				sendEvent(name: "thunderCategory", value: obs1.daypart[0].thunderCategory[1], isStateChange: state.force )
				sendEvent(name: "thunderIndex", value: obs1.daypart[0].thunderCategory[1], isStateChange: state.force )
				sendEvent(name: "snowRange", value: obs1.daypart[0].snowRange[1], isStateChange: state.force )
				sendEvent(name: "qpfSnow", value: obs1.daypart[0].qpfSnow[1], isStateChange: state.force ) 
				sendEvent(name: "fCstRainToday", value: obs1.daypart[0].qpf[1], isStateChange: state.force )
				sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[1], isStateChange: state.force )
				sendEvent(name: "weather", value: (obs1.daypart[0].narrative[1]), isStateChange: state.force )
				sendEvent(name: "wind_dir", value: obs1.daypart[0].windDirectionCardinal[1], isStateChange: state.force )
				sendEvent(name: "windPhrase", value: obs1.daypart[0].windPhrase[1], isStateChange: state.force )
				sendEvent(name: "windPhraseForecast", value: obs1.daypart[0].windPhrase[1], isStateChange: state.force )			
				sendEvent(name: "UVHarm", value: obs1.daypart[0].uvDescription[1], isStateChange: state.force )	     
        	}
// Weather Daytime Data
        	else {
				sendEvent(name: "forcastPhraseToday", value: obs1.daypart[0].wxPhraseLong[0], isStateChange: state.force )
				sendEvent(name: "precipChanceToday", value: obs1.daypart[0].precipChance[0], isStateChange: state.force )
				sendEvent(name: "precipType", value: obs1.daypart[0].precipType[0], isStateChange: state.force )
				sendEvent(name: "cloudCover", value: obs1.daypart[0].precipChance[0], isStateChange: state.force )         
				sendEvent(name: "uvDescription", value: obs1.daypart[0].uvDescription[0], isStateChange: state.force )
				sendEvent(name: "uvIndex", value: obs1.daypart[0].uvIndex[0], isStateChange: state.force )
				sendEvent(name: "thunderCategory", value: obs1.daypart[0].thunderCategory[0], isStateChange: state.force )
				sendEvent(name: "thunderIndex", value: obs1.daypart[0].thunderCategory[0], isStateChange: state.force )
				sendEvent(name: "snowRange", value: obs1.daypart[0].snowRange[0], isStateChange: state.force )
				sendEvent(name: "qpfSnow", value: obs1.daypart[0].qpfSnow[0], isStateChange: state.force ) 
				sendEvent(name: "fCstRainToday", value: obs1.daypart[0].qpf[0], isStateChange: state.force )
				sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[0], isStateChange: state.force )
				sendEvent(name: "weather", value: (obs1.daypart[0].narrative[0]), isStateChange: state.force )
				sendEvent(name: "wind_dir", value: obs1.daypart[0].windDirectionCardinal[0], isStateChange: state.force )
				sendEvent(name: "windPhrase", value: obs1.daypart[0].windPhrase[0], isStateChange: state.force )
				sendEvent(name: "windPhraseForecast", value: obs1.daypart[0].windPhrase[0], isStateChange: state.force )			
				sendEvent(name: "UVHarm", value: obs1.daypart[0].uvDescription[0], isStateChange: state.force )	     
            }  
               
// Weather Warnings Data
			if(state.dayOrNight == null){	
				state.weatherWarning = (obs1.daypart[0].qualifierPhrase[1])
				if(state.weatherWarning == null){sendEvent(name: "weatherWarning", value: "None", isStateChange: state.force )}
				else {sendEvent(name: "weatherWarning", value: (obs1.daypart[0].qualifierPhrase[1]), isStateChange: state.force )}	             		
				state.weatherWarningCode = (obs1.daypart[0].qualifierCode[1])
				if(state.weatherWarningCode == null){sendEvent(name: "weatherWarningCode", value: "None", isStateChange: state.force )}
				else {sendEvent(name: "weatherWarningCode", value: (obs1.daypart[0].qualifierCode[1]), isStateChange: state.force )}	}            
            else{	
				state.weatherWarning = (obs1.daypart[0].qualifierPhrase[0])
				if(state.weatherWarning == null){sendEvent(name: "weatherWarning", value: "None", isStateChange: state.force )}
				else {sendEvent(name: "weatherWarning", value: (obs1.daypart[0].qualifierPhrase[0]), isStateChange: state.force )}	             		
				state.weatherWarningCode = (obs1.daypart[0].qualifierCode[0])
				if(state.weatherWarningCode == null){sendEvent(name: "weatherWarningCode", value: "None", isStateChange: state.force )}
				else {sendEvent(name: "weatherWarningCode", value: (obs1.daypart[0].qualifierCode[0]), isStateChange: state.force )}         
				}
					
			state.weatherWarningTommorrow = (obs1.daypart[0].qualifierPhrase[2])
			if(state.weatherWarningTomorrow == null){sendEvent(name: "weatherWarningTomorrow", value: "None", isStateChange: state.force )}
			else {sendEvent(name: "weatherWarningTomorrow", value: (obs1.daypart[0].qualifierPhrase[2]), isStateChange: state.force )}	              		
			state.weatherWarningCodeTomorrow = (obs1.daypart[0].qualifierCode[2])
			if(state.weatherWarningCodeTomorrow == null){sendEvent(name: "weatherWarningCodeTomorrow", value: "None", isStateChange: state.force )}
			else {sendEvent(name: "weatherWarningCodeTomorrow", value: (obs1.daypart[0].qualifierCode[2]), isStateChange: state.force )}
				
			state.weatherWarningDATomorrow = (obs1.daypart[0].qualifierPhrase[4])
			if(state.weatherWarningDATomorrow == null){sendEvent(name: "weatherWarningDATomorrow", value: "None", isStateChange: state.force )}
			else {sendEvent(name: "weatherWarningDATomorrow", value: (obs1.daypart[0].qualifierPhrase[4]), isStateChange: state.force )}	              		
			state.weatherWarningCodeDATomorrow = (obs1.daypart[0].qualifierCode[4])
			if(state.weatherWarningCodeDATomorrow == null){sendEvent(name: "weatherWarningCodeDATomorrow", value: "None", isStateChange: state.force )}
			else {sendEvent(name: "weatherWarningCodeDATomorrow", value: (obs1.daypart[0].qualifierCode[4]), isStateChange: state.force )}
            
// Weather Icons Logic
			state.dayOrNight = (obs1.daypart[0].dayOrNight[0])
			if(useIcons){
			if(state.dayOrNight == null){	
				state.iconCode1 = (obs1.daypart[0].iconCode[1])
				state.iconCode2 = (obs1.daypart[0].iconCode[2])
				state.iconCode3 = (obs1.daypart[0].iconCode[4])		
				}				
			else{	
				state.iconCode1 = (obs1.daypart[0].iconCode[0])
				state.iconCode2 = (obs1.daypart[0].iconCode[2])
				state.iconCode3 = (obs1.daypart[0].iconCode[4])		
				}
            iconURL1 = "https://github.com/dJOS1475/Hubitat_WU_Driver/raw/main/wuIcons/"
            state.icon1 = "<img src='" +iconURL1 +state.iconCode1 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			state.icon2 = "<img src='" +iconURL1 +state.iconCode2 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			state.icon3 = "<img src='" +iconURL1 +state.iconCode3 +".png" +"' width='" +iconWidth1 +"' height='" +iconHeight1 +"'>"
			sendEvent(name: "forecastTodayIcon", value: state.icon1, isStateChange: state.force )
			sendEvent(name: "forecastTomorrowIcon", value: state.icon2, isStateChange: state.force )
			sendEvent(name: "forecastDayAfterTomorrowIcon", value: state.icon3, isStateChange: state.force )
			} 
        	     

        } 
      else {
        def res1 = resp1.getStatus()
		log.error "WU weather api did not return data from poll2 - $res1"
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

    String sTD='<td>'
    String sTR='<tr><td>'
    String iconSunrise = '<img src=https://tinyurl.com/icnqz/wsr.png>'
    String iconSunset = '<img src=https://tinyurl.com/icnqz/wss.png>'
    String degreeSign
    
    if(state.unit == "e")
    {
        degreeSign = "°F"
    }
    else
        if(state.unit == "m")
        {
        degreeSign = "°C"
        }
        else
            if(state.unit == "h")
            {
                degreeSign = "°C"
            }

    if(logSet == true){log.info "state.unit = $state.unit"}
    if(logSet == true){log.info "DegreeSign = $degreeSign"}


    int Tstart = "${device.currentValue('sunriseTimeLocal')}".indexOf('T')
    int Tstop1 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstart)
    int Tstop2 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstop1+1)
    String sunriseLocal = "${device.currentValue('sunriseTimeLocal')}".substring(Tstart+1, Tstop2)
    String strSunrise = "${convert24to12(sunriseLocal)}" 
    if(logSet == true){log.info "Sunset = $sunsetLocal"}

    Tstart = "${device.currentValue('sunsetTimeLocal')}".indexOf('T')
    Tstop1 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstart)
    Tstop2 = "${device.currentValue('sunriseTimeLocal')}".indexOf(':', Tstop1+1)
    String sunsetLocal = "${device.currentValue('sunsetTimeLocal')}".substring(Tstart+1, Tstop2)
    String strSunset = "${convert24to12(sunsetLocal)}"
    if(logSet == true){log.info "Sunrise = $sunriseLocal"}
    
        String strRainToday = ''
    if("${device.currentValue('precip_today')}" != '')
    {
        BigDecimal rainToday = "${device.currentValue('precip_today')}".toBigDecimal()
        if(rainToday > 0.00)
        {
            strRainToday = ' / ' + rainToday.toString()
        }
    }

    if(logSet == true){log.info "rainToday = $rainToday"}
    
    String lastPoll = convert24to12("${device.currentValue('lastPollTime')}")
    
	String my3day
	my3day = '<table >'
    my3day += '<TR>' 
	my3day += '<th>' + "<B> ${device.currentValue('station_location')}</B>" + '</th>'
	my3day += '<th style="min-width:5%"></th>'
	my3day += '<th>Today</th>'
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
    my3day += '<tr style="font-size:75%"> <td colspan="7">' + iconSunrise + strSunrise + ' ' + iconSunset + strSunset + ' @' + lastPoll
	my3day += '</table>'

    if(logSet == true){log.info 'html3dayfcst length: (' + my3day.length() + ')'}

    if(my3day.length() > 1024) {
		log.error('Too much data to display.</br></br>Current threedayfcstTile length (' + my3day.length() + ') exceeds maximum tile length by ' + (my3day.length() - 1024).toString()  + ' characters.')
        my3day = '<table>' + sTR + 'Error! Tile greater than 1024 characters. ' + sTR + my3day.length() + ' exceeds maximum tile length by ' + (my3day.length() - 1024).toString() + ' characters.' + sTR + 'Replacing 3 day with todays forecast<br>' + "${device.currentValue('htmlToday')}" + '</table>'
	}
	sendEvent(name: 'html3dayfcst', value: my3day.take(1024))

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