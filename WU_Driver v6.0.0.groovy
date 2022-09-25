		
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
 *  v6.0.1 - Added HTML Tiles for easier / prettier Dashboard configuration (thanks to @thebearmay for his extensive coaching)
 *  v5.7.0 - Added a 3rd day of forecast data "forecastDayAfterTomorrow" including Icon
 *  v5.6.5 - Fixed Polling Bug
 *  v5.6.4 - Removed extra fields due to excess events being generated
 *  v5.6.3 - Removed Snow fields due to excess events being generated
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
    definition (name: "Wunderground Driver", namespace: "dJOS", author: "Derek Osborn") {
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
 	    
 	    attribute "html", "string"
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
        attribute "chanceOfRain", "number"
        attribute "rainTomorrow", "number"
        attribute "rainDayAfterTomorrow", "number"
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
            input "logSet", "bool", title: "Enable Logging", required: true, defaultValue: false
            input "cutOff", "time", title: "New Day Starts", required: true
			
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
    
    if(logSet){runIn(1800, logsOff)}
}


def forceUpdateOn(){
    state.force = true 
    log.info "Force attribute refresh set to 'true'"
}
def forceUpdateOff(){
    state.force = null
    log.info "Force attribute refresh set to 'false'"
}

def resetPollCount(){
	state.NumOfPolls = -1
    log.info "Poll counter reset.."
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
        if(logSet == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "Metric"){
		state.unit = "m"
        if(logSet == true){log.info "state.unit = $state.unit"}
	}
	if(unitFormat == "UK Hybrid"){
		state.unit = "h"
        if(logSet == true){log.info "state.unit = $state.unit"}
	}
	if(language == "US"){
		state.languagef = "en-US"
		if(logSet == true){log.info "state.languagef = $state.languagef"}
	}
	if(language == "GB"){
		state.languagef = "en-GB"
		if(logSet == true){log.info "state.languagef = $state.languagef"}
	}
		
	
	
}
def forcePoll(){
    if(logSet == true){log.debug "WU: Poll called"}
    state.NumOfPolls = (state.NumOfPolls) + 1
    sendEvent(name: "pollsSinceReset", value: state.NumOfPolls, isStateChange: state.force )
	poll1()
    pauseExecution(5000)
	poll2()
	pauseExecution(5000)
    updateTile()
    def date = new Date()
	        state.LastTime1 = date.format('HH:mm', location.timeZone)
            sendEvent(name: "lastPollTime", value: state.LastTime1)
	
}
	
def pollHandler1(resp, data) {
	if(resp.getStatus() == 200 || resp.getStatus() == 207) {
		obs = parseJson(resp.data)
        if(logSet == true){log.debug "Response Data1 = $obs"}		// log the data returned by WU
       
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
            if(logSet == true){log.debug "Poll1 - state.latt1 = $state.latt1 -- state.long1 = $state.long1 -- latt1Saved = $latt1Saved -- long1Saved = $long1Saved  "}
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
    if(logSet == true){log.debug "Poll2 - state.latt1 = $state.latt1 -- state.long1 = $state.long1"}
    asynchttpGet("pollHandler2", params2)
}

def pollHandler2(resp1, data) {
	if(resp1.getStatus() == 200 || resp1.getStatus() == 207) {
		obs1 = parseJson(resp1.data)
        if(logSet == true){log.debug "Response Data2 = $obs1"}		// log the data returned by WU
            sendEvent(name: "precipType", value: obs1.daypart[0].precipType[0], isStateChange: state.force )
            sendEvent(name: "cloudCover", value: obs1.daypart[0].cloudCover[0], isStateChange: state.force )
            sendEvent(name: "uvDescription", value: obs1.daypart[0].uvDescription[0], isStateChange: state.force )
            sendEvent(name: "uvIndex", value: obs1.daypart[0].uvIndex[0], isStateChange: state.force )
            sendEvent(name: "thunderCategory", value: obs1.daypart[0].thunderCategory[0], isStateChange: state.force )
            sendEvent(name: "thunderIndex", value: obs1.daypart[0].thunderCategory[0], isStateChange: state.force )
            sendEvent(name: "snowRange", value: obs1.daypart[0].snowRange[0], isStateChange: state.force )
            sendEvent(name: "qpfSnow", value: obs1.daypart[0].qpfSnow[0], isStateChange: state.force )
            sendEvent(name: "chanceOfRain", value: obs1.daypart[0].precipChance[0], isStateChange: state.force )
			sendEvent(name: "rainTomorrow", value: obs1.daypart[0].qpf[0], isStateChange: state.force )
            sendEvent(name: "rainDayAfterTomorrow", value: obs1.daypart[0].qpf[1], isStateChange: state.force )
			sendEvent(name: "forecastShort", value: obs1.narrative[0], isStateChange: state.force )
			state.forecastTemp = (obs1.daypart[0].narrative[0])
			if(state.forecastTemp == null){sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[1], isStateChange: state.force )}
            else {sendEvent(name: "forecastToday", value: obs1.daypart[0].narrative[0], isStateChange: state.force )}		
			sendEvent(name: "forecastTomorrow", value: obs1.daypart[0].narrative[2], isStateChange: state.force )
			sendEvent(name: "forecastDayAfterTomorrow", value: obs1.daypart[0].narrative[3], isStateChange: state.force )  
            sendEvent(name: "weather", value: obs1.daypart[0].wxPhraseLong[0], isStateChange: state.force )
            sendEvent(name: "wind_dir", value: obs1.daypart[0].windDirectionCardinal[0], isStateChange: state.force )
			sendEvent(name: "windPhrase", value: obs1.daypart[0].windPhrase[0], isStateChange: state.force )
			sendEvent(name: "windPhraseForecast", value: obs1.daypart[0].windPhrase[1], isStateChange: state.force )
			sendEvent(name: "forecastHigh", value: obs1.temperatureMax[0], isStateChange: state.force )
			sendEvent(name: "forecastLow", value: obs1.temperatureMin[0], isStateChange: state.force )
			sendEvent(name: "moonPhase", value: obs1.moonPhase[0], isStateChange: state.force )
            sendEvent(name: "today", value: obs1.dayOfWeek[0], isStateChange: state.force )
            sendEvent(name: "tomorrow", value: obs1.dayOfWeek[1], isStateChange: state.force )
            sendEvent(name: "dayAfterTomorrow", value: obs1.dayOfWeek[2], isStateChange: state.force )
			sendEvent(name: "UVHarm", value: obs1.daypart[0].uvDescription[0], isStateChange: state.force )
			state.dayOrNight = (obs1.daypart[0].dayOrNight[0])
			if(useIcons){
			if(state.forecastTemp == null){	
			state.iconCode1 = (obs1.daypart[0].iconCode[1])
			state.iconCode2 = (obs1.daypart[0].iconCode[2])
			state.iconCode3 = (obs1.daypart[0].iconCode[3])		
				}				
			else{	
			state.iconCode1 = (obs1.daypart[0].iconCode[0])
			state.iconCode2 = (obs1.daypart[0].iconCode[2])
			state.iconCode3 = (obs1.daypart[0].iconCode[3])		
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


def updateTile() {
	log.debug "updateTile called"
	html = "<table>"
	html +="<caption>3 Day Forecast</caption>"
	html +="<tr><th><small>Day: </small></th><td><small> ${device.currentValue('today')} </small></td></tr>"
	html +="<tr><th><small>Forecast: </small></th><td><small> ${device.currentValue('forecastToday')} </small></td></tr>"
	html +="<tr><th><small>Day: </small></th><td><small> ${device.currentValue('tomorrow')} </small></td></tr>"
	html +="<tr><th><small>Forecast: </small></th><td><small> ${device.currentValue('forecastTomorrow')} </small></td></tr>"
	html +="<tr><th><small>Day: </small></th><td><small> ${device.currentValue('dayAfterTomorrow')} </small></td></tr>"
	html +="<tr><th><small>Forecast: </small></th><td><small> ${device.currentValue('forecastDayAfterTomorrow')}</small> </td></tr>"
	html +="</table>"
	log.debug "html contains ${html}"
	sendEvent(name: "html", value: "$html")
	}
	

def logsOff() {
log.warn "Debug logging disabled..."
device.updateSetting("logSet", [value: "false", type: "bool"])}

