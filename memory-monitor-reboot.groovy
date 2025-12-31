/**
 *  Memory Monitor & Auto Reboot
 *
 *  Description:
 *  Monitors hub memory usage and automatically reboots when free memory falls below
 *  a configured threshold during a specified time window.
 *
 *  Features:
 *  - Configurable minimum free memory threshold
 *  - Configurable reboot time window
 *  - Manual test reboot function
 *  - Optional database rebuild on reboot
 *  - Detailed logging
 *  - Memory status tracking
 *
 *  Version: 1.0.4
 *  Author: Derek Osborn
 *  Date: 2026-01-01
 * 
 *  v1.0.4 - added import url and updated endpoint for reboot with db rebuild
 *  v1.0.2 - Added option to rebuild the Database on reboot
 *  v1.0.1 - removed Hub Security as no longer required
 *  v1.0.0 - First public release
 */
 */

definition(
    name: "Memory Monitor & Auto Reboot",
    namespace: "custom",
    author: "Derek Osborn",
    description: "Monitors hub memory usage and automatically reboots when free memory falls below threshold during configured time window",
    category: "Utility",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
    importUrl: "https://raw.githubusercontent.com/dJOS1475/Hubitat-Memory-Monitor-Auto-Reboot/refs/heads/main/memory-monitor-reboot.groovy"
)

preferences {
    page(name: "mainPage")
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "Memory Monitor & Auto Reboot", install: true, uninstall: true) {
        section("Memory Monitoring") {
            paragraph "<b>Version:</b> 1.0.4"
            paragraph "Current Hub Memory Status:"
            def memInfo = getMemoryInfo()
            if (memInfo) {
                paragraph "<b>Free Memory:</b> ${memInfo.free} MB<br>" +
                         "<b>Total Memory:</b> ${memInfo.total} MB<br>" +
                         "<b>Used Memory:</b> ${memInfo.used} MB<br>" +
                         "<b>Usage:</b> ${memInfo.percentUsed}%"
            } else {
                paragraph "Unable to retrieve memory information"
            }
        }
        
        section("Reboot Settings") {
            input "memoryThreshold", "number", 
                title: "Minimum Free Memory Threshold (MB)", 
                description: "Reboot when free memory falls below this value",
                required: true, 
                defaultValue: 50,
                range: "10..500"
            
            input "enableAutoReboot", "bool",
                title: "Enable Automatic Reboot",
                description: "Allow app to automatically reboot hub when threshold is reached",
                defaultValue: false,
                submitOnChange: true
            
            input "rebuildDatabase", "bool",
                title: "Rebuild Database on Reboot",
                description: "Perform database rebuild when rebooting (may take longer)",
                defaultValue: false
        }
        
        if (enableAutoReboot) {
            section("Reboot Time Window") {
                paragraph "The hub will only reboot within this time window when the memory threshold is reached"
                
                input "rebootStartTime", "time",
                    title: "Window Start Time",
                    description: "Start of allowed reboot window",
                    required: true
                
                input "rebootEndTime", "time",
                    title: "Window End Time", 
                    description: "End of allowed reboot window",
                    required: true
            }
        }
        
        section("Monitoring Schedule") {
            input "checkInterval", "enum",
                title: "Memory Check Interval",
                description: "How often to check memory usage",
                options: [
                    "1": "Every 1 minute",
                    "5": "Every 5 minutes",
                    "10": "Every 10 minutes",
                    "15": "Every 15 minutes",
                    "30": "Every 30 minutes",
                    "60": "Every 1 hour"
                ],
                defaultValue: "15",
                required: true
        }
        
        section("Notifications") {
            input "notifyBeforeReboot", "bool",
                title: "Log Warning Before Reboot",
                description: "Log a warning message before initiating reboot",
                defaultValue: true
        }
        
        section("Test Reboot") {
            paragraph "<b>Warning:</b> This will immediately reboot your hub!"
            input "testReboot", "button", title: "Test Reboot Now"
        }
        
        section("Logging") {
            input "enableDebug", "bool",
                title: "Enable Debug Logging",
                defaultValue: false
        }
        
        section("Statistics") {
            if (state.lastCheck) {
                paragraph "<b>Last Check:</b> ${new Date(state.lastCheck).format('yyyy-MM-dd HH:mm:ss')}"
            }
            if (state.lastReboot) {
                paragraph "<b>Last Auto Reboot:</b> ${new Date(state.lastReboot).format('yyyy-MM-dd HH:mm:ss')}"
            }
            if (state.rebootCount) {
                paragraph "<b>Total Auto Reboots:</b> ${state.rebootCount}"
            }
        }
    }
}

def installed() {
    log.info "Memory Monitor & Auto Reboot installed"
    initialize()
}

def updated() {
    log.info "Memory Monitor & Auto Reboot updated"
    unsubscribe()
    unschedule()
    initialize()
}

def uninstalled() {
    log.info "Memory Monitor & Auto Reboot uninstalled"
    unschedule()
}

def initialize() {
    state.rebootCount = state.rebootCount ?: 0
    
    // Schedule memory checks based on configured interval
    def interval = (checkInterval ?: "15").toInteger()
    
    switch(interval) {
        case 1:
            runEvery1Minute(checkMemory)
            break
        case 5:
            runEvery5Minutes(checkMemory)
            break
        case 10:
            runEvery10Minutes(checkMemory)
            break
        case 15:
            runEvery15Minutes(checkMemory)
            break
        case 30:
            runEvery30Minutes(checkMemory)
            break
        case 60:
            runEvery1Hour(checkMemory)
            break
        default:
            runEvery15Minutes(checkMemory)
    }
    
    log.info "Memory monitoring initialized - checking every ${interval} minute(s)"
    log.info "Threshold: ${memoryThreshold} MB free memory"
    
    if (enableAutoReboot) {
        log.info "Auto-reboot enabled for time window: ${rebootStartTime} to ${rebootEndTime}"
    } else {
        log.info "Auto-reboot is DISABLED"
    }
    
    // Do an initial check
    runIn(5, checkMemory)
}

def appButtonHandler(btn) {
    switch(btn) {
        case "testReboot":
            log.warn "TEST REBOOT button pressed - rebooting hub NOW"
            performReboot(true)
            break
    }
}

def checkMemory() {
    state.lastCheck = now()
    
    def memInfo = getMemoryInfo()
    
    if (!memInfo) {
        log.error "Unable to retrieve memory information"
        return
    }
    
    logDebug "Memory check - Free: ${memInfo.free} MB, Used: ${memInfo.used} MB (${memInfo.percentUsed}%)"
    
    // Check if we're below threshold
    if (memInfo.free < memoryThreshold) {
        log.warn "Free memory (${memInfo.free} MB) is below threshold (${memoryThreshold} MB)"
        
        if (enableAutoReboot) {
            if (isWithinRebootWindow()) {
                log.warn "Within reboot time window - initiating reboot"
                performReboot(false)
            } else {
                log.info "Below threshold but outside reboot time window - will reboot when window opens"
            }
        } else {
            log.info "Auto-reboot is disabled - no action taken"
        }
    } else {
        logDebug "Memory levels OK - ${memInfo.free} MB free (threshold: ${memoryThreshold} MB)"
    }
}

def getMemoryInfo() {
    try {
        // Get memory information using httpGet to the hub's local API
        // Requests from 127.0.0.1 bypass Hub Security authentication
        def params = [
            uri: "http://127.0.0.1:8080",
            path: "/hub/advanced/freeOSMemory",
            timeout: 5
        ]
        
        def freeMemKB = null
        
        httpGet(params) { resp ->
            if (resp.success) {
                // Response is the free memory in KB (kilobytes)
                freeMemKB = resp.data.toString().toLong()
            }
        }
        
        if (freeMemKB != null) {
            // Convert KB to MB
            def freeMemMB = Math.round(freeMemKB / 1024)
            
            // Determine total memory based on hub model
            // C-8 and C-8 Pro have 1GB RAM, C-7 has 512MB
            // If free memory is over 600MB, it's likely a 2GB hub (future models)
            def totalMemMB
            if (freeMemMB > 600) {
                totalMemMB = 2048 // 2GB hub
            } else if (freeMemMB > 300) {
                totalMemMB = 1024 // 1GB hub (C-8, C-8 Pro)
            } else {
                totalMemMB = 512  // 512MB hub (C-7)
            }
            
            def usedMemMB = totalMemMB - freeMemMB
            def percentUsed = Math.round((usedMemMB / totalMemMB) * 100)
            
            return [
                free: freeMemMB,
                total: totalMemMB,
                used: usedMemMB,
                percentUsed: percentUsed
            ]
        }
    } catch (Exception e) {
        log.error "Error getting memory stats: ${e.message}"
    }
    
    return null
}

def isWithinRebootWindow() {
    if (!rebootStartTime || !rebootEndTime) {
        return false
    }
    
    def now = new Date()
    def start = timeToday(rebootStartTime, location.timeZone)
    def end = timeToday(rebootEndTime, location.timeZone)
    
    // Handle time window spanning midnight
    if (end < start) {
        return (now >= start || now <= end)
    } else {
        return (now >= start && now <= end)
    }
}

def performReboot(isTest) {
    def memInfo = getMemoryInfo()
    
    if (notifyBeforeReboot || isTest) {
        def reason = isTest ? "TEST REBOOT" : "Low Memory (${memInfo?.free} MB free)"
        def dbAction = rebuildDatabase ? " with Database Rebuild" : ""
        log.warn "═══════════════════════════════════════"
        log.warn "REBOOTING HUB${dbAction} - Reason: ${reason}"
        log.warn "═══════════════════════════════════════"
    }
    
    if (!isTest) {
        state.lastReboot = now()
        state.rebootCount = (state.rebootCount ?: 0) + 1
    }
    
    // Pause briefly to ensure log message is written
    pauseExecution(2000)
    
    // Reboot the hub using the local API
    // Requests from 127.0.0.1 bypass Hub Security authentication
    try {
        // Determine reboot path based on database rebuild setting
        def rebootPath = rebuildDatabase ? "/hub/rebuildDatabaseAndReboot" : "/hub/reboot"
        
        def params = [
            uri: "http://127.0.0.1:8080",
            path: rebootPath,
            timeout: 5
        ]
        
        httpPost(params) { resp ->
            if (rebuildDatabase) {
                log.info "Database rebuild and reboot command sent successfully"
            } else {
                log.info "Reboot command sent successfully"
            }
        }
    } catch (Exception e) {
        log.error "Error sending reboot command: ${e.message}"
        log.error "You may need to reboot manually from Settings > Reboot"
    }
}

def logDebug(msg) {
    if (enableDebug) {
        log.debug msg
    }
}
