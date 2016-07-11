library(DBI)
library(RMySQL)

# Not available value for database queries
NAV <- "#NA"

# Database connection parameters
dbuser <- "root"
dbpassword <- "mario"
dbname <- "loganalysis"
dbhost <- "localhost"
dbport <- 3306
dbconnectionurl <- paste("jdbc:mysql://",dbhost,":",dbport,"/",dbname,sep="")

# Create database connection


###############################
# Merge resource table with rav
###############################
buildTables <- function(sourcedb=NULL) {
  con <<- dbConnect(MySQL(),dbname=sourcedb,username="root",password="mario")
  resources <<- buildResourcesTable()
  users <<- buildUsersTable()
  actions <<- buildActionsTable()
}

###############################
# Merge resource table with rav
###############################
buildResourcesTable <- function() {
  table <- "resource"
  eav <- "rav"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idResource"]) {

    # Get rav table
    sql <- paste("select * from ","rav","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idResource == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }

  }

  #resources <<- entity
  return(entity)
}

###############################
# Merge user table with uav
###############################
buildUsersTable <- function() {
  table <- "user"
  eav <- "uav"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idUser"])  {

    # Get rav table
    sql <- paste("select * from ","uav","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idUser == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  #users <<- entity
  return(entity)
}

###############################
# Merge action table with aav
###############################
buildActionsTable <- function() {
  table <- "action"
  eav <- "aav"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idAction"])  {

    # Get rav table
    sql <- paste("select * from ","aav","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idAction == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  #actions <<- entity
  return(entity)
}

getUsers <- function() {
  return(users)
}

getResources <- function() {
  return(resources)
}

getActions <- function() {
  return(actions)
}