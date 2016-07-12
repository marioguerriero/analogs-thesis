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


###############################
# Merge resource table with eavs
###############################
buildTables <- function(sourcedb=NULL) {
  dbconnectionurl <- paste("jdbc:mysql://",dbhost,":",dbport,"/",sourcedb,"?useSSL=false",sep="")
  con <<- dbConnect(RMySQL(),url=dbconnectionurl,user=dbuser,password=dbpassword)
  resources <<- buildResourcesTable()
  users <<- buildUsersTable()
  actions <<- buildActionsTable()
}

###############################
# Merge resource table with resourceproperty
###############################
buildResourcesTable <- function() {
  table <- "resource"
  eav <- "resourceproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get resourceproperty table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idResource"]) {

    # Get resourceproperty table
    sql <- paste("select * from ","resourceproperty","where entityId =",id)
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
# Merge user table with userproperty
###############################
buildUsersTable <- function() {
  table <- "user"
  eav <- "userproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get resourceproperty table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idUser"])  {

    # Get resourceproperty table
    sql <- paste("select * from ","userproperty","where entityId =",id)
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
# Merge action table with actionproperty
###############################
buildActionsTable <- function() {
  table <- "action"
  eav <- "actionproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get resourceproperty table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idAction"])  {

    # Get resourceproperty table
    sql <- paste("select * from ","actionproperty","where entityId =",id)
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