#source("/home/mario/dev/r/tesi/db-query.R")

#buildTables()

# Higher numeric precision
# useful when handling milliseconds
options(digits=22)

# ok - testata
# https://demo.vaadin.com/charts/#ColumnWithMultiLevelDrilldown
resourcesUsage <- function(users=NULL,from=NULL,to=NULL,attributes=NULL,normalize=FALSE){
type <- unique(resources[,"type"])
usage <- c(0)

result <- data.frame(type,usage)

for(r in resources$idResource) {
t <- resources[which(resources$idResource == r),"type"]

u <- actions[which(actions$idResource == r),"idUser"]
u <- u[1]

actionsCount <- length(which(
actions$idResource == r & (
(is.null(users) || all(users %in% u)) &
(is.null(from) || actions$millis >= from) &
(is.null(to) || actions$millis <= to) &
(is.null(attributes) || length(actions[,paste("action.",attributes$key,sep="")]) > 0 &&
attributes$value %in% actions[which(
actions$idResource == r  & actions$idUser %in% u &
actions[,paste("action.",attr$key,sep="")] != NAV
),paste("action.",attributes$key,sep="")] ) &
(is.null(attributes) | actions[,paste("action.",attr$key,sep="")] != NAV)
)
))

oldValue <- result[which(result$type == resources[which(resources$idResource == r),"type"]),"usage"]
result[which(result$type == resources[which(resources$idResource == r),"type"]),"usage"] <- (oldValue+actionsCount)
}

# Normalize
if(normalize) {
  total <- sum(result[,"usage"])
result[,"usage"] <- (result[,"usage"] / total)
}

result$type <- as.character(result$type)

return(result)
}

# ok - testata
# https://demo.vaadin.com/charts/#ColumnWithMultiLevelDrilldown
resourcesUsageTime <- function(users=NULL,from=NULL,to=NULL,attributes=NULL,normalize=FALSE){
  type <- unique(resources[,"type"])
time <- c(0)

result <- data.frame(type,time)

for(r in resources$idResource) {
  t <- resources[which(resources$idResource == r),"type"]

u <- actions[which(actions$idResource == r),"idUser"]

times <- (actions$action.totaltime[
which(
actions$idResource == r & actions$action.totaltime != NAV & (
(is.null(users) || all(users %in% u)) &
(is.null(from) || actions$millis >= from) &
(is.null(to) || actions$millis <= to) ) &
(is.null(attributes) || length(actions[,paste("action.",attributes$key,sep="")]) > 0 &&
attributes$value %in% actions[which(
actions$idResource == r  & actions$idUser %in% u &
actions[,paste("action.",attr$key,sep="")] != NAV
),paste("action.",attributes$key,sep="")] ) &
(is.null(attributes) | actions[,paste("action.",attr$key,sep="")] != NAV)
)
])

# Convert string to numeric
times <- as.numeric(times)

totaltime <- sum(times)

result[which(type == t),"time"] <- (result[which(type == t),"time"] + totaltime)
}

# Normalize
if(normalize) {
  total <- sum(result[,"time"])
result[,"time"] <- (result[,"time"] / total)
}

result$type <- as.character(result$type)

return(result)
}

# ok - testata
# https://demo.vaadin.com/charts/#BasicLine
dailyActiveUsers <- function(from,to,normalize=FALSE) {
  days <- c()
activeUsers <- c()

fromDate <- as.POSIXct(from/1000, origin="1970-01-01")
fromDate <- as.Date(fromDate)

toDate <- as.POSIXct(to/1000, origin="1970-01-01")
toDate <- as.Date(toDate)

actionDays <- actions$millis
actionDays <- as.POSIXct(actionDays/1000, origin="1970-01-01")
actionDays <- as.Date(actionDays)

while(fromDate <= toDate) {
  days <- append(days, as.Date(fromDate))
activeUsers <- append(activeUsers, length(unique(actions[which(actionDays == fromDate),"idUser"])))

fromDate <- (fromDate + 1)
}

# Normalize
if(normalize) {
  total <- sum(activeUsers)
activeUsers <- activeUsers / total
}

result <- data.frame(days,activeUsers)

result$days <- as.character(result$days)

return(result)
}

# ok - testata
# https://demo.vaadin.com/charts/#BasicLine
dailyActiveResources <- function(from,to,normalize=FALSE) {
  days <- c()
activeResources <- c()

fromDate <- as.POSIXct(from/1000, origin="1970-01-01")
fromDate <- as.Date(fromDate)

toDate <- as.POSIXct(to/1000, origin="1970-01-01")
toDate <- as.Date(toDate)

actionDays <- actions$millis
actionDays <- as.POSIXct(actionDays/1000, origin="1970-01-01")
actionDays <- as.Date(actionDays)

while(fromDate <= toDate) {
  days <- append(days, as.Date(fromDate))
activeResources <- append(activeResources, length(unique(actions[which(actionDays == fromDate),"idResource"])))

fromDate <- (fromDate + 1)
}

# Normalize
if(normalize) {
  total <- sum(activeResources)
activeResources <- activeResources / total
}

result <- data.frame(days,activeResources)

result$days <- as.character(result$days)

return(result)
}

# ok - testata
# https://demo.vaadin.com/charts/#BasicLineGettingMousePointerPosition
dailyActivitiesReleatedToUsersAndResources <- function(from,to,normalize=FALSE) {
  merge(dailyActiveUsers(from,to,normalize),dailyActiveResources(from,to,normalize))
}

# ok - testata
# https://demo.vaadin.com/charts/#BasicLine
timeRangesUsage <- function(normalize=FALSE) {
  hours <- c("00:00-00:59", "00:01-01:59", "02:00-02:59", "00:03-03:59", "00:04-04:59",
"05:00-05:59", "06:00-06:59", "07:00-07:59", "08:00-08:59", "00:09-09:59",
"10:00-10:59", "11:00-11:59", "12:00-12:59", "13:00-13:59", "14:00-14:59",
"15:00-15:59", "16:00-16:59", "17:00-17:59", "18:00-18:59", "19:00-19:59",
"20:00-20:59", "21:00-21:59", "22:00-22:59", "23:00-23:59")

hourCount <- vector(mode="integer", length=24)

usageDf <- data.frame(hours,hourCount)

for(millis in actions$millis) {
  hour <- (millis/(1000*60*60)) %% 24
usageDf[hour+1,2] <- usageDf[hour+1,2] + 1
}

# Normalize
if(normalize) {
  total <- sum(usageDf[,"hourCount"])
usageDf[,"hourCount"] <- (usageDf[,"hourCount"] / total)
}

usageDf$hours <- as.character(usageDf$hours)

return(usageDf)
}

# ok - testata
# https://demo.vaadin.com/charts/#PieChart
mostUsedOS <- function(users=NULL,normalize=FALSE) {
  os <- unique(actions[which(actions$action.os != NAV),"action.os"])
count <- rep(0, length(os))

result <- data.frame(os,count)

for(idAction in actions$idAction) {
  u <- actions[which(actions$idAction == idAction),"idUser"]

actionOs <- actions[which(actions$idAction == idAction & (
(is.null(users) || all(u %in% users)))),"action.os"]

result[which(result$os == actionOs),"count"] <- (result[which(result$os == actionOs),"count"] + 1)
}

# Normalize
if(normalize) {
  total <- sum(result[,"count"])
result[,"count"] <- (result[,"count"] / total)
}

result$os <- as.character(result$os)

return(result)
}

# ok - testata
# https://demo.vaadin.com/charts/#BasicLineWithDataLabels
resourceAddedPerDays <- function(users=NULL,from=NULL,to=NULL,normalize=FALSE) {
  days <- c()
resourcesAdded <- c()

fromDate <- as.POSIXct(from/1000, origin="1970-01-01")
fromDate <- as.Date(fromDate)

toDate <- as.POSIXct(to/1000, origin="1970-01-01")
toDate <- as.Date(toDate)

actionDays <- actions$millis
actionDays <- as.POSIXct(actionDays/1000, origin="1970-01-01")
actionDays <- as.Date(actionDays)

while(fromDate <= toDate) {
  days <- append(days, as.Date(fromDate))

tmp <- actions
tmp$millis <- as.POSIXct(tmp$millis/1000, origin="1970-01-01")
tmp$millis <- as.Date(actionDays)

createActionsInDay <- (length(
which(
tmp$type == "c" & tmp$millis == fromDate &
(is.null(users) || all(users %in% tmp$actions.idUser)) )))

resourcesAdded <- append(resourcesAdded,createActionsInDay)

fromDate <- (fromDate + 1)
}

# Normalize
if(normalize) {
  total <- sum(resourcesAdded)
resourcesAdded <- resourcesAdded / total
}

result <- data.frame(days,resourcesAdded)

result$days <- as.character(result$days)

return(result)
}

# tempo uso, action starttime e endtime