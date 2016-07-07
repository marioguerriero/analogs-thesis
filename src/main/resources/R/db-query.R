library(DBI)
library(RMySQL)

# Not available value for database queries
NAV <- "#NA"

# Database connection parameters
dbuser <- "thesis"
dbpassword <- "thesis"
dbname <- "loganalysis"
dbhost <- "localhost"
dbport <- 3306
dbconnectionurl <- paste("jdbc:mysql://",dbhost,":",dbport,"/",dbname,sep="")

# Create database connection
con <<- dbConnect(RMySQL(),url=dbconnectionurl,user=dbuser,password=dbpassword)

buildTables <- function(sourcedb=NULL) {
  resources <<- buildResourcesTable(sourcedb)
  users <<- buildUsersTable(sourcedb)
  actions <<- buildActionsTable(sourcedb)
}

###############################
# Merge resource table with rav
###############################
buildResourcesTable <- function(sourcedb=NULL) {
  table <- "resource"
  eav <- "rav"

  # QueryType resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  for(a in attrs) {
    cname <- paste(table,".",a,sep="")
    entity[,cname] <- NAV
  }

  # Add resources attributes
  for(id in entity[,"idResource"]) {
    for(attr in attrs) {
      row <- which(grepl(id, eav$entityId) & grepl(attr, eav$attribute))
      value <- eav[row,"value"]

      row <- which(grepl(id, entity$idResource))

      if(length(value) == 1) {
        entity[row,paste(table,".",attr,sep="")] <- value
      }
    }
  }

  if(!is.null(sourcedb)) {
    entity <- entity[which(entity$resource.sourcedb == sourcedb),]
  }

  return(entity)
}

###############################
# Merge user table with uav
###############################
buildUsersTable <- function(sourcedb=NULL) {
  table <- "user"
  eav <- "uav"

  # QueryType resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  #  add columns for attributes
  attrs <- unique(eav$attribute)
  for(a in attrs) {
    cname <- paste(table,".",a,sep="")
    entity[,cname] <- NAV
  }

  # Add resources attributes
  for(id in entity[,"idUser"]) {
    for(attr in attrs) {
      row <- which(grepl(id, eav$entityId) & grepl(attr, eav$attribute))
      value <- eav[row,"value"]

      row <- which(grepl(id, entity$idUser))

      if(length(value) == 1) {
        entity[row,paste(table,".",attr,sep="")] <- value
      }
    }
  }

  if(!is.null(sourcedb)) {
    entity <- entity[which(entity$user.sourcedb == sourcedb),]
  }

  return(entity)
}

###############################
# Merge action table with aav
###############################
buildActionsTable <- function(sourcedb=NULL) {
  table <- "action"
  eav <- "aav"

  # QueryType resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  #  add columns for attributes
  attrs <- unique(eav$attribute)
  for(a in attrs) {
    cname <- paste(table,".",a,sep="")
    entity[,cname] <- NAV
  }

  # Add resources attributes
  for(id in entity[,"idAction"]) {
    for(attr in attrs) {
      row <- which(grepl(id, eav$entityId) & grepl(attr, eav$attribute))
      value <- eav[row,"value"]

      row <- which(grepl(id, entity$idAction))

      if(length(value) == 1) {
        entity[row,paste(table,".",attr,sep="")] <- value
      }
    }
  }

  if(!is.null(sourcedb)) {
    entity <- entity[which(entity$action.sourcedb == sourcedb),]
  }

  return(entity)
}

getUsers <- function() {
  return(users[,c("idUser","username")])
}